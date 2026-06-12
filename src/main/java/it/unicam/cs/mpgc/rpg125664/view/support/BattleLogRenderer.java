package it.unicam.cs.mpgc.rpg125664.view.support;

import java.util.List;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

/** Renderizza le righe di cronaca battaglia su un {@link TextFlow} con auto-scroll. */
public final class BattleLogRenderer {

  private static final double SCROLL_DELAY_MS = 50;
  private static final double MIN_SCROLL_WIDTH = 520;
  private static final double SCROLL_WIDTH_PADDING = 12;
  private static final double MIN_SCROLL_PANE_WIDTH = 16;

  private BattleLogRenderer() {}

  public static void render(TextFlow logFlow, ScrollPane logScroll, List<BattleLogLine> lines) {
    logFlow.getChildren().clear();
    String gap = System.lineSeparator() + System.lineSeparator();
    for (int i = 0; i < lines.size(); i++) {
      BattleLogLine line = lines.get(i);
      String suffix = i < lines.size() - 1 ? gap : "";
      Text chunk = new Text(line.text() + suffix);
      chunk.getStyleClass().add(styleClassFor(line.kind()));
      logFlow.getChildren().add(chunk);
    }
    scrollToLatest(logScroll, lines.isEmpty());
  }

  public static void bindLogWidth(TextFlow logFlow, ScrollPane logScroll) {
    logFlow.setLineSpacing(4);
    logFlow.maxWidthProperty().bind(
      Bindings.createDoubleBinding(() -> {
        double width = logScroll.getWidth();
        return width <= MIN_SCROLL_PANE_WIDTH ? MIN_SCROLL_WIDTH : width - SCROLL_WIDTH_PADDING;
      }, logScroll.widthProperty())
    );
  }

  private static void snapBottom(ScrollPane logScroll) {
    logScroll.applyCss();
    logScroll.layout();
    logScroll.setVvalue(1.0);
  }

  private static void scrollToLatest(ScrollPane logScroll, boolean empty) {
    if (empty) return;

    Platform.runLater(() -> snapBottom(logScroll));
    PauseTransition afterWrap = new PauseTransition(Duration.millis(SCROLL_DELAY_MS));
    afterWrap.setOnFinished(e -> snapBottom(logScroll));
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
