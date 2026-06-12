package it.unicam.cs.mpgc.rpg125664.controller.navigation.implementations;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.VictoryActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.VictoryNavigation;
import java.util.Objects;

/** Implementazione {@link VictoryActions} delegata a {@link VictoryNavigation}. */
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
