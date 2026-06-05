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

  protected <T> T withEntityManager(Function<EntityManager, T> work) {
    return runWithEntityManager(work, false);
  }

  protected <T> T withEntityManagerThrowing(IoEntityWork<T> work) throws IOException {
    try {
      return runWithEntityManager(
          em -> {
            try {
              return work.apply(em);
            } catch (IOException ex) {
              throw new IoRuntimeException(ex);
            }
          },
          false);
    } catch (IoRuntimeException ex) {
      throw ex.cause();
    }
  }

  protected <T> T inTransaction(Function<EntityManager, T> work) {
    return runWithEntityManager(work, true);
  }

  protected <T> T inTransactionThrowing(IoEntityWork<T> work) throws IOException {
    try {
      return runWithEntityManager(
          em -> {
            try {
              return work.apply(em);
            } catch (IOException ex) {
              throw new IoRuntimeException(ex);
            }
          },
          true);
    } catch (IoRuntimeException ex) {
      throw ex.cause();
    }
  }

  private <T> T runWithEntityManager(Function<EntityManager, T> work, boolean transactional) {
    EntityManager em = entityManagerFactory.createEntityManager();
    try {
      if (!transactional) {
        return work.apply(em);
      }
      em.getTransaction().begin();
      try {
        T result = work.apply(em);
        em.getTransaction().commit();
        return result;
      } catch (IoRuntimeException ex) {
        rollbackIfActive(em);
        throw ex;
      } catch (RuntimeException ex) {
        rollbackIfActive(em);
        throw ex;
      }
    } finally {
      em.close();
    }
  }

  private static void rollbackIfActive(EntityManager em) {
    if (em.getTransaction().isActive()) {
      em.getTransaction().rollback();
    }
  }

  @FunctionalInterface
  protected interface IoEntityWork<T> {
    T apply(EntityManager em) throws IOException;
  }

  private static final class IoRuntimeException extends RuntimeException {
    private final IOException cause;

    IoRuntimeException(IOException cause) {
      super(cause);
      this.cause = cause;
    }

    IOException cause() {
      return cause;
    }
  }
}
