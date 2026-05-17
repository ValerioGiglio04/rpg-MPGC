package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.service.GameStateHolder;
import it.unicam.cs.mpgc.rpg125664.model.combat.AttackOutcome;
import it.unicam.cs.mpgc.rpg125664.model.combat.BossMoveStrategy;
import it.unicam.cs.mpgc.rpg125664.model.combat.combatEngne;
import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.event.Side;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Ciclo di vita della battaglia: prepara l'ingresso, esegue un singolo round di attacco, switcha la
 * creatura attiva. Pilota il {@link CombatEngine} per la risoluzione di colpo/danno, delega la
 * scelta della mossa del boss a {@link BossMoveStrategy} e i premi di completamento palestra a
 * {@link GymCompletionHandler}. {@link #begin()} resetta boss e team del giocatore a HP pieni per
 * un nuovo tentativo di palestra (lo slot attivo del giocatore viene mantenuto).
 */
public final class BattleService {

  private final GameStateHolder holder;
  private final CombatEngine combatEngine;
  private final BossMoveStrategy bossMoveStrategy;
  private final GymCompletionHandler gymCompletionHandler;

  public BattleService(
      GameStateHolder holder,
      CombatEngine combatEngine,
      BossMoveStrategy bossMoveStrategy,
      GymCompletionHandler gymCompletionHandler) {
    this.holder = Objects.requireNonNull(holder, "holder");
    this.combatEngine = Objects.requireNonNull(combatEngine, "combatEngine");
    this.bossMoveStrategy = Objects.requireNonNull(bossMoveStrategy, "bossMoveStrategy");
    this.gymCompletionHandler =
        Objects.requireNonNull(gymCompletionHandler, "gymCompletionHandler");
  }

  public void begin() {
    GymRoom gym = requireChallengeable();
    gym.boss().holder().healAll();
    holder.current().player().holder().healAllToFullPreservingActive();
    switchToFirstAvailable(holder.current().player().holder());
    switchToFirstAvailable(gym.boss().holder());
  }

  public void prepare() {
    requireChallengeable();
    switchToFirstAvailable(holder.current().player().holder());
    switchToFirstAvailable(holder.current().currentGym().boss().holder());
  }

  public List<BattleEvent> attack(int moveIndex) {
    requireChallengeable();
    if (holder.current().player().holder().allKnockedOut()) {
      throw new IllegalStateException("Player team is knocked out");
    }
    return runRound(moveIndex);
  }

  public void switchTo(long creatureCatalogId) {
    holder.current().player().holder().switchTo(creatureCatalogId);
  }

  private List<BattleEvent> runRound(int moveIndex) {
    GameState state = holder.current();
    CreatureHolder playerHolder = state.player().holder();
    CreatureHolder bossHolder = state.currentGym().boss().holder();
    switchToFirstAvailable(playerHolder);
    switchToFirstAvailable(bossHolder);

    List<BattleEvent> events = new ArrayList<>();
    Creature playerCreature = playerHolder.activeCreature();
    Creature bossCreature = bossHolder.activeCreature();
    Side first = playerCreature.speed() >= bossCreature.speed() ? Side.PLAYER : Side.BOSS;
    events.add(new BattleEvent.RoundStarted(first, activeCreature(state, first).name()));

    attackOnce(events, state, first, moveIndex);
    if (!battleEnded(events, state)) {
      swapKoIfNeeded(events, state, first.opposite());
      attackOnce(events, state, first.opposite(), moveIndex);
      battleEnded(events, state);
    }
    boolean allBossKnockedOut = state.currentGym().boss().holder().allKnockedOut();
    boolean gymCompleted = state.currentGym().completed();
    if (allBossKnockedOut && !gymCompleted) {
      gymCompletionHandler.awardGymCompletion(events, state, state.currentGym());
    }
    return events;
  }

  private void attackOnce(
      List<BattleEvent> events, GameState state, Side side, int playerMoveIndex) {
    Creature attacker = activeCreature(state, side);
    if (attacker.isKnockedOut()) return;
    Creature defender = activeCreature(state, side.opposite());
    Move move =
        side == Side.PLAYER
            ? clampedPlayerMove(attacker, playerMoveIndex)
            : bossMoveStrategy.pickMove(attacker);
    BattleEvent moveUsedEvent = new BattleEvent.MoveUsed(side, attacker.name(), move.name());
    events.add(moveUsedEvent);
    AttackOutcome outcome = combatEngine.execute(attacker, defender, move);
    if (!outcome.hit()) {
      BattleEvent attackMissedEvent = new BattleEvent.AttackMissed(side);
      events.add(attackMissedEvent);
      return;
    }
    BattleEvent attackHitEvent = new BattleEvent.AttackHit(side, defender.name(), outcome.damage());
    events.add(attackHitEvent);
    boolean defenderKnockedOut = outcome.defenderKnockedOut();
    if (!defenderKnockedOut) return;
    BattleEvent creatureKnockedOutEvent =
        new BattleEvent.CreatureKnockedOut(side.opposite(), defender.name());
    events.add(creatureKnockedOutEvent);
  }

  private void swapKoIfNeeded(List<BattleEvent> events, GameState state, Side side) {
    CreatureHolder creatureHolder = sideHolder(state, side);
    if (!creatureHolder.activeCreature().isKnockedOut()) return;
    String previousName = creatureHolder.activeCreature().name();
    switchToFirstAvailable(creatureHolder);
    Creature now = creatureHolder.activeCreature();
    boolean shouldSwitch = !now.isKnockedOut() && !previousName.equals(now.name());
    if (!shouldSwitch) return;
    events.add(new BattleEvent.CreatureSwitched(side, previousName, now.name()));
  }

  private boolean battleEnded(List<BattleEvent> events, GameState state) {
    if (state.currentGym().boss().holder().allKnockedOut()) return true;
    if (state.player().holder().allKnockedOut()) {
      BattleEvent playerTeamWipedEvent = new BattleEvent.PlayerTeamWiped();
      events.add(playerTeamWipedEvent);
      return true;
    }
    return false;
  }

  private Move clampedPlayerMove(Creature attacker, int requestedIndex) {
    List<Move> moves = attacker.moves();
    int safe = Math.min(Math.max(0, requestedIndex), moves.size() - 1);
    return moves.get(safe);
  }

  private void switchToFirstAvailable(CreatureHolder creatureHolder) {
    if (!creatureHolder.activeCreature().isKnockedOut()) return;
    creatureHolder.creatures().stream()
        .filter(creature -> !creature.isKnockedOut())
        .findFirst()
        .ifPresent(creature -> creatureHolder.switchTo(creature.catalogId()));
  }

  private GymRoom requireChallengeable() {
    GameState state = holder.current();
    GymRoom gym = state.currentGym();
    if (state.canChallengeGym(gym)) {
      return gym;
    }
    if (gym.completed()) {
      throw new IllegalStateException("Gym already completed");
    }
    throw new IllegalStateException("Not enough points or gym is not challengeable");
  }

  private CreatureHolder sideHolder(GameState state, Side side) {
    return side == Side.PLAYER ? state.player().holder() : state.currentGym().boss().holder();
  }

  private Creature activeCreature(GameState state, Side side) {
    return sideHolder(state, side).activeCreature();
  }
}
