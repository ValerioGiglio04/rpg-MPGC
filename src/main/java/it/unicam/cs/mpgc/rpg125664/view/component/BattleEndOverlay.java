package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.controller.BattleEndOverlayController;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.support.FxmlPaths;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.support.FxmlScreenLoader;
import javafx.scene.layout.StackPane;

/** Overlay in-scena a fine battaglia (layout in {@code BattleEndOverlay.fxml}). */
public final class BattleEndOverlay {

  private BattleEndOverlay() {}

  public static StackPane create(String title, String message, Runnable onOk) {
    BattleEndOverlayController controller = new BattleEndOverlayController();
    StackPane layer = (StackPane) FxmlScreenLoader.load(FxmlPaths.BATTLE_END_OVERLAY, controller);
    controller.bind(title, message, onOk);
    return layer;
  }
}
