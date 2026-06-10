package it.unicam.cs.mpgc.rpg125664.controller.navigation.support;

import java.util.Objects;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/** Gestisce lo swap del figlio root e la chiusura dei menu aperti prima della transizione. */
public final class RootScreenStack {

  private final StackPane root;

  public RootScreenStack(StackPane root) {
    this.root = Objects.requireNonNull(root, "root");
  }

  public void setScreen(Parent screen) {
    dismissOpenMenus(root);
    root.getChildren().setAll(screen);
    screen.setManaged(true);
    screen.setVisible(true);
    if (screen instanceof Region region) {
      region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }
    root.applyCss();
    root.layout();
    screen.requestFocus();
  }

  /** Chiude popup MenuButton ancora aperti: altrimenti coprono la schermata successiva. */
  private static void dismissOpenMenus(Node node) {
    if (node instanceof MenuButton menuButton && menuButton.isShowing()) {
      menuButton.hide();
    }
    if (node instanceof Parent parent) {
      parent.getChildrenUnmodifiable().forEach(child -> dismissOpenMenus(child));
    }
  }
}
