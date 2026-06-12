package it.unicam.cs.mpgc.rpg125664.model.overworld;

import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import java.util.Map;
import java.util.Objects;

/** Contesto griglia overworld condiviso tra posizionamento palestre e spawn giocatore. */
public final class MapGridContext {

  private final boolean[][] blockedTiles;
  private final Map<String, GymRoom> gymsByCell;
  private final int mapRows;
  private final int mapCols;

  private MapGridContext(Builder builder) {
    this.blockedTiles = Objects.requireNonNull(builder.blockedTiles, "blockedTiles");
    this.gymsByCell = Objects.requireNonNull(builder.gymsByCell, "gymsByCell");
    this.mapRows = builder.mapRows;
    this.mapCols = builder.mapCols;
  }

  public static Builder builder() {
    return new Builder();
  }

  public boolean[][] blockedTiles() {
    return blockedTiles;
  }

  public Map<String, GymRoom> gymsByCell() {
    return gymsByCell;
  }

  public int mapRows() {
    return mapRows;
  }

  public int mapCols() {
    return mapCols;
  }

  public boolean isInside(int row, int col) {
    return row >= 0 && row < mapRows && col >= 0 && col < mapCols;
  }

  public boolean isWalkableSpawnTile(int row, int col) {
    if (!isInside(row, col)) {
      return false;
    }
    if (blockedTiles[row][col]) {
      return false;
    }
    return !gymsByCell.containsKey(GymCellPlacement.cellKey(row, col));
  }

  public static final class Builder {

    private boolean[][] blockedTiles;
    private Map<String, GymRoom> gymsByCell;
    private int mapRows;
    private int mapCols;

    public Builder blockedTiles(boolean[][] blockedTiles) {
      this.blockedTiles = blockedTiles;
      return this;
    }

    public Builder gymsByCell(Map<String, GymRoom> gymsByCell) {
      this.gymsByCell = gymsByCell;
      return this;
    }

    public Builder mapRows(int mapRows) {
      this.mapRows = mapRows;
      return this;
    }

    public Builder mapCols(int mapCols) {
      this.mapCols = mapCols;
      return this;
    }

    public MapGridContext build() {
      return new MapGridContext(this);
    }
  }
}
