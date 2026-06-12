package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.event.BattleEvent;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.view.support.BattleEventTranslator;
import it.unicam.cs.mpgc.rpg125664.view.support.BattleLogLine;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import it.unicam.cs.mpgc.rpg125664.view.support.UiErrorReporter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Presenter del duello: avvio turni, log eventi ed esito round. */
public final class BattlePresenter {

  private final GameModel gameModel;
  private final List<BattleLogLine> battleLog = new ArrayList<>();

  public BattlePresenter(GameModel gameModel) {
    this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
  }

  private boolean isBattleNeeded() {
    return !gameModel.gameState().currentGym().completed();
  }

  public void startBattleIfNeeded() {
    if (isBattleNeeded()) {
      gameModel.beginCurrentBattle();
    }
  }

  public void prepareBattleIfNeeded() {
    if (isBattleNeeded()) {
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
      logActionFailure("battle round failed", error, "battle.invalid.action");
      return RoundOutcome.failed();
    }
  }

  public void switchCreature(long creatureCatalogId) {
    try {
      gameModel.switchPlayerCreature(creatureCatalogId);
      battleLog.add(BattleLogLine.neutral(Messages.get("battle.switch.message")));
    } catch (RuntimeException error) {
      logActionFailure("battle switch failed", error, "battle.invalid.switch");
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

  private void logActionFailure(String action, RuntimeException error, String messageKey) {
    UiErrorReporter.reportActionError(action, error);
    battleLog.add(BattleLogLine.neutral(Messages.format(messageKey, errorMessage(error))));
  }

  private static String errorMessage(RuntimeException error) {
    return error.getMessage() != null ? error.getMessage() : Messages.get("battle.unknown.error");
  }

  private static boolean defeatNeeded(GameState state) {
    return state.player().holder().allKnockedOut() && !state.currentGym().completed();
  }

  private static boolean isCreaturesAcquiredEvent(BattleEvent event) {
    return event instanceof BattleEvent.CreaturesAcquired;
  }

  private static List<String> extractAcquiredCreatureNames(List<BattleEvent> events) {
    Optional<BattleEvent.CreaturesAcquired> firstCreaturesAcquired =
        events.stream()
            .filter(BattlePresenter::isCreaturesAcquiredEvent)
            .map(BattleEvent.CreaturesAcquired.class::cast)
            .findFirst();
    if (firstCreaturesAcquired.isEmpty()) return List.of();
    return List.copyOf(firstCreaturesAcquired.get().creatureNames());
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
