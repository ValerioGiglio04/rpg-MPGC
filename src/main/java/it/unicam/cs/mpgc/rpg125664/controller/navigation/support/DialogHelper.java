package it.unicam.cs.mpgc.rpg125664.controller.navigation.support;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

/** Utility dialoghi JavaFX (errore, conferma, input testo). */
public final class DialogHelper {

  private DialogHelper() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static void showError(String title, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static boolean confirm(String title, String message) {
    Alert confirm = new Alert(AlertType.CONFIRMATION);
    confirm.setTitle(title);
    confirm.setHeaderText(null);
    confirm.setContentText(message);
    Optional<ButtonType> answer = confirm.showAndWait();
    return answer.isPresent() && answer.get() == ButtonType.OK;
  }

  public static Optional<String> promptText(String title, String prompt, String defaultValue) {
    TextInputDialog dialog = new TextInputDialog(defaultValue);
    dialog.setTitle(title);
    dialog.setHeaderText(null);
    dialog.setContentText(prompt);
    return dialog
      .showAndWait()
      .map(String::trim)
      .filter(s -> !s.isEmpty());
  }
}
