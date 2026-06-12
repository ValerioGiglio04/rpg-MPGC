package it.unicam.cs.mpgc.rpg125664.controller.navigation;

/** Azioni menu principale (nuova partita, load, esci). */
public interface MainMenuActions {
  void onNewGame();

  void onLoadGame();

  void onExit();
}
