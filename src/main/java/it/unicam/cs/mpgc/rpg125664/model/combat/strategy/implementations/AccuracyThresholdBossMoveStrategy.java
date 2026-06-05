package it.unicam.cs.mpgc.rpg125664.model.combat.strategy.implementations;

import it.unicam.cs.mpgc.rpg125664.model.combat.strategy.BossMoveStrategy;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import java.util.Objects;

/**
 * Sceglie la prima mossa la cui accuracy raggiunge una soglia minima; altrimenti la prima mossa.
 */
public final class AccuracyThresholdBossMoveStrategy implements BossMoveStrategy {

  private final int minAccuracyInclusive;

  public AccuracyThresholdBossMoveStrategy() {
    this(80);
  }

  public AccuracyThresholdBossMoveStrategy(int minAccuracyInclusive) {
    this.minAccuracyInclusive = minAccuracyInclusive;
  }

  @Override
  public Move pickMove(Creature bossCreature) {
    Objects.requireNonNull(bossCreature, "bossCreature");
    return bossCreature.moves().stream()
        .filter(move -> move.accuracy() >= minAccuracyInclusive)
        .findFirst()
        .orElseGet(() -> bossCreature.moves().getFirst());
  }
}
