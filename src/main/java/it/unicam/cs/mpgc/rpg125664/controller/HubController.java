package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.HubActions;
import it.unicam.cs.mpgc.rpg125664.view.component.CreatureCard;
import it.unicam.cs.mpgc.rpg125664.view.component.GameButton;
import it.unicam.cs.mpgc.rpg125664.view.component.HamburgerMenu;
import it.unicam.cs.mpgc.rpg125664.view.component.PlayerPortrait;
import it.unicam.cs.mpgc.rpg125664.view.overworld.OverworldMap;
import it.unicam.cs.mpgc.rpg125664.controller.HubPresenter;
import it.unicam.cs.mpgc.rpg125664.controller.HubPresenter.TeamRowViewModel;
import it.unicam.cs.mpgc.rpg125664.controller.OverworldPresenter;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
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

  private final HubPresenter presenter;
  private final OverworldPresenter overworldPresenter;
  private final HubActions actions;

  @FXML private Label subtitleLabel;
  @FXML private StackPane portraitHost;
  @FXML private HamburgerMenu hamburgerMenu;
  @FXML private VBox teamList;
  @FXML private StackPane mapContentHost;

  public HubController(GameModel gameModel, HubActions actions) {
    this.presenter = new HubPresenter(gameModel);
    this.overworldPresenter = new OverworldPresenter(gameModel);
    this.actions = actions;
  }

  private void initializePortrait(GameState state) {
    PlayerPortrait portrait =
        PlayerPortrait.builder()
            .playerName(state.player().name())
            .skinPath(state.player().skinPath())
            .size(PORTRAIT_SIZE)
            .build();
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
    subtitleLabel.setText(presenter.subtitle());
    initializeTeamList();
  }

  private void initializeTeamList() {
    teamList.getChildren().clear();
    int spendable = presenter.spendableGlory();
    var holder = presenter.state().player().holder();
    for (int index = 0; index < holder.creatures().size(); index++) {
      var creature = holder.creatures().get(index);
      TeamRowViewModel row = presenter.teamRow(creature, holder.isActive(creature), spendable);
      CreatureCard card = CreatureCard.builder(creature).active(row.active()).build();
      wireCreatureCardSelection(card, row);
      GameButton healButton = buildHealButton(row);
      teamList.getChildren().add(hubTeamRow(card, healButton));
    }
  }

  private void wireCreatureCardSelection(CreatureCard card, TeamRowViewModel row) {
    if (row.active() || row.knockedOut()) {
      return;
    }
    card.setCursor(Cursor.HAND);
    Tooltip.install(card, new Tooltip(Messages.get("hub.team.select.tooltip")));
    card.setOnMouseClicked(
        event -> {
          presenter.selectCreature(row.catalogId());
          refreshTeamAndSubtitle();
        });
  }

  private GameButton buildHealButton(TeamRowViewModel row) {
    GameButton healButton =
        new GameButton(Messages.format("hub.heal.button", row.healCost())).asSecondary();
    healButton.getStyleClass().add("switch-button");
    healButton.setDisable(!row.healEnabled());
    Tooltip.install(healButton, new Tooltip(row.healTooltip()));
    healButton.setOnAction(
        event -> {
          presenter.healCreature(row.catalogId());
          refreshTeamAndSubtitle();
        });
    return healButton;
  }

  private static VBox hubTeamRow(CreatureCard card, GameButton healButton) {
    VBox row = new VBox(GYM_ROW_SPACING, card, healButton);
    row.getStyleClass().add("hub-team-row");
    return row;
  }

  private void initializeOverworldMap() {
    OverworldMap overworldMap = new OverworldMap(overworldPresenter, actions::onStartBattle);
    overworldMap.setMinSize(0, 0);
    overworldMap.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    mapContentHost.getChildren().setAll(overworldMap);
    Platform.runLater(() -> Platform.runLater(overworldMap::requestFocus));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    GameState state = presenter.state();
    subtitleLabel.setText(presenter.subtitle());
    initializePortrait(state);
    initializeTeamList();
    initializeHamburgerMenu();
    initializeOverworldMap();
  }
}
