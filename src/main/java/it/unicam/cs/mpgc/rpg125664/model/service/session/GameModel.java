package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.service.BattleService;
import it.unicam.cs.mpgc.rpg125664.model.service.HealingService;
import it.unicam.cs.mpgc.rpg125664.model.service.NewGameService;
import it.unicam.cs.mpgc.rpg125664.model.overworld.GymStatus;
import it.unicam.cs.mpgc.rpg125664.model.overworld.strategy.GymStatusStrategy;
import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.session.LoadedSession;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import it.unicam.cs.mpgc.rpg125664.model.session.SaveSessionCommand;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Singolo entry point con cui parla la UI. */
public final class GameModel {

  private final GameStateHolder holder;
  private final NewGameService newGame;
  private final BattleService battle;
  private final HealingService healing;
  private final SessionPersistenceFacade persistence;
  private final GymStatusStrategy gymStatusStrategy;

  public GameModel(
      GameStateHolder holder,
      NewGameService newGame,
      BattleService battle,
      HealingService healing,
      SessionPersistenceFacade persistence,
      GymStatusStrategy gymStatusStrategy) {
    this.holder = Objects.requireNonNull(holder, "holder");
    this.newGame = Objects.requireNonNull(newGame, "newGame");
    this.battle = Objects.requireNonNull(battle, "battle");
    this.healing = Objects.requireNonNull(healing, "healing");
    this.persistence = Objects.requireNonNull(persistence, "persistence");
    this.gymStatusStrategy = Objects.requireNonNull(gymStatusStrategy, "gymStatusStrategy");
  }

  public GameState gameState() {
    return holder.current();
  }

  public boolean hasAnySave() {
    return persistence.hasAnySave();
  }

  public List<SavedSessionSummary> listSaves() {
    return persistence.listSaves();
  }

  public Optional<String> currentSessionName() {
    return holder.currentSessionName();
  }

  public void startNewGame() {
    holder.clearOverworldPosition();
    holder.clearCurrentSession();
    newGame.start();
  }

  public Optional<OverworldPosition> overworldPosition() {
    return holder.overworldPosition();
  }

  public void setOverworldPosition(OverworldPosition position) {
    holder.setOverworldPosition(position);
  }

  public void moveTo(long gymId) {
    holder.current().moveTo(gymId);
  }

  public boolean isGymReachable(GymRoom gym) {
    return holder.current().isGymReachable(gym);
  }

  public boolean canChallengeGym(GymRoom gym) {
    return holder.current().canChallengeGym(gym);
  }

  public GymStatus statusOf(GymRoom gym) {
    return gymStatusStrategy.resolve(holder.current(), gym);
  }

  public void beginCurrentBattle() {
    battle.begin();
  }

  public void prepareCurrentBattle() {
    battle.prepare();
  }

  public List<BattleEvent> attack(int moveIndex) {
    return battle.attack(moveIndex);
  }

  public void switchPlayerCreature(long creatureCatalogId) {
    battle.switchTo(creatureCatalogId);
  }

  public void healPlayerCreature(long creatureCatalogId) {
    healing.healCreature(holder.current(), creatureCatalogId);
  }

  public int spendableGloryForHealing() {
    return healing.spendableGlory(holder.current());
  }

  public int healCostForCreature(long creatureCatalogId) {
    return healing.healCostForCreature(holder.current(), creatureCatalogId);
  }

  public void saveCurrent() {
    long id =
        persistence.save(
            new SaveSessionCommand(
                holder.current(),
                holder.overworldPosition(),
                holder.currentSessionId(),
                holder.currentSessionName()));
    refreshSessionMeta(id);
    persistence.markLastPlayed(id);
  }

  public void saveAsNew(String name) {
    long id =
        persistence.save(
            new SaveSessionCommand(
                holder.current(), holder.overworldPosition(), Optional.empty(), Optional.of(name)));
    refreshSessionMeta(id);
    persistence.markLastPlayed(id);
  }

  public void loadSession(long sessionId) {
    LoadedSession loaded = persistence.load(sessionId);
    applyLoadedSession(loaded);
    persistence.markLastPlayed(sessionId);
    SavedSessionSummary meta =
        listSaves().stream()
            .filter(s -> s.id() == sessionId)
            .findFirst()
            .orElse(new SavedSessionSummary(sessionId, "Partita", java.time.Instant.now(), 0));
    holder.setCurrentSession(sessionId, meta.name());
  }

  public void deleteSession(long sessionId) {
    persistence.delete(sessionId);
    if (holder.currentSessionId().filter(id -> id == sessionId).isPresent()) {
      holder.clearCurrentSession();
    }
  }

  private void applyLoadedSession(LoadedSession loaded) {
    holder.replace(loaded.state());
    holder.clearOverworldPosition();
    loaded.overworldPosition().ifPresent(holder::setOverworldPosition);
  }

  private void refreshSessionMeta(long sessionId) {
    listSaves().stream()
        .filter(s -> s.id() == sessionId)
        .findFirst()
        .ifPresent(s -> holder.setCurrentSession(sessionId, s.name()));
  }
}
