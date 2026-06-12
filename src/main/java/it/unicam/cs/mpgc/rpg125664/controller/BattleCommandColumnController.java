package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import it.unicam.cs.mpgc.rpg125664.view.component.CreatureCard;
import it.unicam.cs.mpgc.rpg125664.view.component.GameButton;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
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

  private record MoveCellRequest(
      GridPane moveGrid,
      Creature playerCreature,
      GymRoom gym,
      int index,
      IntConsumer onMoveSelected) {}

  private record TeamSwitchRowRequest(
      VBox switches,
      CreatureHolder playerHolder,
      GymRoom gym,
      PortraitAssetResolver portraitAssets,
      int index,
      LongConsumer onSwitchCreature) {}

  @FXML private GridPane moveGrid;

  @FXML private VBox teamSwitches;

  @FXML private GameButton backButton;

  public void wire(BattleCommandBindings bindings) {
    backButton.setOnAction(event -> bindings.onBack().run());
    populateMoveGrid(bindings.playerCreature(), bindings.gym(), bindings.onMoveSelected());
    populateTeamSwitches(
        bindings.holder(), bindings.gym(), bindings.portraitAssets(), bindings.onSwitchCreature());
  }

  private void populateMoveGrid(Creature playerCreature, GymRoom gym, IntConsumer onMoveSelected) {
    moveGrid.getChildren().clear();
    IntStream.range(0, playerCreature.moves().size())
        .forEach(
            index ->
                addMoveCell(
                    new MoveCellRequest(moveGrid, playerCreature, gym, index, onMoveSelected)));
  }

  private static void addMoveCell(MoveCellRequest request) {
    Move move = request.playerCreature().moves().get(request.index());
    GameButton moveButton = new GameButton(move.name());
    moveButton.getStyleClass().add("move-tile");
    moveButton.setTooltip(new Tooltip(moveTooltipText(move)));
    moveButton.setDisable(request.gym().completed() || request.playerCreature().isKnockedOut());
    int moveIndex = request.index();
    moveButton.setOnAction(event -> request.onMoveSelected().accept(moveIndex));
    request.moveGrid().add(moveButton, request.index() % 2, request.index() / 2);
  }

  private static String moveTooltipText(Move move) {
    String stats =
        Messages.format("battle.move.tooltip.stats", move.name(), move.power(), move.accuracy());
    return stats + System.lineSeparator() + move.description();
  }

  private void populateTeamSwitches(
      CreatureHolder playerHolder,
      GymRoom gym,
      PortraitAssetResolver portraitAssets,
      LongConsumer onSwitchCreature) {
    teamSwitches.getChildren().clear();
    IntStream.range(0, playerHolder.creatures().size())
        .forEach(
            index ->
                appendTeamRow(
                    new TeamSwitchRowRequest(
                        teamSwitches, playerHolder, gym, portraitAssets, index, onSwitchCreature)));
  }

  private static void appendTeamRow(TeamSwitchRowRequest request) {
    Creature creature = request.playerHolder().creatures().get(request.index());
    long catalogId = creature.catalogId();
    GameButton switchButton =
        new GameButton(Messages.format("battle.button.send", creature.name())).asSecondary();
    switchButton.getStyleClass().add("switch-button");
    switchButton.setDisable(
        request.playerHolder().isActive(creature)
            || !request.playerHolder().canSwitchTo(catalogId));
    switchButton.setOnAction(event -> request.onSwitchCreature().accept(catalogId));
    CreatureCard creatureCard =
        CreatureCard.builder(creature, request.portraitAssets())
            .active(request.playerHolder().isActive(creature))
            .portraitSize(TEAM_PORTRAIT_SIZE)
            .sideStyleClass("player-creature-card")
            .build();
    request.switches().getChildren().add(creatureCard);
    request.switches().getChildren().add(switchButton);
  }
}
