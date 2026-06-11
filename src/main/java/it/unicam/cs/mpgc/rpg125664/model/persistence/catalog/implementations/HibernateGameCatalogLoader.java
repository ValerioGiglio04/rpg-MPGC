package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.implementations;

import it.unicam.cs.mpgc.rpg125664.model.catalog.CatalogIds;
import it.unicam.cs.mpgc.rpg125664.model.catalog.CreatureTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GymTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.NewGameSettings;
import it.unicam.cs.mpgc.rpg125664.model.persistence.GameCatalogLoader;
import it.unicam.cs.mpgc.rpg125664.model.persistence.base.AbstractHibernateAdapter;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.CreaturaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.GiocatoreEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.MossaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.PalestraEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.mapper.CatalogEntityMapper;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.support.CatalogLoadSupport;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.support.PalestraCollegamentiSupport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Costruisce {@link GameCatalog} dalle tabelle catalogo su H2. */
public final class HibernateGameCatalogLoader extends AbstractHibernateAdapter
    implements GameCatalogLoader {

  private final NewGameSettings newGameSettings;

  public HibernateGameCatalogLoader(
      EntityManagerFactory entityManagerFactory, NewGameSettings newGameSettings) {
    super(entityManagerFactory);
    this.newGameSettings = Objects.requireNonNull(newGameSettings, "newGameSettings");
  }

  @Override
  public GameCatalog load() {
    return withEntityManager(this::buildCatalogFromDatabase);
  }

  private GameCatalog buildCatalogFromDatabase(EntityManager entityManager) {
    ensureCatalogWasSeeded(entityManager);

    List<CreaturaEntity> creatureEntities = CatalogLoadSupport.loadCreatureEntities(entityManager);
    List<MossaEntity> moveEntities = CatalogLoadSupport.loadMoveEntities(entityManager);
    List<PalestraEntity> gymEntities = CatalogLoadSupport.loadGymEntities(entityManager);

    Map<Long, String> bossNameByPlayerId = CatalogLoadSupport.loadBossNameByPlayerId(entityManager);
    Map<Long, List<Long>> bossTeamCreatureIdsByPlayerId =
        CatalogLoadSupport.loadBossTeamCreatureIdsByPlayerId(entityManager);
    Map<Long, List<Long>> connectedGymIdsByGymId =
        PalestraCollegamentiSupport.linearByOrdine(gymEntities);

    CatalogLoadSupport.validateCatalogIntegrity(
        creatureEntities, gymEntities, bossTeamCreatureIdsByPlayerId);

    Map<Long, List<MossaEntity>> moveEntitiesByCreatureId =
        CatalogLoadSupport.groupMoveEntitiesByCreatureId(moveEntities);

    return new GameCatalog(
        newGameSettings,
        toCreatureTemplates(creatureEntities, moveEntitiesByCreatureId),
        toGymTemplates(
            gymEntities,
            bossNameByPlayerId,
            bossTeamCreatureIdsByPlayerId,
            connectedGymIdsByGymId));
  }

  private static void ensureCatalogWasSeeded(EntityManager entityManager) {
    if (entityManager.find(GiocatoreEntity.class, CatalogIds.GIOCATORE_UMANO) == null) {
      throw new IllegalStateException(
          "Catalog database is empty: run CatalogDatabaseSeeder before load()");
    }
  }

  private static List<CreatureTemplate> toCreatureTemplates(
      List<CreaturaEntity> creatureEntities,
      Map<Long, List<MossaEntity>> moveEntitiesByCreatureId) {
    return creatureEntities.stream()
        .map(
            creatureEntity ->
                CatalogEntityMapper.toCreature(
                    creatureEntity,
                    moveEntitiesByCreatureId.getOrDefault(
                        creatureEntity.getIdCreatura(), List.of())))
        .toList();
  }

  private static List<GymTemplate> toGymTemplates(
      List<PalestraEntity> gymEntities,
      Map<Long, String> bossNameByPlayerId,
      Map<Long, List<Long>> bossTeamCreatureIdsByPlayerId,
      Map<Long, List<Long>> connectedGymIdsByGymId) {
    return gymEntities.stream()
        .map(
            gymEntity ->
                CatalogEntityMapper.toGym(
                    gymEntity,
                    bossNameByPlayerId,
                    bossTeamCreatureIdsByPlayerId,
                    connectedGymIdsByGymId))
        .toList();
  }
}
