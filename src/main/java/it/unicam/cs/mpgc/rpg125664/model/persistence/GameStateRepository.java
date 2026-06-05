package it.unicam.cs.mpgc.rpg125664.model.persistence;

import it.unicam.cs.mpgc.rpg125664.model.session.LoadedSession;
import it.unicam.cs.mpgc.rpg125664.model.session.SaveSessionCommand;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import java.util.List;
import java.util.Optional;

/**
 * Port per persistere e ripristinare la parte dinamica del gioco (team del giocatore, progresso
 * palestre). I dati statici di catalogo vengono reidratati tramite {@link
 * it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog} quando si ricostruisce il {@link
 * it.unicam.cs.mpgc.rpg125664.model.entity.GameState}.
 */
public interface GameStateRepository {

  boolean hasAnySave();

  List<SavedSessionSummary> listSaves();

  Optional<Long> findLastPlayedSessionId();

  long save(SaveSessionCommand command);

  LoadedSession load(long sessionId);

  void delete(long sessionId);

  void markLastPlayed(long sessionId);
}
