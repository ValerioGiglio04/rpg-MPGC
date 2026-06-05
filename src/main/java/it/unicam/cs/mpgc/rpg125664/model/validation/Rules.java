package it.unicam.cs.mpgc.rpg125664.model.validation;

/**
 * Regole comuni di validazione riusate dai validators di pacchetto. Tutte le verifiche falliscono
 * con {@link IllegalArgumentException} contenente il messaggio passato.
 */
public final class Rules {

  private Rules() {}

  public static void requireNonNull(Object value, String message) {
    if (value == null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void requireText(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void requirePositive(int value, String message) {
    if (value <= 0) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void requirePositiveId(long id, String message) {
    if (id <= 0) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void requireNonNegative(int value, String message) {
    if (value < 0) {
      throw new IllegalArgumentException(message);
    }
  }
}
