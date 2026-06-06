package it.unicam.cs.mpgc.rpg125664.model.builder;

import it.unicam.cs.mpgc.rpg125664.model.entity.GymBoss;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;
import it.unicam.cs.mpgc.rpg125664.model.validation.support.ValidatorFactory;
import java.util.List;

public final class GymRoomBuilder {

  private long id;
  private String name;
  private List<Long> connectedGymIds = List.of();
  private GymBoss boss;
  private int requiredPoints;
  private boolean completed;

  public GymRoomBuilder id(long id) {
    this.id = id;
    return this;
  }

  public GymRoomBuilder name(String name) {
    this.name = name;
    return this;
  }

  public GymRoomBuilder connectedGymIds(List<Long> connectedGymIds) {
    this.connectedGymIds = connectedGymIds;
    return this;
  }

  public GymRoomBuilder boss(GymBoss boss) {
    this.boss = boss;
    return this;
  }

  public GymRoomBuilder requiredPoints(int requiredPoints) {
    this.requiredPoints = requiredPoints;
    return this;
  }

  public GymRoomBuilder completed(boolean completed) {
    this.completed = completed;
    return this;
  }

  public GymRoom build() {
    GymRoom room = new GymRoom(id, name, connectedGymIds, boss, requiredPoints, completed);
    Validator<GymRoom> validator = ValidatorFactory.getGymRoomValidator();
    validator.validate(room);
    return room;
  }
}
