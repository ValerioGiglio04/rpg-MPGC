package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.BattleEventTranslator;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.BattleLogLine;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.UiErrorReporter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BattlePresenter {

  private final GameModel gameModel;
  private final List<BattleLogLine> battleLog = new ArrayList<>();

  public BattlePresenter(GameModel gameModel) {
    this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
  }

  public void startBattleIfNeeded() {
    if (!gameModel.gameState().currentGym().completed()) {
      gameModel.beginCurrentBattle();
    }
  }

  public void prepareBattleIfNeeded() {
    if (!gameModel.gameState().currentGym().completed()) {
      gameModel.prepareCurrentBattle();
    }
  }

  public GameState state() {
    return gameModel.gameState();
  }

  public GymRoom currentGym() {
    return gameModel.gameState().currentGym();
  }

  public List<BattleLogLine> logLines() {
    return List.copyOf(battleLog);
  }

  public RoundOutcome playRound(int moveIndex) {
    try {
      List<BattleEvent> events = gameModel.attack(moveIndex);
      List<BattleLogLine> lines = BattleEventTranslator.translate(events);
      battleLog.addAll(lines);
      GameState state = gameModel.gameState();
      return new RoundOutcome(
          true,
          defeatNeeded(state),
          state.currentGym().completed(),
          extractAcquiredCreatureNames(events),
          null);
    } catch (RuntimeException error) {
      UiErrorReporter.reportActionError("battle round failed", error);
      String message =
          error.getMessage() != null ? error.getMessage() : Messages.get("battle.unknown.error");
      battleLog.add(BattleLogLine.neutral(Messages.format("battle.invalid.action", message)));
      return RoundOutcome.failed();
    }
  }

  public void switchCreature(long creatureCatalogId) {
    try {
      gameModel.switchPlayerCreature(creatureCatalogId);
      battleLog.add(BattleLogLine.neutral(Messages.get("battle.switch.message")));
    } catch (RuntimeException error) {
      UiErrorReporter.reportActionError("battle switch failed", error);
      battleLog.add(
          BattleLogLine.neutral(
              Messages.format(
                  "battle.invalid.switch",
                  error.getMessage() != null
                      ? error.getMessage()
                      : Messages.get("battle.unknown.error"))));
    }
  }

  public void appendNoticeIfMissing(String message) {
    if (battleLog.stream().noneMatch(line -> line.text().equals(message))) {
      battleLog.add(BattleLogLine.neutral(message));
    }
  }

  public String victoryDialogBody(GameState state, List<String> acquiredNames) {
    GymRoom gym = state.currentGym();
    String extra =
        acquiredNames.isEmpty()
            ? ""
            : Messages.format(
                "battle.dialog.victory.body.creatures.extra", String.join(", ", acquiredNames));
    return Messages.format("battle.dialog.victory.body", gym.boss().pointsReward(), extra);
  }

  public String victoryDialogTitle(GameState state) {
    GymRoom gym = state.currentGym();
    return Messages.format("battle.dialog.victory.title", gym.boss().name(), gym.name());
  }

  private static boolean defeatNeeded(GameState state) {
    return state.player().holder().allKnockedOut() && !state.currentGym().completed();
  }

  private static List<String> extractAcquiredCreatureNames(List<BattleEvent> events) {
    for (BattleEvent event : events) {
      if (event instanceof BattleEvent.CreaturesAcquired acquired) {
        return List.copyOf(acquired.creatureNames());
      }
    }
    return List.of();
  }

  public record RoundOutcome(
      boolean success,
      boolean defeat,
      boolean gymCompleted,
      List<String> acquiredCreatureNames,
      String errorMessage) {

    static RoundOutcome failed() {
      return new RoundOutcome(false, false, false, List.of(), "failed");
    }
  }
}
