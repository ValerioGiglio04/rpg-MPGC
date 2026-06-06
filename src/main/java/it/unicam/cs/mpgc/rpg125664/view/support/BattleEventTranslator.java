package it.unicam.cs.mpgc.rpg125664.view.support;

import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.event.Side;
import java.util.List;
import java.util.Optional;

/**
 * Traduce un {@link BattleEvent} in una riga di log localizzata. Sta nel layer UI perche' il
 * fraseggio e' una questione di presentazione; il dominio emette solo il fatto grezzo.
 */
public final class BattleEventTranslator {

  private BattleEventTranslator() {}

  public static List<BattleLogLine> translate(List<BattleEvent> events) {
    return events.stream().map(BattleEventTranslator::translate).flatMap(Optional::stream).toList();
  }

  private static BattleLogLine translateRoundStarted(BattleEvent.RoundStarted started) {
    String text = Messages.format("battle.event.round.started", started.firstAttackerName());
    return new BattleLogLine(kindFor(started.firstAttacker()), text);
  }

  private static BattleLogLine translateMoveUsed(BattleEvent.MoveUsed used) {
    String sideFormatted =
        formatForSide(
            used.side(),
            "battle.event.move.used.player",
            "battle.event.move.used.boss",
            used.moveName());
    return new BattleLogLine(kindFor(used.side()), sideFormatted);
  }

  private static BattleLogLine translateAttackHit(BattleEvent.AttackHit hit) {
    String sideFormatted =
        formatForSide(
            hit.side(),
            "battle.event.attack.hit.player",
            "battle.event.attack.hit.boss",
            hit.defenderName(),
            hit.damage());
    return new BattleLogLine(kindFor(hit.side()), sideFormatted);
  }

  private static BattleLogLine translateAttackMissed(BattleEvent.AttackMissed missed) {
    String sideFormatted =
        getForSide(
            missed.side(), "battle.event.attack.missed.player", "battle.event.attack.missed.boss");
    return new BattleLogLine(kindFor(missed.side()), sideFormatted);
  }

  private static BattleLogLine translateCreatureKnockedOut(BattleEvent.CreatureKnockedOut ko) {
    String sideFormatted =
        formatForSide(
            ko.side(),
            "battle.event.creature.knocked.out.player",
            "battle.event.creature.knocked.out.boss",
            ko.creatureName());
    return new BattleLogLine(kindFor(ko.side()), sideFormatted);
  }

  private static BattleLogLine translateCreatureSwitched(BattleEvent.CreatureSwitched switched) {
    String sideFormatted =
        formatForSide(
            switched.side(),
            "battle.event.creature.switched.player",
            "battle.event.creature.switched.boss",
            switched.toName());
    return new BattleLogLine(kindFor(switched.side()), sideFormatted);
  }

  private static BattleLogLine translateBossDefeated(BattleEvent.BossDefeated defeated) {
    String text =
        Messages.format(
            "battle.event.boss.defeated", defeated.bossName(), defeated.pointsRewarded());
    return new BattleLogLine(BattleLogLine.Kind.NEUTRAL, text);
  }

  private static BattleLogLine translateCreaturesAcquired(BattleEvent.CreaturesAcquired acquired) {
    String text =
        Messages.format(
            "battle.event.creatures.acquired", String.join(", ", acquired.creatureNames()));
    return new BattleLogLine(BattleLogLine.Kind.NEUTRAL, text);
  }

  private static BattleLogLine translatePlayerTeamWiped() {
    String text = Messages.get("battle.event.team.wiped.log");
    return new BattleLogLine(BattleLogLine.Kind.NEUTRAL, text);
  }

  public static Optional<BattleLogLine> translate(BattleEvent event) {
    return switch (event) {
      case BattleEvent.RoundStarted started -> Optional.of(translateRoundStarted(started));
      case BattleEvent.MoveUsed used -> Optional.of(translateMoveUsed(used));
      case BattleEvent.AttackHit hit -> Optional.of(translateAttackHit(hit));
      case BattleEvent.AttackMissed missed -> Optional.of(translateAttackMissed(missed));
      case BattleEvent.CreatureKnockedOut ko -> Optional.of(translateCreatureKnockedOut(ko));
      case BattleEvent.CreatureSwitched switched ->
          Optional.of(translateCreatureSwitched(switched));
      case BattleEvent.BossDefeated defeated -> Optional.of(translateBossDefeated(defeated));
      case BattleEvent.CreaturesAcquired acquired ->
          Optional.of(translateCreaturesAcquired(acquired));
      case BattleEvent.PlayerTeamWiped() -> Optional.of(translatePlayerTeamWiped());
    };
  }

  private static String getForSide(Side side, String playerKey, String bossKey) {
    return Messages.get(side == Side.PLAYER ? playerKey : bossKey);
  }

  private static String formatForSide(
      Side side, String playerKey, String bossKey, Object... formatArgs) {
    String key = side == Side.PLAYER ? playerKey : bossKey;
    return Messages.format(key, formatArgs);
  }

  private static BattleLogLine.Kind kindFor(Side side) {
    return side == Side.PLAYER ? BattleLogLine.Kind.PLAYER : BattleLogLine.Kind.BOSS;
  }
}
