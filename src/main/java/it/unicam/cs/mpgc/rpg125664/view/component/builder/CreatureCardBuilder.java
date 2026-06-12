package it.unicam.cs.mpgc.rpg125664.view.component.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.view.component.CreatureCard;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import java.util.Objects;

/** Builder configurabile per {@link CreatureCard} (attiva, dimensioni). */
public final class CreatureCardBuilder {

  private final Creature creature;
  private final PortraitAssetResolver portraitAssets;
  private boolean active;
  private double portraitSize = 132;
  private String sideStyleClass;

  public CreatureCardBuilder(Creature creature, PortraitAssetResolver portraitAssets) {
    this.creature = creature;
    this.portraitAssets = Objects.requireNonNull(portraitAssets, "portraitAssets");
  }

  public CreatureCardBuilder active(boolean active) {
    this.active = active;
    return this;
  }

  public CreatureCardBuilder portraitSize(double portraitSize) {
    this.portraitSize = portraitSize;
    return this;
  }

  public CreatureCardBuilder sideStyleClass(String sideStyleClass) {
    this.sideStyleClass = sideStyleClass;
    return this;
  }

  public CreatureCard build() {
    return new CreatureCard(creature, portraitAssets, active, portraitSize, sideStyleClass);
  }
}
