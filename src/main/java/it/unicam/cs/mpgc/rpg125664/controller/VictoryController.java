package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.VictoryActions;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/** Controller FXML schermata vittoria (punteggio, nuova run, menu). */
public final class VictoryController implements Initializable {

  private final GameModel gameModel;
  private final VictoryActions actions;

  @FXML
  private Label scoreLabel;

  @FXML
  private Button newRunButton;

  @FXML
  private Button menuButton;

  public VictoryController(GameModel gameModel, VictoryActions actions) {
    this.gameModel = gameModel;
    this.actions = actions;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    int points = gameModel.gameState().player().score().points();
    scoreLabel.setText(Messages.format("victory.score", points));
    newRunButton.setOnAction(event -> actions.onNewRun());
    menuButton.setOnAction(event -> actions.onBackToMenu());
  }
}
