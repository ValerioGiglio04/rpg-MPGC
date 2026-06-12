package it.unicam.cs.mpgc.rpg125664.view.support;

import it.unicam.cs.mpgc.rpg125664.model.event.Side;

/** Formattazione messaggi di log battaglia dipendenti dal lato (giocatore / boss). */
final class BattleSideMessages {

  record SideMessageKeys(String playerKey, String bossKey) {}

  private BattleSideMessages() {}

  static String get(Side side, SideMessageKeys keys) {
    return Messages.get(side == Side.PLAYER ? keys.playerKey() : keys.bossKey());
  }

  static String format(Side side, SideMessageKeys keys, Object... formatArgs) {
    String key = side == Side.PLAYER ? keys.playerKey() : keys.bossKey();
    return Messages.format(key, formatArgs);
  }

  static BattleLogLine.Kind logKind(Side side) {
    return side == Side.PLAYER ? BattleLogLine.Kind.PLAYER : BattleLogLine.Kind.BOSS;
  }
}
