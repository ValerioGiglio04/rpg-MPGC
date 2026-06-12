package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.MainMenuActions;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/** Controller FXML menu principale (nuova partita, load, uscita). */
public final class MainMenuController implements Initializable {

  private final MainMenuActions actions;
  private final boolean loadEnabled;

  @FXML private Button newGameButton;

  @FXML private Button loadButton;

  @FXML private Button exitButton;

  public MainMenuController(MainMenuActions actions, boolean loadEnabled) {
    this.actions = actions;
    this.loadEnabled = loadEnabled;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    newGameButton.setOnAction(event -> actions.onNewGame());
    loadButton.setDisable(!loadEnabled);
    loadButton.setOnAction(event -> actions.onLoadGame());
    exitButton.setOnAction(event -> actions.onExit());
  }
}
