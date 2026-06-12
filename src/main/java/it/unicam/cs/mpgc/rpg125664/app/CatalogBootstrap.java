package it.unicam.cs.mpgc.rpg125664.app;

import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto.CatalogSeedBundle;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.implementations.HibernateGameCatalogLoader;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.seed.CatalogDatabaseSeeder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/** Bootstrap del catalogo: seed DB e caricamento Hibernate. */
final class CatalogBootstrap {

  private CatalogBootstrap() {}

  static GameCatalog ensureCatalogPresent(
    EntityManagerFactory entityManagerFactory,
    CatalogSeedBundle seed
  ) {
    EntityManager em = entityManagerFactory.createEntityManager();
    try {
      em.getTransaction().begin();
      CatalogDatabaseSeeder.ensureCatalogPresent(em, seed);
      em.getTransaction().commit();
    } catch (RuntimeException ex) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw ex;
    } finally {
      if (em.isOpen()) {
        em.close();
      }
    }
    return new HibernateGameCatalogLoader(entityManagerFactory, seed.newGameSettings()).load();
  }
}
