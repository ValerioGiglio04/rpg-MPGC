package it.unicam.cs.mpgc.rpg125664.model.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import java.util.List;

public final class CreatureHolderBuilder {

  private List<Creature> creatures;
  private Long activeCatalogId;

  public CreatureHolderBuilder creatures(List<Creature> creatures) {
    this.creatures = creatures;
    return this;
  }

  public CreatureHolderBuilder activeCatalogId(long activeCatalogId) {
    this.activeCatalogId = activeCatalogId;
    return this;
  }

  public CreatureHolder build() {
    long resolvedActiveId =
        activeCatalogId != null ? activeCatalogId : creatures.getFirst().catalogId();
    return new CreatureHolder(creatures, resolvedActiveId);
  }
}
