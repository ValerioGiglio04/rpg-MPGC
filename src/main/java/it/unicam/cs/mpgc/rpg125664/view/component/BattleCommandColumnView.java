package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.controller.BattleCommandBindings;
import it.unicam.cs.mpgc.rpg125664.controller.BattleCommandColumnController;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.support.FxmlPaths;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.support.FxmlScreenLoader;
import javafx.scene.layout.VBox;

/** Colonna comandi duello (layout in {@code BattleCommandColumn.fxml}). */
public final class BattleCommandColumnView {

  private BattleCommandColumnView() {}

  public static VBox create(BattleCommandBindings bindings) {
    BattleCommandColumnController controller = new BattleCommandColumnController();
    VBox column = (VBox) FxmlScreenLoader.load(FxmlPaths.BATTLE_COMMAND_COLUMN, controller);
    controller.wire(bindings);
    return column;
  }
}
