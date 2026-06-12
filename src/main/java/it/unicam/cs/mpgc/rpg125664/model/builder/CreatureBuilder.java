package it.unicam.cs.mpgc.rpg125664.model.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;
import it.unicam.cs.mpgc.rpg125664.model.validation.support.ValidatorFactory;
import java.util.List;

/** Builder fluente per {@link Creature} con validazione. */
public final class CreatureBuilder {

  private long catalogId;
  private String name;
  private String role = "Creatura equilibrata";
  private String skinPath = "/images/creatures/default.png";
  private int maxHealth;
  private int currentHealth;
  private boolean currentHealthSet;
  private int attack;
  private int defense;
  private int speed = 5;
  private List<Move> moves;

  public CreatureBuilder catalogId(long catalogId) {
    this.catalogId = catalogId;
    return this;
  }

  public CreatureBuilder name(String name) {
    this.name = name;
    return this;
  }

  public CreatureBuilder role(String role) {
    this.role = role;
    return this;
  }

  public CreatureBuilder skinPath(String skinPath) {
    this.skinPath = skinPath;
    return this;
  }

  public CreatureBuilder maxHealth(int maxHealth) {
    this.maxHealth = maxHealth;
    return this;
  }

  public CreatureBuilder currentHealth(int currentHealth) {
    this.currentHealth = currentHealth;
    this.currentHealthSet = true;
    return this;
  }

  public CreatureBuilder attack(int attack) {
    this.attack = attack;
    return this;
  }

  public CreatureBuilder defense(int defense) {
    this.defense = defense;
    return this;
  }

  public CreatureBuilder speed(int speed) {
    this.speed = speed;
    return this;
  }

  public CreatureBuilder moves(List<Move> moves) {
    this.moves = moves;
    return this;
  }

  public Creature build() {
    int effectiveHealth = currentHealthSet ? currentHealth : maxHealth;
    Creature creature = new Creature(
      catalogId,
      name,
      role,
      skinPath,
      maxHealth,
      effectiveHealth,
      attack,
      defense,
      speed,
      moves
    );
    Validator<Creature> validator = ValidatorFactory.getCreatureValidator();
    validator.validate(creature);
    return creature;
  }
}
