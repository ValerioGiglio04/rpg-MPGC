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

  public CreatureHolder build() {
    long resolvedActiveId =
        activeCatalogIdSet ? activeCatalogId : creatures.getFirst().catalogId();
    CreatureHolder holder = new CreatureHolder(creatures, resolvedActiveId);
    Validator<CreatureHolder> validator = ValidatorFactory.getCreatureHolderValidator();
    validator.validate(holder);
    return holder;
  }
}
