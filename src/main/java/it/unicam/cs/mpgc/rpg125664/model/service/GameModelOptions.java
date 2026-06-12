package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.overworld.strategy.GymStatusStrategy;
import java.util.Objects;

/** Opzioni di assemblaggio per {@link GameModel}. */
public final class GameModelOptions {

  private final GameStateHolder holder;
  private final NewGameService newGame;
  private final BattleService battle;
  private final HealingService healing;
  private final SessionPersistenceFacade persistence;
  private final GymStatusStrategy gymStatusStrategy;

  private GameModelOptions(Builder builder) {
    this.holder = Objects.requireNonNull(builder.holder, "holder");
    this.newGame = Objects.requireNonNull(builder.newGame, "newGame");
    this.battle = Objects.requireNonNull(builder.battle, "battle");
    this.healing = Objects.requireNonNull(builder.healing, "healing");
    this.persistence = Objects.requireNonNull(builder.persistence, "persistence");
    this.gymStatusStrategy = Objects.requireNonNull(builder.gymStatusStrategy, "gymStatusStrategy");
  }

  public static Builder builder() {
    return new Builder();
  }

  public GameStateHolder holder() {
    return holder;
  }

  public NewGameService newGame() {
    return newGame;
  }

  public BattleService battle() {
    return battle;
  }

  public HealingService healing() {
    return healing;
  }

  public SessionPersistenceFacade persistence() {
    return persistence;
  }

  public GymStatusStrategy gymStatusStrategy() {
    return gymStatusStrategy;
  }

  public static final class Builder {

    private GameStateHolder holder;
    private NewGameService newGame;
    private BattleService battle;
    private HealingService healing;
    private SessionPersistenceFacade persistence;
    private GymStatusStrategy gymStatusStrategy;

    public Builder holder(GameStateHolder holder) {
      this.holder = holder;
      return this;
    }

    public Builder newGame(NewGameService newGame) {
      this.newGame = newGame;
      return this;
    }

    public Builder battle(BattleService battle) {
      this.battle = battle;
      return this;
    }

    public Builder healing(HealingService healing) {
      this.healing = healing;
      return this;
    }

    public Builder persistence(SessionPersistenceFacade persistence) {
      this.persistence = persistence;
      return this;
    }

    public Builder gymStatusStrategy(GymStatusStrategy gymStatusStrategy) {
      this.gymStatusStrategy = gymStatusStrategy;
      return this;
    }

    public GameModelOptions build() {
      return new GameModelOptions(this);
    }
  }
}
