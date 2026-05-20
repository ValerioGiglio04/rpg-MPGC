package it.unicam.cs.mpgc.rpg125664.model.session;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import java.util.Objects;
import java.util.Optional;

public record SaveSessionCommand(
    GameState state,
    Optional<OverworldPosition> overworldPosition,
    Optional<Long> sessionId,
    Optional<String> name) {

  public SaveSessionCommand {
    Objects.requireNonNull(state, "state");
    Objects.requireNonNull(overworldPosition, "overworldPosition");
    Objects.requireNonNull(sessionId, "sessionId");
    Objects.requireNonNull(name, "name");
  }
}
