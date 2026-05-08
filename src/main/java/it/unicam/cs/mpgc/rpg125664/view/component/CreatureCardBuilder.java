package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;

public final class CreatureCardBuilder {

  private final Creature creature;
  private boolean active;
  private double portraitSize = 132;
  private String sideStyleClass;

  public CreatureCardBuilder(Creature creature) {
    this.creature = creature;
  }

  public CreatureCardBuilder active(boolean active) {
    this.active = active;
    return this;
  }

  public CreatureCardBuilder portraitSize(double portraitSize) {
    this.portraitSize = portraitSize;
    return this;
  }

  public CreatureCardBuilder sideStyleClass(String sideStyleClass) {
    this.sideStyleClass = sideStyleClass;
    return this;
  }

  public CreatureCard build() {
    return new CreatureCard(creature, active, portraitSize, sideStyleClass);
  }
}
