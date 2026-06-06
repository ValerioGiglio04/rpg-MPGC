package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.session.SaveSlotLabels;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import java.util.List;
import java.util.Objects;

public final class LoadGamePresenter {

  private final GameModel gameModel;

  public LoadGamePresenter(GameModel gameModel) {
    this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
  }

  public List<SavedSessionSummary> saves() {
    return gameModel.listSaves();
  }

  public String formatSaveRow(SavedSessionSummary summary) {
    String when = SaveSlotLabels.formatSavedAt(summary.savedAt());
    return Messages.format("load.screen.row", summary.name(), when, summary.gloryPoints());
  }
}
