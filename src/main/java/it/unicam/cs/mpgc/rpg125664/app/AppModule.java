package it.unicam.cs.mpgc.rpg125664.app;

import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.CatalogDatabaseSeeder;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.CatalogSeedBundle;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.CatalogSeedJsonLoader;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.HibernateGameCatalogLoader;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.HibernateGameStateRepository;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.SessioneJsonMapper;
import it.unicam.cs.mpgc.rpg125664.model.service.BattleService;
import it.unicam.cs.mpgc.rpg125664.model.service.GymCompletionHandler;
import it.unicam.cs.mpgc.rpg125664.model.service.HealingService;
import it.unicam.cs.mpgc.rpg125664.model.service.NewGameService;
import it.unicam.cs.mpgc.rpg125664.model.service.GymStatusResolver;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.service.GameStateHolder;
import it.unicam.cs.mpgc.rpg125664.model.service.SessionPersistenceFacade;
import it.unicam.cs.mpgc.rpg125664.model.GameStateRepository;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.combat.AccuracyThresholdBossMoveStrategy;
import it.unicam.cs.mpgc.rpg125664.model.combat.BossMoveStrategy;
import it.unicam.cs.mpgc.rpg125664.model.combat.CombatEngine;
import it.unicam.cs.mpgc.rpg125664.model.combat.TurnBasedCombatEngine;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import jakarta.persistence.EntityManager;
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

  public static AppModule create(
      EntityManagerFactory entityManagerFactory,
      GameCatalog catalog,
      SessioneJsonMapper sessionMapper) {
    return new AppModule(entityManagerFactory, catalog, sessionMapper);
  }

  public static AppModule bootstrap() {
    CatalogSeedBundle seed = CatalogSeedJsonLoader.load();
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    EntityManager em = emf.createEntityManager();
    try {
      em.getTransaction().begin();
      CatalogDatabaseSeeder.ensureCatalogPresent(em, seed);
      em.getTransaction().commit();
    } catch (RuntimeException ex) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      emf.close();
      throw ex;
    } finally {
      if (em.isOpen()) {
        em.close();
      }
    }
    GameCatalog catalog =
        new HibernateGameCatalogLoader(emf, seed.newGameSettings()).load();
    SessioneJsonMapper sessionMapper = new SessioneJsonMapper(catalog);
    return new AppModule(emf, catalog, sessionMapper);
  }

  AppModule(
      EntityManagerFactory entityManagerFactory,
      GameCatalog catalog,
      SessioneJsonMapper sessionMapper) {
    this.entityManagerFactory =
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory");
    Objects.requireNonNull(catalog, "catalog");
    this.repository = new HibernateGameStateRepository(entityManagerFactory, sessionMapper);

    GameState initialState = NewGameService.buildInitialState(catalog);
    GameStateHolder holder = new GameStateHolder(initialState);

    NewGameService newGame = new NewGameService(holder, catalog);
    CombatEngine combatEngine = new TurnBasedCombatEngine();
    BossMoveStrategy bossMoveStrategy = new AccuracyThresholdBossMoveStrategy();
    GymCompletionHandler gymCompletionHandler = new GymCompletionHandler(catalog);
    BattleService battle =
        new BattleService(holder, combatEngine, bossMoveStrategy, gymCompletionHandler);
    HealingService healing = new HealingService();
    SessionPersistenceFacade persistence = new SessionPersistenceFacade(repository);
    GymStatusResolver gymStatusResolver = new GymStatusResolver();

    this.gameModel =
        new GameModel(holder, newGame, battle, healing, persistence, gymStatusResolver);
  }

  public GameModel gameModel() {
    return gameModel;
  }

  @Override
  public void close() {
    if (entityManagerFactory.isOpen()) entityManagerFactory.close();
  }
}
