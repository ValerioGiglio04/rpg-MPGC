package it.unicam.cs.mpgc.rpg125664.model.persistence.session;

import it.unicam.cs.mpgc.rpg125664.model.persistence.session.dto.UltimaSessioneSalvataDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.entities.SessioneSalvataEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.serializer.SessionJsonSerializer;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import java.io.IOException;
import java.util.Objects;

/** Mappa una riga JPA in {@link SavedSessionSummary} per la UI. */
public final class SessioneSalvataSummaryMapper {

  private final SessionJsonSerializer serializer;

  public SessioneSalvataSummaryMapper(SessionJsonSerializer serializer) {
    this.serializer = Objects.requireNonNull(serializer, "serializer");
  }

  SavedSessionSummary toSummary(SessioneSalvataEntity row) throws IOException {
    UltimaSessioneSalvataDto dto = serializer.fromJson(row.getDatiSalvatiJson());
    return new SavedSessionSummary(
        row.getIdSessione(), row.getNome(), row.getDataSalvataggio(), dto.getNumPuntiFama());
  }
}
