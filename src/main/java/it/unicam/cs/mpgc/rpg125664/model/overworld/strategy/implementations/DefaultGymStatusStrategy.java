package it.unicam.cs.mpgc.rpg125664.model.overworld.strategy.implementations;

import it.unicam.cs.mpgc.rpg125664.model.overworld.GymStatus;
import it.unicam.cs.mpgc.rpg125664.model.overworld.strategy.GymStatusStrategy;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;

/** Implementazione default delle regole {@link GymStatus} per la mappa overworld. */
public final class DefaultGymStatusStrategy implements GymStatusStrategy {

  @Override
  public GymStatus resolve(GameState state, GymRoom gym) {
    if (gym.completed()) {
      return GymStatus.COMPLETED;
    }
    if (state.canChallengeGym(gym)) {
      return GymStatus.AVAILABLE;
    }
    if (state.currentGym().id() == gym.id()) {
      return GymStatus.CURRENT;
    }
    if (state.isGymReachable(gym)) {
      return GymStatus.NEEDS_POINTS;
    }
    return GymStatus.UNREACHABLE;
  }
}
