package it.unicam.cs.mpgc.rpg125664.model.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.ValidatorFactory;
import java.util.List;

public final class CreatureHolderBuilder {

  private List<Creature> creatures;
  private long activeCatalogId;
  private boolean activeCatalogIdSet;

  public CreatureHolderBuilder creatures(List<Creature> creatures) {
    this.creatures = creatures;
    return this;
  }

  public CreatureHolderBuilder activeCatalogId(long activeCatalogId) {
    this.activeCatalogId = activeCatalogId;
    this.activeCatalogIdSet = true;
    return this;
  }

  private long resolveActiveCatalogId() {
    if (activeCatalogIdSet) return activeCatalogId;
    return creatures.stream()
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("Creature holder needs at least one creature"))
        .catalogId();
  }

  public CreatureHolder build() {
    long resolvedActiveCatalogId = resolveActiveCatalogId();
    CreatureHolder holder = new CreatureHolder(creatures, resolvedActiveCatalogId);
    Validator<CreatureHolder> validator = ValidatorFactory.getCreatureHolderValidator();
    validator.validate(holder);
    return holder;
  }
}
