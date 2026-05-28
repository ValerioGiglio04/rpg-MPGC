package it.unicam.cs.mpgc.rpg125664.view.component;

import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

public final class HamburgerMenu extends MenuButton {

  private static final String GLYPH = "\u2630";

  public HamburgerMenu() {
    super(GLYPH);
    setFocusTraversable(false);
  }

  public HamburgerMenu(MenuItem... items) {
    super(GLYPH);
    setFocusTraversable(false);
    getItems().addAll(items);
  }
}
