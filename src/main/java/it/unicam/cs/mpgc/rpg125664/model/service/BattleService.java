package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.service.GameStateHolder;
import it.unicam.cs.mpgc.rpg125664.model.combat.BattleRoundExecutor;
import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import java.util.List;
import java.util.Objects;

/**
 * Ciclo di vita della battaglia: prepara l'ingresso, esegue un singolo round di attacco, switcha la
 * creatura attiva.
 */
public final class BattleService {

  private final GameStateHolder holder;
  private final BattleRoundExecutor roundExecutor;
  private final GymCompletionHandler gymCompletionHandler;

  public BattleService(
      GameStateHolder holder,
      BattleRoundExecutor roundExecutor,
      GymCompletionHandler gymCompletionHandler) {
    this.holder = Objects.requireNonNull(holder, "holder");
    this.roundExecutor = Objects.requireNonNull(roundExecutor, "roundExecutor");
    this.gymCompletionHandler =
        Objects.requireNonNull(gymCompletionHandler, "gymCompletionHandler");
  }

  public void begin() {
    GymRoom gym = requireChallengeable();
    gym.boss().holder().healAll();
    holder.current().player().holder().healAllToFullPreservingActive();
    holder.current().player().holder().switchToFirstAliveIfNeeded();
    gym.boss().holder().switchToFirstAliveIfNeeded();
  }

  public void prepare() {
    requireChallengeable();
    holder.current().player().holder().switchToFirstAliveIfNeeded();
    holder.current().currentGym().boss().holder().switchToFirstAliveIfNeeded();
  }

  public List<BattleEvent> attack(int moveIndex) {
    requireChallengeable();
    if (holder.current().player().holder().allKnockedOut()) {
      throw new IllegalStateException("Player team is knocked out");
    }
    GameState state = holder.current();
    List<BattleEvent> events = roundExecutor.execute(state, moveIndex);
    boolean allBossKnockedOut = state.currentGym().boss().holder().allKnockedOut();
    if (allBossKnockedOut && !state.currentGym().completed()) {
      gymCompletionHandler.awardGymCompletion(events, state, state.currentGym());
    }
    return events;
  }

  public void switchTo(long creatureCatalogId) {
    holder.current().player().holder().switchTo(creatureCatalogId);
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
}
