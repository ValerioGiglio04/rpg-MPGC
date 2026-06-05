package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import it.unicam.cs.mpgc.rpg125664.model.validation.AbstractDomainValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import java.util.List;

/**
 * Valida una {@link Creature}: campi identita', statistiche, mosse e limiti di salute corrente. Si
 * ottiene tramite {@link Validators#getCreatureValidator()}; il costruttore e' package-private
 * cosi' la factory e' l'unico modo per arrivarci.
 */
public final class CreatureValidator extends AbstractDomainValidator<Creature> {

  public CreatureValidator() {}

  @Override
  protected String nullMessage() {
    return "Creature cannot be null";
  }

  @Override
  protected void validateRules(Creature creature) {
    validateIdentity(creature);
    validateStats(creature);
    validateMoves(creature);
    validateCurrentHealth(creature);
  }

  private static void validateIdentity(Creature creature) {
    Rules.requirePositiveId(creature.catalogId(), "Creature catalogId must be positive");
    Rules.requireText(creature.name(), "Creature name cannot be blank");
    Rules.requireText(creature.role(), "Creature role cannot be blank");
    Rules.requireText(creature.skinPath(), "Creature skin path cannot be blank");
  }

  private static void validateStats(Creature creature) {
    Rules.requirePositive(creature.maxHealth(), "Creature max health must be positive");
    Rules.requirePositive(creature.attack(), "Creature attack must be positive");
    Rules.requireNonNegative(creature.defense(), "Creature defense cannot be negative");
    Rules.requirePositive(creature.speed(), "Creature speed must be positive");
  }

  private static void validateMoves(Creature creature) {
    List<Move> moves = creature.moves();
    if (moves == null || moves.isEmpty()) {
      throw new IllegalArgumentException("Creature needs at least one move");
    }
  }

  private static void validateCurrentHealth(Creature creature) {
    int currentHealth = creature.currentHealth();
    int maxHealth = creature.maxHealth();
    if (currentHealth < 0 || currentHealth > maxHealth) {
      throw new IllegalArgumentException("Current health must be between 0 and max health");
    }
  }

  public static void validateDamage(int damage) {
    if (damage < 0) {
      throw new IllegalArgumentException("Damage cannot be negative");
    }
  }
}
