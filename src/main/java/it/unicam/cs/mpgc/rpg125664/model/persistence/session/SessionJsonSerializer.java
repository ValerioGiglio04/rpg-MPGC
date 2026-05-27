package it.unicam.cs.mpgc.rpg125664.model.persistence.session;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import it.unicam.cs.mpgc.rpg125664.view.overworld.MapCoordinate;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/** Serializza {@link UltimaSessioneSalvataDto} in {@code dati_salvati_json}. */
final class SessionJsonSerializer {

  private static final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private final SessioneJsonMapper mapper;

  SessionJsonSerializer(SessioneJsonMapper mapper) {
    this.mapper = Objects.requireNonNull(mapper, "mapper");
  }

  String toJson(GameState state, Optional<OverworldPosition> overworldPosition) throws IOException {
    Optional<MapCoordinate> mapCoord =
        overworldPosition.map(p -> new MapCoordinate(p.row(), p.column()));
    UltimaSessioneSalvataDto dto = mapper.toDto(state, mapCoord);
    return MAPPER.writeValueAsString(dto);
  }

  UltimaSessioneSalvataDto fromJson(String json) throws IOException {
    return MAPPER.readValue(json, UltimaSessioneSalvataDto.class);
  }

  GameState toGameState(String json) throws IOException {
    return mapper.fromDto(fromJson(json));
  }

  Optional<OverworldPosition> overworldPositionFromJson(String json) throws IOException {
    UltimaSessioneSalvataDto dto = fromJson(json);
    MapCoordinate coord = mapper.mapPositionFromDto(dto);
    if (coord == null) {
      return Optional.empty();
    }
    return Optional.of(new OverworldPosition(coord.row(), coord.column()));
  }
}
