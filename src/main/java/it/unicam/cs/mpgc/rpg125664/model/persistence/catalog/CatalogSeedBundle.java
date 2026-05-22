package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog;

import it.unicam.cs.mpgc.rpg125664.model.catalog.NewGameSettings;
import java.util.List;

public record CatalogSeedBundle(
    List<GiocatoreEntity> giocatori,
    List<CreaturaEntity> creature,
    List<MossaEntity> mosse,
    List<PalestraEntity> palestre,
    NewGameSettings newGameSettings) {}
