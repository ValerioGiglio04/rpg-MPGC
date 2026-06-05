package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import java.util.List;
import java.util.Objects;

/** Applica i premi di completamento palestra dopo la sconfitta del boss. */
public final class GymCompletionHandler {

  private final GameCatalog catalog;

  public GymCompletionHandler(GameCatalog catalog) {
    this.catalog = Objects.requireNonNull(catalog, "catalog");
  }

  public void awardGymCompletion(List<BattleEvent> events, GameState state, GymRoom gym) {
    gym.markCompleted();
    state.player().score().add(gym.boss().pointsReward());
    events.add(new BattleEvent.BossDefeated(gym.boss().name(), gym.boss().pointsReward()));
    List<Creature> rewards = transferBossCreaturesToPlayer(state, gym);
    if (!rewards.isEmpty()) {
      List<String> creatureNames = rewards.stream().map(Creature::name).toList();
      events.add(new BattleEvent.CreaturesAcquired(creatureNames));
    }
  }

  private List<Creature> transferBossCreaturesToPlayer(GameState state, GymRoom gym) {
    CreatureHolder playerHolder = state.player().holder();
    List<Creature> rewards =
        gym.boss().holder().creatures().stream()
            .map(creature -> catalog.buildCreature(creature.catalogId()))
            .peek(Creature::healToFull)
            .toList();
    rewards.forEach(playerHolder::addCreature);
    return rewards;
  }
}
