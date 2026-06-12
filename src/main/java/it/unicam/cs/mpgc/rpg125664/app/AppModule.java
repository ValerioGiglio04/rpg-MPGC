package it.unicam.cs.mpgc.rpg125664.app;

import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.persistence.GameStateRepository;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto.CatalogSeedBundle;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.seed.CatalogSeedJsonLoader;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.SessioneSalvataSummaryMapper;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.implementations.HibernateGameStateRepository;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.implementations.SessionRepositoryOptions;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.implementations.SessioneSalvataJpaRepository;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.mapper.SessioneJsonMapper;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.serializer.SessionJsonSerializer;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Objects;

/**
 * Composition root: crea l'EMF, assicura il catalogo su H2 (seed idempotente), costruisce il {@link
 * GameCatalog} in memoria, collega i servizi in {@link GameModel} ed espone un hook {@link
 * #close()} cosi' l'applicazione puo' rilasciare le risorse JPA in modo pulito.
 */
public final class AppModule implements AutoCloseable {

  private static final String PERSISTENCE_UNIT = "rpg-palestre-creature";

  private final EntityManagerFactory entityManagerFactory;
  private final GameStateRepository repository;
  private final GameModel gameModel;
  private final PortraitAssetResolver portraitAssets;

  public static AppModule create(
    EntityManagerFactory entityManagerFactory,
    GameCatalog catalog,
    SessioneJsonMapper sessionMapper
  ) {
    return new AppModule(entityManagerFactory, catalog, sessionMapper);
  }

  public static AppModule bootstrap() {
    CatalogSeedBundle seed = CatalogSeedJsonLoader.load();
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    try {
      GameCatalog catalog = CatalogBootstrap.ensureCatalogPresent(emf, seed);
      SessioneJsonMapper sessionMapper = new SessioneJsonMapper(catalog);
      return new AppModule(emf, catalog, sessionMapper);
    } catch (RuntimeException ex) {
      emf.close();
      throw ex;
    }
  }

  AppModule(
    EntityManagerFactory entityManagerFactory,
    GameCatalog catalog,
    SessioneJsonMapper sessionMapper
  ) {
    this.entityManagerFactory = Objects.requireNonNull(
      entityManagerFactory,
      "entityManagerFactory"
    );
    Objects.requireNonNull(catalog, "catalog");
    SessionJsonSerializer serializer = new SessionJsonSerializer(sessionMapper);
    SessioneSalvataSummaryMapper summaryMapper = new SessioneSalvataSummaryMapper(serializer);
    SessioneSalvataJpaRepository jpaRepository = new SessioneSalvataJpaRepository(
      entityManagerFactory
    );
    this.repository = new HibernateGameStateRepository(
      SessionRepositoryOptions.builder()
        .entityManagerFactory(entityManagerFactory)
        .jpaRepository(jpaRepository)
        .serializer(serializer)
        .summaryMapper(summaryMapper)
        .build()
    );

    ServiceGraph.Runtime runtime = ServiceGraph.assemble(repository, catalog);
    this.gameModel = runtime.gameModel();
    this.portraitAssets = runtime.portraitAssets();
  }

  public GameModel gameModel() {
    return gameModel;
  }

  public PortraitAssetResolver portraitAssets() {
    return portraitAssets;
  }

  @Override
  public void close() {
    if (entityManagerFactory.isOpen()) entityManagerFactory.close();
  }
}
