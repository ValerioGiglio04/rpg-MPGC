package it.unicam.cs.mpgc.rpg125664.view.support;

import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import java.util.List;
import java.util.Optional;

/**
 * Traduce un {@link BattleEvent} in una riga di log localizzata. Sta nel layer UI perche' il
 * fraseggio e' una questione di presentazione; il dominio emette solo il fatto grezzo.
 */
public final class BattleEventTranslator {

  private static final BattleSideMessages.SideMessageKeys MOVE_USED =
    new BattleSideMessages.SideMessageKeys(
      "battle.event.move.used.player",
      "battle.event.move.used.boss"
    );
  private static final BattleSideMessages.SideMessageKeys ATTACK_HIT =
    new BattleSideMessages.SideMessageKeys(
      "battle.event.attack.hit.player",
      "battle.event.attack.hit.boss"
    );
  private static final BattleSideMessages.SideMessageKeys ATTACK_MISSED =
    new BattleSideMessages.SideMessageKeys(
      "battle.event.attack.missed.player",
      "battle.event.attack.missed.boss"
    );
  private static final BattleSideMessages.SideMessageKeys KNOCKED_OUT =
    new BattleSideMessages.SideMessageKeys(
      "battle.event.creature.knocked.out.player",
      "battle.event.creature.knocked.out.boss"
    );
  private static final BattleSideMessages.SideMessageKeys SWITCHED =
    new BattleSideMessages.SideMessageKeys(
      "battle.event.creature.switched.player",
      "battle.event.creature.switched.boss"
    );

  private BattleEventTranslator() {}

  public static List<BattleLogLine> translate(List<BattleEvent> events) {
    return events.stream().map(BattleEventTranslator::translate).flatMap(Optional::stream).toList();
  }

  private static BattleLogLine translateRoundStarted(BattleEvent.RoundStarted started) {
    String text = Messages.format("battle.event.round.started", started.firstAttackerName());
    return new BattleLogLine(BattleSideMessages.logKind(started.firstAttacker()), text);
  }

  private static BattleLogLine translateMoveUsed(BattleEvent.MoveUsed used) {
    String sideFormatted = BattleSideMessages.format(used.side(), MOVE_USED, used.moveName());
    return new BattleLogLine(BattleSideMessages.logKind(used.side()), sideFormatted);
  }

  private static BattleLogLine translateAttackHit(BattleEvent.AttackHit hit) {
    String sideFormatted = BattleSideMessages.format(
      hit.side(),
      ATTACK_HIT,
      hit.defenderName(),
      hit.damage()
    );
    return new BattleLogLine(BattleSideMessages.logKind(hit.side()), sideFormatted);
  }

  private static BattleLogLine translateAttackMissed(BattleEvent.AttackMissed missed) {
    String sideFormatted = BattleSideMessages.get(missed.side(), ATTACK_MISSED);
    return new BattleLogLine(BattleSideMessages.logKind(missed.side()), sideFormatted);
  }

  private static BattleLogLine translateCreatureKnockedOut(BattleEvent.CreatureKnockedOut ko) {
    String sideFormatted = BattleSideMessages.format(ko.side(), KNOCKED_OUT, ko.creatureName());
    return new BattleLogLine(BattleSideMessages.logKind(ko.side()), sideFormatted);
  }

  private static BattleLogLine translateCreatureSwitched(BattleEvent.CreatureSwitched switched) {
    String sideFormatted = BattleSideMessages.format(switched.side(), SWITCHED, switched.toName());
    return new BattleLogLine(BattleSideMessages.logKind(switched.side()), sideFormatted);
  }

  private static BattleLogLine translateBossDefeated(BattleEvent.BossDefeated defeated) {
    String text = Messages.format(
      "battle.event.boss.defeated",
      defeated.bossName(),
      defeated.pointsRewarded()
    );
    return new BattleLogLine(BattleLogLine.Kind.NEUTRAL, text);
  }

  private static BattleLogLine translateCreaturesAcquired(BattleEvent.CreaturesAcquired acquired) {
    String text = Messages.format(
      "battle.event.creatures.acquired",
      String.join(", ", acquired.creatureNames())
    );
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
      case BattleEvent.CreatureSwitched switched -> Optional.of(
        translateCreatureSwitched(switched)
      );
      case BattleEvent.BossDefeated defeated -> Optional.of(translateBossDefeated(defeated));
      case BattleEvent.CreaturesAcquired acquired -> Optional.of(
        translateCreaturesAcquired(acquired)
      );
      case BattleEvent.PlayerTeamWiped() -> Optional.of(translatePlayerTeamWiped());
    };
  }
}
