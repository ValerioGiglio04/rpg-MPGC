package it.unicam.cs.mpgc.rpg125664.view.component;

import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

public final class HamburgerMenu extends MenuButton {

  private static final String GLYPH = "\u2630";

  public HamburgerMenu() {
    super(GLYPH);
    applyChrome();
  }

  public HamburgerMenu(MenuItem... items) {
    super(GLYPH);
    applyChrome();
    getItems().addAll(items);
  }

  private void applyChrome() {
    getStyleClass().add("hamburger-menu");
    setFocusTraversable(false);
  }
}
