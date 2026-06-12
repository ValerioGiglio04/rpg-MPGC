package it.unicam.cs.mpgc.rpg125664.controller.navigation;

/** Navigazione esposta alla schermata di caricamento. */
public interface LoadGameNavigation {
  void loadSession(long sessionId);

  void deleteSession(long sessionId);

  void showMainMenu();
}
