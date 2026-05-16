package it.unicam.cs.mpgc.rpg125664.model.combat;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import java.util.Objects;
import java.util.Random;

/**
 * {@link CombatEngine} di default: risolve un singolo attacco contro il difensore. Danno =
 * attaccante.attack + mossa.power - difensore.defense, con minimo a 1. La hit chance e' tirata su
 * 100. Il {@link Random} e' iniettato cosi' i test possono passargliene uno seeded.
 */
public final class TurnBasedCombatEngine implements CombatEngine {

  private static final int ACCURACY_MAX = 100;

  private final Random random;

  public TurnBasedCombatEngine() {
    this(new Random());
  }

  public TurnBasedCombatEngine(Random random) {
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
