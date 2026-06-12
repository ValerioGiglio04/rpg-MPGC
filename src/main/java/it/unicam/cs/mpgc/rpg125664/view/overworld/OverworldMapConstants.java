package it.unicam.cs.mpgc.rpg125664.view.overworld;

import it.unicam.cs.mpgc.rpg125664.model.overworld.OverworldGridLayout;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;

/** Costanti di rendering overworld (zoom, tile, decor). */
public final class OverworldMapConstants {

  public static final int MAP_ROWS = OverworldGridLayout.MAP_ROWS;
  public static final int MAP_COLS = OverworldGridLayout.MAP_COLS;
  public static final int TILE_SIZE = 60;

  public static final double MIN_ZOOM = 0.5;
  public static final double MAX_ZOOM = 2.0;
  public static final double ZOOM_STEP = 0.15;
  public static final double DEFAULT_ZOOM = 1.0;

  public static final double TREE_PROBABILITY = 0.08;
  public static final double BUSH_PROBABILITY = 0.14;
  public static final int GYM_MIN_DISTANCE = OverworldGridLayout.GYM_MIN_DISTANCE;

  public static final int DEFAULT_SPAWN_ROW = 4;
  public static final int DEFAULT_SPAWN_COLUMN = 1;

  private OverworldMapConstants() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static String legendText() {
    return Messages.get("overworld.legend");
  }

  public static boolean[][] createBlockedTiles() {
    return OverworldGridLayout.createBlockedTiles();
  }
}
