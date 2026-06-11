package it.unicam.cs.mpgc.rpg125664.view.overworld;

import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.overworld.GymCellPlacement;
import it.unicam.cs.mpgc.rpg125664.model.overworld.GymStatus;
import it.unicam.cs.mpgc.rpg125664.view.component.PlayerPortrait;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Costruisce una cella della griglia overworld: stile terreno, marker palestra, decor, sprite
 * player.
 */
public final class OverworldTileRenderer {

  // Costanti di layout per la cella overworld (tile + label palestra + giocatore).
  private static final Insets GYM_LABEL_MARGIN = new Insets(0, 2, 1, 2);
  private static final int GYM_BUILDING_WIDTH_OFFSET = 6;
  private static final int GYM_BUILDING_HEIGHT_OFFSET = 20;
  private static final int PLAYER_SPRITE_SIZE = 32;

  private final int tileSize;
  private final Image gymBuildingImage;
  private final Image treeImage;
  private final Image bushImage;

  public OverworldTileRenderer(
      int tileSize, Image gymBuildingImage, Image treeImage, Image bushImage) {
    this.tileSize = tileSize;
    this.gymBuildingImage = gymBuildingImage;
    this.treeImage = treeImage;
    this.bushImage = bushImage;
  }

  /**
   * Builder fluente per una singola cella della griglia. Uso tipico:
   *
   * <pre>{@code
   * renderer.tile(row, col).gridState(gymsByCell, decorByCell, blockedTiles)
   *     .playerOverlay(playerRow, playerCol, name, skinPath).statusOf(session::statusOf).build();
   * }</pre>
   */
  public TileBuilder tile(int row, int col) {
    return new TileBuilder(this, row, col);
  }

  /**
   * @see #tile(int, int)
   */
  public static final class TileBuilder {
    private final OverworldTileRenderer renderer;
    private final int row;
    private final int col;
    private Map<String, GymRoom> gymsByCell;
    private Map<String, OverworldDecor> decorByCell;
    private boolean[][] blockedTiles;
    private int playerRow;
    private int playerCol;
    private String playerName;
    private String playerSkinPath;
    private Function<GymRoom, GymStatus> statusOf;

    private TileBuilder(OverworldTileRenderer renderer, int row, int col) {
      this.renderer = renderer;
      this.row = row;
      this.col = col;
    }

    public TileBuilder gridState(
        Map<String, GymRoom> gymsByCell,
        Map<String, OverworldDecor> decorByCell,
        boolean[][] blockedTiles) {
      this.gymsByCell = gymsByCell;
      this.decorByCell = decorByCell;
      this.blockedTiles = blockedTiles;
      return this;
    }

    public TileBuilder playerOverlay(
        int playerRow, int playerCol, String playerName, String playerSkinPath) {
      this.playerRow = playerRow;
      this.playerCol = playerCol;
      this.playerName = playerName;
      this.playerSkinPath = playerSkinPath;
      return this;
    }

    public TileBuilder statusOf(Function<GymRoom, GymStatus> statusOf) {
      this.statusOf = statusOf;
      return this;
    }

    public StackPane build() {
      Objects.requireNonNull(gymsByCell, "gymsByCell");
      Objects.requireNonNull(decorByCell, "decorByCell");
      Objects.requireNonNull(blockedTiles, "blockedTiles");
      Objects.requireNonNull(playerName, "playerName");
      Objects.requireNonNull(playerSkinPath, "playerSkinPath");
      Objects.requireNonNull(statusOf, "statusOf");
      return renderer.assembleTile(
          row,
          col,
          gymsByCell,
          decorByCell,
          blockedTiles,
          playerRow,
          playerCol,
          playerName,
          playerSkinPath,
          statusOf);
    }
  }

  private StackPane assembleTile(
      int row,
      int col,
      Map<String, GymRoom> gymsByCell,
      Map<String, OverworldDecor> decorByCell,
      boolean[][] blockedTiles,
      int playerRow,
      int playerCol,
      String playerName,
      String playerSkinPath,
      Function<GymRoom, GymStatus> statusOf) {
    String key = GymCellPlacement.cellKey(row, col);
    GymRoom gym = gymsByCell.get(key);
    StackPane tile = newBaseTile();
    applyGroundStyle(tile, blockedTiles[row][col], gym, key, decorByCell);
    if (gym != null) {
      appendGymLayer(tile, gym, statusOf);
    }
    maybeAppendDecor(tile, decorByCell.get(key), gym, blockedTiles[row][col]);
    maybeAppendPlayer(tile, row, col, playerRow, playerCol, playerName, playerSkinPath);
    return tile;
  }

  private StackPane newBaseTile() {
    StackPane tile = new StackPane();
    tile.setFocusTraversable(false);
    tile.setMinSize(tileSize, tileSize);
    tile.setPrefSize(tileSize, tileSize);
    tile.getStyleClass().add("overworld-tile");
    return tile;
  }

  private void applyGroundStyle(
      StackPane tile,
      boolean blocked,
      GymRoom gym,
      String key,
      Map<String, OverworldDecor> decorByCell) {
    if (blocked) {
      tile.getStyleClass().add("tile-wall");
      return;
    }
    if (gym != null) return;
    String groundClass = decorByCell.containsKey(key) ? "tile-ground-decor" : "tile-ground";
    tile.getStyleClass().add(groundClass);
  }

  private void appendGymLayer(StackPane tile, GymRoom gym, Function<GymRoom, GymStatus> statusOf) {
    tile.getStyleClass().add("tile-gym");
    tile.getStyleClass().add(gymStatusStyle(statusOf.apply(gym)));
    ImageView gymView = styledGymImageView();
    Label gymLabel = styledGymLabel(gym.name());
    tile.getChildren().addAll(gymView, gymLabel);
  }

  private String gymStatusStyle(GymStatus status) {
    return switch (status) {
      case COMPLETED -> "tile-gym-completed";
      case CURRENT -> "tile-gym-current";
      case AVAILABLE -> "tile-gym-reachable";
      case NEEDS_POINTS, UNREACHABLE -> "tile-gym-locked";
    };
  }

  private ImageView styledGymImageView() {
    ImageView gymView = new ImageView(gymBuildingImage);
    gymView.setPreserveRatio(true);
    gymView.setSmooth(true);
    gymView.setMouseTransparent(true);
    gymView.getStyleClass().add("tile-gym-building");
    gymView.setFitWidth(tileSize - GYM_BUILDING_WIDTH_OFFSET);
    gymView.setFitHeight(tileSize - GYM_BUILDING_HEIGHT_OFFSET);
    StackPane.setAlignment(gymView, Pos.CENTER);
    return gymView;
  }

  private Label styledGymLabel(String name) {
    Label gymLabel = new Label(name);
    gymLabel.setFocusTraversable(false);
    gymLabel.getStyleClass().add("tile-gym-label");
    StackPane.setAlignment(gymLabel, Pos.BOTTOM_CENTER);
    StackPane.setMargin(gymLabel, GYM_LABEL_MARGIN);
    return gymLabel;
  }

  private void maybeAppendDecor(
      StackPane tile, OverworldDecor decor, GymRoom gym, boolean blocked) {
    if (decor == null || gym != null || blocked) return;
    tile.getChildren().add(buildDecorView(decor));
  }

  private void maybeAppendPlayer(
      StackPane tile,
      int row,
      int col,
      int playerRow,
      int playerCol,
      String playerName,
      String playerSkinPath) {
    if (row != playerRow || col != playerCol) return;
    tile.getStyleClass().add("tile-player");
    tile.getChildren()
        .add(
            PlayerPortrait.builder()
                .playerName(playerName)
                .skinPath(playerSkinPath)
                .size(PLAYER_SPRITE_SIZE)
                .build());
  }

  private ImageView buildDecorView(OverworldDecor decor) {
    Image image = decor == OverworldDecor.TREE ? treeImage : bushImage;
    ImageView view = new ImageView(image);
    view.setFitWidth(tileSize);
    view.setFitHeight(tileSize);
    view.setPreserveRatio(true);
    view.setSmooth(true);
    view.setMouseTransparent(true);
    view.getStyleClass().add(decor == OverworldDecor.TREE ? "tile-decor-tree" : "tile-decor-bush");
    return view;
  }
}
