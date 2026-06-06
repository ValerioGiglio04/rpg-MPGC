package it.unicam.cs.mpgc.rpg125664.model.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import it.unicam.cs.mpgc.rpg125664.model.validation.MoveRules;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;
import it.unicam.cs.mpgc.rpg125664.model.validation.support.ValidatorFactory;

public final class MoveBuilder {

  private String name;
  private int power;
  private int accuracy = MoveRules.DEFAULT_ACCURACY;
  private String description = "Mossa base.";

  public MoveBuilder name(String name) {
    this.name = name;
    return this;
  }

  public MoveBuilder power(int power) {
    this.power = power;
    return this;
  }

  public MoveBuilder accuracy(int accuracy) {
    this.accuracy = accuracy;
    return this;
  }

  public MoveBuilder description(String description) {
    this.description = description;
    return this;
  }

  public Move build() {
    Move move = new Move(name, power, accuracy, description);
    Validator<Move> validator = ValidatorFactory.getMoveValidator();
    validator.validate(move);
    return move;
  }
}
