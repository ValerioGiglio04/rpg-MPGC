package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.session.SaveSlotLabels;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import javafx.scene.control.ListCell;

final class SavedSessionSummaryCell extends ListCell<SavedSessionSummary> {

  @Override
  protected void updateItem(SavedSessionSummary item, boolean empty) {
    super.updateItem(item, empty);
    if (empty || item == null) {
      setText(null);
      return;
    }
    String when = SaveSlotLabels.formatSavedAt(item.savedAt());
    setText(Messages.format("load.screen.row", item.name(), when, item.gloryPoints()));
  }
}
