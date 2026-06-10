package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import java.io.InputStream;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public final class CreaturePortrait extends StackPane {

  private static final double INNER_PADDING = 14;

  public CreaturePortrait(Creature creature, PortraitAssetResolver portraitAssets, double size) {
    this(portraitAssets.creatureSkinPath(creature.catalogId()), creature.name(), size);
  }

  public CreaturePortrait(String skinPath, String displayName, double size) {
    setMinSize(size, size);
    setPrefSize(size, size);
    setMaxSize(size, size);
    setAlignment(Pos.CENTER);
    getStyleClass().add("creature-portrait");

    InputStream imageStream = getClass().getResourceAsStream(skinPath);
    if (imageStream == null) {
      String initial = displayName.isEmpty() ? "?" : displayName.substring(0, 1);
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
