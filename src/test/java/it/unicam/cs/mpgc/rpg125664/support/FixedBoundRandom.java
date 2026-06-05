package it.unicam.cs.mpgc.rpg125664.support;

import java.util.Random;

/** {@link Random} that returns predetermined values for {@link #nextInt(int)}. */
public final class FixedBoundRandom extends Random {

  private final int[] values;
  private int index;

  public FixedBoundRandom(int... values) {
    this.values = values.clone();
  }

  @Override
  public int nextInt(int bound) {
    if (values.length == 0) {
      return super.nextInt(bound);
    }
    int value = values[index % values.length];
    index++;
    return Math.floorMod(value, bound);
  }
}
