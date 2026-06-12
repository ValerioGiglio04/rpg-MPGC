package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;
import java.util.List;

/** Validatore team e creatura attiva in {@link CreatureHolder}. */
public final class CreatureHolderValidator extends Validator<CreatureHolder> {

  @Override
  protected String nullMessage() {
    return "CreatureHolder cannot be null";
  }

  @Override
  protected void validateRules(CreatureHolder holder) {
    List<Creature> creatures = holder.creatures();
    if (creatures.isEmpty()) {
      throw new IllegalArgumentException("Holder needs at least one creature");
    }
    Rules.requirePositiveId(
        holder.activeCatalogId(), "Active creature catalog id must be positive");
    boolean found =
        creatures.stream().anyMatch(creature -> creature.catalogId() == holder.activeCatalogId());
    if (!found) {
      throw new IllegalArgumentException("Active creature is not in the team");
    }
  }
}
