package it.unicam.cs.mpgc.rpg125664.view.overworld;

import it.unicam.cs.mpgc.rpg125664.model.service.GymStatus;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;
import it.unicam.cs.mpgc.rpg125664.view.component.GameButton;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

public final class OverworldMap extends StackPane {

  // Costanti di layout per la chrome della mappa overworld.
  private static final Insets ZOOM_CONTROLS_MARGIN = new Insets(12);
  private static final Insets LABEL_BOTTOM_MARGIN = new Insets(0, 0, 12, 0);
  private static final int MODAL_ACTIONS_SPACING = 12;
  private static final int MAP_GRID_GAP = 2;
  private static final int ZOOM_BUTTONS_SPACING = 6;
  private static final int ZOOM_BUTTON_SIZE = 36;
  private static final int ACTION_BUTTON_PREF_WIDTH = 140;

  private static final OverworldTileRenderer TILE_RENDERER =
      new OverworldTileRenderer(
          OverworldMapConstants.TILE_SIZE,
          OverworldTextures.GYM_BUILDING,
          OverworldTextures.TREE,
          OverworldTextures.BUSH);

  private final GameModel gameModel;
  private final Runnable onStartBattle;
  private final GridPane mapGrid;
  private final Map<String, GymRoom> gymsByCell;
  private final Map<String, OverworldDecor> decorByCell;
  private final boolean[][] blockedTiles;
  private final Scale scaleTransform;
  private ScrollPane mapScrollPane;
  private VBox zoomControls;
  private Label legendLabel;
  private StackPane modalLayer;
  private Label modalTitle;
  private HBox modalActions;
  private int playerRow;
  private int playerCol;
  private int lastRow;
  private int lastCol;
  private double currentZoom = OverworldMapConstants.DEFAULT_ZOOM;
  private boolean modalOpen;
  private GymRoom pendingGym;

  public OverworldMap(GameModel gameModel, Runnable onStartBattle) {
    this.gameModel = gameModel;
    this.onStartBattle = onStartBattle;
    this.mapGrid = new GridPane();
    this.gymsByCell = new HashMap<>();
    this.decorByCell = new HashMap<>();
    this.blockedTiles = OverworldMapConstants.createBlockedTiles();
    this.scaleTransform =
        new Scale(OverworldMapConstants.DEFAULT_ZOOM, OverworldMapConstants.DEFAULT_ZOOM, 0, 0);
    wireRootChrome();
    wireMapAndScroll();
    wireModalShell();
    completeInitialPopulation();
  }

  private void wireRootChrome() {
    getStyleClass().add("overworld-map");
    setFocusTraversable(true);
    // Capture phase: receive movement keys even when focus drifts into the ScrollPane or a tile
    // after redraw; bubbling handlers on this node would miss those events.
    addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
    setOnMouseClicked(event -> requestFocus());
    setOnScroll(this::handleScroll);
  }

  private void wireMapAndScroll() {
    configureMapGrid();
    this.mapScrollPane = createMapScrollPane();
    this.zoomControls = buildZoomControls();
    StackPane.setAlignment(zoomControls, Pos.TOP_RIGHT);
    StackPane.setMargin(zoomControls, ZOOM_CONTROLS_MARGIN);
    this.legendLabel = createLegendLabel();
  }

  private void wireModalShell() {
    this.modalTitle = new Label();
    modalTitle.setFocusTraversable(false);
    this.modalActions = new HBox(MODAL_ACTIONS_SPACING);
    this.modalLayer = OverworldModalShell.buildLayer(modalTitle, modalActions);
    modalLayer.setFocusTraversable(false);
    modalLayer.setMouseTransparent(true);
    getChildren().addAll(mapScrollPane, zoomControls, legendLabel, modalLayer);
    ensureUiChromeVisible();
  }

  private void completeInitialPopulation() {
    gymsByCell.putAll(
        OverworldLayoutSupport.assignGymsDeterministic(gameModel.gameState().gyms()));
    decorByCell.putAll(OverworldLayoutSupport.assignDecorDeterministic(gymsByCell, blockedTiles));
    initializePlayerPosition();
    redrawMap();
  }

  private void configureMapGrid() {
    mapGrid.getStyleClass().add("overworld-grid");
    mapGrid.setHgap(MAP_GRID_GAP);
    mapGrid.setVgap(MAP_GRID_GAP);
    mapGrid.setAlignment(Pos.CENTER);
    mapGrid.getTransforms().add(scaleTransform);
  }

  private ScrollPane createMapScrollPane() {
    mapGrid.setFocusTraversable(false);
    Group mapGroup = new Group(mapGrid);
    mapGroup.setFocusTraversable(false);
    StackPane viewportHolder = new StackPane(mapGroup);
    viewportHolder.setAlignment(Pos.CENTER);
    viewportHolder.setPickOnBounds(false);
    viewportHolder.setFocusTraversable(false);
    ScrollPane pane = new ScrollPane(viewportHolder);
    pane.getStyleClass().add("overworld-scroll");
    pane.setFitToWidth(true);
    pane.setFitToHeight(true);
    pane.setPannable(true);
    pane.setFocusTraversable(false);
    return pane;
  }

  private Label createLegendLabel() {
    Label label = new Label(OverworldMapConstants.legendText());
    label.setFocusTraversable(false);
    label.getStyleClass().add("muted-label");
    StackPane.setAlignment(label, Pos.BOTTOM_CENTER);
    StackPane.setMargin(label, LABEL_BOTTOM_MARGIN);
    return label;
  }

  private void handleScroll(ScrollEvent event) {
    if (modalOpen) return;
    if (event.getDeltaY() > 0) {
      zoomIn();
    } else if (event.getDeltaY() < 0) {
      zoomOut();
    }
    event.consume();
  }

  public void zoomIn() {
    setZoom(currentZoom + OverworldMapConstants.ZOOM_STEP);
  }

  public void zoomOut() {
    setZoom(currentZoom - OverworldMapConstants.ZOOM_STEP);
  }

  public void setZoom(double zoom) {
    currentZoom =
        Math.max(OverworldMapConstants.MIN_ZOOM, Math.min(OverworldMapConstants.MAX_ZOOM, zoom));
    scaleTransform.setX(currentZoom);
    scaleTransform.setY(currentZoom);
  }

  private VBox buildZoomControls() {
    VBox controls =
        new VBox(
            ZOOM_BUTTONS_SPACING,
            styledZoomButton("+", this::zoomIn),
            styledZoomButton("\u2212", this::zoomOut));
    controls.getStyleClass().add("zoom-controls");
    controls.setPickOnBounds(false);
    controls.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
    return controls;
  }

  private GameButton styledZoomButton(String label, Runnable action) {
    GameButton button = new GameButton(label);
    button.getStyleClass().add("zoom-button");
    button.setMaxWidth(ZOOM_BUTTON_SIZE);
    button.setMinSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
    button.setPrefSize(ZOOM_BUTTON_SIZE, ZOOM_BUTTON_SIZE);
    button.setFocusTraversable(false);
    button.setOnAction(event -> action.run());
    return button;
  }

  private GameButton modalButton(String text, Runnable handler) {
    GameButton button = new GameButton(text);
    button.setMaxWidth(Region.USE_PREF_SIZE);
    button.setPrefWidth(ACTION_BUTTON_PREF_WIDTH);
    button.setOnAction(event -> handler.run());
    return button;
  }

  private void handleEnterKey() {
    if (pendingGym != null) {
      confirmChallenge();
      return;
    }
    cancelChallenge();
  }

  private void handleKeyPressed(KeyEvent event) {
    KeyCode code = event.getCode();
    if (modalOpen) {
      handleModalKey(code);
      if (code == KeyCode.ENTER || code == KeyCode.ESCAPE) {
        event.consume();
      } else if (movementStep(code) != MapOffset.ZERO) {
        event.consume();
      }
      return;
    }
    if (movementStep(code) != MapOffset.ZERO) {
      handleMovementKey(code);
      event.consume();
    }
  }

  private void handleModalKey(KeyCode code) {
    switch (code) {
      case ENTER -> handleEnterKey();
      case ESCAPE -> cancelChallenge();
      default -> {}
    }
  }

  private void handleMovementKey(KeyCode code) {
    MapOffset step = movementStep(code);
    int nextRow = playerRow + step.rowDelta();
    int nextCol = playerCol + step.columnDelta();
    if (!isWalkable(nextRow, nextCol)) return;
    commitMove(nextRow, nextCol);
  }

  /**
   * Un passo di griglia per un tasto di movimento; {@link MapOffset#ZERO} se il tasto non muove.
   */
  private static MapOffset movementStep(KeyCode code) {
    return switch (code) {
      case W, UP -> MapOffset.UP;
      case S, DOWN -> MapOffset.DOWN;
      case A, LEFT -> MapOffset.LEFT;
      case D, RIGHT -> MapOffset.RIGHT;
      default -> MapOffset.ZERO;
    };
  }

  private static boolean isInRange(int value, int min, int max) {
    return value >= min && value <= max;
  }

  private static boolean isOutOfBounds(int row, int col) {
    return !isInRange(col, 0, OverworldMapConstants.MAP_COLS)
        || !isInRange(row, 0, OverworldMapConstants.MAP_ROWS);
  }

  private boolean isWalkable(int row, int col) {
    if (isOutOfBounds(row, col)) return false;
    return !blockedTiles[row][col];
  }

  private void commitMove(int nextRow, int nextCol) {
    lastRow = playerRow;
    lastCol = playerCol;
    playerRow = nextRow;
    playerCol = nextCol;
    syncOverworldPositionToSession();
    redrawMap();
    maybeEnterGym();
  }

  private void syncOverworldPositionToSession() {
    gameModel.setOverworldPosition(new MapCoordinate(playerRow, playerCol));
  }

  private void maybeEnterGym() {
    GymRoom gym = gymsByCell.get(GymCellAssignment.cellKey(playerRow, playerCol));
    if (gym == null) return;
    GymStatus status = gameModel.statusOf(gym);
    if (status == GymStatus.AVAILABLE) {
      showChallengeModal(gym);
      return;
    }
    showBlockedModal(blockedReason(gym, status));
  }

  private String blockedReason(GymRoom gym, GymStatus status) {
    int playerPoints = gameModel.gameState().player().score().points();
    return switch (status) {
      case COMPLETED -> reasonCompleted(gym);
      case UNREACHABLE -> reasonUnreachable(gym);
      case NEEDS_POINTS -> reasonNeedsPoints(gym, playerPoints);
      case CURRENT, AVAILABLE -> "";
    };
  }

  private static String reasonCompleted(GymRoom gym) {
    return Messages.format("overworld.blocked.completed", gym.name());
  }

  private static String reasonUnreachable(GymRoom gym) {
    return Messages.format("overworld.blocked.unreachable", gym.name());
  }

  private static String reasonNeedsPoints(GymRoom gym, int playerPoints) {
    return Messages.format(
        "overworld.blocked.needs.points", gym.name(), gym.requiredPoints(), playerPoints);
  }

  private void showChallengeModal(GymRoom gym) {
    pendingGym = gym;
    modalTitle.setText(challengePrompt(gym.name()));
    GameButton challengeButton =
        modalButton(Messages.get("overworld.modal.challenge"), this::confirmChallenge);
    GameButton cancelButton =
        modalButton(Messages.get("overworld.modal.cancel"), this::cancelChallenge).asSecondary();
    modalActions.getChildren().setAll(challengeButton, cancelButton.asSecondary());
    openModal();
  }

  private static String challengePrompt(String gymName) {
    return Messages.format("overworld.modal.challenge.prompt", gymName);
  }

  private void showBlockedModal(String reason) {
    pendingGym = null;
    modalTitle.setText(reason);
    GameButton closeButton =
        modalButton(Messages.get("overworld.modal.close"), this::cancelChallenge);
    modalActions.getChildren().setAll(closeButton.asSecondary());
    openModal();
  }

  private void openModal() {
    modalOpen = true;
    modalLayer.setMouseTransparent(false);
    modalLayer.setVisible(true);
    ensureUiChromeVisible();
  }

  private void confirmChallenge() {
    if (!modalOpen || pendingGym == null) return;
    GymRoom gym = pendingGym;
    hideModal();
    moveSessionToGymIfNeeded(gym);
    onStartBattle.run();
  }

  private void moveSessionToGymIfNeeded(GymRoom gym) {
    GymRoom currentGym = gameModel.gameState().currentGym();
    if (currentGym.id() == gym.id()) return;

    gameModel.moveTo(gym.id());
  }

  private void cancelChallenge() {
    if (!modalOpen) return;
    hideModal();
    playerRow = lastRow;
    playerCol = lastCol;
    syncOverworldPositionToSession();
    redrawMap();
  }

  private void hideModal() {
    modalOpen = false;
    pendingGym = null;
    modalLayer.setVisible(false);
    modalLayer.setMouseTransparent(true);
    ensureUiChromeVisible();
    requestFocus();
  }

  private void redrawMap() {
    mapGrid.getChildren().clear();
    var player = gameModel.gameState().player();
    for (int row = 0; row < OverworldMapConstants.MAP_ROWS; row++) {
      for (int col = 0; col < OverworldMapConstants.MAP_COLS; col++) {
        addTileForCell(row, col, player.name(), player.skinPath());
      }
    }
    ensureUiChromeVisible();
    if (!modalOpen) {
      Platform.runLater(this::requestFocus);
    }
  }

  private void addTileForCell(int row, int col, String playerName, String skinPath) {
    StackPane tile =
        TILE_RENDERER
            .tile(row, col)
            .gridState(gymsByCell, decorByCell, blockedTiles)
            .playerOverlay(playerRow, playerCol, playerName, skinPath)
            .statusOf(gameModel::statusOf)
            .build();
    mapGrid.add(tile, col, row);
  }

  private void ensureUiChromeVisible() {
    mapScrollPane.setManaged(true);
    mapScrollPane.setVisible(true);
    zoomControls.setManaged(true);
    zoomControls.setVisible(true);
    legendLabel.setManaged(true);
    legendLabel.setVisible(true);
    modalLayer.setManaged(true);
    modalLayer.setVisible(modalOpen);
    if (modalOpen) {
      modalLayer.toFront();
      return;
    }
    legendLabel.toFront();
    zoomControls.toFront();
  }

  private void initializePlayerPosition() {
    if (gameModel.overworldPosition().isPresent()) {
      MapCoordinate saved = gameModel.overworldPosition().orElseThrow();
      playerRow = saved.row();
      playerCol = saved.column();
      lastRow = playerRow;
      lastCol = playerCol;
      return;
    }
    Entry<String, GymRoom> entry = findCurrentGymEntry();
    if (entry != null) {
      placePlayerNearGym(entry.getKey());
    } else {
      setDefaultPlayerSpawn();
    }
    syncOverworldPositionToSession();
  }

  private Entry<String, GymRoom> findCurrentGymEntry() {
    return gymsByCell.entrySet().stream()
        .filter(e -> e.getValue().id() == gameModel.gameState().currentGym().id())
        .findFirst()
        .orElse(null);
  }

  private void placePlayerNearGym(String cellKey) {
    String[] coords = cellKey.split(":");
    int gymRow = Integer.parseInt(coords[0]);
    int gymCol = Integer.parseInt(coords[1]);
    MapCoordinate home =
        GymCellAssignment.findHomeTile(
            gymRow,
            gymCol,
            blockedTiles,
            gymsByCell,
            OverworldMapConstants.MAP_ROWS,
            OverworldMapConstants.MAP_COLS);
    playerRow = home.row();
    playerCol = home.column();
    lastRow = playerRow;
    lastCol = playerCol;
  }

  private void setDefaultPlayerSpawn() {
    playerRow = 4;
    playerCol = 1;
    lastRow = playerRow;
    lastCol = playerCol;
  }
}
