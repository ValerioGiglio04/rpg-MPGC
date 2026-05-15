package it.unicam.cs.mpgc.rpg125664.model.event;

/**
 * Identificatore in fase di combattimento per uno dei due lati della battaglia. Estratto da {@link
 * BattleEvent} cosi' comandi e view model possono condividerlo.
 */
public enum Side {
  PLAYER,
  BOSS;

  public Side opposite() {
    return this == PLAYER ? BOSS : PLAYER;
  }
}
