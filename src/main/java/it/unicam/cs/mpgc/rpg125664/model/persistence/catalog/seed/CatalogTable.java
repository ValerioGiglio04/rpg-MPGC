package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.seed;

import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto.CatalogSeedBundle;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

/** Una tabella del catalogo: sa contare, svuotare e inserire le righe del seed. */
public final class CatalogTable<T> {

  private static final Logger LOGGER = Logger.getLogger(CatalogTable.class.getName());
  private static final int INSERT_BATCH_SIZE = 50;

  private final String label;
  private final String entityName;
  private final Function<CatalogSeedBundle, List<T>> seedRows;

  private CatalogTable(
    String label,
    String entityName,
    Function<CatalogSeedBundle, List<T>> seedRows
  ) {
    this.label = label;
    this.entityName = entityName;
    this.seedRows = seedRows;
  }

  public static <T> CatalogTable<T> of(
    String label,
    Class<T> entityClass,
    Function<CatalogSeedBundle, List<T>> seedRows
  ) {
    return new CatalogTable<>(label, entityClass.getSimpleName(), seedRows);
  }

  public String getLabel() {
    return label;
  }

  public long count(EntityManager em) {
    return em
      .createQuery("select count(e) from " + entityName + " e", Long.class)
      .getSingleResult();
  }

  public int expectedRows(CatalogSeedBundle seed) {
    return seedRows.apply(seed).size();
  }

  public void insertAll(EntityManager em, CatalogSeedBundle seed) {
    List<T> rows = seedRows.apply(seed);
    if (rows.isEmpty()) return;
    for (int i = 0; i < rows.size(); i++) {
      em.persist(rows.get(i));
      boolean isEndOfBatch = (i + 1) % INSERT_BATCH_SIZE == 0;
      if (isEndOfBatch) {
        em.flush();
        em.clear();
      }
    }
  }

  public void deleteAll(EntityManager em) {
    int deleted = em.createQuery("delete from " + entityName + " e").executeUpdate();
    LOGGER.fine(() -> "Wipe " + label + ": " + deleted + " righe eliminate.");
  }
}
