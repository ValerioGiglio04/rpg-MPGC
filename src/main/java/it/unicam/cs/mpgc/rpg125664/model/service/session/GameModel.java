package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.service.BattleService;
import it.unicam.cs.mpgc.rpg125664.model.service.GymStatus;
import it.unicam.cs.mpgc.rpg125664.model.service.HealingService;
import it.unicam.cs.mpgc.rpg125664.model.service.NewGameService;
import it.unicam.cs.mpgc.rpg125664.model.GameStateRepository;
import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.session.LoadedSession;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import it.unicam.cs.mpgc.rpg125664.model.session.SaveSessionCommand;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import it.unicam.cs.mpgc.rpg125664.view.overworld.MapCoordinate;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Singolo entry point con cui parla la UI. Compone i servizi ({@link NewGameService}, {@link
 * BattleService}, {@link HealingService}) ed espone wrapper sottili per navigazione, save/load e
 * stato palestre -- operazioni one-liner di per se' che non giustificano una loro classe.
 */
public final class GameModel {

  private final GameStateHolder holder;
  private final NewGameService newGame;
  private final BattleService battle;
  private final HealingService healing;
  private final GameStateRepository repository;

  public GameModel(
      GameStateHolder holder,
      NewGameService newGame,
      BattleService battle,
      HealingService healing,
      GameStateRepository repository) {
    this.holder = Objects.requireNonNull(holder, "holder");
    this.newGame = Objects.requireNonNull(newGame, "newGame");
    this.battle = Objects.requireNonNull(battle, "battle");
    this.healing = Objects.requireNonNull(healing, "healing");
    this.repository = Objects.requireNonNull(repository, "repository");
  }

  public GameState gameState() {
    return holder.current();
  }

  public boolean hasAnySave() {
    return repository.hasAnySave();
  }

  public List<SavedSessionSummary> listSaves() {
    return repository.listSaves();
  }

  public Optional<String> currentSessionName() {
    return holder.currentSessionName();
  }

  public void startNewGame() {
    holder.clearOverworldPosition();
    holder.clearCurrentSession();
    newGame.start();
  }

  public Optional<MapCoordinate> overworldPosition() {
    return holder.overworldPosition();
  }

  public void setOverworldPosition(MapCoordinate position) {
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
    GameState state = holder.current();
    if (gym.completed()) return GymStatus.COMPLETED;
    if (state.canChallengeGym(gym)) return GymStatus.AVAILABLE;
    if (state.currentGym().id() == gym.id()) return GymStatus.CURRENT;
    if (state.isGymReachable(gym)) return GymStatus.NEEDS_POINTS;
    return GymStatus.UNREACHABLE;
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

  public void saveCurrent() throws IOException {
    long id =
        repository.save(
            new SaveSessionCommand(
                holder.current(),
                toOverworldPosition(holder.overworldPosition()),
                holder.currentSessionId(),
                holder.currentSessionName()));
    refreshSessionMeta(id);
    repository.markLastPlayed(id);
  }

  public void saveAsNew(String name) throws IOException {
    long id =
        repository.save(
            new SaveSessionCommand(
                holder.current(),
                toOverworldPosition(holder.overworldPosition()),
                Optional.empty(),
                Optional.of(name)));
    refreshSessionMeta(id);
    repository.markLastPlayed(id);
  }

  public void loadSession(long sessionId) throws IOException {
    LoadedSession loaded = repository.load(sessionId);
    applyLoadedSession(loaded);
    repository.markLastPlayed(sessionId);
    SavedSessionSummary meta =
        listSaves().stream()
            .filter(s -> s.id() == sessionId)
            .findFirst()
            .orElse(new SavedSessionSummary(sessionId, "Partita", java.time.Instant.now(), 0));
    holder.setCurrentSession(sessionId, meta.name());
  }

  public void deleteSession(long sessionId) throws IOException {
    repository.delete(sessionId);
    if (holder.currentSessionId().filter(id -> id == sessionId).isPresent()) {
      holder.clearCurrentSession();
    }
  }

  private void applyLoadedSession(LoadedSession loaded) {
    holder.replace(loaded.state());
    holder.clearOverworldPosition();
    loaded
        .overworldPosition()
        .ifPresent(pos -> holder.setOverworldPosition(new MapCoordinate(pos.row(), pos.column())));
  }

  private void refreshSessionMeta(long sessionId) {
    listSaves().stream()
        .filter(s -> s.id() == sessionId)
        .findFirst()
        .ifPresent(s -> holder.setCurrentSession(sessionId, s.name()));
  }

  private static Optional<OverworldPosition> toOverworldPosition(Optional<MapCoordinate> coord) {
    return coord.map(c -> new OverworldPosition(c.row(), c.column()));
  }
}
