package it.unicam.cs.mpgc.rpg125664.controller.navigation;

/**
 * Comandi di navigazione esposti alle implementazioni {@code *Actions} in {@code
 * ui.javafx.actions}.
 */
public interface ScreenNavigation {

  void startNewGame();

  void showMainMenu();

  void showLoadGame();

  void showBattle();

  void loadSession(long sessionId);

  void deleteSession(long sessionId);

  void saveCurrent();

  void saveAsNew();
}
