package it.unicam.cs.mpgc.rpg125664.model.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymBoss;

public final class GymBossBuilder {

  private String name;
  private CreatureHolder holder;
  private int pointsReward;

  public GymBossBuilder name(String name) {
    this.name = name;
    return this;
  }

  public GymBossBuilder holder(CreatureHolder holder) {
    this.holder = holder;
    return this;
  }

  public GymBossBuilder pointsReward(int pointsReward) {
    this.pointsReward = pointsReward;
    return this;
  }

  public GymBoss build() {
    return new GymBoss(name, holder, pointsReward);
  }
}
