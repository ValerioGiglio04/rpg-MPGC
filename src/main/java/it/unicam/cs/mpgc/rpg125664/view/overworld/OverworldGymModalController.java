package it.unicam.cs.mpgc.rpg125664.view.overworld;

import it.unicam.cs.mpgc.rpg125664.controller.OverworldPresenter;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.view.component.GameButton;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/** State machine del modale palestra (challenge / blocked / cancel). */
public final class OverworldGymModalController {

  private static final int ACTIONS_SPACING = 12;
  private static final int ACTION_BUTTON_PREF_WIDTH = 140;

  public interface Host {
    int lastRow();

    int lastCol();

    void restorePlayerPosition(int row, int column);

    void redrawMap();

    void ensureUiChromeVisible();

    void requestMapFocus();
  }

  private final OverworldPresenter presenter;
  private final Runnable onStartBattle;
  private final Host host;
  private final Label modalTitle;
  private final HBox modalActions;
  private final StackPane modalLayer;

  private boolean modalOpen;
  private GymRoom pendingGym;

  public OverworldGymModalController(
      OverworldPresenter presenter,
      Runnable onStartBattle,
      Host host,
      Label modalTitle,
      HBox modalActions,
      StackPane modalLayer) {
    this.presenter = presenter;
    this.onStartBattle = onStartBattle;
    this.host = host;
    this.modalTitle = modalTitle;
    this.modalActions = modalActions;
    this.modalLayer = modalLayer;
  }

  boolean isModalOpen() {
    return modalOpen;
  }

  void showChallengeModal(GymRoom gym) {
    pendingGym = gym;
    modalTitle.setText(Messages.format("overworld.modal.challenge.prompt", gym.name()));
    GameButton challengeButton =
        modalButton(Messages.get("overworld.modal.challenge"), this::confirmChallenge);
    GameButton cancelButton =
        modalButton(Messages.get("overworld.modal.cancel"), this::cancelChallenge).asSecondary();
    modalActions.getChildren().setAll(challengeButton, cancelButton.asSecondary());
    openModal();
  }

  void showBlockedModal(String reason) {
    pendingGym = null;
    modalTitle.setText(reason);
    GameButton closeButton =
        modalButton(Messages.get("overworld.modal.close"), this::cancelChallenge);
    modalActions.getChildren().setAll(closeButton.asSecondary());
    openModal();
  }

  void handleModalKey(KeyCode code) {
    switch (code) {
      case ENTER -> handleEnterKey();
      case ESCAPE -> cancelChallenge();
      default -> {}
    }
  }

  private void handleEnterKey() {
    if (pendingGym != null) {
      confirmChallenge();
      return;
    }
    cancelChallenge();
  }

  private void confirmChallenge() {
    if (!modalOpen || pendingGym == null) return;
    GymRoom gym = pendingGym;
    hideModal();
    moveSessionToGymIfNeeded(gym);
    onStartBattle.run();
  }

  private void moveSessionToGymIfNeeded(GymRoom gym) {
    GymRoom currentGym = presenter.gameState().currentGym();
    if (currentGym.id() == gym.id()) return;
    presenter.moveToGym(gym.id());
  }

  private void cancelChallenge() {
    if (!modalOpen) return;
    hideModal();
    host.restorePlayerPosition(host.lastRow(), host.lastCol());
    host.redrawMap();
  }

  private void openModal() {
    modalOpen = true;
    modalLayer.setMouseTransparent(false);
    modalLayer.setVisible(true);
    host.ensureUiChromeVisible();
  }

  private void hideModal() {
    modalOpen = false;
    pendingGym = null;
    modalLayer.setVisible(false);
    modalLayer.setMouseTransparent(true);
    host.ensureUiChromeVisible();
    host.requestMapFocus();
  }

  private GameButton modalButton(String text, Runnable handler) {
    GameButton button = new GameButton(text);
    button.setMaxWidth(Region.USE_PREF_SIZE);
    button.setPrefWidth(ACTION_BUTTON_PREF_WIDTH);
    button.setOnAction(event -> handler.run());
    return button;
  }
}
