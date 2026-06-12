package it.unicam.cs.mpgc.rpg125664.model.overworld;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class OverworldSpawnPosition {

  private OverworldSpawnPosition() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static OverworldPosition defaultFor(GameState state) {
    List<GymRoom> gyms = state.gyms();
    long currentGymId = state.currentGymId();
    boolean[][] blockedTiles = OverworldGridLayout.createBlockedTiles();
    Map<String, GymRoom> gymsByCell =
        GymCellPlacement.assignCells(
            GymPlacementRequest.builder()
                .gyms(gyms)
                .blockedTiles(blockedTiles)
                .random(new Random(OverworldGridLayout.LAYOUT_SEED))
                .minDistance(OverworldGridLayout.GYM_MIN_DISTANCE)
                .build());
    MapGridContext grid =
        MapGridContext.builder()
            .blockedTiles(blockedTiles)
            .gymsByCell(gymsByCell)
            .mapRows(OverworldGridLayout.MAP_ROWS)
            .mapCols(OverworldGridLayout.MAP_COLS)
            .build();
    for (Map.Entry<String, GymRoom> entry : gymsByCell.entrySet()) {
      if (entry.getValue().id() != currentGymId) {
        continue;
      }
      String[] parts = entry.getKey().split(":");
      int gymRow = Integer.parseInt(parts[0]);
      int gymCol = Integer.parseInt(parts[1]);
      return GymCellPlacement.findHomeTile(gymRow, gymCol, grid);
    }
    return OverworldGridLayout.DEFAULT_PLAYER_SPAWN;
  }
}
