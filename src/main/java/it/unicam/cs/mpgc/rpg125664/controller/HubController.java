package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.HubActions;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;
import it.unicam.cs.mpgc.rpg125664.view.component.CreatureCard;
import it.unicam.cs.mpgc.rpg125664.view.component.GameButton;
import it.unicam.cs.mpgc.rpg125664.view.component.HamburgerMenu;
import it.unicam.cs.mpgc.rpg125664.view.component.PlayerPortrait;
import it.unicam.cs.mpgc.rpg125664.view.overworld.OverworldMap;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class HubController implements Initializable {
  private static final int PORTRAIT_SIZE = 80;
  private static final int GYM_ROW_SPACING = 8;
  private final GameModel gameModel;
  private final HubActions actions;

  @FXML private Label subtitleLabel;

  @FXML private StackPane portraitHost;

  @FXML private HamburgerMenu hamburgerMenu;

  @FXML private VBox teamList;

  @FXML private StackPane mapContentHost;

  public HubController(GameModel gameModel, HubActions actions) {
    this.gameModel = gameModel;
    this.actions = actions;
  }

  private void initializeSubtitle(GameState state) {
    String name = state.player().name();
    int points = state.player().score().points();
    String subtitle = Messages.format("hub.subtitle", name, points);
    subtitleLabel.setText(subtitle);
  }

  private void initializePortrait(GameState state) {
    String name = state.player().name();
    String skinPath = state.player().skinPath();
    PlayerPortrait portrait =
        PlayerPortrait.builder().playerName(name).skinPath(skinPath).size(PORTRAIT_SIZE).build();
    portraitHost.getChildren().setAll(portrait);
  }

  private void initializeHamburgerMenu() {
    MenuItem saveItem = new MenuItem(Messages.get("hub.menu.save"));
    saveItem.setOnAction(event -> actions.onSave());
    MenuItem saveAsNewItem = new MenuItem(Messages.get("hub.menu.saveAsNew"));
    saveAsNewItem.setOnAction(event -> actions.onSaveAsNew());
    MenuItem menuItem = new MenuItem(Messages.get("hub.menu.menu"));
    menuItem.setOnAction(
        event -> {
          hamburgerMenu.hide();
          Platform.runLater(actions::onBackToMenu);
        });
    hamburgerMenu.getItems().setAll(saveItem, saveAsNewItem, menuItem);
  }

  private void refreshTeamAndSubtitle() {
    GameState state = gameModel.gameState();
    initializeSubtitle(state);
    initializeTeamList(state);
  }

  private void initializeTeamList(GameState state) {
    teamList.getChildren().clear();
    int spendable = gameModel.spendableGloryForHealing();
    var holder = state.player().holder();
    int creatureCount = holder.creatures().size();
    for (int index = 0; index < creatureCount; index++) {
      Creature creature = holder.creatures().get(index);
      boolean active = holder.isActive(creature);
      long catalogId = creature.catalogId();
      CreatureCard card = CreatureCard.builder(creature).active(active).build();
      wireCreatureCardSelection(card, creature, active, catalogId);
      GameButton healButton = buildHealButton(state, creature, catalogId, spendable);
      teamList.getChildren().add(hubTeamRow(card, healButton));
    }
  }

  private void wireCreatureCardSelection(
      CreatureCard card, Creature creature, boolean active, long creatureCatalogId) {
    if (active || creature.isKnockedOut()) return;
    card.setCursor(Cursor.HAND);
    Tooltip.install(card, new Tooltip(Messages.get("hub.team.select.tooltip")));
    card.setOnMouseClicked(event -> selectActiveCreature(creatureCatalogId));
  }

  private GameButton buildHealButton(
      GameState state, Creature creature, long creatureCatalogId, int spendable) {
    int cost = gameModel.healCostForCreature(creatureCatalogId);
    GameButton healButton = new GameButton(Messages.format("hub.heal.button", cost)).asSecondary();
    healButton.getStyleClass().add("switch-button");
    boolean fullHp = creature.currentHealth() >= creature.maxHealth();
    int playerPoints = state.player().score().points();
    boolean disabled = fullHp || cost > playerPoints || cost > spendable;
    healButton.setDisable(disabled);
    String tooltipText = healTooltip(state, creature, cost, spendable);
    Tooltip.install(healButton, new Tooltip(tooltipText));
    healButton.setOnAction(event -> healCreatureAt(creatureCatalogId));
    return healButton;
  }

  private static VBox hubTeamRow(CreatureCard card, GameButton healButton) {
    VBox row = new VBox(GYM_ROW_SPACING, card, healButton);
    row.getStyleClass().add("hub-team-row");
    return row;
  }

  private static String healTooltip(GameState state, Creature creature, int cost, int spendable) {
    if (creature.currentHealth() >= creature.maxHealth()) {
      return Messages.get("hub.heal.tooltip.fullHp");
    }
    if (cost > state.player().score().points()) {
      return Messages.format("hub.heal.tooltip.noGlory", cost);
    }
    if (cost > spendable) {
      return Messages.format("hub.heal.tooltip.gymFloor", cost, spendable);
    }
    return Messages.format("hub.heal.tooltip.ok", cost);
  }

  private void healCreatureAt(long creatureCatalogId) {
    try {
      gameModel.healPlayerCreature(creatureCatalogId);
      refreshTeamAndSubtitle();
    } catch (RuntimeException ignored) {
      // Disabled button should prevent failures; refresh in case of concurrent state change
      refreshTeamAndSubtitle();
    }
  }

  private void selectActiveCreature(long creatureCatalogId) {
    try {
      gameModel.switchPlayerCreature(creatureCatalogId);
      refreshTeamAndSubtitle();
    } catch (RuntimeException ignored) {
      // KO or invalid index: UI already prevents most cases
    }
  }

  private void initializeOverworldMap() {
    OverworldMap overworldMap = new OverworldMap(gameModel, actions::onStartBattle);
    overworldMap.setMinSize(0, 0);
    overworldMap.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    mapContentHost.getChildren().setAll(overworldMap);
    // MainView schedules requestFocus on the hub root in the same pulse; defer twice so
    // keyboard movement on the overworld runs immediately after hub is shown.
    Platform.runLater(() -> Platform.runLater(overworldMap::requestFocus));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    GameState state = gameModel.gameState();
    initializeSubtitle(state);
    initializePortrait(state);
    initializeTeamList(state);
    initializeHamburgerMenu();
    initializeOverworldMap();
  }
}
