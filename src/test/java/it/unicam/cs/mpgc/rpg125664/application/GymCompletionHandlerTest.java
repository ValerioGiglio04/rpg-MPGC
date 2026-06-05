package it.unicam.cs.mpgc.rpg125664.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.support.TestCatalogFactory;
import it.unicam.cs.mpgc.rpg125664.support.TestGameStates;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class GymCompletionHandlerTest {

  @Test
  void awardGymCompletionMarksGymAddsScoreAndCreatures() {
    GameState state = TestGameStates.challengeableState(TestCatalogFactory.minimal());
    GymRoom gym = state.currentGym();
    int scoreBefore = state.player().score().points();
    int teamSizeBefore = state.player().holder().creatures().size();
    List<BattleEvent> events = new ArrayList<>();

    new GymCompletionHandler(TestCatalogFactory.minimal()).awardGymCompletion(events, state, gym);

    assertTrue(gym.completed());
    assertEquals(scoreBefore + gym.boss().pointsReward(), state.player().score().points());
    assertEquals(teamSizeBefore + gym.boss().holder().creatures().size(), state.player().holder().creatures().size());
    assertTrue(events.stream().anyMatch(BattleEvent.BossDefeated.class::isInstance));
    assertTrue(events.stream().anyMatch(BattleEvent.CreaturesAcquired.class::isInstance));
  }
}
