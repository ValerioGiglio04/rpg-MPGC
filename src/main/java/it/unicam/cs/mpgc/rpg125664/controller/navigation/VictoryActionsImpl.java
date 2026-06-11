package it.unicam.cs.mpgc.rpg125664.controller.navigation;

import java.util.Objects;

public final class VictoryActionsImpl implements VictoryActions {

  private final VictoryNavigation navigation;

  public VictoryActionsImpl(VictoryNavigation navigation) {
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
