package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.VictoryActions;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public final class VictoryController implements Initializable {

  private final GameModel gameModel;
  private final VictoryActions actions;

  @FXML private Label scoreLabel;

  @FXML private Button newRunButton;

  @FXML private Button menuButton;

  public VictoryController(GameModel gameModel, VictoryActions actions) {
    this.gameModel = gameModel;
    this.actions = actions;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    scoreLabel.setText(
        Messages.format("victory.score", gameModel.gameState().player().score().points()));
    newRunButton.setOnAction(event -> actions.onNewRun());
    menuButton.setOnAction(event -> actions.onBackToMenu());
  }
}
