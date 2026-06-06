package it.unicam.cs.mpgc.rpg125664.model.session;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Etichette e formattazione condivise per gli slot in {@code sessioni_salvate}. */
public final class SaveSlotLabels {
  private static final String DATE_STRING_PATTERN = "dd/MM/yyyy HH:mm";
  private static final DateTimeFormatter SAVED_AT =
      DateTimeFormatter.ofPattern(DATE_STRING_PATTERN).withLocale(Locale.ITALY);

  private SaveSlotLabels() {}

  public static String defaultSaveName(Instant instant) {
    return "Partita " + formatSavedAt(instant);
  }

  public static String formatSavedAt(Instant instant) {
    return SAVED_AT.format(instant.atZone(ZoneId.systemDefault()));
  }
}
