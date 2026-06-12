package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.catalog.NewGameSettings;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.Player;
import it.unicam.cs.mpgc.rpg125664.model.entity.Score;
import java.util.List;
import java.util.Objects;

/**
 * Costruisce un {@link GameState} fresco a partire dal {@link GameCatalog} e lo installa
 * nell'holder condiviso. Singola responsabilita': avvio / riavvio di una run. L'helper statico
 * {@link #buildInitialState(GameCatalog)} e' chiamato una sola volta al boot per inizializzare
 * l'holder, poi {@link #start()} lo sostituisce su richiesta dell'utente.
 */
public final class NewGameService {

  private final GameStateHolder holder;
  private final GameCatalog catalog;

  public NewGameService(GameStateHolder holder, GameCatalog catalog) {
    this.holder = Objects.requireNonNull(holder, "holder");
    this.catalog = Objects.requireNonNull(catalog, "catalog");
  }

  public void start() {
    holder.replace(buildInitialState(catalog));
  }

  public static GameState buildInitialState(GameCatalog catalog) {
    Objects.requireNonNull(catalog, "catalog");
    NewGameSettings settings = catalog.settings();
    List<Creature> starterTeam = settings
      .starterTeamIds()
      .stream()
      .map(catalog::buildCreature)
      .toList();
    Player player = Player.builder()
      .name(settings.playerName())
      .holder(CreatureHolder.builder().creatures(starterTeam).build())
      .score(Score.builder().build())
      .skinPath(settings.playerSkinPath())
      .build();
    return GameState.builder()
      .player(player)
      .gyms(catalog.buildAllGyms(null))
      .currentGymId(settings.startingGymId())
      .build();
  }
}
