package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Overlay in-scena a fine battaglia (vittoria/sconfitta), senza {@link javafx.stage.Stage} extra.
 */
public final class BattleEndOverlay {

  // Costanti di layout per la card di fine battaglia.
  private static final Insets CARD_MARGIN = new Insets(24, 32, 56, 32);
  private static final Insets CARD_PADDING = new Insets(36, 44, 40, 44);
  private static final int CARD_VBOX_SPACING = 22;
  private static final int CARD_MAX_WIDTH = 580;
  private static final int TITLE_MAX_WIDTH = 500;
  private static final int BODY_MAX_WIDTH = 480;

  private BattleEndOverlay() {}

  public static StackPane create(String title, String message, Runnable onOk) {
    StackPane layer = new StackPane();
    layer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    layer.getStyleClass().add("battle-end-overlay");
    VBox card = centeredCard(title, message, onOk);
    layer.getChildren().addAll(backdrop(), card);
    StackPane.setAlignment(card, Pos.CENTER);
    StackPane.setMargin(card, CARD_MARGIN);
    return layer;
  }

  private static Region backdrop() {
    Region backdrop = new Region();
    backdrop.getStyleClass().add("battle-end-backdrop");
    backdrop.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    return backdrop;
  }

  private static VBox centeredCard(String title, String message, Runnable onOk) {
    Label titleLabel = titleLabel(title);
    Label bodyLabel = bodyLabel(message);
    GameButton ok = okButton(onOk);
    VBox card = new VBox(CARD_VBOX_SPACING, titleLabel, bodyLabel, ok);
    card.setAlignment(Pos.CENTER);
    card.setPadding(CARD_PADDING);
    card.setMaxWidth(CARD_MAX_WIDTH);
    card.getStyleClass().addAll("game-panel", "battle-end-card");
    return card;
  }

  private static Label titleLabel(String title) {
    Label titleLabel = new Label(title);
    titleLabel.setWrapText(true);
    titleLabel.setMaxWidth(TITLE_MAX_WIDTH);
    titleLabel.getStyleClass().addAll("game-title", "battle-end-title");
    return titleLabel;
  }

  private static Label bodyLabel(String message) {
    Label bodyLabel = new Label(message);
    bodyLabel.setWrapText(true);
    bodyLabel.setMaxWidth(BODY_MAX_WIDTH);
    bodyLabel.getStyleClass().addAll("game-label", "battle-end-body");
    return bodyLabel;
  }

  private static GameButton okButton(Runnable onOk) {
    GameButton ok = new GameButton(Messages.get("battle.dialog.ok"));
    ok.setDefaultButton(true);
    ok.setOnAction(e -> onOk.run());
    return ok;
  }
}
