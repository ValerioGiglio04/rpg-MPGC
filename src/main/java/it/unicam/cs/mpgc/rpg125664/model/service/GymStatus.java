package it.unicam.cs.mpgc.rpg125664.model.service;

/**
 * Singola sorgente di verita' per lo stato di una palestra rispetto al giocatore. Le schermate UI
 * (overworld, mappa palestra, selezione palestra) traducono questo enum in label localizzate e
 * classi CSS.
 */
public enum GymStatus {
  COMPLETED,
  CURRENT,
  AVAILABLE,
  NEEDS_POINTS,
  UNREACHABLE
}
