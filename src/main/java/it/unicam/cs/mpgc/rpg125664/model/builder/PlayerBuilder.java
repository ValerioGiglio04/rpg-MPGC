package it.unicam.cs.mpgc.rpg125664.model.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.Player;
import it.unicam.cs.mpgc.rpg125664.model.entity.Score;

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
    if (skinPath == null) {
      return new Player(name, holder, score);
    }
    return new Player(name, holder, score, skinPath);
  }
}
