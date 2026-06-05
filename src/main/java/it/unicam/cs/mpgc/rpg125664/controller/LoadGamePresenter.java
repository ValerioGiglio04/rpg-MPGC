package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class LoadGamePresenter {

  private static final DateTimeFormatter SAVED_AT_FORMAT =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withLocale(Locale.ITALY);

  private final GameModel gameModel;

  public LoadGamePresenter(GameModel gameModel) {
    this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
  }

  public List<SavedSessionSummary> saves() {
    return gameModel.listSaves();
  }

  public String formatSaveRow(SavedSessionSummary summary) {
    String when = SAVED_AT_FORMAT.format(summary.savedAt().atZone(ZoneId.systemDefault()));
    return Messages.format("load.screen.row", summary.name(), when, summary.gloryPoints());
  }
}
