package it.unicam.cs.mpgc.rpg125664.view.overworld;

import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Layout mappa overworld deterministico (stesso seed = palestre e decor fissi tra partite). */
public final class OverworldLayoutSupport {

  public static final long LAYOUT_SEED = 42L;

  private OverworldLayoutSupport() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static Map<String, GymRoom> assignGymsDeterministic(List<GymRoom> gyms) {
    boolean[][] blockedTiles = OverworldMapConstants.createBlockedTiles();
    return GymCellAssignment.assignCells(
        gyms, blockedTiles, new Random(LAYOUT_SEED), OverworldMapConstants.GYM_MIN_DISTANCE);
  }

  /** Alberi e cespugli: stesso seed del posizionamento palestre, indipendente dal numero di gym. */
  public static Map<String, OverworldDecor> assignDecorDeterministic(
      Map<String, GymRoom> gymsByCell, boolean[][] blockedTiles) {
    Map<String, OverworldDecor> decorByCell = new HashMap<>();
    Random random = new Random(LAYOUT_SEED);
    for (int row = 1; row < OverworldMapConstants.MAP_ROWS - 1; row++) {
      for (int col = 1; col < OverworldMapConstants.MAP_COLS - 1; col++) {
        if (blockedTiles[row][col]) continue;
        String key = GymCellAssignment.cellKey(row, col);
        if (gymsByCell.containsKey(key)) continue;
        double roll = random.nextDouble();
        if (roll < OverworldMapConstants.TREE_PROBABILITY) {
          decorByCell.put(key, OverworldDecor.TREE);
        } else if (roll
            < OverworldMapConstants.TREE_PROBABILITY + OverworldMapConstants.BUSH_PROBABILITY) {
          decorByCell.put(key, OverworldDecor.BUSH);
        }
      }
    }
    return decorByCell;
  }

  public static MapCoordinate defaultPlayerPosition(List<GymRoom> gyms, long currentGymId) {
    Map<String, GymRoom> gymsByCell = assignGymsDeterministic(gyms);
    boolean[][] blockedTiles = OverworldMapConstants.createBlockedTiles();
    for (Map.Entry<String, GymRoom> entry : gymsByCell.entrySet()) {
      if (entry.getValue().id() != currentGymId) {
        continue;
      }
      String[] parts = entry.getKey().split(":");
      int gymRow = Integer.parseInt(parts[0]);
      int gymCol = Integer.parseInt(parts[1]);
      return GymCellAssignment.findHomeTile(
          gymRow,
          gymCol,
          blockedTiles,
          gymsByCell,
          OverworldMapConstants.MAP_ROWS,
          OverworldMapConstants.MAP_COLS);
    }
    return new MapCoordinate(4, 1);
  }
}
