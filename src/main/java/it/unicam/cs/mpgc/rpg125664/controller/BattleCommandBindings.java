package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/** Binding FXML colonna comandi duello con stato e callback azioni. */
public final class BattleCommandBindings {

  private final Creature playerCreature;
  private final GymRoom gym;
  private final CreatureHolder holder;
  private final PortraitAssetResolver portraitAssets;
  private final Runnable onBack;
  private final IntConsumer onMoveSelected;
  private final LongConsumer onSwitchCreature;

  private BattleCommandBindings(Builder builder) {
    this.playerCreature = Objects.requireNonNull(builder.playerCreature, "playerCreature");
    this.gym = Objects.requireNonNull(builder.gym, "gym");
    this.holder = Objects.requireNonNull(builder.holder, "holder");
    this.portraitAssets = Objects.requireNonNull(builder.portraitAssets, "portraitAssets");
    this.onBack = Objects.requireNonNull(builder.onBack, "onBack");
    this.onMoveSelected = Objects.requireNonNull(builder.onMoveSelected, "onMoveSelected");
    this.onSwitchCreature = Objects.requireNonNull(builder.onSwitchCreature, "onSwitchCreature");
  }

  public static Builder builder() {
    return new Builder();
  }

  public Creature playerCreature() {
    return playerCreature;
  }

  public GymRoom gym() {
    return gym;
  }

  public CreatureHolder holder() {
    return holder;
  }

  public PortraitAssetResolver portraitAssets() {
    return portraitAssets;
  }

  public Runnable onBack() {
    return onBack;
  }

  public IntConsumer onMoveSelected() {
    return onMoveSelected;
  }

  public LongConsumer onSwitchCreature() {
    return onSwitchCreature;
  }

  public static final class Builder {

    private Creature playerCreature;
    private GymRoom gym;
    private CreatureHolder holder;
    private PortraitAssetResolver portraitAssets;
    private Runnable onBack;
    private IntConsumer onMoveSelected;
    private LongConsumer onSwitchCreature;

    public Builder playerCreature(Creature playerCreature) {
      this.playerCreature = playerCreature;
      return this;
    }

    public Builder gym(GymRoom gym) {
      this.gym = gym;
      return this;
    }

    public Builder holder(CreatureHolder holder) {
      this.holder = holder;
      return this;
    }

    public Builder portraitAssets(PortraitAssetResolver portraitAssets) {
      this.portraitAssets = portraitAssets;
      return this;
    }

    public Builder onBack(Runnable onBack) {
      this.onBack = onBack;
      return this;
    }

    public Builder onMoveSelected(IntConsumer onMoveSelected) {
      this.onMoveSelected = onMoveSelected;
      return this;
    }

    public Builder onSwitchCreature(LongConsumer onSwitchCreature) {
      this.onSwitchCreature = onSwitchCreature;
      return this;
    }

    public BattleCommandBindings build() {
      return new BattleCommandBindings(this);
    }
  }
}
