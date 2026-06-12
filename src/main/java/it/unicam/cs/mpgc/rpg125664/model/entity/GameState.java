package it.unicam.cs.mpgc.rpg125664.model.entity;

import it.unicam.cs.mpgc.rpg125664.model.builder.GameStateBuilder;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;
import it.unicam.cs.mpgc.rpg125664.model.validation.support.ValidatorFactory;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/** Stato partita corrente (giocatore, palestre, palestra attiva). */
public final class GameState implements Serializable {

  private final Player player;
  private final List<GymRoom> gyms;
  private long currentGymId;

  public static GameStateBuilder builder() {
    return new GameStateBuilder();
  }

  public GameState(Player player, List<GymRoom> gyms, long currentGymId) {
    this.player = player;
    this.gyms = List.copyOf(gyms);
    this.currentGymId = currentGymId;
  }

  public Player player() {
    return player;
  }

  public List<GymRoom> gyms() {
    return Collections.unmodifiableList(gyms);
  }

  public long currentGymId() {
    return currentGymId;
  }

  public GymRoom currentGym() {
    return gyms
      .stream()
      .filter(gym -> gym.id() == currentGymId)
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("Current gym does not exist"));
  }

  public void moveTo(long gymId) {
    if (!currentGym().connectedGymIds().contains(gymId)) {
      throw new IllegalArgumentException("Gym is not connected to current gym");
    }
    currentGymId = gymId;
    Validator<GameState> validator = ValidatorFactory.getGameStateValidator();
    validator.validate(this);
  }

  public boolean allGymsCompleted() {
    return gyms.stream().allMatch(GymRoom::completed);
  }

  /** Se {@code gym} e' la posizione corrente o direttamente connessa da essa. */
  public boolean isGymReachable(GymRoom gym) {
    GymRoom current = currentGym();
    return current.id() == gym.id() || current.connectedGymIds().contains(gym.id());
  }

  public boolean hasReachedRequiredPoints(GymRoom gym) {
    return player.score().points() >= gym.requiredPoints();
  }

  /**
   * Se il giocatore puo' iniziare una battaglia in {@code gym} da questo snapshot del mondo (non
   * completata, raggiungibile, punti sufficienti).
   */
  public boolean canChallengeGym(GymRoom gym) {
    return !gym.completed() && isGymReachable(gym) && hasReachedRequiredPoints(gym);
  }
}
