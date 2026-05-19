package it.unicam.cs.mpgc.rpg125664.model.session;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import java.util.Objects;
import java.util.Optional;

public record LoadedSession(GameState state, Optional<OverworldPosition> overworldPosition) {

  public LoadedSession {
    Objects.requireNonNull(state, "state");
    Objects.requireNonNull(overworldPosition, "overworldPosition");
  }
}
