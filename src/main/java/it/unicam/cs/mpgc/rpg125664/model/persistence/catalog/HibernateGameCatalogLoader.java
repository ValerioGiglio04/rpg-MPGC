import java.util.ArrayList;
package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog;

import it.unicam.cs.mpgc.rpg125664.model.persistence.AbstractHibernateAdapter;
import it.unicam.cs.mpgc.rpg125664.model.GameCatalogLoader;
import it.unicam.cs.mpgc.rpg125664.model.catalog.BossTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.CatalogIds;
import it.unicam.cs.mpgc.rpg125664.model.catalog.CreatureTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GymTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.MoveTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.NewGameSettings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Costruisce {@link GameCatalog} dalle tabelle catalogo su H2. */
public final class HibernateGameCatalogLoader extends AbstractHibernateAdapter
    implements GameCatalogLoader {

  public HibernateGameCatalogLoader(EntityManagerFactory entityManagerFactory) {
    super(entityManagerFactory);
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
                    toCreature(row, movesByCreature.getOrDefault(row.getIdCreatura(), List.of())))
            .toList();
    List<GymTemplate> gyms =
        gymRows.stream()
            .map(
                row ->
                    toGym(
                        row, bossNamesById, bossCreatureIdsByGiocatoreId, collegamentiByPalestraId))
            .toList();
    NewGameSettings settings = CatalogSeedJsonLoader.load().newGameSettings();
    return new GameCatalog(settings, creatures, gyms);
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

  private MoveTemplate toMove(MossaEntity row) {
    return new MoveTemplate(
        row.getNome(), row.getPotenza(), row.getPrecisione(), row.getDescrizione());
  }

  private CreatureTemplate toCreature(CreaturaEntity row, List<MossaEntity> moves) {
    List<MoveTemplate> moveTemplates = moves.stream().map(this::toMove).toList();
    return new CreatureTemplate(
        row.getIdCreatura(),
        row.getNome(),
        row.getRuolo(),
        row.getPercorsoSkin(),
        row.getHp(),
        row.getAttacco(),
        row.getDifesa(),
        row.getVelocita(),
        moveTemplates);
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

  private GymTemplate toGym(
      PalestraEntity row,
      Map<Long, String> bossNamesById,
      Map<Long, List<Long>> bossCreatureIdsByGiocatoreId,
      Map<Long, List<Long>> collegamentiByPalestraId) {
    String bossName = bossNamesById.getOrDefault(row.getIdBoss(), "Boss");
    List<Long> bossTeam =
        List.copyOf(bossCreatureIdsByGiocatoreId.getOrDefault(row.getIdBoss(), List.of()));
    BossTemplate boss = new BossTemplate(bossName, row.getPuntiRicompensaBoss(), bossTeam);
    List<Long> collegamenti =
        List.copyOf(collegamentiByPalestraId.getOrDefault(row.getIdPalestra(), List.of()));
    return new GymTemplate(
        row.getIdPalestra(),
        row.getNome(),
        row.getOrdine(),
        row.getPuntiRichiesti(),
        collegamenti,
        boss);
  }
}
