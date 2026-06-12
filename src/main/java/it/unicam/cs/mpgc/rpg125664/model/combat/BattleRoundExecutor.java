package it.unicam.cs.mpgc.rpg125664.model.combat;

import it.unicam.cs.mpgc.rpg125664.model.combat.strategy.AttackResolutionStrategy;
import it.unicam.cs.mpgc.rpg125664.model.combat.strategy.BossMoveStrategy;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.event.Side;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Esegue un round di duello (ordine turni, due attacchi, switch su KO). */
public final class BattleRoundExecutor {

  private record RoundAttack(
    List<BattleEvent> events,
    GameState state,
    Side side,
    int playerMoveIndex
  ) {}

  private final AttackResolutionStrategy attackResolutionStrategy;
  private final BossMoveStrategy bossMoveStrategy;

  public BattleRoundExecutor(
    AttackResolutionStrategy attackResolutionStrategy,
    BossMoveStrategy bossMoveStrategy
  ) {
    this.attackResolutionStrategy = Objects.requireNonNull(
      attackResolutionStrategy,
      "attackResolutionStrategy"
    );
    this.bossMoveStrategy = Objects.requireNonNull(bossMoveStrategy, "bossMoveStrategy");
  }

  public List<BattleEvent> execute(GameState state, int playerMoveIndex) {
    CreatureHolder playerHolder = state.player().holder();
    CreatureHolder bossHolder = state.currentGym().boss().holder();
    playerHolder.switchToFirstAliveIfNeeded();
    bossHolder.switchToFirstAliveIfNeeded();

    List<BattleEvent> events = new ArrayList<>();
    Creature playerCreature = playerHolder.activeCreature();
    Creature bossCreature = bossHolder.activeCreature();
    Side first = playerCreature.speed() >= bossCreature.speed() ? Side.PLAYER : Side.BOSS;
    events.add(new BattleEvent.RoundStarted(first, activeCreature(state, first).name()));

    attackOnce(new RoundAttack(events, state, first, playerMoveIndex));
    if (!battleEnded(events, state)) {
      swapKoIfNeeded(events, state, first.opposite());
      attackOnce(new RoundAttack(events, state, first.opposite(), playerMoveIndex));
      battleEnded(events, state);
    }
    return events;
  }

  private void attackOnce(RoundAttack attack) {
    Creature attacker = activeCreature(attack.state(), attack.side());
    if (attacker.isKnockedOut()) {
      return;
    }
    Creature defender = activeCreature(attack.state(), attack.side().opposite());
    Move move =
      attack.side() == Side.PLAYER
        ? clampedPlayerMove(attacker, attack.playerMoveIndex())
        : bossMoveStrategy.pickMove(attacker);
    attack.events().add(new BattleEvent.MoveUsed(attack.side(), attacker.name(), move.name()));
    AttackOutcome outcome = attackResolutionStrategy.execute(attacker, defender, move);
    if (!outcome.hit()) {
      attack.events().add(new BattleEvent.AttackMissed(attack.side()));
      return;
    }
    attack
      .events()
      .add(new BattleEvent.AttackHit(attack.side(), defender.name(), outcome.damage()));
    if (!outcome.defenderKnockedOut()) {
      return;
    }
    attack
      .events()
      .add(new BattleEvent.CreatureKnockedOut(attack.side().opposite(), defender.name()));
  }

  private void swapKoIfNeeded(List<BattleEvent> events, GameState state, Side side) {
    CreatureHolder creatureHolder = sideHolder(state, side);
    if (!creatureHolder.activeCreature().isKnockedOut()) {
      return;
    }
    String previousName = creatureHolder.activeCreature().name();
    creatureHolder.switchToFirstAliveIfNeeded();
    Creature now = creatureHolder.activeCreature();
    if (!now.isKnockedOut() && !previousName.equals(now.name())) {
      events.add(new BattleEvent.CreatureSwitched(side, previousName, now.name()));
    }
  }

  private boolean battleEnded(List<BattleEvent> events, GameState state) {
    if (state.currentGym().boss().holder().allKnockedOut()) {
      return true;
    }
    if (state.player().holder().allKnockedOut()) {
      events.add(new BattleEvent.PlayerTeamWiped());
      return true;
    }
    return false;
  }

  private Move clampedPlayerMove(Creature attacker, int requestedIndex) {
    List<Move> moves = attacker.moves();
    int safe = Math.min(Math.max(0, requestedIndex), moves.size() - 1);
    return moves.get(safe);
  }

  private CreatureHolder sideHolder(GameState state, Side side) {
    return side == Side.PLAYER ? state.player().holder() : state.currentGym().boss().holder();
  }

  private Creature activeCreature(GameState state, Side side) {
    return sideHolder(state, side).activeCreature();
  }
}
