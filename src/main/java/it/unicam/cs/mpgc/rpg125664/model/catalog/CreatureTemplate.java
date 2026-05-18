package it.unicam.cs.mpgc.rpg125664.model.catalog;

import java.util.List;

public record CreatureTemplate(
    long id,
    String name,
    String role,
    String skinPath,
    int maxHealth,
    int attack,
    int defense,
    int speed,
    List<MoveTemplate> moves) {
  public CreatureTemplate {
    moves = List.copyOf(moves);
  }
}
