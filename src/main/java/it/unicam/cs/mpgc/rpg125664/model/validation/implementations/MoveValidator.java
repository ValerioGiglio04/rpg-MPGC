package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import it.unicam.cs.mpgc.rpg125664.model.validation.MoveRules;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;

/** Validatore {@link Move} (nome, potenza, precisione, descrizione). */
public final class MoveValidator extends Validator<Move> {

  @Override
  protected String nullMessage() {
    return "Move cannot be null";
  }

  @Override
  protected void validateRules(Move move) {
    Rules.requireText(move.name(), "Move name cannot be blank");
    Rules.requirePositive(move.power(), "Move power must be positive");
    requireAccuracy(move.accuracy());
    Rules.requireText(move.description(), "Move description cannot be blank");
  }

  private static void requireAccuracy(int accuracy) {
    if (accuracy < MoveRules.MIN_ACCURACY || accuracy > MoveRules.MAX_ACCURACY) {
      throw new IllegalArgumentException(
        "Move accuracy must be between " + MoveRules.MIN_ACCURACY + " and " + MoveRules.MAX_ACCURACY
      );
    }
  }
}
