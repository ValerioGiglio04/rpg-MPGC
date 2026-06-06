package it.unicam.cs.mpgc.rpg125664.model.persistence.base;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Objects;
import java.util.function.Function;

/**
 * Base per adapter Hibernate: apre un {@link EntityManager}, esegue l'operazione e lo chiude
 * sempre. {@link #inTransaction} avvolge l'operazione in begin/commit/rollback.
 */
public abstract class AbstractHibernateAdapter {

  protected final EntityManagerFactory entityManagerFactory;

  protected AbstractHibernateAdapter(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory =
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory");
  }

  /** Lettura/scrittura senza transazione esplicita (es. query di catalogo). */
  protected <T> T withEntityManager(Function<EntityManager, T> operation) {
    try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
      return operation.apply(entityManager);
    }
  }

  /** Operazione in transazione con commit automatico. */
  protected <T> T inTransaction(Function<EntityManager, T> operation) {
    try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
      return transact(entityManager, operation);
    }
  }

  private static <T> T transact(EntityManager entityManager, Function<EntityManager, T> operation) {
    entityManager.getTransaction().begin();
    try {
      T result = operation.apply(entityManager);
      entityManager.getTransaction().commit();
      return result;
    } catch (RuntimeException ex) {
      rollbackIfActive(entityManager);
      throw ex;
    }
  }

  private static void rollbackIfActive(EntityManager entityManager) {
    if (entityManager.getTransaction().isActive()) {
      entityManager.getTransaction().rollback();
    }
  }
}
