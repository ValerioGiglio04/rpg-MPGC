package it.unicam.cs.mpgc.rpg125664.model.validation;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import java.util.List;

/**
 * Valida un {@link CreatureHolder}: team non vuoto e {@code activeCatalogId} che corrisponde a una
 * creatura del team. Si ottiene tramite {@link Validators#getCreatureHolderValidator()}.
 */
public final class CreatureHolderValidator extends AbstractDomainValidator<CreatureHolder> {

  CreatureHolderValidator() {}

  @Override
  protected String nullMessage() {
    return "CreatureHolder cannot be null";
  }

  @Override
  protected void validateRules(CreatureHolder holder) {
    validateTeam(holder);
    validateActiveCatalogId(holder);
  }

  private static void validateTeam(CreatureHolder holder) {
    List<Creature> creatures = holder.creatures();
    if (creatures == null || creatures.isEmpty()) {
      throw new IllegalArgumentException("Holder needs at least one creature");
    }
  }

  private static void validateActiveCatalogId(CreatureHolder holder) {
    long activeCatalogId = holder.activeCatalogId();
    Rules.requirePositiveId(activeCatalogId, "Active creature catalog id must be positive");
    boolean found =
        holder.creatures().stream().anyMatch(creature -> creature.catalogId() == activeCatalogId);
    if (!found) {
      throw new IllegalArgumentException("Active creature is not in the team");
    }
  }

  public static void validateSwitch(List<Creature> creatures, long catalogId) {
    Rules.requirePositiveId(catalogId, "Creature catalog id must be positive");
    Creature creature =
        creatures.stream()
            .filter(c -> c.catalogId() == catalogId)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Creature is not in the team"));
    if (creature.isKnockedOut()) {
      throw new IllegalStateException("Cannot select a knocked-out creature");
    }
  }

  public static boolean canSwitchTo(List<Creature> creatures, long catalogId) {
    return creatures.stream()
        .anyMatch(creature -> creature.catalogId() == catalogId && !creature.isKnockedOut());
  }

  public static void validateNewCreature(Creature creature) {
    if (creature == null) {
      throw new IllegalArgumentException("Cannot add a null creature");
    }
  }
}
