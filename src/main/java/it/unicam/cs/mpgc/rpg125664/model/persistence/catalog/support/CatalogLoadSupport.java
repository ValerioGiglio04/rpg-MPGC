package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.support;

import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.CreaturaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.GiocatoreEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.MossaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.PalestraEntity;
import jakarta.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Query, indici e controlli per il caricamento del catalogo da H2. */
public final class CatalogLoadSupport {

  private static final String QUERY_ALL_CREATURES =
    "select c from CreaturaEntity c order by c.idCreatura";
  private static final String QUERY_ALL_MOVES = "select m from MossaEntity m order by m.idMossa";
  private static final String QUERY_ALL_GYMS = "select p from PalestraEntity p order by p.ordine";
  private static final String QUERY_BOSS_PLAYERS =
    "select g from GiocatoreEntity g where g.boss = true";
  private static final String QUERY_BOSS_TEAM_CREATURES =
    "select c from CreaturaEntity c where c.idGiocatore is not null order by c.idGiocatore, c.idCreatura";

  private CatalogLoadSupport() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static List<CreaturaEntity> loadCreatureEntities(EntityManager entityManager) {
    return entityManager.createQuery(QUERY_ALL_CREATURES, CreaturaEntity.class).getResultList();
  }

  public static List<MossaEntity> loadMoveEntities(EntityManager entityManager) {
    return entityManager.createQuery(QUERY_ALL_MOVES, MossaEntity.class).getResultList();
  }

  public static List<PalestraEntity> loadGymEntities(EntityManager entityManager) {
    return entityManager.createQuery(QUERY_ALL_GYMS, PalestraEntity.class).getResultList();
  }

  public static Map<Long, List<MossaEntity>> groupMoveEntitiesByCreatureId(
    List<MossaEntity> moveEntities
  ) {
    Map<Long, List<MossaEntity>> moveEntitiesByCreatureId = moveEntities
      .stream()
      .collect(Collectors.groupingBy(MossaEntity::getIdCreatura));
    moveEntitiesByCreatureId.values().forEach(CatalogLoadSupport::sortMovesByOrder);
    return moveEntitiesByCreatureId;
  }

  public static Map<Long, String> loadBossNameByPlayerId(EntityManager entityManager) {
    return entityManager
      .createQuery(QUERY_BOSS_PLAYERS, GiocatoreEntity.class)
      .getResultStream()
      .collect(Collectors.toMap(GiocatoreEntity::getIdGiocatore, GiocatoreEntity::getNome));
  }

  public static Map<Long, List<Long>> loadBossTeamCreatureIdsByPlayerId(
    EntityManager entityManager
  ) {
    return entityManager
      .createQuery(QUERY_BOSS_TEAM_CREATURES, CreaturaEntity.class)
      .getResultStream()
      .collect(
        Collectors.groupingBy(
          CreaturaEntity::getIdGiocatore,
          Collectors.mapping(CreaturaEntity::getIdCreatura, Collectors.toList())
        )
      );
  }

  public static void validateCatalogIntegrity(
    List<CreaturaEntity> creatureEntities,
    List<PalestraEntity> gymEntities,
    Map<Long, List<Long>> bossTeamCreatureIdsByPlayerId
  ) {
    if (creatureEntities.isEmpty()) {
      throw new IllegalStateException("Catalog has no creatures in database");
    }
    if (gymEntities.isEmpty()) {
      throw new IllegalStateException("Catalog has no gyms in database");
    }
    for (PalestraEntity gymEntity : gymEntities) {
      List<Long> bossTeamCreatureIds = bossTeamCreatureIdsByPlayerId.get(gymEntity.getIdBoss());
      if (bossTeamCreatureIds == null || bossTeamCreatureIds.isEmpty()) {
        throw new IllegalStateException(
          "Boss giocatore has no creatures in catalog: gym=" + gymEntity.getIdPalestra()
        );
      }
    }
  }

  private static void sortMovesByOrder(List<MossaEntity> moveEntities) {
    moveEntities.sort(Comparator.comparingInt(MossaEntity::getOrdine));
  }
}
