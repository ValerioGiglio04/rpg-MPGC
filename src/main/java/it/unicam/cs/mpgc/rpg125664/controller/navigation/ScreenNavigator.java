package it.unicam.cs.mpgc.rpg125664.controller.navigation;

import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.session.SessionPersistenceException;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.UiErrorReporter;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.HubActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.LoadGameActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.MainMenuActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.VictoryActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.HubActionsImpl;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.LoadGameActionsImpl;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.MainMenuActionsImpl;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.VictoryActionsImpl;
import it.unicam.cs.mpgc.rpg125664.controller.BattleController;
import it.unicam.cs.mpgc.rpg125664.controller.HubController;
import it.unicam.cs.mpgc.rpg125664.controller.LoadGameController;
import it.unicam.cs.mpgc.rpg125664.controller.MainMenuController;
import it.unicam.cs.mpgc.rpg125664.controller.VictoryController;
import it.unicam.cs.mpgc.rpg125664.controller.LoadGamePresenter;
import it.unicam.cs.mpgc.rpg125664.controller.VictoryPresenter;
import it.unicam.cs.mpgc.rpg125664.view.theme.UiTheme;
import it.unicam.cs.mpgc.rpg125664.view.theme.DuelUiTheme;
import java.util.Objects;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Swappa i figli del {@link StackPane} principale tra root FXML e collega le azioni di
 * menu/hub/vittoria. Tiene {@link MainView} libera dal branching di navigazione.
 */
public final class ScreenNavigator implements ScreenNavigation {

  private static final UiTheme BATTLE_THEME = new DuelUiTheme();

  private final StackPane root;
  private final GameModel gameModel;

  public ScreenNavigator(StackPane root, GameModel gameModel) {
    this.root = Objects.requireNonNull(root, "root");
    this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
  }

  @Override
  public void showMainMenu() {
    setScreen(
        FxmlScreenLoader.load(
            "/fxml/MainMenu.fxml",
            new MainMenuController(mainMenuActions(), gameModel.hasAnySave())));
  }

  @Override
  public void showLoadGame() {
    setScreen(
        FxmlScreenLoader.load(
            "/fxml/LoadGame.fxml",
            new LoadGameController(new LoadGamePresenter(gameModel), loadGameActions())));
  }

  public void showHub() {
    if (gameModel.gameState().allGymsCompleted()) {
      showVictory();
      return;
    }
    setScreen(
        FxmlScreenLoader.load("/fxml/Hub.fxml", new HubController(gameModel, hubActions())));
  }

  @Override
  public void showBattle() {
    if (gameModel.gameState().allGymsCompleted()) {
      showVictory();
      return;
    }
    GymRoom current = gameModel.gameState().currentGym();
    if (!gameModel.canChallengeGym(current)) {
      DialogHelper.showError(
          Messages.get("battle.navigation.title"), Messages.get("battle.navigation.notAllowed"));
      showHub();
      return;
    }
    BattleController controller = new BattleController(gameModel, this::showHub);
    Parent screen = FxmlScreenLoader.load("/fxml/Battle.fxml", controller);
    BATTLE_THEME.applyTo(screen);
    setScreen(screen);
  }

  public void showVictory() {
    Parent screen =
        FxmlScreenLoader.load(
            "/fxml/Victory.fxml",
            new VictoryController(new VictoryPresenter(gameModel), victoryActions()));
    setScreen(screen);
  }

  @Override
  public void startNewGame() {
    gameModel.startNewGame();
    showHub();
  }

  @Override
  public void saveCurrent() {
    try {
      gameModel.saveCurrent();
    } catch (SessionPersistenceException error) {
      UiErrorReporter.reportPersistenceError("persistence.save.failed.title", error);
    }
    showHub();
  }

  @Override
  public void saveAsNew() {
    Optional<String> name =
        DialogHelper.promptText(
            Messages.get("hub.saveAsNew.title"),
            Messages.get("hub.saveAsNew.prompt"),
            Messages.get("hub.saveAsNew.defaultName"));
    if (name.isEmpty()) {
      return;
    }
    try {
      gameModel.saveAsNew(name.get());
    } catch (SessionPersistenceException error) {
      UiErrorReporter.reportPersistenceError("persistence.save.failed.title", error);
    }
    showHub();
  }

  @Override
  public void loadSession(long sessionId) {
    try {
      gameModel.loadSession(sessionId);
      showHub();
    } catch (SessionPersistenceException error) {
      UiErrorReporter.reportPersistenceError("persistence.load.failed.title", error);
      showLoadGame();
    }
  }

  @Override
  public void deleteSession(long sessionId) {
    if (!DialogHelper.confirm(
        Messages.get("load.delete.confirm.title"), Messages.get("load.delete.confirm.body"))) {
      return;
    }
    try {
      gameModel.deleteSession(sessionId);
    } catch (SessionPersistenceException error) {
      UiErrorReporter.reportPersistenceError("persistence.delete.failed.title", error);
    }
    if (gameModel.hasAnySave()) {
      showLoadGame();
    } else {
      showMainMenu();
    }
  }

  private void changeScreen(Parent screen) {
    screen.setManaged(true);
    screen.setVisible(true);
    if (screen instanceof Region region) {
      region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }
    root.applyCss();
    root.layout();
    screen.requestFocus();
  }

  private void setScreen(Parent screen) {
    dismissOpenMenus(root);
    root.getChildren().setAll(screen);
    changeScreen(screen);
  }

  /** Chiude popup MenuButton ancora aperti: altrimenti coprono la schermata successiva. */
  private static void dismissOpenMenus(Node node) {
    if (node instanceof MenuButton menuButton && menuButton.isShowing()) {
      menuButton.hide();
    }
    if (node instanceof Parent parent) {
      for (Node child : parent.getChildrenUnmodifiable()) {
        dismissOpenMenus(child);
      }
    }
  }

  private MainMenuActions mainMenuActions() {
    return new MainMenuActionsImpl(this);
  }

  private LoadGameActions loadGameActions() {
    return new LoadGameActionsImpl(this);
  }

  private HubActions hubActions() {
    return new HubActionsImpl(this);
  }

  private VictoryActions victoryActions() {
    return new VictoryActionsImpl(this);
  }
}
