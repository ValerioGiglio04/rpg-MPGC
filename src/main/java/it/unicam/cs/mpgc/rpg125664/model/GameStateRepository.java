package it.unicam.cs.mpgc.rpg125664.model;

import it.unicam.cs.mpgc.rpg125664.model.session.LoadedSession;
import it.unicam.cs.mpgc.rpg125664.model.session.SaveSessionCommand;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Port per persistere e ripristinare la parte dinamica del gioco (team del giocatore, progresso
 * palestre). I dati statici di catalogo vengono reidratati tramite {@link
 * it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog} quando si ricostruisce il {@link
 * it.unicam.cs.mpgc.rpg125664.model.entity.GameState}.
 */
public interface GameStateRepository {

  /** Se esiste almeno un salvataggio (es. per abilitare "Continua" nel menu principale). */
  boolean hasAnySave();

  /** Salvataggi locali pre-login ({@code id_utente} nullo nel DB). */
  List<SavedSessionSummary> listSaves();

  Optional<Long> findLastPlayedSessionId();

  long save(SaveSessionCommand command) throws IOException;

  LoadedSession load(long sessionId) throws IOException;

  void delete(long sessionId) throws IOException;

  void markLastPlayed(long sessionId);
}
