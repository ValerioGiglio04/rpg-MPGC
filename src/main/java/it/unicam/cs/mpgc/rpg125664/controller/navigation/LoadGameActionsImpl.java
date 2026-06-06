package it.unicam.cs.mpgc.rpg125664.controller.navigation;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.LoadGameActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.ScreenNavigation;
import java.util.Objects;

public final class LoadGameActionsImpl implements LoadGameActions {

  private final ScreenNavigation navigation;

  public LoadGameActionsImpl(ScreenNavigation navigation) {
    this.navigation = Objects.requireNonNull(navigation, "navigation");
  }

  @Override
  public void onLoadSelected(long sessionId) {
    navigation.loadSession(sessionId);
  }

  @Override
  public void onDeleteSelected(long sessionId) {
    navigation.deleteSession(sessionId);
  }

  @Override
  public void onBack() {
    navigation.showMainMenu();
  }
}
