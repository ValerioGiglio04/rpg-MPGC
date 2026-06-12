package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.overworld.GymStatus;
import it.unicam.cs.mpgc.rpg125664.model.overworld.strategy.GymStatusStrategy;
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

  public GameModel(GameModelOptions options) {
    Objects.requireNonNull(options, "options");
    this.holder = options.holder();
    this.newGame = options.newGame();
    this.battle = options.battle();
    this.healing = options.healing();
    this.persistence = options.persistence();
    this.gymStatusStrategy = options.gymStatusStrategy();
  }

  public GameState gameState() {
    return holder.current();
  }

  public boolean allGymsCompleted() {
    return holder.current().allGymsCompleted();
  }

  public GymRoom currentGym() {
    return holder.current().currentGym();
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
    persistSession(currentSaveCommand());
  }

  public void saveAsNew(String name) {
    persistSession(newSaveCommand(name));
  }

  private SaveSessionCommand currentSaveCommand() {
    return new SaveSessionCommand(
      holder.current(),
      holder.overworldPosition(),
      holder.currentSessionId(),
      holder.currentSessionName()
    );
  }

  private SaveSessionCommand newSaveCommand(String name) {
    return new SaveSessionCommand(
      holder.current(),
      holder.overworldPosition(),
      Optional.empty(),
      Optional.of(name)
    );
  }

  private void persistSession(SaveSessionCommand command) {
    long id = persistence.save(command);
    refreshSessionMeta(id);
    persistence.markLastPlayed(id);
  }

  public void loadSession(long sessionId) {
    LoadedSession loaded = persistence.load(sessionId);
    applyLoadedSession(loaded);
    persistence.markLastPlayed(sessionId);
    SavedSessionSummary meta = listSaves()
      .stream()
      .filter(s -> s.id() == sessionId)
      .findFirst()
      .orElse(new SavedSessionSummary(sessionId, "Partita", java.time.Instant.now(), 0));
    holder.setCurrentSession(sessionId, meta.name());
  }

  public void deleteSession(long sessionId) {
    persistence.delete(sessionId);
    if (
      holder
        .currentSessionId()
        .filter(id -> id == sessionId)
        .isPresent()
    ) {
      holder.clearCurrentSession();
    }
  }

  private void applyLoadedSession(LoadedSession loaded) {
    holder.replace(loaded.state());
    holder.clearOverworldPosition();
    loaded.overworldPosition().ifPresent(holder::setOverworldPosition);
  }

  private void refreshSessionMeta(long sessionId) {
    listSaves()
      .stream()
      .filter(s -> s.id() == sessionId)
      .findFirst()
      .ifPresent(s -> holder.setCurrentSession(sessionId, s.name()));
  }
}
