package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import java.util.Objects;

public final class VictoryPresenter {

  private final GameModel gameModel;

  public VictoryPresenter(GameModel gameModel) {
    this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
  }

  public String scoreText() {
    int points = gameModel.gameState().player().score().points();
    return Messages.format("victory.score", points);
  }
}
