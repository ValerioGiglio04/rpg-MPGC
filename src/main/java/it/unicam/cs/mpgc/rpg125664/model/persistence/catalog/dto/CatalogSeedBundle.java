package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto;

import it.unicam.cs.mpgc.rpg125664.model.catalog.NewGameSettings;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.CreaturaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.GiocatoreEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.MossaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.PalestraEntity;
import java.util.List;

/** Bundle entità catalogo e impostazioni nuova partita post-seed. */
public record CatalogSeedBundle(
    List<GiocatoreEntity> giocatori,
    List<CreaturaEntity> creature,
    List<MossaEntity> mosse,
    List<PalestraEntity> palestre,
    NewGameSettings newGameSettings) {}
