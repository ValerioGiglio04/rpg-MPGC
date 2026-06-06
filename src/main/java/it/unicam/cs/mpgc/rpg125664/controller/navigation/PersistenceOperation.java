package it.unicam.cs.mpgc.rpg125664.controller.navigation;

import it.unicam.cs.mpgc.rpg125664.model.session.SessionPersistenceException;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.UiErrorReporter;

/** Operazioni di persistenza esposte dalla UI con chiave i18n per errori. */
public enum PersistenceOperation {
  SAVE,
  LOAD,
  DELETE;

  private final String failedTitleKey = "persistence." + name().toLowerCase() + ".failed.title";

  public void reportError(SessionPersistenceException error) {
    UiErrorReporter.reportPersistenceError(failedTitleKey, error);
  }
}
