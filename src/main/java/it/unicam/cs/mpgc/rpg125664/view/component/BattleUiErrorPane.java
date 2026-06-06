package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import javafx.scene.control.Label;

/** Label visibile all'utente quando la schermata di battaglia fallisce a costruirsi. */
public final class BattleUiErrorPane {

  private BattleUiErrorPane() {}

  public static Label labelFor(Throwable error) {
    String intro = resolveIntro();
    String detail =
        error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName();
    String text = Messages.format("battle.ui.error.block", intro, detail);
    Label problem = new Label(text);
    problem.setWrapText(true);
    problem.getStyleClass().add("game-label");
    return problem;
  }

  private static String resolveIntro() {
    try {
      return Messages.get("battle.ui.build.error");
    } catch (RuntimeException ignored) {
      return "battle.ui.build.error";
    }
  }
}
