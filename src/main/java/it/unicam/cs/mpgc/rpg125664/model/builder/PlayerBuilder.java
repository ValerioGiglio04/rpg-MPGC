package it.unicam.cs.mpgc.rpg125664.model.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.Player;
import it.unicam.cs.mpgc.rpg125664.model.entity.Score;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;
import it.unicam.cs.mpgc.rpg125664.model.validation.support.ValidatorFactory;

public final class PlayerBuilder {

  private String name;
  private CreatureHolder holder;
  private Score score;
  private String skinPath;

  public PlayerBuilder name(String name) {
    this.name = name;
    return this;
  }

  public PlayerBuilder holder(CreatureHolder holder) {
    this.holder = holder;
    return this;
  }

  public PlayerBuilder score(Score score) {
    this.score = score;
    return this;
  }

  public PlayerBuilder skinPath(String skinPath) {
    this.skinPath = skinPath;
    return this;
  }

  public Player build() {
    Player player =
        skinPath == null
            ? new Player(name, holder, score)
            : new Player(name, holder, score, skinPath);
    Validator<Player> validator = ValidatorFactory.getPlayerValidator();
    validator.validate(player);
    return player;
  }
}
