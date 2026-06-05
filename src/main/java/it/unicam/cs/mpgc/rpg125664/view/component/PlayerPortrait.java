package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;
import it.unicam.cs.mpgc.rpg125664.view.component.builder.PlayerPortraitBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public final class PlayerPortrait extends StackPane {

  private static final String DEFAULT_TRAINER_IMAGE = "/images/player/player-skin.png";

  /** Vecchio default PNG nel catalogo: stesso portrait del nuovo asset. */
  private static final String LEGACY_TRAINER_PNG = "/images/player/trainer-default.png";

  private static final double INNER_PADDING = 12;

  public PlayerPortrait(String playerName, String skinPath, double size) {
    applyPortraitSizing(size);
    getStyleClass().addAll("creature-portrait", "player-portrait");
    String resolvedPath = resolveSkinPath(skinPath);
    loadPortraitOrFallback(playerName, resolvedPath, size);
  }

  public static PlayerPortraitBuilder builder() {
    return new PlayerPortraitBuilder();
  }

  private void applyPortraitSizing(double size) {
    setMinSize(size, size);
    setPrefSize(size, size);
    setMaxSize(size, size);
    setAlignment(Pos.CENTER);
    setFocusTraversable(false);
  }

  private static String resolveSkinPath(String skinPath) {
    if (LEGACY_TRAINER_PNG.equals(skinPath)) {
      return DEFAULT_TRAINER_IMAGE;
    }
    return skinPath;
  }

  private void loadPortraitOrFallback(String playerName, String path, double size) {
    try (InputStream imageStream = resolveImageStream(path)) {
      if (imageStream == null) {
        throw new IOException(missingStreamMessage(path));
      }
      renderPortraitImage(size, imageStream);
    } catch (IOException ignored) {
      showFallbackInitial(playerName);
    }
  }

  private static String missingStreamMessage(String path) {
    return Messages.format("ui.error.player.portrait.stream", path);
  }

  private void renderPortraitImage(double size, InputStream imageStream) {
    ImageView imageView = new ImageView(new Image(imageStream));
    double imageSize = Math.max(1, size - INNER_PADDING);
    imageView.setFitWidth(imageSize);
    imageView.setFitHeight(imageSize);
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);
    getChildren().add(imageView);
  }

  private static String extractFallbackInitial(String playerName) {
    if (playerName == null || playerName.isEmpty()) return "?";
    return playerName.substring(0, 1).toUpperCase();
  }

  private void showFallbackInitial(String playerName) {
    String letter = extractFallbackInitial(playerName);
    Label fallback = new Label(letter);
    fallback.getStyleClass().addAll("panel-title", "portrait-fallback");
    getChildren().add(fallback);
  }

  private InputStream resolveImageStream(String skinPath) throws IOException {
    InputStream imageStream = getClass().getResourceAsStream(skinPath);
    if (imageStream != null) return imageStream;
    Path filePath = Path.of(skinPath);
    if (!Files.exists(filePath)) return null;
    return Files.newInputStream(filePath);
  }
}
