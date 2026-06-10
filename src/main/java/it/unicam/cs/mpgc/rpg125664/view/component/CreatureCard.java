package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.view.component.builder.CreatureCardBuilder;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public final class CreatureCard extends VBox {

  public static CreatureCardBuilder builder(
      Creature creature, PortraitAssetResolver portraitAssets) {
    return new CreatureCardBuilder(creature, portraitAssets);
  }

  private Label createStatsLabel(Creature creature) {
    String text =
        Messages.format(
            "creature.stats.line",
            creature.role(),
            creature.attack(),
            creature.defense(),
            creature.speed());
    Label statsLabel = new Label(text);
    statsLabel.getStyleClass().add("muted-label");
    return statsLabel;
  }

  private Label createRoleLabel(Creature creature) {
    Label role = new Label(creature.role());
    role.getStyleClass().add("game-label");
    return role;
  }

  private Label createNameLabel(Creature creature, boolean active) {
    String prefix = active ? Messages.get("creature.active.marker") : "";
    Label name = new Label(Messages.format("creature.card.name", prefix, creature.name()));
    name.getStyleClass().add("panel-title");
    return name;
  }

  private void initializeStyles(boolean active, String sideStyleClass, double portraitSize) {
    setAlignment(Pos.CENTER);
    getStyleClass().add(active ? "game-panel-highlight" : "game-panel");
    if (sideStyleClass != null && !sideStyleClass.isBlank()) {
      getStyleClass().add(sideStyleClass);
    }
    if (portraitSize <= 90) {
      getStyleClass().add("compact-creature-card");
    }
  }

  public CreatureCard(
      Creature creature,
      PortraitAssetResolver portraitAssets,
      boolean active,
      double portraitSize,
      String sideStyleClass) {
    super(8);
    initializeStyles(active, sideStyleClass, portraitSize);
    Label name = createNameLabel(creature, active);
    Label role = createRoleLabel(creature);
    Label stats = createStatsLabel(creature);
    HealthBar healthBar = new HealthBar("", creature.currentHealth(), creature.maxHealth());
    CreaturePortrait portrait = new CreaturePortrait(creature, portraitAssets, portraitSize);
    getChildren().addAll(portrait, name, role, healthBar, stats);
  }
}
