package it.unicam.cs.mpgc.rpg125664.controller.navigation;

public interface LoadGameActions {

  void onLoadSelected(long sessionId);

  void onDeleteSelected(long sessionId);

  void onBack();
}
