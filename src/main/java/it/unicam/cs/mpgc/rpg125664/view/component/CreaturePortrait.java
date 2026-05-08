package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import java.io.InputStream;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public final class CreaturePortrait extends StackPane {

  private static final double INNER_PADDING = 14;

  public CreaturePortrait(Creature creature, double size) {
    setMinSize(size, size);
    setPrefSize(size, size);
    setMaxSize(size, size);
    setAlignment(Pos.CENTER);
    getStyleClass().add("creature-portrait");

    InputStream imageStream = getClass().getResourceAsStream(creature.skinPath());
    if (imageStream == null) {
      String name = creature.name();
      String initial = name.isEmpty() ? "?" : name.substring(0, 1);
      Label fallback = new Label(initial);
      fallback.getStyleClass().addAll("panel-title", "portrait-fallback");
      getChildren().add(fallback);
      setAlignment(fallback, Pos.CENTER);
      return;
    }

    ImageView imageView = new ImageView(new Image(imageStream));
    double imageSize = Math.max(1, size - INNER_PADDING);
    imageView.setFitWidth(imageSize);
    imageView.setFitHeight(imageSize);
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);
    getChildren().add(imageView);
  }
}
