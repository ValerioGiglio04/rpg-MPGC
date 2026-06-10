package it.unicam.cs.mpgc.rpg125664.controller.navigation.support;

import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.HubActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.LoadGameActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.MainMenuActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.VictoryActions;
import it.unicam.cs.mpgc.rpg125664.controller.BattleController;
import it.unicam.cs.mpgc.rpg125664.controller.HubController;
import it.unicam.cs.mpgc.rpg125664.controller.LoadGameController;
import it.unicam.cs.mpgc.rpg125664.controller.MainMenuController;
import it.unicam.cs.mpgc.rpg125664.controller.VictoryController;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import it.unicam.cs.mpgc.rpg125664.controller.LoadGamePresenter;
import it.unicam.cs.mpgc.rpg125664.controller.VictoryPresenter;
import it.unicam.cs.mpgc.rpg125664.view.theme.UiTheme;
import it.unicam.cs.mpgc.rpg125664.view.theme.DuelUiTheme;
import java.util.Objects;
import javafx.scene.Parent;

/** Costruisce le schermate FXML con i controller e presenter collegati. */
public final class ScreenFactory {

  private static final UiTheme BATTLE_THEME = new DuelUiTheme();

  private final GameModel gameModel;
  private final PortraitAssetResolver portraitAssets;

  public ScreenFactory(GameModel gameModel, PortraitAssetResolver portraitAssets) {
    this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
    this.portraitAssets = Objects.requireNonNull(portraitAssets, "portraitAssets");
  }

  public Parent createMainMenu(MainMenuActions actions) {
    return FxmlScreenLoader.load(
        "/fxml/MainMenu.fxml", new MainMenuController(actions, gameModel.hasAnySave()));
  }

  public Parent createLoadGame(LoadGameActions actions) {
    return FxmlScreenLoader.load(
        "/fxml/LoadGame.fxml", new LoadGameController(new LoadGamePresenter(gameModel), actions));
  }

  public Parent createHub(HubActions actions) {
    return FxmlScreenLoader.load(
        "/fxml/Hub.fxml", new HubController(gameModel, portraitAssets, actions));
  }

  public Parent createBattle(Runnable onBack) {
    BattleController controller = new BattleController(gameModel, portraitAssets, onBack);
    Parent screen = FxmlScreenLoader.load("/fxml/Battle.fxml", controller);
    BATTLE_THEME.applyTo(screen);
    return screen;
  }

  public Parent createVictory(VictoryActions actions) {
    return FxmlScreenLoader.load(
        "/fxml/Victory.fxml", new VictoryController(new VictoryPresenter(gameModel), actions));
  }
}
