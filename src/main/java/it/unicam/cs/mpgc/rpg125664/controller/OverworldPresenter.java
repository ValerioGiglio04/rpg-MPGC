package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.overworld.GymStatus;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import java.util.Objects;
import java.util.Optional;

/** Presenter mappa overworld: posizione, spostamento e stato palestre. */
public final class OverworldPresenter {

  private final GameModel gameModel;

  public OverworldPresenter(GameModel gameModel) {
    this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
  }

  public GameState gameState() {
    return gameModel.gameState();
  }

  public Optional<OverworldPosition> savedPosition() {
    return gameModel.overworldPosition();
  }

  public void syncPosition(int row, int column) {
    gameModel.setOverworldPosition(new OverworldPosition(row, column));
  }

  public void moveToGym(long gymId) {
    gameModel.moveTo(gymId);
  }

  public GymStatus statusOf(GymRoom gym) {
    return gameModel.statusOf(gym);
  }

  public String blockedReason(GymRoom gym, GymStatus status) {
    int playerPoints = gameModel.gameState().player().score().points();
    return switch (status) {
      case COMPLETED -> Messages.format("overworld.blocked.completed", gym.name());
      case UNREACHABLE -> Messages.format("overworld.blocked.unreachable", gym.name());
      case NEEDS_POINTS ->
          Messages.format(
              "overworld.blocked.needs.points", gym.name(), gym.requiredPoints(), playerPoints);
      case CURRENT, AVAILABLE -> "";
    };
  }

  public boolean canStartChallenge(GymRoom gym) {
    return gameModel.statusOf(gym) == GymStatus.AVAILABLE;
  }
}
