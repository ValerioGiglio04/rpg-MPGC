package it.unicam.cs.mpgc.rpg125664.model.entity;

import it.unicam.cs.mpgc.rpg125664.model.builder.GymBossBuilder;
import java.io.Serializable;

public final class GymBoss implements Serializable {

  private final String name;
  private final CreatureHolder holder;
  private final int pointsReward;

  public static GymBossBuilder builder() {
    return new GymBossBuilder();
  }

  public GymBoss(String name, CreatureHolder holder, int pointsReward) {
    this.name = name;
    this.holder = holder;
    this.pointsReward = pointsReward;
  }

  public String name() {
    return name;
  }

  public CreatureHolder holder() {
    return holder;
  }

  public int pointsReward() {
    return pointsReward;
  }
}
