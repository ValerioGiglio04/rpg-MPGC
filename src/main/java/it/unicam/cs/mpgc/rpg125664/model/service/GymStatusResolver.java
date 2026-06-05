package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;

public final class GymStatusResolver {

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
