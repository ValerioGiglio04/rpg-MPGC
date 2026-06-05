package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.GymBoss;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.validation.AbstractDomainValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import java.util.List;

/**
 * Valida una {@link GymRoom}: id, nome, connessioni, boss e punti richiesti. Si ottiene tramite
 * {@link Validators#getGymRoomValidator()}.
 */
public final class GymRoomValidator extends AbstractDomainValidator<GymRoom> {

  GymRoomValidator() {}

  @Override
  protected String nullMessage() {
    return "GymRoom cannot be null";
  }

  @Override
  protected void validateRules(GymRoom room) {
    Rules.requirePositiveId(room.id(), "Gym id must be positive");
    Rules.requireText(room.name(), "Gym name cannot be blank");
    List<Long> connections = room.connectedGymIds();
    Rules.requireNonNull(connections, "Gym connections cannot be null");
    GymBoss boss = room.boss();
    Rules.requireNonNull(boss, "Gym needs a boss");
    Rules.requireNonNegative(room.requiredPoints(), "Gym required points cannot be negative");
  }
}
