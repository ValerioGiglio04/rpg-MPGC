package it.unicam.cs.mpgc.rpg125664.view.overworld;

import it.unicam.cs.mpgc.rpg125664.controller.OverworldPresenter;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.overworld.GymCellPlacement;
import it.unicam.cs.mpgc.rpg125664.model.overworld.MapGridContext;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/** Risolve la posizione iniziale del giocatore sulla mappa overworld. */
final class OverworldPlayerSpawn {

  record Result(int row, int column, int lastRow, int lastColumn, boolean syncSession) {}

  private OverworldPlayerSpawn() {}

  static Result resolve(
    OverworldPresenter presenter,
    Map<String, GymRoom> gymsByCell,
    boolean[][] blockedTiles
  ) {
    Optional<OverworldPosition> saved = presenter.savedPosition();
    if (saved.isPresent()) {
      OverworldPosition position = saved.orElseThrow();
      return new Result(
        position.row(),
        position.column(),
        position.row(),
        position.column(),
        false
      );
    }
    Entry<String, GymRoom> currentGym = findCurrentGymEntry(presenter, gymsByCell);
    if (currentGym != null) {
      return spawnNearGym(currentGym.getKey(), blockedTiles, gymsByCell);
    }
    return defaultSpawn();
  }

  private static Entry<String, GymRoom> findCurrentGymEntry(
    OverworldPresenter presenter,
    Map<String, GymRoom> gymsByCell
  ) {
    long currentGymId = presenter.gameState().currentGym().id();
    return gymsByCell
      .entrySet()
      .stream()
      .filter(entry -> entry.getValue().id() == currentGymId)
      .findFirst()
      .orElse(null);
  }

  private static Result spawnNearGym(
    String cellKey,
    boolean[][] blockedTiles,
    Map<String, GymRoom> gymsByCell
  ) {
    String[] coords = cellKey.split(":");
    int gymRow = Integer.parseInt(coords[0]);
    int gymCol = Integer.parseInt(coords[1]);
    MapGridContext grid = MapGridContext.builder()
      .blockedTiles(blockedTiles)
      .gymsByCell(gymsByCell)
      .mapRows(OverworldMapConstants.MAP_ROWS)
      .mapCols(OverworldMapConstants.MAP_COLS)
      .build();
    OverworldPosition home = GymCellPlacement.findHomeTile(gymRow, gymCol, grid);
    return new Result(home.row(), home.column(), home.row(), home.column(), true);
  }

  private static Result defaultSpawn() {
    int row = OverworldMapConstants.DEFAULT_SPAWN_ROW;
    int column = OverworldMapConstants.DEFAULT_SPAWN_COLUMN;
    return new Result(row, column, row, column, true);
  }
}
