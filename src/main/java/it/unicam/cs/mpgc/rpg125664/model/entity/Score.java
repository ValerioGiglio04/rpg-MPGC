package it.unicam.cs.mpgc.rpg125664.model.entity;

import it.unicam.cs.mpgc.rpg125664.model.builder.ScoreBuilder;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;
import it.unicam.cs.mpgc.rpg125664.model.validation.support.ValidatorFactory;
import java.io.Serializable;

/** Punteggio gloria spendibile e accumulabile del giocatore. */
public final class Score implements Serializable {

  private int points;
  private final Validator<Score> scoreValidator;

  public static ScoreBuilder builder() {
    return new ScoreBuilder();
  }

  public Score() {
    this(0);
  }

  public Score(int points) {
    this.points = points;
    this.scoreValidator = ValidatorFactory.getScoreValidator();
  }

  public int points() {
    return points;
  }

  public void add(int pointsToAdd) {
    Rules.requireNonNegative(pointsToAdd, "Cannot add negative points");
    this.points += pointsToAdd;
    scoreValidator.validate(this);
  }

  /** Spende punti gloria. Fallisce se {@code amount} eccede il totale corrente. */
  public void spend(int amount) {
    Rules.requireNonNegative(amount, "Spend amount cannot be negative");
    if (amount > this.points) {
      throw new IllegalArgumentException("Cannot spend more points than available");
    }
    this.points -= amount;
    scoreValidator.validate(this);
  }
}
