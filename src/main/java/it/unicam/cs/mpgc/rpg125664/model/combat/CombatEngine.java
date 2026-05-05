package it.unicam.cs.mpgc.rpg125664.model.combat;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;

/**
 * Risolve una singola mossa dell'attaccante contro il difensore. Le implementazioni decidono la
 * randomness e le regole di danno; i client ricevono un {@link AttackOutcome} senza testo.
 */
public interface CombatEngine {

  AttackOutcome execute(Creature attacker, Creature defender, Move move);
}

// turm-based
