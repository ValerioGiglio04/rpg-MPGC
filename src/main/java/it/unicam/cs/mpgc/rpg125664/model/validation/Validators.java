package it.unicam.cs.mpgc.rpg125664.model.validation;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymBoss;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import it.unicam.cs.mpgc.rpg125664.model.entity.Player;
import it.unicam.cs.mpgc.rpg125664.model.entity.Score;

/**
 * Factory + registry per i validators del dominio. Tiene un'istanza in cache per ogni tipo e li
 * espone tramite piccoli accessor. Ogni validator concreto estende {@link AbstractDomainValidator}
 * (Template Method) e implementa {@link Validator}, cosi' il contratto e' verificato a
 * compile-time.
 *
 * <p>Helper specializzati una-tantum (es. {@code validateDamage(int)}, {@code validateSwitch(...)})
 * restano metodi statici sulla loro classe validator perche' non rientrano nella forma a singolo
 * argomento.
 */
public final class Validators {

  private static final Validator<Creature> CREATURE = new CreatureValidator();
  private static final Validator<CreatureHolder> CREATURE_HOLDER = new CreatureHolderValidator();
  private static final Validator<GameState> GAME_STATE = new GameStateValidator();
  private static final Validator<GymBoss> GYM_BOSS = new GymBossValidator();
  private static final Validator<GymRoom> GYM_ROOM = new GymRoomValidator();
  private static final Validator<Move> MOVE = new MoveValidator();
  private static final Validator<Player> PLAYER = new PlayerValidator();
  private static final Validator<Score> SCORE = new ScoreValidator();

  private Validators() {}

  public static Validator<Creature> getCreatureValidator() {
    return CREATURE;
  }

  public static Validator<CreatureHolder> getCreatureHolderValidator() {
    return CREATURE_HOLDER;
  }

  public static Validator<GameState> getGameStateValidator() {
    return GAME_STATE;
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

  public static Validator<Player> getPlayerValidator() {
    return PLAYER;
  }

  public static Validator<Score> getScoreValidator() {
    return SCORE;
  }
}
