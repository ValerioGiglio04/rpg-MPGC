package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.controller.HubPresenter.TeamRowViewModel;
import it.unicam.cs.mpgc.rpg125664.view.component.CreatureCard;
import it.unicam.cs.mpgc.rpg125664.view.component.GameButton;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

final class HubTeamRowFactory {

  private static final int ROW_SPACING = 8;

  private HubTeamRowFactory() {}

  static VBox create(
      TeamRowViewModel row, CreatureCard card, GameButton healButton, Runnable onSelectCreature) {
    wireSelection(card, row, onSelectCreature);
    wireHealButton(healButton, row);
    VBox teamRow = new VBox(ROW_SPACING, card, healButton);
    teamRow.getStyleClass().add("hub-team-row");
    return teamRow;
  }

  private static void wireSelection(CreatureCard card, TeamRowViewModel row, Runnable onSelect) {
    if (row.active() || row.knockedOut()) {
      return;
    }
    card.setCursor(Cursor.HAND);
    Tooltip.install(card, new Tooltip(Messages.get("hub.team.select.tooltip")));
    card.setOnMouseClicked(event -> onSelect.run());
  }

  private static void wireHealButton(GameButton healButton, TeamRowViewModel row) {
    healButton.getStyleClass().add("switch-button");
    healButton.setDisable(!row.healEnabled());
    Tooltip.install(healButton, new Tooltip(row.healTooltip()));
  }
}
