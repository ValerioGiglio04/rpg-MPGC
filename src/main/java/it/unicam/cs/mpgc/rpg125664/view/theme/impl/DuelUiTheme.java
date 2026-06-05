package it.unicam.cs.mpgc.rpg125664.view.theme.impl;

import it.unicam.cs.mpgc.rpg125664.view.theme.UiTheme;
import javafx.scene.Parent;

/** Presentazione di default in stile handheld del duello: attiva {@code theme-duel} sulla root. */
public final class DuelUiTheme implements UiTheme {

  @Override
  public void applyTo(Parent root) {
    var styleClass = root.getStyleClass();
    if (!styleClass.contains("theme-duel")) {
      styleClass.add("theme-duel");
    }
  }
}
