package it.unicam.cs.mpgc.rpg125664.model.persistence.session;

import it.unicam.cs.mpgc.rpg125664.model.persistence.session.UltimaSessioneSalvataDto.CreaturaTeamDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.UltimaSessioneSalvataDto.PalestraProgressoDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.UltimaSessioneSalvataDto.PosizioneMappaDto;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.entity.Player;
import it.unicam.cs.mpgc.rpg125664.model.entity.Score;
import it.unicam.cs.mpgc.rpg125664.view.overworld.MapCoordinate;
import it.unicam.cs.mpgc.rpg125664.view.overworld.OverworldLayoutSupport;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class SessioneJsonMapper {

  private final GameCatalog catalog;

  public SessioneJsonMapper(GameCatalog catalog) {
    this.catalog = Objects.requireNonNull(catalog, "catalog");
  }

  public UltimaSessioneSalvataDto toDto(GameState state) {
    return toDto(state, Optional.empty());
  }

  public UltimaSessioneSalvataDto toDto(
      GameState state, Optional<MapCoordinate> overworldPosition) {
    Player player = state.player();
    List<Creature> creatures = player.holder().creatures();
    Creature active = player.holder().activeCreature();
    UltimaSessioneSalvataDto dto = new UltimaSessioneSalvataDto();
    dto.setDataSalvataggio(Instant.now());
    dto.setNumPuntiFama(player.score().points());
    dto.setIdCreaturaAttivaSelezionata(active.catalogId());
    dto.setIdPalestraCorrente(state.currentGymId());
    dto.setListaCreatureTeamGiocatore(
        creatures.stream()
            .map(creature -> new CreaturaTeamDto(creature.catalogId(), creature.currentHealth()))
            .toList());
    dto.setPalestreCompletate(
        state.gyms().stream()
            .map(gym -> new PalestraProgressoDto(gym.id(), gym.completed()))
            .toList());
    MapCoordinate position =
        overworldPosition.orElseGet(
            () -> OverworldLayoutSupport.defaultPlayerPosition(state.gyms(), state.currentGymId()));
    dto.setPosizioneGiocatoreMappa(new PosizioneMappaDto(position.column(), position.row()));
    return dto;
  }

  public GameState fromDto(UltimaSessioneSalvataDto dto) {
    Objects.requireNonNull(dto, "dto");
    List<Creature> team = rebuildTeam(dto.getListaCreatureTeamGiocatore());
    long activeCatalogId = dto.getIdCreaturaAttivaSelezionata();
    CreatureHolder holder =
        CreatureHolder.builder().creatures(team).activeCatalogId(activeCatalogId).build();
    Player player =
        Player.builder()
            .name(catalog.settings().playerName())
            .holder(holder)
            .score(Score.builder().points(dto.getNumPuntiFama()).build())
            .skinPath(catalog.settings().playerSkinPath())
            .build();
    Map<Long, Boolean> completion = toCompletionMap(dto.getPalestreCompletate());
    List<GymRoom> gyms = catalog.buildAllGyms(completion);
    long currentGymId = dto.getIdPalestraCorrente();
    if (currentGymId <= 0) {
      currentGymId = catalog.settings().startingGymId();
    }
    return GameState.builder().player(player).gyms(gyms).currentGymId(currentGymId).build();
  }

  public MapCoordinate mapPositionFromDto(UltimaSessioneSalvataDto dto) {
    PosizioneMappaDto pos = dto.getPosizioneGiocatoreMappa();
    if (pos == null) {
      return null;
    }
    return new MapCoordinate(pos.getY(), pos.getX());
  }

  private List<Creature> rebuildTeam(List<CreaturaTeamDto> stored) {
    if (stored == null || stored.isEmpty()) {
      throw new IllegalStateException("Saved player team is empty");
    }
    return stored.stream()
        .map(row -> catalog.buildCreature(row.getIdCreatura(), row.getHp()))
        .toList();
  }

  private Map<Long, Boolean> toCompletionMap(List<PalestraProgressoDto> progress) {
    Map<Long, Boolean> completionMap = new HashMap<>();
    if (progress == null) {
      return completionMap;
    }
    for (PalestraProgressoDto row : progress) {
      completionMap.put(row.getIdPalestra(), row.isCompletata());
    }
    return completionMap;
  }
}
