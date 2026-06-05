package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog;

import it.unicam.cs.mpgc.rpg125664.model.catalog.CatalogIds;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Popola le tabelle catalogo da {@code catalog-seed.json}.
 *
 * <p>Il chiamante deve eseguire {@link #ensureCatalogPresent(EntityManager)} dentro una transazione
 * attiva (commit/rollback a carico del chiamante).
 *
 * <p>Per aggiungere una tabella: dichiarare una voce nel registry e aggiornare {@link
 * #PERSIST_ORDER} e {@link #WIPE_ORDER} in modo esplicito (genitori in persist, figli per primi in
 * wipe).
 */
public final class CatalogDatabaseSeeder {

  private static final Logger LOGGER = Logger.getLogger(CatalogDatabaseSeeder.class.getName());

  private static final CatalogTable<GiocatoreEntity> GIOCATORI =
      CatalogTable.of("giocatori", GiocatoreEntity.class, CatalogSeedBundle::giocatori);
  private static final CatalogTable<CreaturaEntity> CREATURE =
      CatalogTable.of("creature", CreaturaEntity.class, CatalogSeedBundle::creature);
  private static final CatalogTable<MossaEntity> MOSSE =
      CatalogTable.of("mosse", MossaEntity.class, CatalogSeedBundle::mosse);
  private static final CatalogTable<PalestraEntity> PALESTRE =
      CatalogTable.of("palestre", PalestraEntity.class, CatalogSeedBundle::palestre);

  /** Ordine di insert: genitori prima dei figli (FK-safe). */
  private static final List<CatalogTable<?>> PERSIST_ORDER =
      List.of(GIOCATORI, CREATURE, MOSSE, PALESTRE);

  /**
   * Ordine di delete: figli prima dei genitori.
   *
   * <p>Non e' il semplice reverse di {@link #PERSIST_ORDER}: mosse e palestre sono indipendenti ma
   * referenziano rispettivamente creatura e giocatore boss.
   */
  private static final List<CatalogTable<?>> WIPE_ORDER =
      List.of(MOSSE, PALESTRE, CREATURE, GIOCATORI);

  private static final List<CatalogTable<?>> CATALOG_TABLES = PERSIST_ORDER;

  private CatalogDatabaseSeeder() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }

  /**
   * Garantisce che il catalogo DB corrisponda al seed JSON.
   *
   * <p>Se incompleto: wipe bulk + reinsert nella transazione corrente.
   */
  public static void ensureCatalogPresent(EntityManager em) {
    ensureCatalogPresent(em, CatalogSeedJsonLoader.load());
  }

  public static void ensureCatalogPresent(EntityManager em, CatalogSeedBundle seed) {
    Objects.requireNonNull(seed, "seed");
    try {
      if (isCatalogComplete(em, seed)) {
        LOGGER.info("Catalogo gia' completo: seed ignorato.");
        return;
      }
      requireActiveTransaction(em);
      reseed(em, seed);
    } catch (RuntimeException ex) {
      LOGGER.log(Level.SEVERE, "Impossibile completare il seed del catalogo", ex);
      throw new IllegalStateException("Catalog seeding failed", ex);
    }
  }

  private static boolean isCatalogComplete(EntityManager em, CatalogSeedBundle seed) {
    if (!existsHumanPlayer(em)) {
      return false;
    }
    boolean someTableHasMoreRowsThanExpected = CATALOG_TABLES.stream()
        .anyMatch(table -> table.count(em) != table.expectedRows(seed));
    return !someTableHasMoreRowsThanExpected;
  }

  /** EXISTS-style check: nessuna entity caricata nel persistence context. */
  private static boolean existsHumanPlayer(EntityManager em) {
    Long found =
        em.createQuery(
                "select count(g) from GiocatoreEntity g where g.idGiocatore = :id", Long.class)
            .setParameter("id", CatalogIds.GIOCATORE_UMANO)
            .getSingleResult();
    return found > 0;
  }

  private static void reseed(EntityManager em, CatalogSeedBundle seed) {
    LOGGER.warning(() -> "Catalogo incompleto: " + describeCounts(em) + ". Wipe e seed.");
    wipeCatalogTables(em);
    em.flush();
    em.clear();
    insertSeed(em, seed);
    em.flush();
    LOGGER.info(() -> "Catalogo inizializzato: " + describeSeed(seed) + ".");
  }

  private static void requireActiveTransaction(EntityManager em) {
    EntityTransaction tx = em.getTransaction();
    if (tx == null || !tx.isActive()) {
      throw new IllegalStateException(
          "Catalog reseed requires an active transaction on the EntityManager");
    }
  }

  private static String describeCounts(EntityManager em) {
    return CATALOG_TABLES.stream()
        .map(table -> table.getLabel() + "=" + table.count(em))
        .collect(Collectors.joining(", "));
  }

  private static String describeSeed(CatalogSeedBundle seed) {
    return CATALOG_TABLES.stream()
        .map(table -> table.expectedRows(seed) + " " + table.getLabel())
        .collect(Collectors.joining(", "));
  }

  private static void insertSeed(EntityManager em, CatalogSeedBundle seed) {
    for (CatalogTable<?> table : PERSIST_ORDER) {
      table.insertAll(em, seed);
    }
  }

  private static void wipeCatalogTables(EntityManager em) {
    for (CatalogTable<?> table : WIPE_ORDER) {
      table.deleteAll(em);
    }
  }
}
