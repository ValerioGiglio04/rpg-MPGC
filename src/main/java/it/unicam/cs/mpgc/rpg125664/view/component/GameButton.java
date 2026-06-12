package it.unicam.cs.mpgc.rpg125664.view.component;

import javafx.scene.control.Button;

/** Pulsante stilizzato tema gioco (secondario, pericolo). */
public final class GameButton extends Button {

  public GameButton() {
    applyChrome();
  }

  public GameButton(String text) {
    super(text);
    applyChrome();
  }

  private void applyChrome() {
    getStyleClass().add("game-button");
    setMaxWidth(Double.MAX_VALUE);
  }

  public GameButton asSecondary() {
    getStyleClass().add("secondary-button");
    return this;
  }

  public GameButton asDanger() {
    getStyleClass().add("danger-button");
    return this;
  }
}
