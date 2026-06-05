package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.entity.Player;
import it.unicam.cs.mpgc.rpg125664.model.validation.AbstractDomainValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import java.util.List;

/**
 * Valida un {@link GameState}: un giocatore presente, almeno una palestra e un {@code currentGymId}
 * che risolve in una di quelle palestre. Si ottiene tramite {@link
 * Validators#getGameStateValidator()}.
 */
public final class GameStateValidator extends AbstractDomainValidator<GameState> {

  GameStateValidator() {}

  @Override
  protected String nullMessage() {
    return "GameState cannot be null";
  }

  @Override
  protected void validateRules(GameState state) {
    Player player = state.player();
    Rules.requireNonNull(player, "Game state needs player");
    List<GymRoom> gyms = state.gyms();
    Rules.requireNonNull(gyms, "Game state needs gyms");
    if (gyms.isEmpty()) {
      throw new IllegalArgumentException("Game state needs at least one gym");
    }
    long currentGymId = state.currentGymId();
    Rules.requirePositiveId(currentGymId, "Current gym id must be positive");
    boolean found = gyms.stream().anyMatch(gym -> gym.id() == currentGymId);
    if (!found) {
      throw new IllegalStateException("Current gym does not exist");
    }
  }
}
