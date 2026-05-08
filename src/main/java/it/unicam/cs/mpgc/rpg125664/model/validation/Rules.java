package it.unicam.cs.mpgc.rpg125664.model.validation;

/**
 * Regole comuni di validazione riusate dai validators di pacchetto. Tutte le verifiche falliscono
 * con {@link IllegalArgumentException} contenente il messaggio passato.
 */
final class Rules {

  private Rules() {}

  static void requireNonNull(Object value, String message) {
    if (value == null) {
      throw new IllegalArgumentException(message);
    }
  }

  static void requireText(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(message);
    }
  }

  static void requirePositive(int value, String message) {
    if (value <= 0) {
      throw new IllegalArgumentException(message);
    }
  }

  static void requirePositiveId(long id, String message) {
    if (id <= 0) {
      throw new IllegalArgumentException(message);
    }
  }

  static void requireNonNegative(int value, String message) {
    if (value < 0) {
      throw new IllegalArgumentException(message);
    }
  }
}
