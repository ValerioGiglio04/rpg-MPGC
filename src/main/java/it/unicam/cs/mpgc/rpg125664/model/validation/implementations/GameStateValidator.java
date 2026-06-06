package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;

public final class GameStateValidator extends Validator<GameState> {

  @Override
  protected String nullMessage() {
    return "GameState cannot be null";
  }

  @Override
  protected void validateRules(GameState state) {
    Rules.requireNonNull(state.player(), "Game state needs player");
    Rules.requireNonNull(state.gyms(), "Game state needs gyms");
    if (state.gyms().isEmpty()) {
      throw new IllegalArgumentException("Game state needs at least one gym");
    }
    Rules.requirePositiveId(state.currentGymId(), "Current gym id must be positive");
    boolean found = state.gyms().stream().anyMatch(gym -> gym.id() == state.currentGymId());
    if (!found) {
      throw new IllegalStateException("Current gym does not exist");
    }
  }
}
