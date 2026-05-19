package it.unicam.cs.mpgc.rpg125664.model.catalog;

import java.util.List;
import java.util.Objects;

public record NewGameSettings(
    String playerName, long startingGymId, String playerSkinPath, List<Long> starterTeamIds) {
  public NewGameSettings {
    Objects.requireNonNull(playerName, "playerName");
    Objects.requireNonNull(playerSkinPath, "playerSkinPath");
    starterTeamIds = List.copyOf(starterTeamIds);
  }
}
