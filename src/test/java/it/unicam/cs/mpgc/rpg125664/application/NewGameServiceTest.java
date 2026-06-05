package it.unicam.cs.mpgc.rpg125664.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import it.unicam.cs.mpgc.rpg125664.model.service.GameStateHolder;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.support.TestCatalogFactory;
import it.unicam.cs.mpgc.rpg125664.support.TestGameStates;
import org.junit.jupiter.api.Test;

class NewGameServiceTest {

  @Test
  void buildInitialStateUsesCatalogSettings() {
    GameCatalog catalog = TestCatalogFactory.minimal();

    GameState state = NewGameService.buildInitialState(catalog);

    assertEquals(TestCatalogFactory.GYM_START, state.currentGymId());
    assertEquals(0, state.player().score().points());
    assertEquals(1, state.player().holder().creatures().size());
    assertEquals(
        TestCatalogFactory.CREATURE_FAST,
        state.player().holder().activeCreature().catalogId());
    assertEquals(2, state.gyms().size());
  }

  @Test
  void startReplacesHolderState() {
    GameCatalog catalog = TestCatalogFactory.minimal();
    GameStateHolder holder = TestGameStates.freshHolder(catalog);
    GameState before = holder.current();
    before.player().score().add(99);
    NewGameService service = new NewGameService(holder, catalog);

    service.start();

    assertNotSame(before, holder.current());
    assertEquals(0, holder.current().player().score().points());
  }
}
