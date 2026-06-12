package it.unicam.cs.mpgc.rpg125664.model.entity;

import it.unicam.cs.mpgc.rpg125664.model.builder.GymRoomBuilder;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Palestra overworld con collegamenti, requisiti e completamento. */
public final class GymRoom implements Serializable {

  private final long id;
  private final String name;
  private final List<Long> connectedGymIds;
  private final GymBoss boss;
  private final int requiredPoints;
  private boolean completed;

  public static GymRoomBuilder builder() {
    return new GymRoomBuilder();
  }

  public GymRoom(
      long id,
      String name,
      List<Long> connectedGymIds,
      GymBoss boss,
      int requiredPoints,
      boolean completed) {
    Objects.requireNonNull(connectedGymIds, "Gym needs connections list");
    Objects.requireNonNull(boss, "Gym needs a boss");
    this.id = id;
    this.name = name;
    this.connectedGymIds = List.copyOf(connectedGymIds);
    this.boss = boss;
    this.requiredPoints = requiredPoints;
    this.completed = completed;
  }

  public long id() {
    return id;
  }

  public String name() {
    return name;
  }

  public List<Long> connectedGymIds() {
    return Collections.unmodifiableList(connectedGymIds);
  }

  public GymBoss boss() {
    return boss;
  }

  public int requiredPoints() {
    return requiredPoints;
  }

  public boolean completed() {
    return completed;
  }

  public void markCompleted() {
    completed = true;
  }
}
