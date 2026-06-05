package it.unicam.cs.mpgc.rpg125664.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg125664.model.service.GameStateHolder;
import it.unicam.cs.mpgc.rpg125664.model.combat.AccuracyThresholdBossMoveStrategy;
import it.unicam.cs.mpgc.rpg125664.model.combat.AttackOutcome;
import it.unicam.cs.mpgc.rpg125664.model.combat.CombatEngine;
import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.event.Side;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.support.TestCatalogFactory;
import it.unicam.cs.mpgc.rpg125664.support.TestGameStates;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BattleServiceTest {

  private GameStateHolder holder;
  private BattleService battle;

  @BeforeEach
  void setUp() {
    holder = TestGameStates.challengeableHolder();
    battle =
        new BattleService(
            holder,
            lethalEngine(),
            new AccuracyThresholdBossMoveStrategy(),
            new GymCompletionHandler(TestCatalogFactory.minimal()));
  }

  @Test
  void beginHealsAllCombatants() {
    damageActiveCombatants();
    battle.begin();

    assertEquals(
        holder.current().player().holder().activeCreature().maxHealth(),
        holder.current().player().holder().activeCreature().currentHealth());
    assertEquals(
        holder.current().currentGym().boss().holder().activeCreature().maxHealth(),
        holder.current().currentGym().boss().holder().activeCreature().currentHealth());
  }

  @Test
  void roundStartsWithFasterSideFirst() {
    List<BattleEvent> events = battle.attack(0);

    BattleEvent.RoundStarted started = findEvent(events, BattleEvent.RoundStarted.class);
    assertEquals(Side.PLAYER, started.firstAttacker());
  }

  @Test
  void stubEngineProducesAttackHitEvents() {
    BattleService fixed =
        new BattleService(
            holder,
            fixedDamageEngine(7),
            new AccuracyThresholdBossMoveStrategy(),
            new GymCompletionHandler(TestCatalogFactory.minimal()));

    List<BattleEvent> events = fixed.attack(0);

    assertTrue(events.stream().anyMatch(BattleEvent.AttackHit.class::isInstance));
  }

  @Test
  void defeatingBossCompletesGymAndRewardsGlory() {
    int scoreBefore = holder.current().player().score().points();

    List<BattleEvent> events = battle.attack(0);

    assertTrue(holder.current().currentGym().completed());
    assertTrue(holder.current().player().score().points() > scoreBefore);
    assertTrue(events.stream().anyMatch(BattleEvent.BossDefeated.class::isInstance));
  }

  @Test
  void playerWipeEndsBattleWithEvent() {
    battle.begin();
    Creature player = holder.current().player().holder().activeCreature();
    player.receiveDamage(player.currentHealth() - 1);
    BattleService svc =
        new BattleService(
            holder,
            bossLethalEngine(),
            new AccuracyThresholdBossMoveStrategy(),
            new GymCompletionHandler(TestCatalogFactory.minimal()));

    List<BattleEvent> events = svc.attack(0);

    assertTrue(events.stream().anyMatch(BattleEvent.PlayerTeamWiped.class::isInstance));
  }

  @Test
  void attackOnCompletedGymThrows() {
    holder.current().currentGym().markCompleted();

    assertThrows(IllegalStateException.class, () -> battle.attack(0));
  }

  @Test
  void switchToChangesActiveCreature() {
    battle.begin();
    long otherId = TestCatalogFactory.CREATURE_FAST;

    battle.switchTo(otherId);

    assertEquals(otherId, holder.current().player().holder().activeCatalogId());
  }

  private void damageActiveCombatants() {
    holder.current().player().holder().activeCreature().receiveDamage(5);
    holder.current().currentGym().boss().holder().activeCreature().receiveDamage(5);
  }

  private static CombatEngine lethalEngine() {
    return (attacker, defender, move) -> {
      int damage = defender.currentHealth();
      defender.receiveDamage(damage);
      return AttackOutcome.landed(damage, defender.isKnockedOut());
    };
  }

  private static CombatEngine fixedDamageEngine(int damage) {
    return (attacker, defender, move) -> {
      defender.receiveDamage(damage);
      return AttackOutcome.landed(damage, defender.isKnockedOut());
    };
  }

  private static CombatEngine bossLethalEngine() {
    return (attacker, defender, move) -> {
      if (attacker.catalogId() != TestCatalogFactory.CREATURE_SLOW) {
        return AttackOutcome.miss();
      }
      int damage = defender.currentHealth();
      defender.receiveDamage(damage);
      return AttackOutcome.landed(damage, defender.isKnockedOut());
    };
  }

  private static <T extends BattleEvent> T findEvent(List<BattleEvent> events, Class<T> type) {
    return events.stream()
        .filter(type::isInstance)
        .map(type::cast)
        .findFirst()
        .orElseThrow();
  }
}
