package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import java.util.List;

/**
 * Applica i premi di completamento palestra: marca la palestra come done, assegna il punteggio,
 * clona le creature del boss nel team del giocatore, ed appende i {@link BattleEvent}
 * corrispondenti.
 */
public final class GymCompletionHandler {

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
            .map(this::cloneHealed)
            .peek(Creature::healToFull)
            .toList();
    rewards.forEach(playerHolder::addCreature);
    return rewards;
  }

  private Creature cloneHealed(Creature creature) {
    return Creature.builder()
        .catalogId(creature.catalogId())
        .name(creature.name())
        .role(creature.role())
        .skinPath(creature.skinPath())
        .maxHealth(creature.maxHealth())
        .attack(creature.attack())
        .defense(creature.defense())
        .speed(creature.speed())
        .moves(creature.moves())
        .build();
  }
}
