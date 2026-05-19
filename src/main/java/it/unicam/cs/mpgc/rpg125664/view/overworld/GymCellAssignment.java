package it.unicam.cs.mpgc.rpg125664.view.overworld;

import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Piazza le palestre su slot candidati fissi con spaziatura minima, e trova un tile walkable
 * accanto alla palestra corrente.
 */
public final class GymCellAssignment {

  /**
   * Celle interne predefinite dove le palestre possono spawnare (non coppie int arbitrarie: ogni
   * entry e' uno slot ammesso).
   */
  private static final List<MapCoordinate> CANDIDATE_CELLS =
      List.of(
          new MapCoordinate(2, 2),
          new MapCoordinate(2, 5),
          new MapCoordinate(2, 8),
          new MapCoordinate(2, 11),
          new MapCoordinate(4, 3),
          new MapCoordinate(4, 6),
          new MapCoordinate(4, 10),
          new MapCoordinate(6, 2),
          new MapCoordinate(6, 5),
          new MapCoordinate(6, 8),
          new MapCoordinate(6, 11));

  /**
   * Ordine preservato: il home tile preferisce il primo vicino walkable in questo scan (destra,
   * sinistra, giu', su rispetto alla cella della palestra).
   */
  private static final List<MapOffset> HOME_TILE_SEARCH_DIRECTIONS =
      List.of(MapOffset.RIGHT, MapOffset.LEFT, MapOffset.DOWN, MapOffset.UP);

  private static final MapCoordinate DEFAULT_HOME = new MapCoordinate(4, 1);

  private GymCellAssignment() {}

  /** Mappa chiavi di cella {@code "row:col"} alle palestre nell'ordine della lista. */
  public static Map<String, GymRoom> assignCells(
      List<GymRoom> gyms, boolean[][] blockedTiles, Random random, int minDistance) {
    List<MapCoordinate> candidates = shuffledCandidates(random);
    List<MapCoordinate> chosen =
        pickWithMinDistance(candidates, blockedTiles, gyms.size(), minDistance);
    fillRemainingSlots(chosen, candidates, blockedTiles, gyms.size());
    return zipCellsToGyms(gyms, chosen);
  }

  private static List<MapCoordinate> shuffledCandidates(Random random) {
    List<MapCoordinate> candidates = new ArrayList<>(CANDIDATE_CELLS);
    Collections.shuffle(candidates, random);
    return candidates;
  }

  private static List<MapCoordinate> pickWithMinDistance(
      List<MapCoordinate> candidates, boolean[][] blockedTiles, int gymCount, int minDistance) {
    List<MapCoordinate> chosen = new ArrayList<>(gymCount);
    candidates.stream()
        .filter(
            cell ->
                !blockedTiles[cell.row()][cell.column()]
                    && hasMinDistance(chosen, cell, minDistance))
        .limit(gymCount)
        .forEach(chosen::add);
    return chosen;
  }

  private static void fillRemainingSlots(
      List<MapCoordinate> chosen,
      List<MapCoordinate> candidates,
      boolean[][] blockedTiles,
      int gymCount) {
    if (chosen.size() >= gymCount) return;
    int needed = gymCount - chosen.size();
    candidates.stream()
        .filter(cell -> !blockedTiles[cell.row()][cell.column()] && !containsCell(chosen, cell))
        .limit(needed)
        .forEach(chosen::add);
  }

  private static Map<String, GymRoom> zipCellsToGyms(
      List<GymRoom> gyms, List<MapCoordinate> chosen) {
    Map<String, GymRoom> gymsByCell = new HashMap<>();
    IntStream.range(0, gyms.size())
        .forEach(
            index -> {
              MapCoordinate slot = chosen.get(Math.min(index, chosen.size() - 1));
              gymsByCell.put(cellKey(slot.row(), slot.column()), gyms.get(index));
            });
    return gymsByCell;
  }

  /** Tile vicino non bloccato senza palestra; fallback spawn centro-sinistra. */
  public static MapCoordinate findHomeTile(
      int gymRow,
      int gymCol,
      boolean[][] blockedTiles,
      Map<String, GymRoom> gymsByCell,
      int mapRows,
      int mapCols) {
    MapCoordinate gymCell = new MapCoordinate(gymRow, gymCol);
    return HOME_TILE_SEARCH_DIRECTIONS.stream()
        .map(dir -> dir.applyTo(gymCell))
        .filter(pos -> inBounds(pos.row(), pos.column(), mapRows, mapCols))
        .filter(pos -> !blockedTiles[pos.row()][pos.column()])
        .filter(pos -> !gymsByCell.containsKey(cellKey(pos.row(), pos.column())))
        .findFirst()
        .orElse(DEFAULT_HOME);
  }

  private static boolean inBounds(int row, int col, int mapRows, int mapCols) {
    return row >= 0 && row < mapRows && col >= 0 && col < mapCols;
  }

  public static String cellKey(int row, int col) {
    return new StringBuilder().append(row).append(':').append(col).toString();
  }

  private static boolean hasMinDistance(
      List<MapCoordinate> taken, MapCoordinate cell, int minDist) {
    return taken.stream()
        .allMatch(
            other ->
                Math.max(
                        Math.abs(other.row() - cell.row()),
                        Math.abs(other.column() - cell.column()))
                    >= minDist);
  }

  private static boolean containsCell(List<MapCoordinate> cells, MapCoordinate cell) {
    return cells.stream()
        .anyMatch(other -> other.row() == cell.row() && other.column() == cell.column());
  }
}
