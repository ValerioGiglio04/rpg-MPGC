package it.unicam.cs.mpgc.rpg125664.view.support;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.support.DialogHelper;
import it.unicam.cs.mpgc.rpg125664.model.session.SessionPersistenceException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class UiErrorReporter {

  private static final Logger LOGGER = Logger.getLogger(UiErrorReporter.class.getName());

  private UiErrorReporter() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static void reportPersistenceError(String titleKey, Throwable error) {
    LOGGER.log(Level.WARNING, titleKey, error);
    String detail =
        error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName();
    if (error instanceof SessionPersistenceException spe && spe.getCause() != null) {
      detail = spe.getCause().getMessage() != null ? spe.getCause().getMessage() : detail;
    }
    DialogHelper.showError(Messages.get(titleKey), detail);
  }

  public static void reportActionError(String logContext, Throwable error) {
    LOGGER.log(Level.WARNING, logContext, error);
  }
}
