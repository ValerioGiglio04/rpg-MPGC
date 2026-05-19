package it.unicam.cs.mpgc.rpg125664.view.overworld;

/** Delta applicato a un {@link MapCoordinate} (es. step di movimento o direzione vicino). */
public record MapOffset(int rowDelta, int columnDelta) {

  /** Nessun movimento (usato come default tastiera). */
  public static final MapOffset ZERO = new MapOffset(0, 0);

  public static final MapOffset UP = new MapOffset(-1, 0);
  public static final MapOffset DOWN = new MapOffset(1, 0);
  public static final MapOffset LEFT = new MapOffset(0, -1);
  public static final MapOffset RIGHT = new MapOffset(0, 1);

  public MapCoordinate applyTo(MapCoordinate origin) {
    return new MapCoordinate(origin.row() + rowDelta, origin.column() + columnDelta);
  }
}
