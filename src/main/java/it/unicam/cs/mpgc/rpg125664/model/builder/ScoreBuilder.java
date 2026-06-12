package it.unicam.cs.mpgc.rpg125664.model.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.Score;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;
import it.unicam.cs.mpgc.rpg125664.model.validation.support.ValidatorFactory;

/** Builder per {@link Score} (punti gloria iniziali). */
public final class ScoreBuilder {

  private int points;

  public ScoreBuilder points(int points) {
    this.points = points;
    return this;
  }

  public Score build() {
    Score score = new Score(points);
    Validator<Score> validator = ValidatorFactory.getScoreValidator();
    validator.validate(score);
    return score;
  }
}
