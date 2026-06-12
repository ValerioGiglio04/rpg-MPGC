package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import java.util.Objects;

/** Dimensioni e creature per il layout arena duello. */
public final class ArenaLayoutSpec {

  private final Creature playerCreature;
  private final Creature bossCreature;
  private final PortraitAssetResolver portraitAssets;
  private final double foePortraitSize;
  private final double playerPortraitSize;

  private ArenaLayoutSpec(Builder builder) {
    this.playerCreature = Objects.requireNonNull(builder.playerCreature, "playerCreature");
    this.bossCreature = Objects.requireNonNull(builder.bossCreature, "bossCreature");
    this.portraitAssets = Objects.requireNonNull(builder.portraitAssets, "portraitAssets");
    this.foePortraitSize = builder.foePortraitSize;
    this.playerPortraitSize = builder.playerPortraitSize;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Creature playerCreature() {
    return playerCreature;
  }

  public Creature bossCreature() {
    return bossCreature;
  }

  public PortraitAssetResolver portraitAssets() {
    return portraitAssets;
  }

  public double foePortraitSize() {
    return foePortraitSize;
  }

  public double playerPortraitSize() {
    return playerPortraitSize;
  }

  public static final class Builder {

    private Creature playerCreature;
    private Creature bossCreature;
    private PortraitAssetResolver portraitAssets;
    private double foePortraitSize;
    private double playerPortraitSize;

    public Builder playerCreature(Creature playerCreature) {
      this.playerCreature = playerCreature;
      return this;
    }

    public Builder bossCreature(Creature bossCreature) {
      this.bossCreature = bossCreature;
      return this;
    }

    public Builder portraitAssets(PortraitAssetResolver portraitAssets) {
      this.portraitAssets = portraitAssets;
      return this;
    }

    public Builder foePortraitSize(double foePortraitSize) {
      this.foePortraitSize = foePortraitSize;
      return this;
    }

    public Builder playerPortraitSize(double playerPortraitSize) {
      this.playerPortraitSize = playerPortraitSize;
      return this;
    }

    public ArenaLayoutSpec build() {
      return new ArenaLayoutSpec(this);
    }
  }
}
