package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.Score;
import it.unicam.cs.mpgc.rpg125664.model.validation.AbstractDomainValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validators;

/**
 * Valida uno {@link Score}: totale punti non negativo. Si ottiene tramite {@link
 * Validators#getScoreValidator()}. L'helper {@link #requireNonNegativeDelta(int)} resta statico
 * cosi' i chiamanti possono validare gli input prima di costruire uno {@link Score}.
 */
public final class ScoreValidator extends AbstractDomainValidator<Score> {

  @Override
  protected String nullMessage() {
    return "Score cannot be null";
  }

  @Override
  protected void validateRules(Score score) {
    Rules.requireNonNegative(score.points(), "Points cannot be negative");
  }

  public static void requireNonNegativeDelta(int pointsToAdd) {
    Rules.requireNonNegative(pointsToAdd, "Cannot add negative points");
  }

  public static void requireNonNegativeSpendAmount(int amount) {
    Rules.requireNonNegative(amount, "Spend amount cannot be negative");
  }
}
