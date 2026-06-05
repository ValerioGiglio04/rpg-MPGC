package it.unicam.cs.mpgc.rpg125664.view.overworld;

import it.unicam.cs.mpgc.rpg125664.model.overworld.GymCellPlacement;
import it.unicam.cs.mpgc.rpg125664.model.overworld.OverworldGridLayout;
import it.unicam.cs.mpgc.rpg125664.model.overworld.OverworldSpawnPosition;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Layout mappa overworld deterministico (stesso seed = palestre e decor fissi tra partite). */
public final class OverworldLayoutSupport {

  private OverworldLayoutSupport() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static Map<String, GymRoom> assignGymsDeterministic(List<GymRoom> gyms) {
    boolean[][] blockedTiles = OverworldMapConstants.createBlockedTiles();
    return GymCellPlacement.assignCells(
        gyms, blockedTiles, new Random(OverworldGridLayout.LAYOUT_SEED), OverworldGridLayout.GYM_MIN_DISTANCE);
  }

  /** Alberi e cespugli: stesso seed del posizionamento palestre, indipendente dal numero di gym. */
  public static Map<String, OverworldDecor> assignDecorDeterministic(
      Map<String, GymRoom> gymsByCell, boolean[][] blockedTiles) {
    Map<String, OverworldDecor> decorByCell = new HashMap<>();
    Random random = new Random(OverworldGridLayout.LAYOUT_SEED);
    for (int row = 1; row < OverworldGridLayout.MAP_ROWS - 1; row++) {
      for (int col = 1; col < OverworldGridLayout.MAP_COLS - 1; col++) {
        if (blockedTiles[row][col]) continue;
        String key = GymCellPlacement.cellKey(row, col);
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

  public static OverworldPosition defaultPlayerPosition(GameState state) {
    return OverworldSpawnPosition.defaultFor(state);
  }
}
