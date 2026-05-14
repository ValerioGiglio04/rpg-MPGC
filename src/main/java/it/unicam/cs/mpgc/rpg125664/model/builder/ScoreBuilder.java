package it.unicam.cs.mpgc.rpg125664.model.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.Score;

public final class ScoreBuilder {

  private int points;

  public ScoreBuilder points(int points) {
    this.points = points;
    return this;
  }

  public Score build() {
    return new Score(points);
  }
}
