package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto;

import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.CreaturaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.GiocatoreEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.MossaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.PalestraEntity;
import it.unicam.cs.mpgc.rpg125664.model.catalog.NewGameSettings;
import java.util.List;

public record CatalogSeedBundle(
    List<GiocatoreEntity> giocatori,
    List<CreaturaEntity> creature,
    List<MossaEntity> mosse,
    List<PalestraEntity> palestre,
    NewGameSettings newGameSettings) {}
