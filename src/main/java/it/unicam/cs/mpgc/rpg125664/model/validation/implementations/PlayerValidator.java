package it.unicam.cs.mpgc.rpg125664.model.validation.implementations;

import it.unicam.cs.mpgc.rpg125664.model.entity.Player;
import it.unicam.cs.mpgc.rpg125664.model.validation.Rules;
import it.unicam.cs.mpgc.rpg125664.model.validation.Validator;

public final class PlayerValidator extends Validator<Player> {

  @Override
  protected String nullMessage() {
    return "Player cannot be null";
  }

  @Override
  protected void validateRules(Player player) {
    Rules.requireText(player.name(), "Player name cannot be blank");
    Rules.requireNonNull(player.holder(), "Player needs holder");
    Rules.requireNonNull(player.score(), "Player needs score");
    Rules.requireText(player.skinPath(), "Player skin path cannot be blank");
  }
}
