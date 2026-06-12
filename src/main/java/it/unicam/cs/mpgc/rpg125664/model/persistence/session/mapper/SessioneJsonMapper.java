package it.unicam.cs.mpgc.rpg125664.model.persistence.session.mapper;

import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.entity.Player;
import it.unicam.cs.mpgc.rpg125664.model.entity.Score;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.dto.CreaturaTeamDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.dto.PalestraProgressoDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.dto.PosizioneMappaDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.dto.UltimaSessioneSalvataDto;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Mapper tra {@link GameState} e payload JSON sessione salvata. */
public final class SessioneJsonMapper {

  private final GameCatalog catalog;

  public SessioneJsonMapper(GameCatalog catalog) {
    this.catalog = Objects.requireNonNull(catalog, "catalog");
  }

  public UltimaSessioneSalvataDto toDto(GameState state, OverworldPosition overworldPosition) {
    Objects.requireNonNull(overworldPosition, "overworldPosition");
    Player player = state.player();
    List<Creature> creatures = player.holder().creatures();
    Creature active = player.holder().activeCreature();
    UltimaSessioneSalvataDto dto = new UltimaSessioneSalvataDto();
    dto.setDataSalvataggio(Instant.now());
    dto.setNumPuntiFama(player.score().points());
    dto.setIdCreaturaAttivaSelezionata(active.catalogId());
    dto.setIdPalestraCorrente(state.currentGymId());
    List<CreaturaTeamDto> creatureTeamGiocatore = creatures
      .stream()
      .map(creature -> new CreaturaTeamDto(creature.catalogId(), creature.currentHealth()))
      .toList();
    dto.setListaCreatureTeamGiocatore(creatureTeamGiocatore);
    List<PalestraProgressoDto> palestreCompletateGiocatore = state
      .gyms()
      .stream()
      .map(gym -> new PalestraProgressoDto(gym.id(), gym.completed()))
      .toList();
    dto.setPalestreCompletate(palestreCompletateGiocatore);
    PosizioneMappaDto posizioneGiocatoreMappa = new PosizioneMappaDto(
      overworldPosition.column(),
      overworldPosition.row()
    );
    dto.setPosizioneGiocatoreMappa(posizioneGiocatoreMappa);
    return dto;
  }

  public GameState fromDto(UltimaSessioneSalvataDto dto) {
    Objects.requireNonNull(dto, "dto");
    List<Creature> team = rebuildTeam(dto.getListaCreatureTeamGiocatore());
    long activeCatalogId = dto.getIdCreaturaAttivaSelezionata();
    CreatureHolder holder = CreatureHolder.builder()
      .creatures(team)
      .activeCatalogId(activeCatalogId)
      .build();
    Player player = Player.builder()
      .name(catalog.settings().playerName())
      .holder(holder)
      .score(Score.builder().points(dto.getNumPuntiFama()).build())
      .skinPath(catalog.settings().playerSkinPath())
      .build();
    Map<Long, Boolean> gymCompletionMap = toCompletionMap(dto.getPalestreCompletate());
    List<GymRoom> gyms = catalog.buildAllGyms(gymCompletionMap);
    long currentGymId = dto.getIdPalestraCorrente();
    if (currentGymId <= 0) {
      currentGymId = catalog.settings().startingGymId();
    }
    return GameState.builder().player(player).gyms(gyms).currentGymId(currentGymId).build();
  }

  public OverworldPosition mapPositionFromDto(UltimaSessioneSalvataDto dto) {
    PosizioneMappaDto pos = dto.getPosizioneGiocatoreMappa();
    if (pos == null) return null;
    return new OverworldPosition(pos.getY(), pos.getX());
  }

  private List<Creature> rebuildTeam(List<CreaturaTeamDto> stored) {
    if (stored == null || stored.isEmpty()) {
      throw new IllegalStateException("Saved player team is empty");
    }
    return stored
      .stream()
      .map(row -> catalog.buildCreature(row.getIdCreatura(), row.getHp()))
      .toList();
  }

  private Map<Long, Boolean> toCompletionMap(List<PalestraProgressoDto> progress) {
    Map<Long, Boolean> completionMap = new HashMap<>();
    if (progress == null) return completionMap;
    for (PalestraProgressoDto row : progress) {
      completionMap.put(row.getIdPalestra(), row.isCompletata());
    }
    return completionMap;
  }
}
