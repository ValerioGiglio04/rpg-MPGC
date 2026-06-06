package it.unicam.cs.mpgc.rpg125664.model.persistence.session;

import it.unicam.cs.mpgc.rpg125664.model.persistence.AbstractHibernateAdapter;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.entities.SessioneSalvataEntity;
import it.unicam.cs.mpgc.rpg125664.model.session.SessionPersistenceException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/** Accesso JPQL alle righe locali di {@code sessioni_salvate}. */
public final class SessioneSalvataJpaRepository extends AbstractHibernateAdapter {

  private static final String LOCAL_SAVE_FILTER = "s.idUtente is null";

  public SessioneSalvataJpaRepository(EntityManagerFactory entityManagerFactory) {
    super(entityManagerFactory);
  }

  List<SessioneSalvataEntity> findAllLocal() {
    return withEntityManager(
        em ->
            em.createQuery(
                    "select s from SessioneSalvataEntity s where "
                        + LOCAL_SAVE_FILTER
                        + " order by s.dataSalvataggio desc",
                    SessioneSalvataEntity.class)
                .getResultList());
  }

  Optional<Long> findLastPlayedId() {
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

  long countLocal() {
    return withEntityManager(
        em ->
            em.createQuery(
                    "select count(s) from SessioneSalvataEntity s where " + LOCAL_SAVE_FILTER,
                    Long.class)
                .getSingleResult());
  }

  SessioneSalvataEntity requireLocal(EntityManager em, long sessionId) {
    SessioneSalvataEntity row = em.find(SessioneSalvataEntity.class, sessionId);
    if (row == null || row.getIdUtente() != null) {
      throw new SessionPersistenceException("Saved session not found: " + sessionId);
    }
    return row;
  }

  void clearUltimaGiocata(EntityManager em) {
    em.createQuery(
            "update SessioneSalvataEntity s set s.ultimaGiocata = false where " + LOCAL_SAVE_FILTER)
        .executeUpdate();
  }
}
