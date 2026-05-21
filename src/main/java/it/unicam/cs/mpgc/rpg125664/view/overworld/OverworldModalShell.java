package it.unicam.cs.mpgc.rpg125664.view.overworld;

import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/** Costruisce lo stack overlay modale (backdrop + pannello) usato sulla mappa overworld. */
public final class OverworldModalShell {

  // Costanti di layout per il pannello modale dell'overworld.
  private static final int PANEL_SPACING = 16;
  private static final int PANEL_MAX_WIDTH = 420;

  private OverworldModalShell() {}

  public static StackPane buildLayer(Label titleLabel, HBox actionsBox) {
    Region backdrop = modalBackdrop();
    styleModalTitle(titleLabel);
    styleModalActions(actionsBox);
    VBox panel = modalPanel(titleLabel, actionsBox);
    StackPane layer = new StackPane(backdrop, panel);
    layer.setAlignment(Pos.CENTER);
    layer.setVisible(false);
    return layer;
  }

  private static Region modalBackdrop() {
    Region backdrop = new Region();
    backdrop.getStyleClass().add("overworld-modal-backdrop");
    backdrop.setOnMouseClicked(Event::consume);
    return backdrop;
  }

  private static void styleModalTitle(Label titleLabel) {
    titleLabel.getStyleClass().add("modal-title");
    titleLabel.setWrapText(true);
    titleLabel.setTextAlignment(TextAlignment.CENTER);
    titleLabel.setAlignment(Pos.CENTER);
    titleLabel.setMaxWidth(Double.MAX_VALUE);
  }

  private static void styleModalActions(HBox actionsBox) {
    actionsBox.setAlignment(Pos.CENTER);
    actionsBox.getStyleClass().add("modal-actions");
  }

  private static VBox modalPanel(Label titleLabel, HBox actionsBox) {
    VBox panel = new VBox(PANEL_SPACING, titleLabel, actionsBox);
    panel.setAlignment(Pos.CENTER);
    panel.getStyleClass().add("overworld-modal");
    panel.setMaxWidth(PANEL_MAX_WIDTH);
    panel.setMaxHeight(Region.USE_PREF_SIZE);
    return panel;
  }
}
