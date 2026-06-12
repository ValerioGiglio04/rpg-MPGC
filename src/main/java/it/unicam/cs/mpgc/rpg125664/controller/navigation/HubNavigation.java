package it.unicam.cs.mpgc.rpg125664.controller.navigation;

/** Navigazione esposta all'hub overworld. */
public interface HubNavigation {
  void showBattle();

  void saveCurrent();

  void saveAsNew();

  void showMainMenu();
}
