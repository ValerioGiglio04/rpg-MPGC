package it.unicam.cs.mpgc.rpg125664.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.support.TestCatalogFactory;
import it.unicam.cs.mpgc.rpg125664.support.TestGameStates;
import org.junit.jupiter.api.Test;

class HealingServiceTest {

  private final HealingService healing = new HealingService();

  @Test
  void healCostForMissingHpUsesMinimumOne() {
    assertEquals(0, healing.healCostForMissingHp(0));
    assertEquals(1, healing.healCostForMissingHp(1));
    assertEquals(5, healing.healCostForMissingHp(5));
  }

  @Test
  void spendableGloryReservesRequiredPointsForChallengeableGyms() {
    GameState state = TestGameStates.challengeableState(TestCatalogFactory.minimal());
    state.player().score().add(20);

    int spendable = healing.spendableGlory(state);

    assertEquals(10, spendable);
  }

  @Test
  void healCreatureDeductsGloryAndRestoresHp() {
    var catalog = TestCatalogFactory.minimal();
    GameState state = TestGameStates.stateWithDamagedPlayer(catalog, 10);
    int before = state.player().score().points();

    healing.healCreature(state, TestCatalogFactory.CREATURE_FAST);

    assertEquals(before - 30, state.player().score().points());
    assertEquals(40, state.player().holder().activeCreature().currentHealth());
  }

  @Test
  void healFullHpCreatureThrows() {
    GameState state = TestGameStates.challengeableState(TestCatalogFactory.minimal());

    assertThrows(
        HealingException.class,
        () -> healing.healCreature(state, TestCatalogFactory.CREATURE_FAST));
  }

  @Test
  void healWhenCostExceedsSpendableThrows() {
    var catalog = TestCatalogFactory.minimal();
    GameState state = TestGameStates.stateWithDamagedPlayer(catalog, 10);
    state.player().score().spend(state.player().score().points() - 11);

    assertThrows(
        HealingException.class,
        () -> healing.healCreature(state, TestCatalogFactory.CREATURE_FAST));
  }
}
