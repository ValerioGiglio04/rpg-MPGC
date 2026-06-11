package it.unicam.cs.mpgc.rpg125664.model.persistence.session.implementations;

import it.unicam.cs.mpgc.rpg125664.model.catalog.CatalogIds;
import it.unicam.cs.mpgc.rpg125664.model.persistence.GameStateRepository;
import it.unicam.cs.mpgc.rpg125664.model.persistence.base.AbstractHibernateAdapter;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.SessioneSalvataSummaryMapper;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.entities.SessioneSalvataEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.serializer.LoadedSessionPayload;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.serializer.SessionJsonSerializer;
import it.unicam.cs.mpgc.rpg125664.model.session.LoadedSession;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import it.unicam.cs.mpgc.rpg125664.model.session.SaveSessionCommand;
import it.unicam.cs.mpgc.rpg125664.model.session.SaveSlotLabels;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import it.unicam.cs.mpgc.rpg125664.model.session.SessionPersistenceException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Persiste piu' partite in {@code sessioni_salvate.dati_salvati_json} su H2. */
public final class HibernateGameStateRepository extends AbstractHibernateAdapter
    implements GameStateRepository {

  private final SessioneSalvataJpaRepository jpaRepository;
  private final SessionJsonSerializer serializer;
  private final SessioneSalvataSummaryMapper summaryMapper;

  public HibernateGameStateRepository(
      EntityManagerFactory entityManagerFactory,
      SessioneSalvataJpaRepository jpaRepository,
      SessionJsonSerializer serializer,
      SessioneSalvataSummaryMapper summaryMapper) {
    super(entityManagerFactory);
    this.jpaRepository = Objects.requireNonNull(jpaRepository, "jpaRepository");
    this.serializer = Objects.requireNonNull(serializer, "serializer");
    this.summaryMapper = Objects.requireNonNull(summaryMapper, "summaryMapper");
  }

  @Override
  public boolean hasAnySave() {
    return jpaRepository.countLocal() > 0;
  }

  @Override
  public List<SavedSessionSummary> listSaves() {
    List<SessioneSalvataEntity> rows = jpaRepository.findAllLocal();
    try {
      List<SavedSessionSummary> summaries = new ArrayList<>(rows.size());
      for (SessioneSalvataEntity row : rows) {
        try {
          summaries.add(summaryMapper.toSummary(row));
        } catch (IOException ex) {
          throw new UncheckedIOException(ex);
        }
      }
      return summaries;
    } catch (UncheckedIOException ex) {
      throw wrapIo("Cannot read saved session summaries", ex.getCause());
    }
  }

  @Override
  public Optional<Long> findLastPlayedSessionId() {
    return jpaRepository.findLastPlayedId();
  }

  @Override
  public long save(SaveSessionCommand command) {
    Instant now = Instant.now();
    String json = serializeOrThrow(command);
    return inTransaction(
        em -> {
          SessioneSalvataEntity row =
              command.sessionId().isPresent()
                  ? updateExisting(em, command, json, now)
                  : insertNew(em, command, json, now);
          em.flush();
          return row.getIdSessione();
        });
  }

  @Override
  public LoadedSession load(long sessionId) {
    SessioneSalvataEntity row = withEntityManager(em -> jpaRepository.requireLocal(em, sessionId));
    try {
      LoadedSessionPayload payload = serializer.deserialize(row.getDatiSalvatiJson());
      return new LoadedSession(payload.gameState(), payload.overworldPosition());
    } catch (IOException ex) {
      throw wrapIo("Cannot load session " + sessionId, ex);
    }
  }

  @Override
  public void delete(long sessionId) {
    inTransaction(
        em -> {
          SessioneSalvataEntity row = jpaRepository.requireLocal(em, sessionId);
          em.remove(row);
          return null;
        });
  }

  @Override
  public void markLastPlayed(long sessionId) {
    inTransaction(
        em -> {
          SessioneSalvataEntity row = jpaRepository.requireLocal(em, sessionId);
          jpaRepository.clearUltimaGiocata(em);
          row.setUltimaGiocata(true);
          return null;
        });
  }

  private String serializeOrThrow(SaveSessionCommand command) {
    try {
      OverworldPosition position =
          command
              .overworldPosition()
              .orElseThrow(
                  () -> new IllegalStateException("Save command missing overworld position"));
      return serializer.toJson(command.state(), position);
    } catch (IOException ex) {
      throw wrapIo("Cannot serialize session", ex);
    }
  }

  private SessioneSalvataEntity updateExisting(
      EntityManager em, SaveSessionCommand command, String json, Instant now) {
    long id = command.sessionId().orElseThrow();
    SessioneSalvataEntity row = jpaRepository.requireLocal(em, id);
    row.setDatiSalvatiJson(json);
    row.setDataSalvataggio(now);
    command.name().ifPresent(row::setNome);
    jpaRepository.clearUltimaGiocata(em);
    row.setUltimaGiocata(true);
    return row;
  }

  private SessioneSalvataEntity insertNew(
      EntityManager em, SaveSessionCommand command, String json, Instant now) {
    String nome = command.name().filter(n -> !n.isBlank()).orElse(defaultName(now));
    SessioneSalvataEntity row =
        SessioneSalvataEntity.newRow(nome, now, json, CatalogIds.GIOCATORE_UMANO, true);
    jpaRepository.clearUltimaGiocata(em);
    em.persist(row);
    return row;
  }

  private static String defaultName(Instant instant) {
    return SaveSlotLabels.defaultSaveName(instant);
  }

  private static SessionPersistenceException wrapIo(String message, IOException cause) {
    return new SessionPersistenceException(message, cause);
  }
}
