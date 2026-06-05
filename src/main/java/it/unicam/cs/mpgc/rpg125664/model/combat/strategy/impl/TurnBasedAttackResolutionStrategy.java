package it.unicam.cs.mpgc.rpg125664.model.combat.strategy.impl;

import it.unicam.cs.mpgc.rpg125664.model.combat.AttackOutcome;
import it.unicam.cs.mpgc.rpg125664.model.combat.strategy.AttackResolutionStrategy;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import java.util.Objects;
import java.util.Random;

/**
 * {@link AttackResolutionStrategy} di default: risolve un singolo attacco contro il difensore.
 * Danno = attaccante.attack + mossa.power - difensore.defense, con minimo a 1. La hit chance e'
 * tirata su 100. Il {@link Random} e' iniettato per controllare la casualita' dei tiri (es. seed
 * fisso).
 */
public final class TurnBasedAttackResolutionStrategy implements AttackResolutionStrategy {

  private static final int ACCURACY_MAX = 100;

  private final Random random;

  public TurnBasedAttackResolutionStrategy() {
    this(new Random());
  }

  public TurnBasedAttackResolutionStrategy(Random random) {
    this.random = Objects.requireNonNull(random, "random");
  }

  @Override
  public AttackOutcome execute(Creature attacker, Creature defender, Move move) {
    if (attacker.isKnockedOut()) {
      throw new IllegalStateException("Knocked out creature cannot attack");
    }
    if (random.nextInt(ACCURACY_MAX) >= move.accuracy()) {
      return AttackOutcome.miss();
    }
    int damage = Math.max(1, attacker.attack() + move.power() - defender.defense());
    defender.receiveDamage(damage);
    return AttackOutcome.landed(damage, defender.isKnockedOut());
  }
}
