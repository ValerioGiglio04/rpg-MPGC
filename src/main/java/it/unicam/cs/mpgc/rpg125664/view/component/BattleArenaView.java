package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Layout nemico vs giocatore dentro l'host dell'arena di battaglia. */
public final class BattleArenaView {

  // Costanti di layout per le colonne nemico/alleato dell'arena.
  private static final int FOE_COLUMN_SPACING = 6;
  private static final int ALLY_COLUMN_SPACING = 8;
  private static final double FOE_TOP_INSET = 8.0;
  private static final double FOE_RIGHT_INSET = 16.0;
  private static final double ALLY_BOTTOM_INSET = 8.0;
  private static final double ALLY_LEFT_INSET = 12.0;

  private BattleArenaView() {}

  public static StackPane create(ArenaLayoutSpec spec) {
    StackPane arenaRoot = new StackPane();
    arenaRoot.getChildren().addAll(backdrop(), anchoredLayer(spec));
    return arenaRoot;
  }

  private static Region backdrop() {
    Region backdrop = new Region();
    backdrop.getStyleClass().add("battle-arena-backdrop");
    backdrop.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    return backdrop;
  }

  private static AnchorPane anchoredLayer(ArenaLayoutSpec spec) {
    AnchorPane layer = new AnchorPane();
    layer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    VBox foeColumn = foeColumn(spec.bossCreature(), spec.portraitAssets(), spec.foePortraitSize());
    VBox allyColumn =
        allyColumn(spec.playerCreature(), spec.portraitAssets(), spec.playerPortraitSize());
    layer.getChildren().addAll(foeColumn, allyColumn);
    AnchorPane.setTopAnchor(foeColumn, FOE_TOP_INSET);
    AnchorPane.setRightAnchor(foeColumn, FOE_RIGHT_INSET);
    AnchorPane.setBottomAnchor(allyColumn, ALLY_BOTTOM_INSET);
    AnchorPane.setLeftAnchor(allyColumn, ALLY_LEFT_INSET);
    return layer;
  }

  private static VBox foeColumn(
      Creature bossCreature, PortraitAssetResolver portraitAssets, double foePortraitSize) {
    VBox foeColumn = new VBox(FOE_COLUMN_SPACING);
    foeColumn.setAlignment(Pos.TOP_RIGHT);
    Label foeName = new Label(bossCreature.name());
    foeName.getStyleClass().add("battle-arena-foe-name");
    Label foeRole = new Label(bossCreature.role());
    foeRole.getStyleClass().add("muted-label");
    HealthBar foeHp = new HealthBar("", bossCreature.currentHealth(), bossCreature.maxHealth());
    CreaturePortrait foePortrait =
        new CreaturePortrait(bossCreature, portraitAssets, foePortraitSize);
    foeColumn.getChildren().addAll(foeName, foeRole, foeHp, foePortrait);
    return foeColumn;
  }

  private static VBox allyColumn(
      Creature playerCreature, PortraitAssetResolver portraitAssets, double playerPortraitSize) {
    VBox allyColumn = new VBox(ALLY_COLUMN_SPACING);
    allyColumn.setAlignment(Pos.BOTTOM_LEFT);
    Label allyName = new Label(playerCreature.name());
    allyName.getStyleClass().add("battle-arena-ally-name");
    CreaturePortrait allyPortrait =
        new CreaturePortrait(playerCreature, portraitAssets, playerPortraitSize);
    HealthBar allyHp =
        new HealthBar("", playerCreature.currentHealth(), playerCreature.maxHealth());
    Label allyStats = statsLabel(playerCreature);
    allyStats.getStyleClass().add("muted-label");
    allyColumn.getChildren().addAll(allyName, allyPortrait, allyHp, allyStats);
    return allyColumn;
  }

  private static Label statsLabel(Creature creature) {
    String text =
        Messages.format(
            "creature.stats.line",
            creature.role(),
            creature.attack(),
            creature.defense(),
            creature.speed());
    return new Label(text);
  }
}
