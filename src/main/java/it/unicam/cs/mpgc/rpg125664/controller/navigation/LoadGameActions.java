package it.unicam.cs.mpgc.rpg125664.controller.navigation;

/** Azioni schermata caricamento (load, delete, indietro). */
public interface LoadGameActions {
  void onLoadSelected(long sessionId);

  void onDeleteSelected(long sessionId);

  void onBack();
}
