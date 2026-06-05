package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.support;

import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.PalestraEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Calcola i collegamenti tra palestre senza tabella dedicata (catena per {@code ordine}). */
public final class PalestraCollegamentiSupport {

  private PalestraCollegamentiSupport() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Ogni palestra e' collegata alla precedente e alla successiva nell'ordine di progressione (come
   * nel seed: palestra 1 — 2 — … — 5).
   */
  public static Map<Long, List<Long>> linearByOrdine(List<PalestraEntity> palestre) {
    Map<Integer, Long> idByOrdine = new TreeMap<>();
    for (PalestraEntity palestra : palestre) {
      idByOrdine.put(palestra.getOrdine(), palestra.getIdPalestra());
    }
    Map<Long, List<Long>> collegamenti = new HashMap<>();
    for (PalestraEntity palestra : palestre) {
      List<Long> vicine = new ArrayList<>();
      Long precedente = idByOrdine.get(palestra.getOrdine() - 1);
      Long successiva = idByOrdine.get(palestra.getOrdine() + 1);
      if (precedente != null) {
        vicine.add(precedente);
      }
      if (successiva != null) {
        vicine.add(successiva);
      }
      collegamenti.put(palestra.getIdPalestra(), List.copyOf(vicine));
    }
    return collegamenti;
  }
}
