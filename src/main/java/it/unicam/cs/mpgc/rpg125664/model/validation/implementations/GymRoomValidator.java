package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;

/** Validatore {@link GymRoom} (id, nome, boss, collegamenti). */
public final class GymRoomValidator extends Validator<GymRoom> {

  @Override
  protected String nullMessage() {
    return "GymRoom cannot be null";
  }

  @Override
  protected void validateRules(GymRoom room) {
    Rules.requirePositiveId(room.id(), "Gym id must be positive");
    Rules.requireText(room.name(), "Gym name cannot be blank");
    Rules.requireNonNull(room.connectedGymIds(), "Gym connections cannot be null");
    Rules.requireNonNull(room.boss(), "Gym needs a boss");
    Rules.requireNonNegative(room.requiredPoints(), "Gym required points cannot be negative");
  }
}
