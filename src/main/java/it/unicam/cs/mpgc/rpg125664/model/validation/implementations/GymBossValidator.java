package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.GymBoss;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;

public final class GymBossValidator extends Validator<GymBoss> {

  @Override
  protected String nullMessage() {
    return "GymBoss cannot be null";
  }

  @Override
  protected void validateRules(GymBoss boss) {
    Rules.requireText(boss.name(), "Boss name cannot be blank");
    Rules.requireNonNull(boss.holder(), "Boss needs a holder");
    Rules.requirePositive(boss.pointsReward(), "Reward must be positive");
  }
}
