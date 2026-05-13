package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.BattleEventTranslator;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.BattleLogLine;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;
import it.unicam.cs.mpgc.rpg125664.view.component.BattleArenaView;
import it.unicam.cs.mpgc.rpg125664.view.component.BattleCommandColumnView;
import it.unicam.cs.mpgc.rpg125664.view.component.BattleEndOverlay;
import it.unicam.cs.mpgc.rpg125664.view.component.BattleUiErrorPane;
import it.unicam.cs.mpgc.rpg125664.view.component.GamePanel;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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

  // Costanti di layout per il pannello cronaca battaglia.
  private static final int LOG_PANEL_MAX_WIDTH = 640;
  private static final int LOG_PANEL_MAX_HEIGHT = 440;
  private static final int LOG_PANEL_MIN_HEIGHT = 200;
  private static final int LOG_PANEL_PREF_WIDTH = 560;

  private final GameModel gameModel;
  private final Runnable onBack;

  private final TextFlow logFlow = new TextFlow();
  private final ScrollPane logScroll = new ScrollPane(logFlow);
  private final List<BattleLogLine> battleLog = new ArrayList<>();

  @FXML private Label battleTitleLabel;

  @FXML private Label battleSubtitleLabel;

  @FXML private StackPane arenaHost;

  @FXML private VBox commandHost;

  public BattleController(GameModel gameModel, Runnable onBack) {
    this.gameModel = gameModel;
    this.onBack = onBack;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    configureLogArea();
    if (!gameModel.gameState().currentGym().completed()) {
      gameModel.beginCurrentBattle();
    }
    build();
  }

  private void configureLogArea() {
    logFlow.setLineSpacing(4);
    logFlow.getStyleClass().add("battle-log-flow");
    logFlow
        .maxWidthProperty()
        .bind(
            Bindings.createDoubleBinding(
                () -> {
                  double w = logScroll.getWidth();
                  return w <= 16 ? 520 : w - 12;
                },
                logScroll.widthProperty()));
    logScroll.setFitToWidth(true);
    logScroll.setMinViewportHeight(200);
    logScroll.getStyleClass().add("battle-log-scroll");
    logScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    VBox.setVgrow(logScroll, Priority.ALWAYS);
  }

  private void build() {
    clearHosts();
    try {
      populateBattleUi();
    } catch (RuntimeException error) {
      error.printStackTrace(System.err);
      arenaHost.getChildren().add(BattleUiErrorPane.labelFor(error));
    }
  }

  private void clearHosts() {
    arenaHost.getChildren().clear();
    commandHost.getChildren().clear();
  }

  private void populateBattleUi() {
    prepareBattleIfNeeded();
    GameState state = gameModel.gameState();
    GymRoom gym = state.currentGym();
    applyHeader(gym);
    refreshNotices(state, gym);
    mountArenaAndCommands(state, gym);
  }

  private void prepareBattleIfNeeded() {
    if (!gameModel.gameState().currentGym().completed()) {
      gameModel.prepareCurrentBattle();
    }
  }

  private void applyHeader(GymRoom gym) {
    battleTitleLabel.setText(gym.name());
    battleSubtitleLabel.setText(
        Messages.format("battle.subtitle", gym.boss().name(), gym.boss().pointsReward()));
  }

  private void refreshNotices(GameState state, GymRoom gym) {
    appendCompletedNotice(gym);
    appendTeamWipeNotice(state.player().holder());
    refreshLogArea();
  }

  private void mountArenaAndCommands(GameState state, GymRoom gym) {
    Creature playerCreature = state.player().holder().activeCreature();
    Creature bossCreature = gym.boss().holder().activeCreature();
    arenaHost
        .getChildren()
        .add(BattleArenaView.create(playerCreature, bossCreature, PORTRAIT_FOE, PORTRAIT_PLAYER));
    arenaHost.getChildren().add(centeredTranscriptLayer());
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

  /** Cronaca sopra l'arena, centrata nello spazio tra i due combattenti. */
  private StackPane centeredTranscriptLayer() {
    VBox logPanel = new GamePanel(Messages.get("battle.panel.log"), logScroll);
    logPanel.setMaxWidth(LOG_PANEL_MAX_WIDTH);
    logPanel.setMaxHeight(LOG_PANEL_MAX_HEIGHT);
    logPanel.setMinHeight(LOG_PANEL_MIN_HEIGHT);
    logPanel.setPrefWidth(LOG_PANEL_PREF_WIDTH);
    logPanel.getStyleClass().add("battle-arena-transcript-panel");
    StackPane layer = new StackPane(logPanel);
    layer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    layer.getStyleClass().add("battle-arena-transcript");
    StackPane.setAlignment(logPanel, Pos.CENTER);
    return layer;
  }

  private void appendCompletedNotice(GymRoom gym) {
    if (gym.completed()
        && battleLog.stream()
            .noneMatch(line -> line.text().equals(Messages.get("battle.completed")))) {
      appendLog(Messages.get("battle.completed"));
    }
  }

  private void appendTeamWipeNotice(CreatureHolder playerHolder) {
    if (playerHolder.allKnockedOut()
        && battleLog.stream()
            .noneMatch(line -> line.text().equals(Messages.get("battle.team.wiped")))) {
      appendLog(Messages.get("battle.team.wiped"));
    }
  }

  private void playRound(int moveIndex) {
    List<BattleEvent> events = tryAttack(moveIndex);
    if (events == null) return;
    List<BattleLogLine> lines = BattleEventTranslator.translate(events);
    battleLog.addAll(lines);
    refreshLogArea();
    afterSuccessfulRound(gameModel.gameState(), events);
  }

  private List<BattleEvent> tryAttack(int moveIndex) {
    try {
      return gameModel.attack(moveIndex);
    } catch (RuntimeException error) {
      String message =
          error.getMessage() != null ? error.getMessage() : Messages.get("battle.unknown.error");
      String formattedMessage = Messages.format("battle.invalid.action", message);
      appendLog(formattedMessage);
      build();
      return null;
    }
  }

  private void afterSuccessfulRound(GameState state, List<BattleEvent> events) {
    if (defeatDialogNeeded(state)) {
      build();
      showDefeatDialogAndReturn(state.currentGym().name());
      return;
    }
    if (state.currentGym().completed()) {
      build();
      showVictoryDialogAndReturn(state, extractAcquiredCreatureNames(events));
      return;
    }
    build();
  }

  private static List<String> extractAcquiredCreatureNames(List<BattleEvent> events) {
    for (BattleEvent event : events) {
      if (event instanceof BattleEvent.CreaturesAcquired acquired) {
        return List.copyOf(acquired.creatureNames());
      }
    }
    return List.of();
  }

  private boolean defeatDialogNeeded(GameState state) {
    return state.player().holder().allKnockedOut() && !state.currentGym().completed();
  }

  private void showDefeatDialogAndReturn(String gymName) {
    showEndBattleDialog(
        Messages.get("battle.dialog.defeat.title"),
        Messages.format("battle.dialog.defeat.body", gymName));
  }

  private void showVictoryDialogAndReturn(GameState state, List<String> acquiredCreatureNames) {
    GymRoom gym = state.currentGym();
    String extra =
        acquiredCreatureNames.isEmpty()
            ? ""
            : Messages.format(
                "battle.dialog.victory.body.creatures.extra",
                String.join(", ", acquiredCreatureNames));
    String body = Messages.format("battle.dialog.victory.body", gym.boss().pointsReward(), extra);
    showEndBattleDialog(
        Messages.format("battle.dialog.victory.title", gym.boss().name(), gym.name()), body);
  }

  /**
   * Fine duello: overlay in-scena sopra l'arena (stessi fogli di stile della schermata duello),
   * senza {@link javafx.stage.Stage} o {@link javafx.scene.control.Alert} aggiuntivi.
   */
  private void showEndBattleDialog(String title, String message) {
    Platform.runLater(
        () -> {
          try {
            arenaHost.getChildren().add(BattleEndOverlay.create(title, message, onBack));
          } catch (RuntimeException ex) {
            ex.printStackTrace(System.err);
            onBack.run();
          }
        });
  }

  private void switchCreature(long creatureCatalogId) {
    try {
      gameModel.switchPlayerCreature(creatureCatalogId);
      appendLog(Messages.get("battle.switch.message"));
    } catch (RuntimeException error) {
      appendLog(
          Messages.format(
              "battle.invalid.switch",
              error.getMessage() != null
                  ? error.getMessage()
                  : Messages.get("battle.unknown.error")));
    }
    build();
  }

  private void appendLog(String message) {
    appendLogLine(BattleLogLine.neutral(message));
  }

  private void appendLogLine(BattleLogLine line) {
    battleLog.add(line);
    refreshLogArea();
  }

  private void refreshLogArea() {
    logFlow.getChildren().clear();
    String gap = System.lineSeparator() + System.lineSeparator();
    for (int i = 0; i < battleLog.size(); i++) {
      BattleLogLine line = battleLog.get(i);
      String suffix = i < battleLog.size() - 1 ? gap : "";
      Text chunk = new Text(line.text() + suffix);
      chunk.getStyleClass().add(styleClassFor(line.kind()));
      logFlow.getChildren().add(chunk);
    }
    scrollCronacaToLatest();
  }

  /**
   * Snap dello scroll verticale alle righe piu' recenti dopo che il {@link TextFlow} ha fatto il
   * layout (il wrapping cambia altezza dopo il primo pulse).
   */
  private void scrollCronacaToLatest() {
    if (battleLog.isEmpty()) return;
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
