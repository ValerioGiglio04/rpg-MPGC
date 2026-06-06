package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.Score;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;

public final class ScoreValidator extends Validator<Score> {

  @Override
  protected String nullMessage() {
    return "Score cannot be null";
  }

  @Override
  protected void validateRules(Score score) {
    Rules.requireNonNegative(score.points(), "Points cannot be negative");
  }
}
