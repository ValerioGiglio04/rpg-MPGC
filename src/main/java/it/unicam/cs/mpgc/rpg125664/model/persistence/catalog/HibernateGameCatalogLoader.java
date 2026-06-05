package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog;

import it.unicam.cs.mpgc.rpg125664.model.persistence.AbstractHibernateAdapter;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.CreaturaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.GiocatoreEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.MossaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.PalestraEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.mapper.CatalogEntityMapper;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.support.PalestraCollegamentiSupport;
import it.unicam.cs.mpgc.rpg125664.model.catalog.CatalogIds;
import it.unicam.cs.mpgc.rpg125664.model.catalog.CreatureTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GymTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.NewGameSettings;
import it.unicam.cs.mpgc.rpg125664.model.persistence.GameCatalogLoader;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.HashMap;
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
    return withEntityManager(this::loadCatalog);
  }

  private GameCatalog loadCatalog(EntityManager em) {
    if (em.find(GiocatoreEntity.class, CatalogIds.GIOCATORE_UMANO) == null) {
      throw new IllegalStateException(
          "Catalog database is empty: run CatalogDatabaseSeeder before load()");
    }
    List<CreaturaEntity> creatureRows =
        em.createQuery("select c from CreaturaEntity c order by c.idCreatura", CreaturaEntity.class)
            .getResultList();
    List<MossaEntity> moveRows =
        em.createQuery("select m from MossaEntity m order by m.idMossa", MossaEntity.class)
            .getResultList();
    List<PalestraEntity> gymRows =
        em.createQuery("select p from PalestraEntity p order by p.ordine", PalestraEntity.class)
            .getResultList();
    Map<Long, String> bossNamesById = loadBossNames(em);
    Map<Long, List<Long>> bossCreatureIdsByGiocatoreId = loadBossCreatureIds(em);
    Map<Long, List<Long>> collegamentiByPalestraId =
        PalestraCollegamentiSupport.linearByOrdine(gymRows);
    validate(creatureRows, gymRows, bossCreatureIdsByGiocatoreId);
    Map<Long, List<MossaEntity>> movesByCreature = groupMovesByCreature(moveRows);
    List<CreatureTemplate> creatures =
        creatureRows.stream()
            .map(
                row ->
                    CatalogEntityMapper.toCreature(
                        row, movesByCreature.getOrDefault(row.getIdCreatura(), List.of())))
            .toList();
    List<GymTemplate> gyms =
        gymRows.stream()
            .map(
                row ->
                    CatalogEntityMapper.toGym(
                        row, bossNamesById, bossCreatureIdsByGiocatoreId, collegamentiByPalestraId))
            .toList();
    return new GameCatalog(newGameSettings, creatures, gyms);
  }

  private Map<Long, List<MossaEntity>> groupMovesByCreature(List<MossaEntity> moveRows) {
    Map<Long, List<MossaEntity>> grouped = new HashMap<>();
    for (MossaEntity move : moveRows) {
      grouped.computeIfAbsent(move.getIdCreatura(), ignored -> new ArrayList<>()).add(move);
    }
    grouped
        .values()
        .forEach(list -> list.sort(java.util.Comparator.comparingInt(MossaEntity::getOrdine)));
    return grouped;
  }

  private void validate(
      List<CreaturaEntity> creatures,
      List<PalestraEntity> gyms,
      Map<Long, List<Long>> bossCreatureIdsByGiocatoreId) {
    if (creatures.isEmpty()) {
      throw new IllegalStateException("Catalog has no creatures in database");
    }
    if (gyms.isEmpty()) {
      throw new IllegalStateException("Catalog has no gyms in database");
    }
    for (PalestraEntity gym : gyms) {
      List<Long> bossTeam = bossCreatureIdsByGiocatoreId.get(gym.getIdBoss());
      if (bossTeam == null || bossTeam.isEmpty()) {
        throw new IllegalStateException(
            "Boss giocatore has no creatures in catalog: gym=" + gym.getIdPalestra());
      }
    }
  }

  private Map<Long, String> loadBossNames(EntityManager em) {
    Map<Long, String> names = new HashMap<>();
    em.createQuery("select g from GiocatoreEntity g where g.boss = true", GiocatoreEntity.class)
        .getResultStream()
        .forEach(g -> names.put(g.getIdGiocatore(), g.getNome()));
    return names;
  }

  private Map<Long, List<Long>> loadBossCreatureIds(EntityManager em) {
    Map<Long, List<Long>> byBossGiocatoreId = new HashMap<>();
    em.createQuery(
            "select c from CreaturaEntity c where c.idGiocatore is not null order by c.idGiocatore, c.idCreatura",
            CreaturaEntity.class)
        .getResultStream()
        .forEach(
            row ->
                byBossGiocatoreId
                    .computeIfAbsent(row.getIdGiocatore(), ignored -> new ArrayList<>())
                    .add(row.getIdCreatura()));
    return byBossGiocatoreId;
  }
}
