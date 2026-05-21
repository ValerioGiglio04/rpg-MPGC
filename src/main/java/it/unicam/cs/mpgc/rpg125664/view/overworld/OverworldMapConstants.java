package it.unicam.cs.mpgc.rpg125664.view.overworld;

import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;

/** Dimensione griglia, zoom, probabilita' decor e bit di layout statico per l'overworld. */
public final class OverworldMapConstants {

  private OverworldMapConstants() {}

  public static final int MAP_ROWS = 9;
  public static final int MAP_COLS = 13;
  // Aumentata leggermente la dimensione del tile cosi' le icone palestra
  // restano leggibili senza zoom.
  public static final int TILE_SIZE = 60;

  // MIN_ZOOM abbassato per permettere una panoramica piu' ampia della mappa.
  public static final double MIN_ZOOM = 0.5;
  public static final double MAX_ZOOM = 2.0;
  public static final double ZOOM_STEP = 0.15;
  public static final double DEFAULT_ZOOM = 1.0;

  public static final double TREE_PROBABILITY = 0.08;
  public static final double BUSH_PROBABILITY = 0.14;
  public static final int GYM_MIN_DISTANCE = 2;

  public static String legendText() {
    return Messages.get("overworld.legend");
  }

  public static boolean[][] createBlockedTiles() {
    boolean[][] blocked = new boolean[MAP_ROWS][MAP_COLS];
    for (int row = 0; row < MAP_ROWS; row++) {
      for (int col = 0; col < MAP_COLS; col++) {
        boolean border = row == 0 || row == MAP_ROWS - 1 || col == 0 || col == MAP_COLS - 1;
        blocked[row][col] = border;
      }
    }
    blocked[3][4] = true;
    blocked[5][8] = true;
    blocked[3][8] = true;
    return blocked;
  }
}
