package it.unicam.cs.mpgc.rpg125664.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg125664.model.service.NewGameService;
import it.unicam.cs.mpgc.rpg125664.support.TestCatalogFactory;
import it.unicam.cs.mpgc.rpg125664.support.TestGameStates;
import org.junit.jupiter.api.Test;

class GameStateTest {

  @Test
  void moveToConnectedGym() {
    var catalog = TestCatalogFactory.minimal();
    GameState state = NewGameService.buildInitialState(catalog);

    state.moveTo(TestCatalogFactory.GYM_NEXT);

    assertEquals(TestCatalogFactory.GYM_NEXT, state.currentGymId());
  }

  @Test
  void moveToDisconnectedGymThrows() {
    var catalog = TestCatalogFactory.minimal();
    GameState state = NewGameService.buildInitialState(catalog);

    assertThrows(IllegalArgumentException.class, () -> state.moveTo(999L));
  }

  @Test
  void canChallengeWhenReachableAndEnoughPoints() {
    GameState state = TestGameStates.challengeableState(TestCatalogFactory.minimal());
    GymRoom gym = state.currentGym();

    assertTrue(state.isGymReachable(gym));
    assertTrue(state.hasReachedRequiredPoints(gym));
    assertTrue(state.canChallengeGym(gym));
  }

  @Test
  void cannotChallengeCompletedGym() {
    GameState state = TestGameStates.challengeableState(TestCatalogFactory.minimal());
    state.currentGym().markCompleted();

    assertFalse(state.canChallengeGym(state.currentGym()));
  }

  @Test
  void cannotChallengeWhenPointsInsufficient() {
    GameState state = NewGameService.buildInitialState(TestCatalogFactory.minimal());
    GymRoom next = state.gyms().stream().filter(g -> g.id() == TestCatalogFactory.GYM_NEXT).findFirst().orElseThrow();
    state.moveTo(TestCatalogFactory.GYM_NEXT);

    assertFalse(state.hasReachedRequiredPoints(next));
    assertFalse(state.canChallengeGym(next));
  }

  @Test
  void allGymsCompletedWhenEveryGymMarked() {
    GameState state = TestGameStates.challengeableState(TestCatalogFactory.minimal());
    state.gyms().forEach(GymRoom::markCompleted);

    assertTrue(state.allGymsCompleted());
  }

}
