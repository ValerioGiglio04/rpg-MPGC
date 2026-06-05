package it.unicam.cs.mpgc.rpg125664.model.persistence.session;

import it.unicam.cs.mpgc.rpg125664.model.persistence.AbstractHibernateAdapter;
import it.unicam.cs.mpgc.rpg125664.model.catalog.CatalogIds;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.persistence.GameStateRepository;
import it.unicam.cs.mpgc.rpg125664.model.session.LoadedSession;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import it.unicam.cs.mpgc.rpg125664.model.session.SaveSessionCommand;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import it.unicam.cs.mpgc.rpg125664.model.session.SessionPersistenceException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/** Persiste piu' partite in {@code sessioni_salvate.dati_salvati_json} su H2. */
public final class HibernateGameStateRepository extends AbstractHibernateAdapter
    implements GameStateRepository {

  private static final String LOCAL_SAVE_FILTER = "s.idUtente is null";

  private static final DateTimeFormatter DEFAULT_NAME_TIME =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withLocale(Locale.ITALY);

  private final SessionJsonSerializer serializer;

  public HibernateGameStateRepository(
      EntityManagerFactory entityManagerFactory, SessioneJsonMapper mapper) {
    super(entityManagerFactory);
    this.serializer = new SessionJsonSerializer(Objects.requireNonNull(mapper, "mapper"));
  }

  @Override
  public boolean hasAnySave() {
    return countLocalSaves() > 0;
  }

  @Override
  public List<SavedSessionSummary> listSaves() {
    try {
      return withEntityManagerThrowing(
          em -> {
            TypedQuery<SessioneSalvataEntity> query =
                em.createQuery(
                    "select s from SessioneSalvataEntity s where "
                        + LOCAL_SAVE_FILTER
                        + " order by s.dataSalvataggio desc",
                    SessioneSalvataEntity.class);
            List<SessioneSalvataEntity> rows = query.getResultList();
            List<SavedSessionSummary> summaries = new ArrayList<>(rows.size());
            for (SessioneSalvataEntity row : rows) {
              summaries.add(toSummary(row));
            }
            return summaries;
          });
    } catch (IOException ex) {
      throw wrapIo("Cannot read saved session summaries", ex);
    }
  }

  @Override
  public Optional<Long> findLastPlayedSessionId() {
    return withEntityManager(
        em -> {
          TypedQuery<Long> query =
              em.createQuery(
                  "select s.idSessione from SessioneSalvataEntity s where "
                      + LOCAL_SAVE_FILTER
                      + " and s.ultimaGiocata = true",
                  Long.class);
          List<Long> ids = query.getResultList();
          if (!ids.isEmpty()) {
            return Optional.of(ids.getFirst());
          }
          TypedQuery<Long> fallback =
              em.createQuery(
                  "select s.idSessione from SessioneSalvataEntity s where "
                      + LOCAL_SAVE_FILTER
                      + " order by s.dataSalvataggio desc",
                  Long.class);
          fallback.setMaxResults(1);
          List<Long> latest = fallback.getResultList();
          return latest.isEmpty() ? Optional.empty() : Optional.of(latest.getFirst());
        });
  }

  @Override
  public long save(SaveSessionCommand command) {
    Instant now = Instant.now();
    String json;
    try {
      OverworldPosition position =
          command
              .overworldPosition()
              .orElseThrow(
                  () -> new IllegalStateException("Save command missing overworld position"));
      json = serializer.toJson(command.state(), position);
    } catch (IOException ex) {
      throw wrapIo("Cannot serialize session", ex);
    }
    try {
      return inTransactionThrowing(
          em -> {
            SessioneSalvataEntity row;
            if (command.sessionId().isPresent()) {
              long id = command.sessionId().orElseThrow();
              row = requireLocalSession(em, id);
              row.setDatiSalvatiJson(json);
              row.setDataSalvataggio(now);
              command.name().ifPresent(row::setNome);
              clearUltimaGiocata(em);
              row.setUltimaGiocata(true);
            } else {
              String nome = command.name().filter(n -> !n.isBlank()).orElse(defaultName(now));
              row = SessioneSalvataEntity.newRow(nome, now, json, CatalogIds.GIOCATORE_UMANO, true);
              clearUltimaGiocata(em);
              em.persist(row);
            }
            em.flush();
            return row.getIdSessione();
          });
    } catch (IOException ex) {
      throw wrapIo("Cannot save session", ex);
    }
  }

  @Override
  public LoadedSession load(long sessionId) {
    try {
      return withEntityManagerThrowing(
          em -> {
            SessioneSalvataEntity row = requireLocalSession(em, sessionId);
            GameState state = serializer.toGameState(row.getDatiSalvatiJson());
            Optional<OverworldPosition> position =
                serializer.overworldPositionFromJson(row.getDatiSalvatiJson());
            return new LoadedSession(state, position);
          });
    } catch (IOException ex) {
      throw wrapIo("Cannot load session " + sessionId, ex);
    }
  }

  @Override
  public void delete(long sessionId) {
    try {
      inTransactionThrowing(
          em -> {
            SessioneSalvataEntity row = requireLocalSession(em, sessionId);
            em.remove(row);
            return null;
          });
    } catch (IOException ex) {
      throw wrapIo("Cannot delete session " + sessionId, ex);
    }
  }

  @Override
  public void markLastPlayed(long sessionId) {
    inTransaction(
        em -> {
          SessioneSalvataEntity row = em.find(SessioneSalvataEntity.class, sessionId);
          if (row != null && row.getIdUtente() == null) {
            clearUltimaGiocata(em);
            row.setUltimaGiocata(true);
          }
          return null;
        });
  }

  private static SessioneSalvataEntity requireLocalSession(EntityManager em, long sessionId)
      throws IOException {
    SessioneSalvataEntity row = em.find(SessioneSalvataEntity.class, sessionId);
    if (row == null || row.getIdUtente() != null) {
      throw new IOException("Saved session not found: " + sessionId);
    }
    return row;
  }

  private SavedSessionSummary toSummary(SessioneSalvataEntity row) throws IOException {
    UltimaSessioneSalvataDto dto = serializer.fromJson(row.getDatiSalvatiJson());
    return new SavedSessionSummary(
        row.getIdSessione(), row.getNome(), row.getDataSalvataggio(), dto.getNumPuntiFama());
  }

  private long countLocalSaves() {
    return withEntityManager(
        em ->
            em.createQuery(
                    "select count(s) from SessioneSalvataEntity s where " + LOCAL_SAVE_FILTER,
                    Long.class)
                .getSingleResult());
  }

  private static void clearUltimaGiocata(EntityManager em) {
    em.createQuery(
            "update SessioneSalvataEntity s set s.ultimaGiocata = false where " + LOCAL_SAVE_FILTER)
        .executeUpdate();
  }

  private static String defaultName(Instant instant) {
    return "Partita " + DEFAULT_NAME_TIME.format(instant.atZone(ZoneId.systemDefault()));
  }

  private static SessionPersistenceException wrapIo(String message, IOException cause) {
    return new SessionPersistenceException(message, cause);
  }
}
