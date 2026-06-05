package it.unicam.cs.mpgc.rpg125664.model.overworld;

import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import java.util.List;

/**
 * Geometria della mappa overworld (9×13): muri, rocce fisse, slot palestre e spawn di default.
 * Condivisa tra salvataggio e rendering.
 */
public final class OverworldGridLayout {

  public static final int MAP_ROWS = 9;
  public static final int MAP_COLS = 13;
  public static final int GYM_MIN_DISTANCE = 2;
  public static final long LAYOUT_SEED = 42L;

  /** Tile iniziale del giocatore se non c'è ancora una posizione salvata. */
  public static final OverworldPosition DEFAULT_PLAYER_SPAWN = new OverworldPosition(4, 1);

  private OverworldGridLayout() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Celle dove possono comparire le palestre: interne alla mappa, lontane dal bordo e dalle rocce
   * in {@link #createBlockedTiles()}. L'ordine non conta: {@link GymCellPlacement} le mescola col
   * seed {@link #LAYOUT_SEED}.
   */
  public static List<OverworldPosition> gymSlots() {
    return List.of(
        slot(2, 2),
        slot(2, 5),
        slot(2, 8),
        slot(2, 11),
        slot(4, 3),
        slot(4, 6),
        slot(4, 10),
        slot(6, 2),
        slot(6, 5),
        slot(6, 8),
        slot(6, 11));
  }

  public static boolean[][] createBlockedTiles() {
    boolean[][] blocked = new boolean[MAP_ROWS][MAP_COLS];
    markBorderAsBlocked(blocked);
    markRockTiles(blocked);
    return blocked;
  }

  private static void markBorderAsBlocked(boolean[][] blocked) {
    for (int row = 0; row < MAP_ROWS; row++) {
      for (int col = 0; col < MAP_COLS; col++) {
        boolean onBorder = row == 0 || row == MAP_ROWS - 1 || col == 0 || col == MAP_COLS - 1;
        blocked[row][col] = onBorder;
      }
    }
  }

  /** Rocce fisse sulla mappa (non camminabili, non usabili per palestre). */
  private static void markRockTiles(boolean[][] blocked) {
    blocked[3][4] = true;
    blocked[3][8] = true;
    blocked[5][8] = true;
  }

  private static OverworldPosition slot(int row, int column) {
    return new OverworldPosition(row, column);
  }
}
