package it.unicam.cs.mpgc.rpg125664.model.validation;

import it.unicam.cs.mpgc.rpg125664.model.entity.Move;

/**
 * Valida una {@link Move}: nome, potenza, limiti di accuracy, descrizione. Si ottiene tramite
 * {@link Validators#getMoveValidator()}. La variante a 4 argomenti {@link #validate(String, int,
 * int, String)} resta statica perche' il compact constructor di {@link Move} deve validare i
 * componenti raw prima che il record stesso esista.
 */
public final class MoveValidator extends AbstractDomainValidator<Move> {

  MoveValidator() {}

  @Override
  protected String nullMessage() {
    return "Move cannot be null";
  }

  @Override
  protected void validateRules(Move move) {
    validate(move.name(), move.power(), move.accuracy(), move.description());
  }

  public static void validate(String name, int power, int accuracy, String description) {
    Rules.requireText(name, "Move name cannot be blank");
    Rules.requirePositive(power, "Move power must be positive");
    requireAccuracy(accuracy);
    Rules.requireText(description, "Move description cannot be blank");
  }

  private static void requireAccuracy(int accuracy) {
    if (accuracy < MoveRules.MIN_ACCURACY || accuracy > MoveRules.MAX_ACCURACY) {
      throw new IllegalArgumentException(
          "Move accuracy must be between "
              + MoveRules.MIN_ACCURACY
              + " and "
              + MoveRules.MAX_ACCURACY);
    }
  }
}
