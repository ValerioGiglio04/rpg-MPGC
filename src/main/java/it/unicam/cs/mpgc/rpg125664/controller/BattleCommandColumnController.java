package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import it.unicam.cs.mpgc.rpg125664.view.component.CreatureCard;
import it.unicam.cs.mpgc.rpg125664.view.component.GameButton;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.IntStream;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/** Popola {@code BattleCommandColumn.fxml} con mosse, switch e azione hub. */
public final class BattleCommandColumnController {

  private static final int TEAM_PORTRAIT_SIZE = 72;

  @FXML private GridPane moveGrid;

  @FXML private VBox teamSwitches;

  @FXML private GameButton backButton;

  public void wire(
      Creature playerCreature,
      GymRoom gym,
      CreatureHolder holder,
      Runnable onBack,
      IntConsumer onMoveSelected,
      LongConsumer onSwitchCreature) {
    backButton.setOnAction(event -> onBack.run());
    populateMoveGrid(playerCreature, gym, onMoveSelected);
    populateTeamSwitches(holder, gym, onSwitchCreature);
  }

  private void populateMoveGrid(Creature playerCreature, GymRoom gym, IntConsumer onMoveSelected) {
    moveGrid.getChildren().clear();
    IntStream.range(0, playerCreature.moves().size())
        .forEach(index -> addMoveCell(moveGrid, playerCreature, gym, index, onMoveSelected));
  }

  private static void addMoveCell(
      GridPane moveGrid,
      Creature playerCreature,
      GymRoom gym,
      int index,
      IntConsumer onMoveSelected) {
    Move move = playerCreature.moves().get(index);
    GameButton moveButton = new GameButton(move.name());
    moveButton.getStyleClass().add("move-tile");
    moveButton.setTooltip(new Tooltip(moveTooltipText(move)));
    moveButton.setDisable(gym.completed() || playerCreature.isKnockedOut());
    int moveIndex = index;
    moveButton.setOnAction(event -> onMoveSelected.accept(moveIndex));
    moveGrid.add(moveButton, index % 2, index / 2);
  }

  private static String moveTooltipText(Move move) {
    String stats =
        Messages.format("battle.move.tooltip.stats", move.name(), move.power(), move.accuracy());
    return stats + System.lineSeparator() + move.description();
  }

  private void populateTeamSwitches(
      CreatureHolder playerHolder, GymRoom gym, LongConsumer onSwitchCreature) {
    teamSwitches.getChildren().clear();
    IntStream.range(0, playerHolder.creatures().size())
        .forEach(index -> appendTeamRow(teamSwitches, playerHolder, gym, index, onSwitchCreature));
  }

  private static void appendTeamRow(
      VBox switches,
      CreatureHolder playerHolder,
      GymRoom gym,
      int index,
      LongConsumer onSwitchCreature) {
    Creature creature = playerHolder.creatures().get(index);
    long catalogId = creature.catalogId();
    GameButton switchButton =
        new GameButton(Messages.format("battle.button.send", creature.name())).asSecondary();
    switchButton.getStyleClass().add("switch-button");
    switchButton.setDisable(
        playerHolder.isActive(creature) || !playerHolder.canSwitchTo(catalogId));
    switchButton.setOnAction(event -> onSwitchCreature.accept(catalogId));
    CreatureCard creatureCard =
        CreatureCard.builder(creature)
            .active(playerHolder.isActive(creature))
            .portraitSize(TEAM_PORTRAIT_SIZE)
            .sideStyleClass("player-creature-card")
            .build();
    switches.getChildren().add(creatureCard);
    switches.getChildren().add(switchButton);
  }
}
