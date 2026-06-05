package it.unicam.cs.mpgc.rpg125664.model.combat.strategy;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;

/**
 * Sceglie quale mossa usa una creatura boss (o controllata dall'AI) ad ogni azione. Le
 * implementazioni si possono swappare al composition root senza cambiare l'orchestrazione della
 * battaglia.
 */
@FunctionalInterface
public interface BossMoveStrategy {

  Move pickMove(Creature bossCreature);
}
