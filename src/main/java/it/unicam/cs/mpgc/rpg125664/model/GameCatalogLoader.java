package it.unicam.cs.mpgc.rpg125664.model;

import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;

/**
 * Carica i dati statici di gioco (creature, palestre, default per nuova partita). Le
 * implementazioni leggono da Hibernate/H2 o da altra sorgente senza accoppiare il dominio ai
 * dettagli di I/O.
 */
@FunctionalInterface
public interface GameCatalogLoader {

  GameCatalog load();
}
