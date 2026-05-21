package it.unicam.cs.mpgc.rpg125664.model.session;

import java.time.Instant;
import java.util.Objects;

/** Metadati di una partita salvata per elenco UI (senza caricare tutto il JSON). */
public record SavedSessionSummary(long id, String name, Instant savedAt, int gloryPoints) {

  public SavedSessionSummary {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(savedAt, "savedAt");
  }
}
