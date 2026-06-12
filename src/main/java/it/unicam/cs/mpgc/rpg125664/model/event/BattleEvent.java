package it.unicam.cs.mpgc.rpg125664.model.event;

import java.util.List;

/**
 * Evento di un singolo turno emesso dal codice di combattimento. La UI lo traduce in righe di log
 * localizzate; la persistenza lo ignora. Sealed cosi' il translator puo' fare pattern matching
 * esaustivo.
 */
public sealed interface BattleEvent {
  record RoundStarted(Side firstAttacker, String firstAttackerName) implements BattleEvent {}

  record MoveUsed(Side side, String attackerName, String moveName) implements BattleEvent {}

  record AttackHit(Side side, String defenderName, int damage) implements BattleEvent {}

  record AttackMissed(Side side) implements BattleEvent {}

  record CreatureKnockedOut(Side side, String creatureName) implements BattleEvent {}

  record CreatureSwitched(Side side, String fromName, String toName) implements BattleEvent {}

  record BossDefeated(String bossName, int pointsRewarded) implements BattleEvent {}

  record CreaturesAcquired(List<String> creatureNames) implements BattleEvent {}

  record PlayerTeamWiped() implements BattleEvent {}
}
