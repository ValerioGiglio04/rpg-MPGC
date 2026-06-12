package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import java.util.Objects;

/** Input condiviso per abilitazione cura e messaggi tooltip hub. */
public final class HealingCheck {

  private final Creature creature;
  private final GameState state;
  private final int spendableGlory;
  private final int healCost;

  private HealingCheck(Builder builder) {
    this.creature = Objects.requireNonNull(builder.creature, "creature");
    this.state = Objects.requireNonNull(builder.state, "state");
    this.spendableGlory = builder.spendableGlory;
    this.healCost = builder.healCost;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Creature creature() {
    return creature;
  }

  public GameState state() {
    return state;
  }

  public int spendableGlory() {
    return spendableGlory;
  }

  public int healCost() {
    return healCost;
  }

  public boolean canHeal() {
    boolean fullHp = creature.currentHealth() >= creature.maxHealth();
    int playerPoints = state.player().score().points();
    return !fullHp && healCost <= playerPoints && healCost <= spendableGlory;
  }

  public static final class Builder {

    private Creature creature;
    private GameState state;
    private int spendableGlory;
    private int healCost;

    public Builder creature(Creature creature) {
      this.creature = creature;
      return this;
    }

    public Builder state(GameState state) {
      this.state = state;
      return this;
    }

    public Builder spendableGlory(int spendableGlory) {
      this.spendableGlory = spendableGlory;
      return this;
    }

    public Builder healCost(int healCost) {
      this.healCost = healCost;
      return this;
    }

    public HealingCheck build() {
      return new HealingCheck(this);
    }
  }
}
