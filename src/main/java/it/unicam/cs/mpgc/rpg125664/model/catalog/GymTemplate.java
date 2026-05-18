package it.unicam.cs.mpgc.rpg125664.model.catalog;

import java.util.List;

public record GymTemplate(
    long id,
    String name,
    int order,
    int requiredPoints,
    List<Long> connectedGymIds,
    BossTemplate boss) {
  public GymTemplate {
    connectedGymIds = List.copyOf(connectedGymIds);
  }
}
