package it.unicam.cs.mpgc.rpg125664.model.entity;

import it.unicam.cs.mpgc.rpg125664.model.builder.PlayerBuilder;
import java.io.Serializable;

/** Giocatore con nome, team, punteggio e percorso skin. */
public final class Player implements Serializable {

  public static final String DEFAULT_SKIN_PATH = "/images/player/player-skin.png";

  private final String name;
  private final CreatureHolder holder;
  private final Score score;
  private final String skinPath;

  public static PlayerBuilder builder() {
    return new PlayerBuilder();
  }

  public Player(String name, CreatureHolder holder, Score score) {
    this(name, holder, score, DEFAULT_SKIN_PATH);
  }

  public Player(String name, CreatureHolder holder, Score score, String skinPath) {
    this.name = name;
    this.holder = holder;
    this.score = score;
    this.skinPath = sanitizeSkinPath(skinPath);
  }

  public String name() {
    return name;
  }

  public CreatureHolder holder() {
    return holder;
  }

  public Score score() {
    return score;
  }

  public String skinPath() {
    return skinPath;
  }

  private String sanitizeSkinPath(String candidate) {
    if (candidate == null || candidate.isBlank()) {
      return DEFAULT_SKIN_PATH;
    }
    return candidate;
  }
}
