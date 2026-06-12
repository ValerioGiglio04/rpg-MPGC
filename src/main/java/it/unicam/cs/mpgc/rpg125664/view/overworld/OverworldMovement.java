package it.unicam.cs.mpgc.rpg125664.view.overworld;

import javafx.scene.input.KeyCode;

/** Regole di movimento e collisione sulla griglia overworld. */
final class OverworldMovement {

  private OverworldMovement() {}

  static MapOffset stepFor(KeyCode code) {
    return switch (code) {
      case W, UP -> MapOffset.UP;
      case S, DOWN -> MapOffset.DOWN;
      case A, LEFT -> MapOffset.LEFT;
      case D, RIGHT -> MapOffset.RIGHT;
      default -> MapOffset.ZERO;
    };
  }

  static boolean isWalkable(int row, int col, boolean[][] blockedTiles) {
    if (isOutOfBounds(row, col)) {
      return false;
    }
    return !blockedTiles[row][col];
  }

  private static boolean isOutOfBounds(int row, int col) {
    return (
      !isInRange(col, 0, OverworldMapConstants.MAP_COLS) ||
      !isInRange(row, 0, OverworldMapConstants.MAP_ROWS)
    );
  }

  private static boolean isInRange(int value, int min, int max) {
    return value >= min && value <= max;
  }
}
