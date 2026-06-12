package it.unicam.cs.mpgc.rpg125664.app;

import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.combat.BattleRoundExecutor;
import it.unicam.cs.mpgc.rpg125664.model.combat.strategy.AttackResolutionStrategy;
import it.unicam.cs.mpgc.rpg125664.model.combat.strategy.BossMoveStrategy;
import it.unicam.cs.mpgc.rpg125664.model.combat.strategy.implementations.AccuracyThresholdBossMoveStrategy;
import it.unicam.cs.mpgc.rpg125664.model.combat.strategy.implementations.TurnBasedAttackResolutionStrategy;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.overworld.strategy.GymStatusStrategy;
import it.unicam.cs.mpgc.rpg125664.model.overworld.strategy.implementations.DefaultGymStatusStrategy;
import it.unicam.cs.mpgc.rpg125664.model.persistence.GameStateRepository;
import it.unicam.cs.mpgc.rpg125664.model.service.BattleService;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModelOptions;
import it.unicam.cs.mpgc.rpg125664.model.service.GameStateHolder;
import it.unicam.cs.mpgc.rpg125664.model.service.GymCompletionHandler;
import it.unicam.cs.mpgc.rpg125664.model.service.HealingService;
import it.unicam.cs.mpgc.rpg125664.model.service.NewGameService;
import it.unicam.cs.mpgc.rpg125664.model.service.SessionPersistenceFacade;
import it.unicam.cs.mpgc.rpg125664.view.mapper.PortraitAssetResolver;
import java.util.Objects;

/** Assembla i servizi di runtime ({@code GameModel}, asset ritratti). */
final class ServiceGraph {

  record Runtime(GameModel gameModel, PortraitAssetResolver portraitAssets) {}

  private ServiceGraph() {}

  static Runtime assemble(GameStateRepository repository, GameCatalog catalog) {
    Objects.requireNonNull(repository, "repository");
    Objects.requireNonNull(catalog, "catalog");

    GameState initialState = NewGameService.buildInitialState(catalog);
    GameStateHolder holder = new GameStateHolder(initialState);

    NewGameService newGame = new NewGameService(holder, catalog);
    AttackResolutionStrategy attackResolutionStrategy = new TurnBasedAttackResolutionStrategy();
    BossMoveStrategy bossMoveStrategy = new AccuracyThresholdBossMoveStrategy();
    BattleRoundExecutor roundExecutor = new BattleRoundExecutor(
      attackResolutionStrategy,
      bossMoveStrategy
    );
    GymCompletionHandler gymCompletionHandler = new GymCompletionHandler(catalog);
    BattleService battle = new BattleService(holder, roundExecutor, gymCompletionHandler);
    HealingService healing = new HealingService();
    SessionPersistenceFacade persistence = new SessionPersistenceFacade(repository);
    GymStatusStrategy gymStatusStrategy = new DefaultGymStatusStrategy();

    GameModel gameModel = new GameModel(
      GameModelOptions.builder()
        .holder(holder)
        .newGame(newGame)
        .battle(battle)
        .healing(healing)
        .persistence(persistence)
        .gymStatusStrategy(gymStatusStrategy)
        .build()
    );
    PortraitAssetResolver portraitAssets = new PortraitAssetResolver(catalog);
    return new Runtime(gameModel, portraitAssets);
  }
}
