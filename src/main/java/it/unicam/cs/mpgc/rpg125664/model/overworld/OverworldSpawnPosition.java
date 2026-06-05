package it.unicam.cs.mpgc.rpg125664.model.overworld;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Posizione di fallback del giocatore sulla mappa quando non e' ancora stata salvata. */
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
            gyms,
            blockedTiles,
            new Random(OverworldGridLayout.LAYOUT_SEED),
            OverworldGridLayout.GYM_MIN_DISTANCE);
    for (Map.Entry<String, GymRoom> entry : gymsByCell.entrySet()) {
      if (entry.getValue().id() != currentGymId) {
        continue;
      }
      String[] parts = entry.getKey().split(":");
      int gymRow = Integer.parseInt(parts[0]);
      int gymCol = Integer.parseInt(parts[1]);
      return GymCellPlacement.findHomeTile(
          gymRow,
          gymCol,
          blockedTiles,
          gymsByCell,
          OverworldGridLayout.MAP_ROWS,
          OverworldGridLayout.MAP_COLS);
    }
    return OverworldGridLayout.DEFAULT_PLAYER_SPAWN;
  }
}
