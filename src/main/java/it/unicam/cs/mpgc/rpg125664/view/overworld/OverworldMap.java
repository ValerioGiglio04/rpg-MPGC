package it.unicam.cs.mpgc.rpg125664.view.overworld;

import it.unicam.cs.mpgc.rpg125664.controller.OverworldPresenter;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.overworld.GymCellPlacement;
import it.unicam.cs.mpgc.rpg125664.model.overworld.GymStatus;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

/** Vista mappa overworld navigabile (griglia palestre, zoom, modale gym). */
public final class OverworldMap extends StackPane implements OverworldGymModalController.Host {

  private static final Insets LABEL_BOTTOM_MARGIN = new Insets(0, 0, 12, 0);
  private static final int MAP_GRID_GAP = 2;

  private static final OverworldTileRenderer TILE_RENDERER =
      new OverworldTileRenderer(
          OverworldMapConstants.TILE_SIZE,
          OverworldTextures.GYM_BUILDING,
          OverworldTextures.TREE,
          OverworldTextures.BUSH);

  private final OverworldPresenter presenter;
  private final PortraitAssetResolver portraitAssets;
  private final OverworldZoomControls zoomControls;
  private final OverworldGymModalController gymModal;
  private final GridPane mapGrid;
  private final Map<String, GymRoom> gymsByCell;
  private final Map<String, OverworldDecor> decorByCell;
  private final boolean[][] blockedTiles;
  private final Scale scaleTransform;
  private ScrollPane mapScrollPane;
  private Label legendLabel;
  private StackPane modalLayer;
  private int playerRow;
  private int playerCol;
  private int lastRow;
  private int lastCol;

  public OverworldMap(
      OverworldPresenter presenter, PortraitAssetResolver portraitAssets, Runnable onStartBattle) {
    this.presenter = presenter;
    this.portraitAssets = portraitAssets;
    this.mapGrid = new GridPane();
    this.gymsByCell = new HashMap<>();
    this.decorByCell = new HashMap<>();
    this.blockedTiles = OverworldMapConstants.createBlockedTiles();
    this.scaleTransform =
        new Scale(OverworldMapConstants.DEFAULT_ZOOM, OverworldMapConstants.DEFAULT_ZOOM, 0, 0);
    this.zoomControls = new OverworldZoomControls(scaleTransform);
    wireRootChrome();
    wireMapAndScroll();
    Label modalTitle = new Label();
    modalTitle.setFocusTraversable(false);
    HBox modalActions = new HBox(12);
    this.modalLayer = OverworldModalShell.buildLayer(modalTitle, modalActions);
    modalLayer.setFocusTraversable(false);
    modalLayer.setMouseTransparent(true);
    this.gymModal =
        new OverworldGymModalController(
            presenter, onStartBattle, this, modalTitle, modalActions, modalLayer);
    getChildren().addAll(mapScrollPane, zoomControls.root(), legendLabel, modalLayer);
    ensureUiChromeVisible();
    completeInitialPopulation();
  }

  private void wireRootChrome() {
    getStyleClass().add("overworld-map");
    setFocusTraversable(true);
    addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
    setOnMouseClicked(event -> requestFocus());
    setOnScroll(event -> zoomControls.onScroll(event, gymModal.isModalOpen()));
  }

  private void wireMapAndScroll() {
    configureMapGrid();
    this.mapScrollPane = createMapScrollPane();
    zoomControls.mountOn(this);
    this.legendLabel = createLegendLabel();
  }

  private void completeInitialPopulation() {
    gymsByCell.putAll(OverworldLayoutSupport.assignGymsDeterministic(presenter.gameState().gyms()));
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

  public void zoomIn() {
    zoomControls.zoomIn();
  }

  public void zoomOut() {
    zoomControls.zoomOut();
  }

  public void setZoom(double zoom) {
    zoomControls.setZoom(zoom);
  }

  private void handleKeyPressed(KeyEvent event) {
    KeyCode code = event.getCode();
    if (gymModal.isModalOpen()) {
      gymModal.handleModalKey(code);
      if (code == KeyCode.ENTER || code == KeyCode.ESCAPE) {
        event.consume();
      } else if (OverworldMovement.stepFor(code) != MapOffset.ZERO) {
        event.consume();
      }
      return;
    }
    if (OverworldMovement.stepFor(code) != MapOffset.ZERO) {
      handleMovementKey(code);
      event.consume();
    }
  }

  private void handleMovementKey(KeyCode code) {
    MapOffset step = OverworldMovement.stepFor(code);
    int nextRow = playerRow + step.rowDelta();
    int nextCol = playerCol + step.columnDelta();
    if (!OverworldMovement.isWalkable(nextRow, nextCol, blockedTiles)) {
      return;
    }
    commitMove(nextRow, nextCol);
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
    presenter.syncPosition(playerRow, playerCol);
  }

  private void maybeEnterGym() {
    GymRoom gym = gymsByCell.get(GymCellPlacement.cellKey(playerRow, playerCol));
    if (gym == null) return;
    GymStatus status = presenter.statusOf(gym);
    if (status == GymStatus.AVAILABLE) {
      gymModal.showChallengeModal(gym);
      return;
    }
    gymModal.showBlockedModal(presenter.blockedReason(gym, status));
  }

  @Override
  public int lastRow() {
    return lastRow;
  }

  @Override
  public int lastCol() {
    return lastCol;
  }

  @Override
  public void restorePlayerPosition(int row, int column) {
    playerRow = row;
    playerCol = column;
    syncOverworldPositionToSession();
  }

  @Override
  public void redrawMap() {
    mapGrid.getChildren().clear();
    var player = presenter.gameState().player();
    String playerSkinPath = portraitAssets.playerSkinPath();
    for (int row = 0; row < OverworldMapConstants.MAP_ROWS; row++) {
      for (int col = 0; col < OverworldMapConstants.MAP_COLS; col++) {
        addTileForCell(row, col, player.name(), playerSkinPath);
      }
    }
    ensureUiChromeVisible();
    if (!gymModal.isModalOpen()) {
      Platform.runLater(this::requestFocus);
    }
  }

  private void addTileForCell(int row, int col, String playerName, String skinPath) {
    StackPane tile =
        TILE_RENDERER
            .tile(row, col)
            .gridState(gymsByCell, decorByCell, blockedTiles)
            .playerOverlay(playerRow, playerCol, playerName, skinPath)
            .statusOf(presenter::statusOf)
            .build();
    mapGrid.add(tile, col, row);
  }

  @Override
  public void ensureUiChromeVisible() {
    OverworldMapChrome.ensureVisible(
        mapScrollPane, zoomControls.root(), legendLabel, modalLayer, gymModal.isModalOpen());
  }

  @Override
  public void requestMapFocus() {
    requestFocus();
  }

  private void initializePlayerPosition() {
    OverworldPlayerSpawn.Result spawn =
        OverworldPlayerSpawn.resolve(presenter, gymsByCell, blockedTiles);
    playerRow = spawn.row();
    playerCol = spawn.column();
    lastRow = spawn.lastRow();
    lastCol = spawn.lastColumn();
    if (spawn.syncSession()) {
      syncOverworldPositionToSession();
    }
  }
}
