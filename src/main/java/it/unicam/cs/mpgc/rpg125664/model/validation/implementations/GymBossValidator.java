package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymBoss;
import it.unicam.cs.mpgc.rpg125664.model.validation.AbstractDomainValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;

/**
 * Valida un {@link GymBoss}: nome, holder e reward. Si ottiene tramite {@link
 * Validators#getGymBossValidator()}.
 */
public final class GymBossValidator extends AbstractDomainValidator<GymBoss> {

  GymBossValidator() {}

  @Override
  protected String nullMessage() {
    return "GymBoss cannot be null";
  }

  @Override
  protected void validateRules(GymBoss boss) {
    Rules.requireText(boss.name(), "Boss name cannot be blank");
    CreatureHolder holder = boss.holder();
    Rules.requireNonNull(holder, "Boss needs a holder");
    Rules.requirePositive(boss.pointsReward(), "Reward must be positive");
  }
}
