package it.unicam.cs.mpgc.rpg125664.model.validation;

import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.Player;
import it.unicam.cs.mpgc.rpg125664.model.entity.Score;

/**
 * Valida un {@link Player}: nome, holder, score e skin path. Si ottiene tramite {@link
 * Validators#getPlayerValidator()}.
 */
public final class PlayerValidator extends AbstractDomainValidator<Player> {

  PlayerValidator() {}

  @Override
  protected String nullMessage() {
    return "Player cannot be null";
  }

  @Override
  protected void validateRules(Player player) {
    Rules.requireText(player.name(), "Player name cannot be blank");
    CreatureHolder holder = player.holder();
    Score score = player.score();
    Rules.requireNonNull(holder, "Player needs holder");
    Rules.requireNonNull(score, "Player needs score");
    Rules.requireText(player.skinPath(), "Player skin path cannot be blank");
  }
}
