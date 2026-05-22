package it.unicam.cs.mpgc.rpg125664.view.overworld;

import java.io.IOException;
import java.io.InputStream;
import javafx.scene.image.Image;

/** Texture di classpath condivise dal renderer di tile overworld. */
public final class OverworldTextures {

  private OverworldTextures() {}

  public static final Image TREE = load("/textures/tree.png");
  public static final Image BUSH = load("/textures/bushes.png");
  public static final Image GYM_BUILDING = load("/textures/gym-building.png");

  private static Image load(String resourcePath) {
    try (InputStream stream = OverworldTextures.class.getResourceAsStream(resourcePath)) {
      if (stream == null) {
        throw new IllegalStateException("Missing texture resource: " + resourcePath);
      }
      return new Image(stream);
    } catch (IOException e) {
      throw new IllegalStateException("Failed reading texture: " + resourcePath, e);
    }
  }
}
