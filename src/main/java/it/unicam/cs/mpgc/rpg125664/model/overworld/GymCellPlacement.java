package it.unicam.cs.mpgc.rpg125664.model.overworld;

import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Assegna ogni palestra a uno slot della griglia e trova il tile accanto dove comparire il
 * giocatore.
 */
public final class GymCellPlacement {

  private GymCellPlacement() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Sceglie {@code gyms.size()} slot da {@link OverworldGridLayout#gymSlots()}, rispettando
   * distanza minima e tile bloccati, poi associa palestra → cella.
   */
  public static Map<String, GymRoom> assignCells(
      List<GymRoom> gyms, boolean[][] blockedTiles, Random random, int minDistance) {
    List<OverworldPosition> shuffledSlots = shuffledGymSlots(random);
    List<OverworldPosition> chosenSlots =
        pickSlotsWithMinDistance(shuffledSlots, blockedTiles, gyms.size(), minDistance);
    fillRemainingSlots(chosenSlots, shuffledSlots, blockedTiles, gyms.size());
    return mapGymsToSlots(gyms, chosenSlots);
  }

  /**
   * Tile camminabile accanto alla palestra dove spawnare il giocatore. Si prova, in ordine: destra,
   * sinistra, sotto, sopra. Se nessuno è libero, si usa {@link OverworldGridLayout#DEFAULT_PLAYER_SPAWN}.
   */
  public static OverworldPosition findHomeTile(
      int gymRow,
      int gymCol,
      boolean[][] blockedTiles,
      Map<String, GymRoom> gymsByCell,
      int mapRows,
      int mapCols) {
    for (AdjacentDirection direction : AdjacentDirection.SPAWN_SEARCH_ORDER) {
      int row = gymRow + direction.rowDelta;
      int col = gymCol + direction.columnDelta;
      if (isWalkableSpawnTile(row, col, blockedTiles, gymsByCell, mapRows, mapCols)) {
        return new OverworldPosition(row, col);
      }
    }
    return OverworldGridLayout.DEFAULT_PLAYER_SPAWN;
  }

  /** Chiave {@code "riga:colonna"} usata dalla mappa per indicizzare palestre e decor. */
  public static String cellKey(int row, int col) {
    return row + ":" + col;
  }

  private static List<OverworldPosition> shuffledGymSlots(Random random) {
    List<OverworldPosition> slots = new ArrayList<>(OverworldGridLayout.gymSlots());
    Collections.shuffle(slots, random);
    return slots;
  }

  private static List<OverworldPosition> pickSlotsWithMinDistance(
      List<OverworldPosition> slots, boolean[][] blockedTiles, int gymCount, int minDistance) {
    List<OverworldPosition> chosen = new ArrayList<>(gymCount);
    slots.stream()
        .filter(slot -> isFreeSlot(slot, blockedTiles))
        .filter(slot -> isFarEnoughFromChosen(chosen, slot, minDistance))
        .limit(gymCount)
        .forEach(chosen::add);
    return chosen;
  }

  private static void fillRemainingSlots(
      List<OverworldPosition> chosen,
      List<OverworldPosition> slots,
      boolean[][] blockedTiles,
      int gymCount) {
    if (chosen.size() >= gymCount) {
      return;
    }
    int stillNeeded = gymCount - chosen.size();
    slots.stream()
        .filter(slot -> isFreeSlot(slot, blockedTiles))
        .filter(slot -> !chosen.contains(slot))
        .limit(stillNeeded)
        .forEach(chosen::add);
  }

  private static Map<String, GymRoom> mapGymsToSlots(
      List<GymRoom> gyms, List<OverworldPosition> chosenSlots) {
    Map<String, GymRoom> gymsByCell = new HashMap<>();
    IntStream.range(0, gyms.size())
        .forEach(
            index -> {
              OverworldPosition slot = chosenSlots.get(Math.min(index, chosenSlots.size() - 1));
              gymsByCell.put(cellKey(slot.row(), slot.column()), gyms.get(index));
            });
    return gymsByCell;
  }

  private static boolean isWalkableSpawnTile(
      int row,
      int col,
      boolean[][] blockedTiles,
      Map<String, GymRoom> gymsByCell,
      int mapRows,
      int mapCols) {
    if (!isInsideMap(row, col, mapRows, mapCols)) {
      return false;
    }
    if (blockedTiles[row][col]) {
      return false;
    }
    return !gymsByCell.containsKey(cellKey(row, col));
  }

  private static boolean isFreeSlot(OverworldPosition slot, boolean[][] blockedTiles) {
    return !blockedTiles[slot.row()][slot.column()];
  }

  private static boolean isInsideMap(int row, int col, int mapRows, int mapCols) {
    return row >= 0 && row < mapRows && col >= 0 && col < mapCols;
  }

  private static boolean isFarEnoughFromChosen(
      List<OverworldPosition> chosen, OverworldPosition candidate, int minDistance) {
    return chosen.stream().allMatch(other -> chebyshevDistance(other, candidate) >= minDistance);
  }

  /** Distanza a scacchiera: quante mosse ortogonali separano due tile. */
  private static int chebyshevDistance(OverworldPosition a, OverworldPosition b) {
    return Math.max(Math.abs(a.row() - b.row()), Math.abs(a.column() - b.column()));
  }

  private enum AdjacentDirection {
    RIGHT(0, 1),
    LEFT(0, -1),
    BELOW(1, 0),
    ABOVE(-1, 0);

    static final AdjacentDirection[] SPAWN_SEARCH_ORDER = {RIGHT, LEFT, BELOW, ABOVE};

    final int rowDelta;
    final int columnDelta;

    AdjacentDirection(int rowDelta, int columnDelta) {
      this.rowDelta = rowDelta;
      this.columnDelta = columnDelta;
    }
  }
}
