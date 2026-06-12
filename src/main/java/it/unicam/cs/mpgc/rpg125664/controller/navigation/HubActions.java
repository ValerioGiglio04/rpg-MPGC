package it.unicam.cs.mpgc.rpg125664.controller.navigation;

/** Azioni utente esposte dall'hub (duello, salva, menu). */
public interface HubActions {
  void onStartBattle();

  void onSave();

  void onSaveAsNew();

  void onBackToMenu();
}
