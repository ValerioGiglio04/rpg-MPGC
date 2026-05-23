package it.unicam.cs.mpgc.rpg125664.model.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

/**
 * Base astratta per gli adapter Hibernate: incapsula {@link EntityManagerFactory} e i pattern
 * comuni di apertura/chiusura dell'{@link EntityManager} (con o senza transazione).
 */
public abstract class AbstractHibernateAdapter {

  protected final EntityManagerFactory entityManagerFactory;

  protected AbstractHibernateAdapter(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory =
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory");
  }

  /** Esegue lavoro in sola lettura o senza transazione esplicita; chiude sempre l'EntityManager. */
  protected <T> T withEntityManager(Function<EntityManager, T> work) {
    EntityManager em = entityManagerFactory.createEntityManager();
    try {
      return work.apply(em);
    } finally {
      em.close();
    }
  }

  /** Come {@link #withEntityManager(Function)} ma consente {@link IOException} dal lavoro. */
  protected <T> T withEntityManagerThrowing(IoEntityWork<T> work) throws IOException {
    EntityManager em = entityManagerFactory.createEntityManager();
    try {
      return work.apply(em);
    } finally {
      em.close();
    }
  }

  /** Come {@link #inTransaction(Function)} ma consente {@link IOException} dal lavoro. */
  protected <T> T inTransactionThrowing(IoEntityWork<T> work) throws IOException {
    EntityManager em = entityManagerFactory.createEntityManager();
    try {
      em.getTransaction().begin();
      try {
        T result = work.apply(em);
        em.getTransaction().commit();
        return result;
      } catch (IOException ex) {
        if (em.getTransaction().isActive()) {
          em.getTransaction().rollback();
        }
        throw ex;
      } catch (RuntimeException ex) {
        if (em.getTransaction().isActive()) {
          em.getTransaction().rollback();
        }
        throw ex;
      }
    } finally {
      em.close();
    }
  }

  /** Apre una transazione, esegue il lavoro, fa commit o rollback in caso di errore. */
  protected <T> T inTransaction(Function<EntityManager, T> work) {
    EntityManager em = entityManagerFactory.createEntityManager();
    try {
      em.getTransaction().begin();
      try {
        T result = work.apply(em);
        em.getTransaction().commit();
        return result;
      } catch (RuntimeException ex) {
        if (em.getTransaction().isActive()) {
          em.getTransaction().rollback();
        }
        throw ex;
      }
    } finally {
      em.close();
    }
  }

  @FunctionalInterface
  protected interface IoEntityWork<T> {
    T apply(EntityManager em) throws IOException;
  }
}
