package it.unicam.cs.mpgc.rpg125664.model.entity;

import it.unicam.cs.mpgc.rpg125664.model.builder.CreatureBuilder;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Entità creatura combattente (statistiche, HP, mosse, skin). */
public final class Creature implements Serializable {

  private final long catalogId;
  private final String name;
  private final String role;
  private final String skinPath;
  private final int maxHealth;
  private final int attack;
  private final int defense;
  private final int speed;
  private final List<Move> moves;
  private int currentHealth;

  public static CreatureBuilder builder() {
    return new CreatureBuilder();
  }

  public Creature(
      long catalogId,
      String name,
      String role,
      String skinPath,
      int maxHealth,
      int currentHealth,
      int attack,
      int defense,
      int speed,
      List<Move> moves) {
    Objects.requireNonNull(moves, "Creature moves cannot be null");
    this.catalogId = catalogId;
    this.name = name;
    this.role = role;
    this.skinPath = skinPath;
    this.maxHealth = maxHealth;
    this.currentHealth = currentHealth;
    this.attack = attack;
    this.defense = defense;
    this.speed = speed;
    this.moves = List.copyOf(moves);
  }

  public long catalogId() {
    return catalogId;
  }

  public String name() {
    return name;
  }

  public String role() {
    return role;
  }

  public String skinPath() {
    return skinPath;
  }

  public int maxHealth() {
    return maxHealth;
  }

  public int currentHealth() {
    return currentHealth;
  }

  public int attack() {
    return attack;
  }

  public int defense() {
    return defense;
  }

  public int speed() {
    return speed;
  }

  public List<Move> moves() {
    return Collections.unmodifiableList(moves);
  }

  public boolean isKnockedOut() {
    return currentHealth == 0;
  }

  public void receiveDamage(int damage) {
    Rules.requireNonNegative(damage, "Damage cannot be negative");
    currentHealth = Math.max(0, currentHealth - damage);
  }

  public void healToFull() {
    currentHealth = maxHealth;
  }
}
