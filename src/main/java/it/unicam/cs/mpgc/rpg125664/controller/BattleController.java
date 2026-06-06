package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.view.component.BattleArenaView;
import it.unicam.cs.mpgc.rpg125664.view.component.BattleCommandColumnView;
import it.unicam.cs.mpgc.rpg125664.view.component.BattleEndOverlay;
import it.unicam.cs.mpgc.rpg125664.view.component.BattleUiErrorPane;
import it.unicam.cs.mpgc.rpg125664.controller.BattlePresenter;
import it.unicam.cs.mpgc.rpg125664.controller.BattlePresenter.RoundOutcome;
import it.unicam.cs.mpgc.rpg125664.view.support.BattleLogLine;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import it.unicam.cs.mpgc.rpg125664.view.support.UiErrorReporter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public final class BattleController implements Initializable {

  private static final double PORTRAIT_FOE = 168;
  private static final double PORTRAIT_PLAYER = 200;

  private final BattlePresenter presenter;
  private final Runnable onBack;

  @FXML private Label battleTitleLabel;
  @FXML private Label battleSubtitleLabel;
  @FXML private StackPane arenaHost;
  @FXML private StackPane transcriptLayer;
  @FXML private VBox logPanel;
  @FXML private ScrollPane logScroll;
  @FXML private TextFlow logFlow;
  @FXML private VBox commandHost;

  public BattleController(GameModel gameModel, Runnable onBack) {
    this.presenter = new BattlePresenter(gameModel);
    this.onBack = onBack;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    configureLogArea();
    presenter.startBattleIfNeeded();
    build();
  }

  private void configureLogArea() {
    logFlow.setLineSpacing(4);
    logFlow
        .maxWidthProperty()
        .bind(
            Bindings.createDoubleBinding(
                () -> {
                  double w = logScroll.getWidth();
                  return w <= 16 ? 520 : w - 12;
                },
                logScroll.widthProperty()));
    VBox.setVgrow(logScroll, Priority.ALWAYS);
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
        Messages.format("battle.subtitle", gym.boss().name(), gym.boss().pointsReward()));
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
            0, BattleArenaView.create(playerCreature, bossCreature, PORTRAIT_FOE, PORTRAIT_PLAYER));
    transcriptLayer.toFront();
    commandHost
        .getChildren()
        .add(
            BattleCommandColumnView.create(
                playerCreature,
                gym,
                state.player().holder(),
                onBack,
                this::playRound,
                this::switchCreature));
  }

  private void playRound(int moveIndex) {
    RoundOutcome outcome = presenter.playRound(moveIndex);
    refreshLogArea();
    if (!outcome.success()) {
      build();
      return;
    }
    if (outcome.defeat()) {
      build();
      showEndBattleDialog(
          Messages.get("battle.dialog.defeat.title"),
          Messages.format("battle.dialog.defeat.body", presenter.currentGym().name()));
      return;
    }
    if (outcome.gymCompleted()) {
      build();
      GameState state = presenter.state();
      showEndBattleDialog(
          presenter.victoryDialogTitle(state),
          presenter.victoryDialogBody(state, outcome.acquiredCreatureNames()));
      return;
    }
    build();
  }

  private void switchCreature(long creatureCatalogId) {
    presenter.switchCreature(creatureCatalogId);
    refreshLogArea();
    build();
  }

  private void showEndBattleDialog(String title, String message) {
    Platform.runLater(
        () -> {
          try {
            arenaHost.getChildren().add(BattleEndOverlay.create(title, message, onBack));
          } catch (RuntimeException ex) {
            UiErrorReporter.reportActionError("battle end overlay failed", ex);
            onBack.run();
          }
        });
  }

  private void refreshLogArea() {
    logFlow.getChildren().clear();
    String gap = System.lineSeparator() + System.lineSeparator();
    var lines = presenter.logLines();
    for (int i = 0; i < lines.size(); i++) {
      BattleLogLine line = lines.get(i);
      String suffix = i < lines.size() - 1 ? gap : "";
      Text chunk = new Text(line.text() + suffix);
      chunk.getStyleClass().add(styleClassFor(line.kind()));
      logFlow.getChildren().add(chunk);
    }
    scrollCronacaToLatest();
  }

  private void scrollCronacaToLatest() {
    if (presenter.logLines().isEmpty()) {
      return;
    }
    Runnable snapBottom =
        () -> {
          logScroll.applyCss();
          logScroll.layout();
          logScroll.setVvalue(1.0);
        };
    Platform.runLater(snapBottom);
    PauseTransition afterWrap = new PauseTransition(Duration.millis(50));
    afterWrap.setOnFinished(e -> snapBottom.run());
    afterWrap.play();
  }

  private static String styleClassFor(BattleLogLine.Kind kind) {
    return switch (kind) {
      case PLAYER -> "battle-log-player";
      case BOSS -> "battle-log-boss";
      case NEUTRAL -> "battle-log-neutral";
    };
  }
}
