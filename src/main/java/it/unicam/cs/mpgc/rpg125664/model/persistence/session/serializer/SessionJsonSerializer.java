package it.unicam.cs.mpgc.rpg125664.model.persistence.session.serializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.dto.UltimaSessioneSalvataDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.mapper.SessioneJsonMapper;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/** Serializza {@link UltimaSessioneSalvataDto} in {@code dati_salvati_json}. */
public final class SessionJsonSerializer {

  private static final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private final SessioneJsonMapper mapper;

  public SessionJsonSerializer(SessioneJsonMapper mapper) {
    this.mapper = Objects.requireNonNull(mapper, "mapper");
  }

  public String toJson(GameState state, OverworldPosition overworldPosition) throws IOException {
    UltimaSessioneSalvataDto dto = mapper.toDto(state, overworldPosition);
    return MAPPER.writeValueAsString(dto);
  }

  public UltimaSessioneSalvataDto fromJson(String json) throws IOException {
    return MAPPER.readValue(json, UltimaSessioneSalvataDto.class);
  }

  public GameState toGameState(String json) throws IOException {
    return mapper.fromDto(fromJson(json));
  }

  public Optional<OverworldPosition> overworldPositionFromJson(String json) throws IOException {
    UltimaSessioneSalvataDto dto = fromJson(json);
    OverworldPosition position = mapper.mapPositionFromDto(dto);
    return Optional.ofNullable(position);
  }
}
