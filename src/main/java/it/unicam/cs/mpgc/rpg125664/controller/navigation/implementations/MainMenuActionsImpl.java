package it.unicam.cs.mpgc.rpg125664.controller.navigation.implementations;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.MainMenuActions;
import it.unicam.cs.mpgc.rpg125664.controller.navigation.MainMenuNavigation;
import java.util.Objects;
import javafx.application.Platform;

public final class MainMenuActionsImpl implements MainMenuActions {

  private final MainMenuNavigation navigation;

  public MainMenuActionsImpl(MainMenuNavigation navigation) {
    this.navigation = Objects.requireNonNull(navigation, "navigation");
  }

  @Override
  public void onNewGame() {
    navigation.startNewGame();
  }

  @Override
  public void onLoadGame() {
    navigation.showLoadGame();
  }

  @Override
  public void onExit() {
    Platform.exit();
  }
}
