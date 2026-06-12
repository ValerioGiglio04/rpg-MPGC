package it.unicam.cs.mpgc.rpg125664.view.overworld;

import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;

/** Delta applicato a una posizione overworld (step di movimento o direzione vicino). */
public record MapOffset(int rowDelta, int columnDelta) {
  public static final MapOffset ZERO = new MapOffset(0, 0);
  public static final MapOffset UP = new MapOffset(-1, 0);
  public static final MapOffset DOWN = new MapOffset(1, 0);
  public static final MapOffset LEFT = new MapOffset(0, -1);
  public static final MapOffset RIGHT = new MapOffset(0, 1);

  public OverworldPosition applyTo(OverworldPosition origin) {
    return new OverworldPosition(origin.row() + rowDelta, origin.column() + columnDelta);
  }
}
