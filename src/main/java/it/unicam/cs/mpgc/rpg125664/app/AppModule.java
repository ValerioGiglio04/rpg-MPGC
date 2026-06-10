package it.unicam.cs.mpgc.rpg125664.app;

import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto.CatalogSeedBundle;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.implementations.HibernateGameCatalogLoader;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.seed.CatalogDatabaseSeeder;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.seed.CatalogSeedJsonLoader;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.SessioneSalvataSummaryMapper;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.implementations.HibernateGameStateRepository;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.implementations.SessioneSalvataJpaRepository;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.mapper.SessioneJsonMapper;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.serializer.SessionJsonSerializer;
import it.unicam.cs.mpgc.rpg125664.model.service.BattleService;
import it.unicam.cs.mpgc.rpg125664.model.service.GymCompletionHandler;
import it.unicam.cs.mpgc.rpg125664.model.service.HealingService;
import it.unicam.cs.mpgc.rpg125664.model.service.NewGameService;
import it.unicam.cs.mpgc.rpg125664.model.overworld.strategy.GymStatusStrategy;
import it.unicam.cs.mpgc.rpg125664.model.overworld.strategy.implementations.DefaultGymStatusStrategy;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.service.GameStateHolder;
import it.unicam.cs.mpgc.rpg125664.model.service.SessionPersistenceFacade;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.combat.BattleRoundExecutor;
import it.unicam.cs.mpgc.rpg125664.model.combat.strategy.AttackResolutionStrategy;
import it.unicam.cs.mpgc.rpg125664.model.combat.strategy.BossMoveStrategy;
import it.unicam.cs.mpgc.rpg125664.model.combat.strategy.implementations.AccuracyThresholdBossMoveStrategy;
import it.unicam.cs.mpgc.rpg125664.model.combat.strategy.implementations.TurnBasedAttackResolutionStrategy;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.persistence.GameStateRepository;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
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
  private final PortraitAssetResolver portraitAssets;

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
    GameCatalog catalog = new HibernateGameCatalogLoader(emf, seed.newGameSettings()).load();
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
    SessionJsonSerializer serializer = new SessionJsonSerializer(sessionMapper);
    SessioneSalvataSummaryMapper summaryMapper = new SessioneSalvataSummaryMapper(serializer);
    SessioneSalvataJpaRepository jpaRepository =
        new SessioneSalvataJpaRepository(entityManagerFactory);
    this.repository =
        new HibernateGameStateRepository(
            entityManagerFactory, jpaRepository, serializer, summaryMapper);

    GameState initialState = NewGameService.buildInitialState(catalog);
    GameStateHolder holder = new GameStateHolder(initialState);

    NewGameService newGame = new NewGameService(holder, catalog);
    AttackResolutionStrategy attackResolutionStrategy = new TurnBasedAttackResolutionStrategy();
    BossMoveStrategy bossMoveStrategy = new AccuracyThresholdBossMoveStrategy();
    BattleRoundExecutor roundExecutor =
        new BattleRoundExecutor(attackResolutionStrategy, bossMoveStrategy);
    GymCompletionHandler gymCompletionHandler = new GymCompletionHandler(catalog);
    BattleService battle = new BattleService(holder, roundExecutor, gymCompletionHandler);
    HealingService healing = new HealingService();
    SessionPersistenceFacade persistence = new SessionPersistenceFacade(repository);
    GymStatusStrategy gymStatusStrategy = new DefaultGymStatusStrategy();

    this.gameModel =
        new GameModel(holder, newGame, battle, healing, persistence, gymStatusStrategy);
    this.portraitAssets = new PortraitAssetResolver(catalog);
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
