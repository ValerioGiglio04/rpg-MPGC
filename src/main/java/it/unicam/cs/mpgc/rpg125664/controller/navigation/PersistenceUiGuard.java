package it.unicam.cs.mpgc.rpg125664.controller.navigation;

import it.unicam.cs.mpgc.rpg125664.model.session.SessionPersistenceException;

/** Esegue azioni di persistenza e mostra errori UI in modo uniforme. */
public final class PersistenceUiGuard {

  private PersistenceUiGuard() {}

  /**
   * @return {@code true} se l'azione è andata a buon fine
   */
  public static boolean run(Runnable action, PersistenceOperation operation) {
    try {
      action.run();
      return true;
    } catch (SessionPersistenceException error) {
      operation.reportError(error);
      return false;
    }
  }
}
