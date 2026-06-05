package it.unicam.cs.mpgc.rpg125664.model.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg125664.model.builder.CreatureBuilder;
import it.unicam.cs.mpgc.rpg125664.model.builder.MoveBuilder;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import it.unicam.cs.mpgc.rpg125664.support.FixedBoundRandom;
import java.util.List;
import org.junit.jupiter.api.Test;

class TurnBasedCombatEngineTest {

  @Test
  void hitWhenRollBelowAccuracy() {
    Creature attacker = creature(10, 5);
    Creature defender = creature(20, 2);
    Move move = move(8, 75);
    TurnBasedCombatEngine engine = new TurnBasedCombatEngine(new FixedBoundRandom(50));

    AttackOutcome outcome = engine.execute(attacker, defender, move);

    assertTrue(outcome.hit());
    assertEquals(16, outcome.damage());
    assertEquals(4, defender.currentHealth());
    assertFalse(outcome.defenderKnockedOut());
  }

  @Test
  void missWhenRollAtOrAboveAccuracy() {
    Creature attacker = creature(10, 5);
    Creature defender = creature(20, 2);
    Move move = move(8, 75);
    TurnBasedCombatEngine engine = new TurnBasedCombatEngine(new FixedBoundRandom(75));

    AttackOutcome outcome = engine.execute(attacker, defender, move);

    assertFalse(outcome.hit());
    assertEquals(20, defender.currentHealth());
  }

  @Test
  void damageFloorsAtOne() {
    Creature attacker = creature(1, 0);
    Creature defender =
        new CreatureBuilder()
            .catalogId(2L)
            .name("Tank")
            .maxHealth(30)
            .attack(1)
            .defense(50)
            .moves(List.of(move(1, 1)))
            .build();
    Move move = move(1, 1);
    TurnBasedCombatEngine engine = new TurnBasedCombatEngine(new FixedBoundRandom(0));

    AttackOutcome outcome = engine.execute(attacker, defender, move);

    assertTrue(outcome.hit());
    assertEquals(1, outcome.damage());
    assertEquals(29, defender.currentHealth());
  }

  @Test
  void defenderKnockedOutWhenHpReachesZero() {
    Creature attacker = creature(50, 0);
    Creature defender = creature(5, 0);
    Move move = move(10, 100);
    TurnBasedCombatEngine engine = new TurnBasedCombatEngine(new FixedBoundRandom(0));

    AttackOutcome outcome = engine.execute(attacker, defender, move);

    assertTrue(outcome.hit());
    assertTrue(outcome.defenderKnockedOut());
    assertEquals(0, defender.currentHealth());
  }

  @Test
  void knockedOutAttackerCannotAttack() {
    Creature attacker =
        new CreatureBuilder()
            .catalogId(1L)
            .name("Test")
            .maxHealth(10)
            .currentHealth(0)
            .attack(10)
            .defense(0)
            .moves(List.of(move(5, 100)))
            .build();
    Creature defender = creature(20, 2);
    Move move = move(5, 100);
    TurnBasedCombatEngine engine = new TurnBasedCombatEngine(new FixedBoundRandom(0));

    assertThrows(
        IllegalStateException.class, () -> engine.execute(attacker, defender, move));
  }

  private static Creature creature(int attack, int defense) {
    Move move = move(5, 100);
    return new CreatureBuilder()
        .catalogId(1L)
        .name("Test")
        .maxHealth(20)
        .attack(attack)
        .defense(defense)
        .moves(List.of(move))
        .build();
  }

  private static Move move(int power, int accuracy) {
    return new MoveBuilder().name("Colpo").power(power).accuracy(accuracy).build();
  }
}
