package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.IntStream;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/** Griglia mosse, lista switch del team e bottone hub per la colonna comandi della battaglia. */
public final class BattleCommandColumnView {

  // Costanti di layout per la colonna comandi della battaglia.
  private static final int MOVES_BOX_SPACING = 8;
  private static final int COMMAND_COLUMN_SPACING = 12;
  private static final int TEAM_SWITCH_SPACING = 10;
  private static final int MOVE_GRID_GAP = 10;
  private static final int TEAM_PORTRAIT_SIZE = 72;
  private static final int TEAM_SCROLL_PREF_HEIGHT = 160;
  private static final int TEAM_SCROLL_MAX_HEIGHT = 180;

  private BattleCommandColumnView() {}

  public static VBox create(
      Creature playerCreature,
      GymRoom gym,
      CreatureHolder holder,
      Runnable onBack,
      IntConsumer onMoveSelected,
      LongConsumer onSwitchCreature) {
    GameButton backButton = hubButton(onBack);
    GridPane moveGrid = buildMoveGrid(playerCreature, gym, onMoveSelected);
    VBox movesBox = new VBox(MOVES_BOX_SPACING, moveGrid);
    GamePanel movesPanel =
        new GamePanel(Messages.get("battle.panel.moves"), movesBox).withStyle("player-panel");
    VBox switches = buildTeamSwitchPanel(holder, gym, onSwitchCreature);
    GamePanel teamPanel =
        new GamePanel(Messages.get("battle.panel.team"), wrapInScroll(switches))
            .withStyle("player-panel");
    VBox column = new VBox(COMMAND_COLUMN_SPACING, movesPanel, teamPanel, backButton);
    column.setFillWidth(true);
    return column;
  }

  private static GameButton hubButton(Runnable onBack) {
    GameButton backButton = new GameButton(Messages.get("battle.button.hub"));
    backButton.getStyleClass().add("secondary-button");
    backButton.setOnAction(event -> onBack.run());
    return backButton;
  }

  private static GridPane buildMoveGrid(
      Creature playerCreature, GymRoom gym, IntConsumer onMoveSelected) {
    GridPane moveGrid = new GridPane();
    moveGrid.setHgap(MOVE_GRID_GAP);
    moveGrid.setVgap(MOVE_GRID_GAP);
    moveGrid.getStyleClass().add("action-list");
    IntStream.range(0, playerCreature.moves().size())
        .forEach(index -> addMoveCell(moveGrid, playerCreature, gym, index, onMoveSelected));
    return moveGrid;
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

  private static VBox buildTeamSwitchPanel(
      CreatureHolder playerHolder, GymRoom gym, LongConsumer onSwitchCreature) {
    VBox switches = new VBox(TEAM_SWITCH_SPACING);
    IntStream.range(0, playerHolder.creatures().size())
        .forEach(index -> appendTeamRow(switches, playerHolder, gym, index, onSwitchCreature));
    switches.getStyleClass().add("action-list");
    return switches;
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

  private static ScrollPane wrapInScroll(VBox content) {
    ScrollPane scroll = new ScrollPane(content);
    scroll.getStyleClass().add("game-scroll");
    scroll.setFitToWidth(true);
    scroll.setPrefViewportHeight(TEAM_SCROLL_PREF_HEIGHT);
    scroll.setMaxHeight(TEAM_SCROLL_MAX_HEIGHT);
    return scroll;
  }
}
