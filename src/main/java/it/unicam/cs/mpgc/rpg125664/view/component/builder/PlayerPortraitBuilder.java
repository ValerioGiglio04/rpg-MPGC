package it.unicam.cs.mpgc.rpg125664.view.component.builder;

import it.unicam.cs.mpgc.rpg125664.view.component.PlayerPortrait;

/** Builder fluente per {@link PlayerPortrait}. */
public final class PlayerPortraitBuilder {

  private String playerName;
  private String skinPath;
  private double size;

  public PlayerPortraitBuilder() {}

  public PlayerPortraitBuilder playerName(String playerName) {
    this.playerName = playerName;
    return this;
  }

  public PlayerPortraitBuilder skinPath(String skinPath) {
    this.skinPath = skinPath;
    return this;
  }

  public PlayerPortraitBuilder size(double size) {
    this.size = size;
    return this;
  }

  public PlayerPortrait build() {
    return new PlayerPortrait(playerName, skinPath, size);
  }
}
