package it.unicam.cs.mpgc.rpg125664.controller.navigation;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.VictoryActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.ScreenNavigation;
import java.util.Objects;

public final class VictoryActionsImpl implements VictoryActions {

  private final ScreenNavigation navigation;

  public VictoryActionsImpl(ScreenNavigation navigation) {
    this.navigation = Objects.requireNonNull(navigation, "navigation");
  }

  @Override
  public void onNewRun() {
    navigation.startNewGame();
  }

  @Override
  public void onBackToMenu() {
    navigation.showMainMenu();
  }
}
