package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.ui.javafx.FxmlScreens;
import it.unicam.cs.mpgc.rpg125664.controller.BattleEndOverlayController;
import javafx.scene.layout.StackPane;

/** Overlay in-scena a fine battaglia (layout in {@code BattleEndOverlay.fxml}). */
public final class BattleEndOverlay {

  private BattleEndOverlay() {}

  public static StackPane create(String title, String message, Runnable onOk) {
    BattleEndOverlayController controller = new BattleEndOverlayController();
    StackPane layer = (StackPane) FxmlScreens.load("/fxml/BattleEndOverlay.fxml", controller);
    controller.bind(title, message, onOk);
    return layer;
  }
}
