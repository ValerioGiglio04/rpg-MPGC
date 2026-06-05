package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.VictoryActions;
import it.unicam.cs.mpgc.rpg125664.controller.VictoryPresenter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public final class VictoryController implements Initializable {

  private final VictoryPresenter presenter;
  private final VictoryActions actions;

  @FXML private Label scoreLabel;

  @FXML private Button newRunButton;

  @FXML private Button menuButton;

  public VictoryController(VictoryPresenter presenter, VictoryActions actions) {
    this.presenter = presenter;
    this.actions = actions;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    scoreLabel.setText(presenter.scoreText());
    newRunButton.setOnAction(event -> actions.onNewRun());
    menuButton.setOnAction(event -> actions.onBackToMenu());
  }
}
