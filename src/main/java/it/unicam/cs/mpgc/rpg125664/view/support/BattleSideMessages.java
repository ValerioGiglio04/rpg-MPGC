package it.unicam.cs.mpgc.rpg125664.view.support;

import it.unicam.cs.mpgc.rpg125664.model.event.Side;

/** Formattazione messaggi di log battaglia dipendenti dal lato (giocatore / boss). */
final class BattleSideMessages {

  private BattleSideMessages() {}

  static String get(Side side, String playerKey, String bossKey) {
    return Messages.get(side == Side.PLAYER ? playerKey : bossKey);
  }

  static String format(Side side, String playerKey, String bossKey, Object... formatArgs) {
    String key = side == Side.PLAYER ? playerKey : bossKey;
    return Messages.format(key, formatArgs);
  }

  static BattleLogLine.Kind logKind(Side side) {
    return side == Side.PLAYER ? BattleLogLine.Kind.PLAYER : BattleLogLine.Kind.BOSS;
  }
}
