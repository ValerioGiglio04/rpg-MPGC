package it.unicam.cs.mpgc.rpg125664.controller.navigation;

import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Carica FXML con {@link Messages#resourceBundle()}; i controller vanno passati da codice, non da
 * FXML.
 */
public final class FxmlScreenLoader {

  private FxmlScreenLoader() {}

  public static Parent load(String classpathLocation, Object controller) {
    String errorMessage = "Missing FXML resource: " + classpathLocation;
    URL url =
        Objects.requireNonNull(FxmlScreenLoader.class.getResource(classpathLocation), errorMessage);
    assertNoFxControllerAttribute(url, classpathLocation);
    FXMLLoader loader = new FXMLLoader(url);
    loader.setResources(Messages.resourceBundle());
    if (controller != null) {
      loader.setController(controller);
    }
    try {
      return loader.load();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * {@code fx:controller} nel file e {@link FXMLLoader#setController(Object)} insieme causano
   * {@code LoadException: Controller value already specified}.
   */
  private static void assertNoFxControllerAttribute(URL url, String classpathLocation) {
    try (InputStream in = url.openStream()) {
      String document = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      if (document.contains("fx:controller")) {
        throw new IllegalStateException(
            classpathLocation
                + " declares fx:controller but FxmlScreenLoader.load() sets the controller in code."
                + " Remove fx:controller from the FXML file.");
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
