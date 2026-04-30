package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.view.overworld.MapCoordinate;
import java.util.Objects;
import java.util.Optional;

/**
 * Container mutabile condiviso dagli use case. Sostituisce l'idiom del field-su-service cosi' ogni
 * use case legge/aggiorna lo stesso riferimento {@link GameState}.
 */
public final class GameStateHolder {

  private GameState gameState;
  private MapCoordinate overworldPosition;
  private Long currentSessionId;
  private String currentSessionName;

  public GameStateHolder(GameState initial) {
    this.gameState = Objects.requireNonNull(initial, "initial");
  }

  public GameState current() {
    return gameState;
  }

  public void replace(GameState newState) {
    this.gameState = Objects.requireNonNull(newState, "newState");
  }

  public Optional<MapCoordinate> overworldPosition() {
    return Optional.ofNullable(overworldPosition);
  }

  public void setOverworldPosition(MapCoordinate position) {
    this.overworldPosition = Objects.requireNonNull(position, "position");
  }

  public void clearOverworldPosition() {
    this.overworldPosition = null;
  }

  public Optional<Long> currentSessionId() {
    return Optional.ofNullable(currentSessionId);
  }

  public Optional<String> currentSessionName() {
    return Optional.ofNullable(currentSessionName);
  }

  public void setCurrentSession(long sessionId, String name) {
    this.currentSessionId = sessionId;
    this.currentSessionName = Objects.requireNonNull(name, "name");
  }

  public void clearCurrentSession() {
    this.currentSessionId = null;
    this.currentSessionName = null;
  }
}
