package it.unicam.cs.mpgc.rpg125664.model.validation.support;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymBoss;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import it.unicam.cs.mpgc.rpg125664.model.entity.Player;
import it.unicam.cs.mpgc.rpg125664.model.entity.Score;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.CreatureHolderValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.CreatureValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.GameStateValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.GymBossValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.GymRoomValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.MoveValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.PlayerValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.ScoreValidator;

/** Registry singleton dei validator di dominio. */
public final class ValidatorFactory {

  private static final Validator<Score> SCORE = new ScoreValidator();
  private static final Validator<Player> PLAYER = new PlayerValidator();
  private static final Validator<GameState> GAME_STATE = new GameStateValidator();
  private static final Validator<Creature> CREATURE = new CreatureValidator();
  private static final Validator<CreatureHolder> CREATURE_HOLDER = new CreatureHolderValidator();
  private static final Validator<GymBoss> GYM_BOSS = new GymBossValidator();
  private static final Validator<GymRoom> GYM_ROOM = new GymRoomValidator();
  private static final Validator<Move> MOVE = new MoveValidator();

  private ValidatorFactory() {}

  public static Validator<Score> getScoreValidator() {
    return SCORE;
  }

  public static Validator<Player> getPlayerValidator() {
    return PLAYER;
  }

  public static Validator<GameState> getGameStateValidator() {
    return GAME_STATE;
  }

  public static Validator<Creature> getCreatureValidator() {
    return CREATURE;
  }

  public static Validator<CreatureHolder> getCreatureHolderValidator() {
    return CREATURE_HOLDER;
  }

  public static Validator<GymBoss> getGymBossValidator() {
    return GYM_BOSS;
  }

  public static Validator<GymRoom> getGymRoomValidator() {
    return GYM_ROOM;
  }

  public static Validator<Move> getMoveValidator() {
    return MOVE;
  }
}
