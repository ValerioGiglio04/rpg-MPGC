package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;

public final class CreatureValidator extends Validator<Creature> {

  @Override
  protected String nullMessage() {
    return "Creature cannot be null";
  }

  @Override
  protected void validateRules(Creature creature) {
    Rules.requirePositiveId(creature.catalogId(), "Creature catalogId must be positive");
    Rules.requireText(creature.name(), "Creature name cannot be blank");
    Rules.requireText(creature.role(), "Creature role cannot be blank");
    Rules.requireText(creature.skinPath(), "Creature skin path cannot be blank");
    Rules.requirePositive(creature.maxHealth(), "Creature max health must be positive");
    Rules.requirePositive(creature.attack(), "Creature attack must be positive");
    Rules.requireNonNegative(creature.defense(), "Creature defense cannot be negative");
    Rules.requirePositive(creature.speed(), "Creature speed must be positive");
    if (creature.moves() == null || creature.moves().isEmpty()) {
      throw new IllegalArgumentException("Creature needs at least one move");
    }
    if (creature.currentHealth() < 0 || creature.currentHealth() > creature.maxHealth()) {
      throw new IllegalArgumentException("Current health must be between 0 and max health");
    }
  }
}
