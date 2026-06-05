package it.unicam.cs.mpgc.rpg125664.model.combat.strategy;

import it.unicam.cs.mpgc.rpg125664.model.combat.AttackOutcome;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;

/**
 * Strategy per risolvere una singola mossa dell'attaccante contro il difensore. Le implementazioni
 * decidono la randomness e le regole di danno; i client ricevono un {@link AttackOutcome} senza
 * testo.
 */
public interface AttackResolutionStrategy {

  AttackOutcome execute(Creature attacker, Creature defender, Move move);
}
