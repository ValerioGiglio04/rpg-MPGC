package it.unicam.cs.mpgc.rpg125664.model.catalog;

import java.util.List;

/** Blueprint di catalogo per un {@link it.unicam.cs.mpgc.rpg125664.model.entity.GymBoss}. */
public record BossTemplate(String name, int pointsReward, List<Long> creatureIds) {
  public BossTemplate {
    creatureIds = List.copyOf(creatureIds);
  }
}
