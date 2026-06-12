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

  private record GridLayer(
      Map<String, GymRoom> gymsByCell,
      Map<String, OverworldDecor> decorByCell,
      boolean[][] blockedTiles) {}

  /** Posizione e aspetto del giocatore sovrapposto al tile corrente. */
  public static final class PlayerMarker {

    private final int playerRow;
    private final int playerCol;
    private final String name;
    private final String skinPath;

    private PlayerMarker(Builder builder) {
      this.playerRow = builder.playerRow;
      this.playerCol = builder.playerCol;
      this.name = Objects.requireNonNull(builder.name, "name");
      this.skinPath = Objects.requireNonNull(builder.skinPath, "skinPath");
    }

    public static Builder builder() {
      return new Builder();
    }

    public int playerRow() {
      return playerRow;
    }

    public int playerCol() {
      return playerCol;
    }

    public String name() {
      return name;
    }

    public String skinPath() {
      return skinPath;
    }

    public static final class Builder {

      private int playerRow;
      private int playerCol;
      private String name;
      private String skinPath;

      public Builder playerRow(int playerRow) {
        this.playerRow = playerRow;
        return this;
      }

      public Builder playerCol(int playerCol) {
        this.playerCol = playerCol;
        return this;
      }

      public Builder name(String name) {
        this.name = name;
        return this;
      }

      public Builder skinPath(String skinPath) {
        this.skinPath = skinPath;
        return this;
      }

      public PlayerMarker build() {
        return new PlayerMarker(this);
      }
    }
  }

  private record GroundStyle(
      StackPane tile,
      boolean blocked,
      GymRoom gym,
      String cellKey,
      Map<String, OverworldDecor> decorByCell) {}

  private record PlayerOnTile(StackPane tile, int row, int col, PlayerMarker player) {}

  private record DecorPlacement(
      StackPane tile, OverworldDecor decor, GymRoom gym, boolean blocked) {}

  private record TileAssembly(
      int row,
      int col,
      GridLayer grid,
      PlayerMarker player,
      Function<GymRoom, GymStatus> statusOf) {}

  private final TileRenderAssets assets;

  public OverworldTileRenderer(TileRenderAssets assets) {
    this.assets = Objects.requireNonNull(assets, "assets");
  }

  /**
   * Builder fluente per una singola cella della griglia. Uso tipico:
   *
   * <pre>{@code
   * renderer.tile(row, col).gridState(gymsByCell, decorByCell, blockedTiles)
   *     .playerOverlay(PlayerMarker.builder()...build()).statusOf(session::statusOf).build();
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
    private GridLayer grid;
    private PlayerMarker player;
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
      this.grid = new GridLayer(gymsByCell, decorByCell, blockedTiles);
      return this;
    }

    public TileBuilder playerOverlay(PlayerMarker player) {
      this.player = player;
      return this;
    }

    public TileBuilder statusOf(Function<GymRoom, GymStatus> statusOf) {
      this.statusOf = statusOf;
      return this;
    }

    public StackPane build() {
      Objects.requireNonNull(grid, "grid");
      Objects.requireNonNull(player, "player");
      Objects.requireNonNull(statusOf, "statusOf");
      return renderer.buildTile(new TileAssembly(row, col, grid, player, statusOf));
    }
  }

  private StackPane buildTile(TileAssembly assembly) {
    String key = GymCellPlacement.cellKey(assembly.row(), assembly.col());
    GymRoom gym = assembly.grid().gymsByCell().get(key);
    StackPane tile = newBaseTile();
    applyGroundStyle(
        new GroundStyle(
            tile,
            assembly.grid().blockedTiles()[assembly.row()][assembly.col()],
            gym,
            key,
            assembly.grid().decorByCell()));
    if (gym != null) {
      appendGymLayer(tile, gym, assembly.statusOf());
    }
    maybeAppendDecor(
        new DecorPlacement(
            tile,
            assembly.grid().decorByCell().get(key),
            gym,
            assembly.grid().blockedTiles()[assembly.row()][assembly.col()]));
    maybeAppendPlayer(new PlayerOnTile(tile, assembly.row(), assembly.col(), assembly.player()));
    return tile;
  }

  private StackPane newBaseTile() {
    StackPane tile = new StackPane();
    tile.setFocusTraversable(false);
    tile.setMinSize(assets.tileSize(), assets.tileSize());
    tile.setPrefSize(assets.tileSize(), assets.tileSize());
    tile.getStyleClass().add("overworld-tile");
    return tile;
  }

  private void applyGroundStyle(GroundStyle style) {
    if (style.blocked()) {
      style.tile().getStyleClass().add("tile-wall");
      return;
    }
    if (style.gym() != null) {
      return;
    }
    String groundClass =
        style.decorByCell().containsKey(style.cellKey()) ? "tile-ground-decor" : "tile-ground";
    style.tile().getStyleClass().add(groundClass);
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
    ImageView gymView = new ImageView(assets.gymBuildingImage());
    gymView.setPreserveRatio(true);
    gymView.setSmooth(true);
    gymView.setMouseTransparent(true);
    gymView.getStyleClass().add("tile-gym-building");
    gymView.setFitWidth(assets.tileSize() - GYM_BUILDING_WIDTH_OFFSET);
    gymView.setFitHeight(assets.tileSize() - GYM_BUILDING_HEIGHT_OFFSET);
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

  private void maybeAppendDecor(DecorPlacement placement) {
    if (placement.decor() == null || placement.gym() != null || placement.blocked()) {
      return;
    }
    placement.tile().getChildren().add(buildDecorView(placement.decor()));
  }

  private void maybeAppendPlayer(PlayerOnTile overlay) {
    if (overlay.row() != overlay.player().playerRow()
        || overlay.col() != overlay.player().playerCol()) {
      return;
    }
    overlay.tile().getStyleClass().add("tile-player");
    overlay
        .tile()
        .getChildren()
        .add(
            PlayerPortrait.builder()
                .playerName(overlay.player().name())
                .skinPath(overlay.player().skinPath())
                .size(PLAYER_SPRITE_SIZE)
                .build());
  }

  private ImageView buildDecorView(OverworldDecor decor) {
    var image = decor == OverworldDecor.TREE ? assets.treeImage() : assets.bushImage();
    ImageView view = new ImageView(image);
    view.setFitWidth(assets.tileSize());
    view.setFitHeight(assets.tileSize());
    view.setPreserveRatio(true);
    view.setSmooth(true);
    view.setMouseTransparent(true);
    view.getStyleClass().add(decor == OverworldDecor.TREE ? "tile-decor-tree" : "tile-decor-bush");
    return view;
  }
}
