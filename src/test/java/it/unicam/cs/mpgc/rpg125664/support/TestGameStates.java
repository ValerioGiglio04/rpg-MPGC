package it.unicam.cs.mpgc.rpg125664.support;

import it.unicam.cs.mpgc.rpg125664.model.service.NewGameService;
import it.unicam.cs.mpgc.rpg125664.model.service.GameStateHolder;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.entity.Player;
import it.unicam.cs.mpgc.rpg125664.model.entity.Score;

/** Builds {@link GameState} / {@link GameStateHolder} instances for application tests. */
public final class TestGameStates {

  private TestGameStates() {}

  public static GameStateHolder freshHolder(GameCatalog catalog) {
    return new GameStateHolder(NewGameService.buildInitialState(catalog));
  }

  public static GameStateHolder freshHolder() {
    return freshHolder(TestCatalogFactory.minimal());
  }

  public static GameState challengeableState(GameCatalog catalog) {
    GameState state = NewGameService.buildInitialState(catalog);
    state.player().score().add(pointsForCurrentGym(state));
    return state;
  }

  public static GameStateHolder challengeableHolder() {
    GameCatalog catalog = TestCatalogFactory.minimal();
    return new GameStateHolder(challengeableState(catalog));
  }

  public static int pointsForCurrentGym(GameState state) {
    return state.currentGym().requiredPoints();
  }

  public static GameState stateWithDamagedPlayer(GameCatalog catalog, int currentHealth) {
    GameState state = challengeableState(catalog);
    Creature damaged = catalog.buildCreature(TestCatalogFactory.CREATURE_FAST, currentHealth);
    Player player =
        Player.builder()
            .name(catalog.settings().playerName())
            .holder(
                it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder.builder()
                    .creatures(java.util.List.of(damaged))
                    .activeCatalogId(TestCatalogFactory.CREATURE_FAST)
                    .build())
            .score(Score.builder().points(50).build())
            .skinPath(catalog.settings().playerSkinPath())
            .build();
    return GameState.builder()
        .player(player)
        .gyms(catalog.buildAllGyms(java.util.Map.of(TestCatalogFactory.GYM_START, true)))
        .currentGymId(TestCatalogFactory.GYM_NEXT)
        .build();
  }
}
