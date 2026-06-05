package it.unicam.cs.mpgc.rpg125664.app;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.CatalogDatabaseSeeder;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.CatalogSeedBundle;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.CatalogSeedJsonLoader;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.HibernateGameCatalogLoader;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.SessioneJsonMapper;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class AppModuleBootstrapTest {

  private static final String PERSISTENCE_UNIT = "rpg-test";

  private EntityManagerFactory emf;

  @AfterEach
  void tearDown() {
    if (emf != null && emf.isOpen()) {
      emf.close();
    }
  }

  @Test
  void createWiresGameModelWithCatalog() {
    CatalogSeedBundle seed = CatalogSeedJsonLoader.load();
    emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    EntityManager em = emf.createEntityManager();
    try {
      em.getTransaction().begin();
      CatalogDatabaseSeeder.ensureCatalogPresent(em, seed);
      em.getTransaction().commit();
    } finally {
      em.close();
    }

    GameCatalog catalog =
        new HibernateGameCatalogLoader(emf, seed.newGameSettings()).load();
    SessioneJsonMapper mapper = new SessioneJsonMapper(catalog);

    try (AppModule module = AppModule.create(emf, catalog, mapper)) {
      assertNotNull(module.gameModel());
      assertTrue(module.gameModel().gameState().gyms().size() > 0);
    }
  }
}
