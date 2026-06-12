package it.unicam.cs.mpgc.rpg125664.controller.navigation.implementations;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.HubActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.HubNavigation;
import java.util.Objects;

/** Implementazione {@link HubActions} delegata a {@link HubNavigation}. */
public final class HubActionsImpl implements HubActions {

  private final HubNavigation navigation;

  public HubActionsImpl(HubNavigation navigation) {
    this.navigation = Objects.requireNonNull(navigation, "navigation");
  }

  @Override
  public void onStartBattle() {
    navigation.showBattle();
  }

  @Override
  public void onSave() {
    navigation.saveCurrent();
  }

  @Override
  public void onSaveAsNew() {
    navigation.saveAsNew();
  }

  @Override
  public void onBackToMenu() {
    navigation.showMainMenu();
  }
}
