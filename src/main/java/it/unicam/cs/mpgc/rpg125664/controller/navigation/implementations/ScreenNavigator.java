package it.unicam.cs.mpgc.rpg125664.controller.navigation.implementations;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.HubActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.LoadGameActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.MainMenuActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.ScreenNavigation;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.VictoryActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.support.DialogHelper;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.support.MainView;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.support.PersistenceOperation;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.support.PersistenceUiGuard;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.support.RootScreenStack;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.support.ScreenFactory;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import java.util.Objects;
import java.util.Optional;
import javafx.scene.layout.StackPane;

/**
 * Router di navigazione: applica le policy di flusso e delega costruzione schermate e swap root.
 * Tiene {@link MainView} libera dal branching di navigazione.
 */
public final class ScreenNavigator implements ScreenNavigation {

  private final RootScreenStack screenStack;
  private final ScreenFactory screenFactory;
  private final GameModel gameModel;

  public ScreenNavigator(
    StackPane root,
    GameModel gameModel,
    PortraitAssetResolver portraitAssets
  ) {
    this.screenStack = new RootScreenStack(Objects.requireNonNull(root, "root"));
    this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
    this.screenFactory = new ScreenFactory(
      gameModel,
      Objects.requireNonNull(portraitAssets, "portraitAssets")
    );
  }

  @Override
  public void showMainMenu() {
    screenStack.setScreen(screenFactory.createMainMenu(mainMenuActions()));
  }

  @Override
  public void showLoadGame() {
    screenStack.setScreen(screenFactory.createLoadGame(loadGameActions()));
  }

  public void showHub() {
    if (redirectToVictoryIfCompleted()) {
      return;
    }
    screenStack.setScreen(screenFactory.createHub(hubActions()));
  }

  @Override
  public void showBattle() {
    if (redirectToVictoryIfCompleted()) {
      return;
    }
    GymRoom current = gameModel.currentGym();
    if (!gameModel.canChallengeGym(current)) {
      showBattleEntryDenied();
      return;
    }
    screenStack.setScreen(screenFactory.createBattle(this::showHub));
  }

  private boolean redirectToVictoryIfCompleted() {
    if (!gameModel.allGymsCompleted()) {
      return false;
    }
    showVictory();
    return true;
  }

  private void showBattleEntryDenied() {
    DialogHelper.showError(
      Messages.get("battle.navigation.title"),
      Messages.get("battle.navigation.notAllowed")
    );
    showHub();
  }

  public void showVictory() {
    screenStack.setScreen(screenFactory.createVictory(victoryActions()));
  }

  @Override
  public void startNewGame() {
    gameModel.startNewGame();
    showHub();
  }

  @Override
  public void saveCurrent() {
    PersistenceUiGuard.run(gameModel::saveCurrent, PersistenceOperation.SAVE);
    showHub();
  }

  @Override
  public void saveAsNew() {
    Optional<String> name = DialogHelper.promptText(
      Messages.get("hub.saveAsNew.title"),
      Messages.get("hub.saveAsNew.prompt"),
      Messages.get("hub.saveAsNew.defaultName")
    );
    if (name.isEmpty()) return;
    PersistenceUiGuard.run(() -> gameModel.saveAsNew(name.get()), PersistenceOperation.SAVE);
    showHub();
  }

  @Override
  public void loadSession(long sessionId) {
    boolean loaded = PersistenceUiGuard.run(
      () -> gameModel.loadSession(sessionId),
      PersistenceOperation.LOAD
    );
    if (!loaded) {
      showLoadGame();
      return;
    }
    showHub();
  }

  @Override
  public void deleteSession(long sessionId) {
    if (
      !DialogHelper.confirm(
        Messages.get("load.delete.confirm.title"),
        Messages.get("load.delete.confirm.body")
      )
    ) {
      return;
    }
    PersistenceUiGuard.run(() -> gameModel.deleteSession(sessionId), PersistenceOperation.DELETE);

    if (gameModel.hasAnySave()) {
      showLoadGame();
    } else {
      showMainMenu();
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
