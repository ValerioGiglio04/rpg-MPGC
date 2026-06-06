package it.unicam.cs.mpgc.rpg125664.view.support;

/** Una riga della cronaca di battaglia, con stile derivato da chi ha agito (quando noto). */
public record BattleLogLine(Kind kind, String text) {

  public enum Kind {
    PLAYER,
    BOSS,
    NEUTRAL
  }

  public static BattleLogLine neutral(String text) {
    return new BattleLogLine(Kind.NEUTRAL, text);
  }
}
