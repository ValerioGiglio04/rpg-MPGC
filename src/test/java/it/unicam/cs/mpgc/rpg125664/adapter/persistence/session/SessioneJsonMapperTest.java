package it.unicam.cs.mpgc.rpg125664.model.persistence.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.support.TestCatalogFactory;
import it.unicam.cs.mpgc.rpg125664.support.TestGameStates;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import org.junit.jupiter.api.Test;

class SessioneJsonMapperTest {

  @Test
  void roundTripPreservesProgress() {
    GameCatalog catalog = TestCatalogFactory.minimal();
    SessioneJsonMapper mapper = new SessioneJsonMapper(catalog);
    GameState original = TestGameStates.stateWithDamagedPlayer(catalog, 12);
    OverworldPosition position = new OverworldPosition(3, 4);

    UltimaSessioneSalvataDto dto = mapper.toDto(original, position);
    GameState restored = mapper.fromDto(dto);
    OverworldPosition restoredPos = mapper.mapPositionFromDto(dto);

    assertEquals(original.player().score().points(), restored.player().score().points());
    assertEquals(
        original.player().holder().activeCatalogId(),
        restored.player().holder().activeCatalogId());
    assertEquals(original.currentGymId(), restored.currentGymId());
    assertEquals(3, restoredPos.row());
    assertEquals(4, restoredPos.column());

    Creature originalCreature = original.player().holder().creatures().getFirst();
    Creature restoredCreature = restored.player().holder().creatures().getFirst();
    assertEquals(originalCreature.catalogId(), restoredCreature.catalogId());
    assertEquals(12, restoredCreature.currentHealth());

    assertNotNull(dto.getPalestreCompletate());
    assertEquals(
        original.gyms().stream().filter(g -> g.id() == TestCatalogFactory.GYM_START).findFirst().orElseThrow().completed(),
        restored.gyms().stream().filter(g -> g.id() == TestCatalogFactory.GYM_START).findFirst().orElseThrow().completed());
  }
}
