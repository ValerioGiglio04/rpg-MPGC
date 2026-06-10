package it.unicam.cs.mpgc.rpg125664.view.overworld;

import it.unicam.cs.mpgc.rpg125664.view.component.GameButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

/** Controlli zoom (+/− e rotella) per la mappa overworld. */
final class OverworldZoomControls {
  private static final String ZOOM_IN_SIGN = "+";
  private static final String ZOOM_OUT_SIGN = "-";
  private static final Insets MARGIN = new Insets(12);
  private static final int BUTTONS_SPACING = 6;
  private static final int BUTTON_SIZE = 36;

  private final VBox root;
  private final Scale scaleTransform;
  private double currentZoom = OverworldMapConstants.DEFAULT_ZOOM;

  OverworldZoomControls(Scale scaleTransform) {
    this.scaleTransform = scaleTransform;
    this.root = buildControls();
    setZoom(OverworldMapConstants.DEFAULT_ZOOM);
  }

  VBox root() {
    return root;
  }

  void mountOn(StackPane host) {
    StackPane.setAlignment(root, Pos.TOP_RIGHT);
    StackPane.setMargin(root, MARGIN);
  }

  void onScroll(ScrollEvent event, boolean modalOpen) {
    if (modalOpen) return;
    if (event.getDeltaY() > 0) {
      zoomIn();
    } else if (event.getDeltaY() < 0) {
      zoomOut();
    }
    event.consume();
  }

  void zoomIn() {
    setZoom(currentZoom + OverworldMapConstants.ZOOM_STEP);
  }

  void zoomOut() {
    setZoom(currentZoom - OverworldMapConstants.ZOOM_STEP);
  }

  void setZoom(double zoom) {
    currentZoom =
        Math.max(OverworldMapConstants.MIN_ZOOM, Math.min(OverworldMapConstants.MAX_ZOOM, zoom));
    scaleTransform.setX(currentZoom);
    scaleTransform.setY(currentZoom);
  }

  private VBox buildControls() {
    VBox controls =
        new VBox(
            BUTTONS_SPACING,
            styledZoomButton(ZOOM_IN_SIGN, this::zoomIn),
            styledZoomButton(ZOOM_OUT_SIGN, this::zoomOut));
    controls.getStyleClass().add("zoom-controls");
    controls.setPickOnBounds(false);
    controls.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
    return controls;
  }

  private static GameButton styledZoomButton(String label, Runnable action) {
    GameButton button = new GameButton(label);
    button.getStyleClass().add("zoom-button");
    button.setMaxWidth(BUTTON_SIZE);
    button.setMinSize(BUTTON_SIZE, BUTTON_SIZE);
    button.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
    button.setFocusTraversable(false);
    button.setOnAction(event -> action.run());
    return button;
  }
}
