package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.view.component.GameButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** Controller per {@code BattleEndOverlay.fxml} (overlay vittoria/sconfitta). */
public final class BattleEndOverlayController {

  @FXML private Label titleLabel;

  @FXML private Label bodyLabel;

  @FXML private GameButton okButton;

  public void bind(String title, String message, Runnable onOk) {
    titleLabel.setText(title);
    bodyLabel.setText(message);
    okButton.setOnAction(event -> onOk.run());
  }
}
