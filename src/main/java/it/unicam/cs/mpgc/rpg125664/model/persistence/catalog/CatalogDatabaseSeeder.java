package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog;

import it.unicam.cs.mpgc.rpg125664.model.catalog.CatalogIds;
import jakarta.persistence.EntityManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Popola le tabelle catalogo (giocatore, creatura, mosse, palestra) da {@code catalog-seed.json}.
 */
public final class CatalogDatabaseSeeder {

  private static final Logger LOG = Logger.getLogger(CatalogDatabaseSeeder.class.getName());

  private CatalogDatabaseSeeder() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static void ensureCatalogPresent(EntityManager em) {
    CatalogSeedBundle seed = CatalogSeedJsonLoader.load();
    try {
      if (isCatalogComplete(em, seed)) {
        LOG.info("Catalogo gia' completo: seed ignorato.");
        return;
      }
      logIncompleteCatalog(em);
      wipeCatalogTables(em);
      em.flush();
      persistSeed(em, seed);
      em.flush();
      LOG.log(
          Level.INFO,
          () ->
              String.format(
                  "Catalogo inizializzato: %d creature, %d palestre, %d giocatori.",
                  seed.creature().size(), seed.palestre().size(), seed.giocatori().size()));
    } catch (RuntimeException ex) {
      LOG.log(Level.SEVERE, "Impossibile completare il seed del catalogo", ex);
      throw new IllegalStateException("Catalog seeding failed", ex);
    }
  }

  private static boolean isCatalogComplete(EntityManager em, CatalogSeedBundle seed) {
    GiocatoreEntity player = em.find(GiocatoreEntity.class, CatalogIds.GIOCATORE_UMANO);
    if (player == null) {
      return false;
    }
    return count(em, CreaturaEntity.class) == seed.creature().size()
        && count(em, PalestraEntity.class) == seed.palestre().size()
        && count(em, GiocatoreEntity.class) == seed.giocatori().size();
  }

  private static long count(EntityManager em, Class<?> entityClass) {
    String entityName = entityClass.getSimpleName();
    return em.createQuery("select count(e) from " + entityName + " e", Long.class)
        .getSingleResult();
  }

  private static void logIncompleteCatalog(EntityManager em) {
    LOG.warning(
        () ->
            String.format(
                "Catalogo incompleto: creature=%d, palestre=%d, giocatori=%d. Wipe e seed.",
                count(em, CreaturaEntity.class),
                count(em, PalestraEntity.class),
                count(em, GiocatoreEntity.class)));
  }

  private static void persistSeed(EntityManager em, CatalogSeedBundle seed) {
    for (GiocatoreEntity row : seed.giocatori()) {
      em.persist(row);
    }
    for (CreaturaEntity row : seed.creature()) {
      em.persist(row);
    }
    for (MossaEntity row : seed.mosse()) {
      em.persist(row);
    }
    for (PalestraEntity row : seed.palestre()) {
      em.persist(row);
    }
  }

  private static void wipeCatalogTables(EntityManager em) {
    em.createQuery("select m from MossaEntity m", MossaEntity.class)
        .getResultStream()
        .forEach(em::remove);
    em.createQuery("select p from PalestraEntity p", PalestraEntity.class)
        .getResultStream()
        .forEach(em::remove);
    em.createQuery("select c from CreaturaEntity c", CreaturaEntity.class)
        .getResultStream()
        .forEach(em::remove);
    em.createQuery("select g from GiocatoreEntity g", GiocatoreEntity.class)
        .getResultStream()
        .forEach(em::remove);
  }
}
