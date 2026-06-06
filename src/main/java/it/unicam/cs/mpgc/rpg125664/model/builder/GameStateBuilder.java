package it.unicam.cs.mpgc.rpg125664.model.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.entity.Player;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;

public final class GameStateBuilder {

  private Player player;
  private List<GymRoom> gyms = new ArrayList<>();
  private long currentGymId;

  public GameStateBuilder player(Player player) {
    this.player = player;
    return this;
  }

  public GameStateBuilder gyms(List<GymRoom> gyms) {
    this.gyms = new ArrayList<>(gyms);
    return this;
  }

  public GameStateBuilder currentGymId(long currentGymId) {
    this.currentGymId = currentGymId;
    return this;
  }

  public GameState build() {
    GameState state = new GameState(player, gyms, currentGymId);
    Validator<GameState> validator = ValidatorFactory.getGameStateValidator();
    validator.validate(state);
    return state;
  }
}
