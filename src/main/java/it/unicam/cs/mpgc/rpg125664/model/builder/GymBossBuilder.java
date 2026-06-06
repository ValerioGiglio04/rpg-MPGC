package it.unicam.cs.mpgc.rpg125664.model.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymBoss;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.ValidatorFactory;

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
    GymBoss boss = new GymBoss(name, holder, pointsReward);
    Validator<GymBoss> validator = ValidatorFactory.getGymBossValidator();
    validator.validate(boss);
    return boss;
  }
}
