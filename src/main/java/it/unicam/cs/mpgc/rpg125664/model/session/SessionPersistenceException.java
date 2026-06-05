package it.unicam.cs.mpgc.rpg125664.model.session;

/** Errore di persistenza sessione (I/O o dati salvati non validi). */
public final class SessionPersistenceException extends RuntimeException {

  public SessionPersistenceException(String message, Throwable cause) {
    super(message, cause);
  }

  public SessionPersistenceException(String message) {
    super(message);
  }
}
