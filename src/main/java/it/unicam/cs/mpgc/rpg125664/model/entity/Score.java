package it.unicam.cs.mpgc.rpg125664.model.entity;

import it.unicam.cs.mpgc.rpg125664.model.builder.ScoreBuilder;
import it.unicam.cs.mpgc.rpg125664.model.validation.ScoreValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validators;
import java.io.Serializable;

public final class Score implements Serializable {

  private int points;

  public static ScoreBuilder builder() {
    return new ScoreBuilder();
  }

  public Score() {
    this(0);
  }

  public Score(int points) {
    this.points = points;
    Validators.getScoreValidator().validate(this);
  }

  public int points() {
    return points;
  }

  public void add(int pointsToAd) {
    ScoreValidator.requireNonNegativeDelta(pointsToAd);
    this.points += pointsToAd;
  }

  /** Spende punti gloria. Fallisce se {@code amount} eccede il totale corrente. */
  public void spend(int amount) {
    ScoreValidator.requireNonNegativeSpendAmount(amount);
    if (amount > this.points) {
      throw new IllegalArgumentException("Cannot spend more points than available");
    }
    this.points -= amount;
    Validators.getScoreValidator().validate(this);
  }
}
