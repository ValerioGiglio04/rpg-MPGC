package it.unicam.cs.mpgc.rpg125664.view.component;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public final class GamePanel extends VBox {

  public GamePanel(String title, Node... content) {
    super(12);
    getStyleClass().add("game-panel");

    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("panel-title");
    getChildren().add(titleLabel);
    getChildren().addAll(content);
  }

  public GamePanel highlighted() {
    getStyleClass().remove("game-panel");
    getStyleClass().add("game-panel-highlight");
    return this;
  }

  public GamePanel withStyle(String styleClass) {
    getStyleClass().add(styleClass);
    return this;
  }
}
