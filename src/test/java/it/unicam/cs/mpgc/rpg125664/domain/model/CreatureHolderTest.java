package it.unicam.cs.mpgc.rpg125664.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg125664.model.builder.CreatureBuilder;
import it.unicam.cs.mpgc.rpg125664.model.builder.MoveBuilder;
import it.unicam.cs.mpgc.rpg125664.support.TestCatalogFactory;
import java.util.List;
import org.junit.jupiter.api.Test;

class CreatureHolderTest {

  @Test
  void switchToKnockedOutCreatureThrows() {
    Creature alive = catalogCreature(TestCatalogFactory.CREATURE_FAST, 40);
    Creature ko = catalogCreature(TestCatalogFactory.CREATURE_SLOW, 0);
    CreatureHolder holder =
        CreatureHolder.builder().creatures(List.of(alive, ko)).activeCatalogId(alive.catalogId()).build();

    assertThrows(
        IllegalStateException.class, () -> holder.switchTo(TestCatalogFactory.CREATURE_SLOW));
  }

  @Test
  void allKnockedOutWhenEveryCreatureAtZeroHp() {
    Creature first = catalogCreature(1L, 0);
    Creature second = catalogCreature(2L, 0);
    CreatureHolder holder =
        CreatureHolder.builder().creatures(List.of(first, second)).activeCatalogId(1L).build();

    assertTrue(holder.allKnockedOut());
  }

  @Test
  void healAllToFullPreservingActiveKeepsActiveId() {
    Creature active = catalogCreature(10L, 5);
    Creature backup = catalogCreature(11L, 3);
    CreatureHolder holder =
        CreatureHolder.builder().creatures(List.of(active, backup)).activeCatalogId(11L).build();

    holder.healAllToFullPreservingActive();

    assertEquals(11L, holder.activeCatalogId());
    assertEquals(40, active.currentHealth());
    assertEquals(40, backup.currentHealth());
  }

  @Test
  void canSwitchToAliveCreatureOnly() {
    Creature alive = catalogCreature(1L, 10);
    Creature ko = catalogCreature(2L, 0);
    CreatureHolder holder =
        CreatureHolder.builder().creatures(List.of(alive, ko)).activeCatalogId(1L).build();

    assertTrue(holder.canSwitchTo(1L));
    assertFalse(holder.canSwitchTo(2L));
  }

  private static Creature catalogCreature(long catalogId, int currentHealth) {
    return new CreatureBuilder()
        .catalogId(catalogId)
        .name("C" + catalogId)
        .maxHealth(40)
        .currentHealth(currentHealth)
        .attack(8)
        .defense(2)
        .moves(List.of(new MoveBuilder().name("Colpo").power(5).build()))
        .build();
  }
}
