package it.unicam.cs.mpgc.rpg125664.model.entity;

import it.unicam.cs.mpgc.rpg125664.model.builder.MoveBuilder;
import it.unicam.cs.mpgc.rpg125664.model.validation.MoveValidator;
import java.io.Serializable;

public record Move(String name, int power, int accuracy, String description)
    implements Serializable {

  public static MoveBuilder builder() {
    return new MoveBuilder();
  }

  public Move {
    MoveValidator.validate(name, power, accuracy, description);
  }
}
