package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.controller.BattlePresenter.RoundOutcome;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.view.component.ArenaLayoutSpec;
import it.unicam.cs.mpgc.rpg125664.view.component.BattleArenaView;
import it.unicam.cs.mpgc.rpg125664.view.component.BattleCommandColumnView;
import it.unicam.cs.mpgc.rpg125664.view.component.BattleEndOverlay;
import it.unicam.cs.mpgc.rpg125664.view.component.BattleUiErrorPane;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import it.unicam.cs.mpgc.rpg125664.view.support.BattleLogRenderer;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import it.unicam.cs.mpgc.rpg125664.view.support.UiErrorReporter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

/** Controller FXML della schermata duello (arena, comandi, log). */
public final class BattleController implements Initializable {

  private static final double PORTRAIT_FOE = 168;
  private static final double PORTRAIT_PLAYER = 200;

  private final BattlePresenter presenter;
  private final PortraitAssetResolver portraitAssets;
  private final Runnable onBack;

  @FXML
  private Label battleTitleLabel;

  @FXML
  private Label battleSubtitleLabel;

  @FXML
  private StackPane arenaHost;

  @FXML
  private StackPane transcriptLayer;

  @FXML
  private VBox logPanel;

  @FXML
  private ScrollPane logScroll;

  @FXML
  private TextFlow logFlow;

  @FXML
  private VBox commandHost;

  public BattleController(
    GameModel gameModel,
    PortraitAssetResolver portraitAssets,
    Runnable onBack
  ) {
    this.presenter = new BattlePresenter(gameModel);
    this.portraitAssets = portraitAssets;
    this.onBack = onBack;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    BattleLogRenderer.bindLogWidth(logFlow, logScroll);
    VBox.setVgrow(logScroll, Priority.ALWAYS);
    presenter.startBattleIfNeeded();
    build();
  }

  private void build() {
    clearHosts();
    try {
      populateBattleUi();
    } catch (RuntimeException error) {
      UiErrorReporter.reportActionError("battle ui build failed", error);
      arenaHost.getChildren().add(BattleUiErrorPane.labelFor(error));
    }
  }

  private void clearHosts() {
    arenaHost.getChildren().removeIf(node -> node != transcriptLayer);
    commandHost.getChildren().clear();
  }

  private void populateBattleUi() {
    presenter.prepareBattleIfNeeded();
    GameState state = presenter.state();
    GymRoom gym = presenter.currentGym();
    applyHeader(gym);
    refreshNotices(state, gym);
    mountArenaAndCommands(state, gym);
  }

  private void applyHeader(GymRoom gym) {
    battleTitleLabel.setText(gym.name());
    battleSubtitleLabel.setText(
      Messages.format("battle.subtitle", gym.boss().name(), gym.boss().pointsReward())
    );
  }

  private void refreshNotices(GameState state, GymRoom gym) {
    if (gym.completed()) {
      presenter.appendNoticeIfMissing(Messages.get("battle.completed"));
    }
    CreatureHolder playerHolder = state.player().holder();
    if (playerHolder.allKnockedOut()) {
      presenter.appendNoticeIfMissing(Messages.get("battle.team.wiped"));
    }
    refreshLogArea();
  }

  private void mountArenaAndCommands(GameState state, GymRoom gym) {
    Creature playerCreature = state.player().holder().activeCreature();
    Creature bossCreature = gym.boss().holder().activeCreature();
    arenaHost
      .getChildren()
      .add(
        0,
        BattleArenaView.create(
          ArenaLayoutSpec.builder()
            .playerCreature(playerCreature)
            .bossCreature(bossCreature)
            .portraitAssets(portraitAssets)
            .foePortraitSize(PORTRAIT_FOE)
            .playerPortraitSize(PORTRAIT_PLAYER)
            .build()
        )
      );
    transcriptLayer.toFront();
    commandHost
      .getChildren()
      .add(
        BattleCommandColumnView.create(
          BattleCommandBindings.builder()
            .playerCreature(playerCreature)
            .gym(gym)
            .holder(state.player().holder())
            .portraitAssets(portraitAssets)
            .onBack(onBack)
            .onMoveSelected(this::playRound)
            .onSwitchCreature(this::switchCreature)
            .build()
        )
      );
  }

  private void playRound(int moveIndex) {
    RoundOutcome outcome = presenter.playRound(moveIndex);
    refreshLogArea();
    handleRoundOutcome(outcome);
  }

  private void handleRoundOutcome(RoundOutcome outcome) {
    if (!outcome.success()) {
      build();
      return;
    }
    if (outcome.defeat()) {
      build();
      showDefeatDialog();
      return;
    }
    if (outcome.gymCompleted()) {
      build();
      showVictoryDialog(outcome);
      return;
    }
    build();
  }

  private void showDefeatDialog() {
    showEndBattleDialog(
      Messages.get("battle.dialog.defeat.title"),
      Messages.format("battle.dialog.defeat.body", presenter.currentGym().name())
    );
  }

  private void showVictoryDialog(RoundOutcome outcome) {
    GameState state = presenter.state();
    showEndBattleDialog(
      presenter.victoryDialogTitle(state),
      presenter.victoryDialogBody(state, outcome.acquiredCreatureNames())
    );
  }

  private void switchCreature(long creatureCatalogId) {
    presenter.switchCreature(creatureCatalogId);
    refreshLogArea();
    build();
  }

  private void showEndBattleDialog(String title, String message) {
    Platform.runLater(() -> {
      try {
        arenaHost.getChildren().add(BattleEndOverlay.create(title, message, onBack));
      } catch (RuntimeException ex) {
        UiErrorReporter.reportActionError("battle end overlay failed", ex);
        onBack.run();
      }
    });
  }

  private void refreshLogArea() {
    BattleLogRenderer.render(logFlow, logScroll, presenter.logLines());
  }
}
