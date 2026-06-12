package it.unicam.cs.mpgc.rpg125664.model.service;

/** Eccezione runtime per fallimento cura ({@link HealingError}). */
public final class HealingException extends RuntimeException {

  private final HealingError error;

  public HealingException(HealingError error) {
    super(error.name());
    this.error = error;
  }

  public HealingError error() {
    return error;
  }
}
