package it.unicam.cs.mpgc.rpg125664.model.overworld;

import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/** Parametri per l'assegnazione palestre agli slot della griglia. */
public final class GymPlacementRequest {

  private final List<GymRoom> gyms;
  private final boolean[][] blockedTiles;
  private final Random random;
  private final int minDistance;

  private GymPlacementRequest(Builder builder) {
    this.gyms = Objects.requireNonNull(builder.gyms, "gyms");
    this.blockedTiles = Objects.requireNonNull(builder.blockedTiles, "blockedTiles");
    this.random = Objects.requireNonNull(builder.random, "random");
    this.minDistance = builder.minDistance;
  }

  public static Builder builder() {
    return new Builder();
  }

  public List<GymRoom> gyms() {
    return gyms;
  }

  public boolean[][] blockedTiles() {
    return blockedTiles;
  }

  public Random random() {
    return random;
  }

  public int minDistance() {
    return minDistance;
  }

  public static final class Builder {

    private List<GymRoom> gyms;
    private boolean[][] blockedTiles;
    private Random random;
    private int minDistance;

    public Builder gyms(List<GymRoom> gyms) {
      this.gyms = gyms;
      return this;
    }

    public Builder blockedTiles(boolean[][] blockedTiles) {
      this.blockedTiles = blockedTiles;
      return this;
    }

    public Builder random(Random random) {
      this.random = random;
      return this;
    }

    public Builder minDistance(int minDistance) {
      this.minDistance = minDistance;
      return this;
    }

    public GymPlacementRequest build() {
      return new GymPlacementRequest(this);
    }
  }
}
