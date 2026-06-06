package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.controller.BattleCommandColumnController;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.FxmlScreenLoader;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import javafx.scene.layout.VBox;

/** Colonna comandi duello (layout in {@code BattleCommandColumn.fxml}). */
public final class BattleCommandColumnView {

  private BattleCommandColumnView() {}

  public static VBox create(
      Creature playerCreature,
      GymRoom gym,
      CreatureHolder holder,
      Runnable onBack,
      IntConsumer onMoveSelected,
      LongConsumer onSwitchCreature) {
    BattleCommandColumnController controller = new BattleCommandColumnController();
    VBox column = (VBox) FxmlScreenLoader.load("/fxml/BattleCommandColumn.fxml", controller);
    controller.wire(playerCreature, gym, holder, onBack, onMoveSelected, onSwitchCreature);
    return column;
  }
}
