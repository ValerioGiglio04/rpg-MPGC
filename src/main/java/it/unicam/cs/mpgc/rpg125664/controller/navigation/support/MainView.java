package it.unicam.cs.mpgc.rpg125664.controller.navigation.support;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.ScreenNavigator;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

/** Layout root: shell FXML con tema/CSS; {@link ScreenNavigator} per gli swap di schermata. */
public final class MainView {

  private final StackPane root;
  private final ScreenNavigator navigator;

  public MainView(GameModel gameModel, PortraitAssetResolver portraitAssets) {
    this.root = (StackPane) FxmlScreenLoader.load("/fxml/MainShell.fxml", null);
    this.navigator = new ScreenNavigator(root, gameModel, portraitAssets);
    navigator.showMainMenu();
  }

  public Parent root() {
    return root;
  }
}
