package it.unicam.cs.mpgc.rpg125664.model.persistence.session.implementations;

import it.unicam.cs.mpgc.rpg125664.model.persistence.session.SessioneSalvataSummaryMapper;
import it.unicam.cs.mpgc.rpg125664.model.persistence.session.serializer.SessionJsonSerializer;
import jakarta.persistence.EntityManagerFactory;
import java.util.Objects;

/** Opzioni JPA e serializzazione per {@link HibernateGameStateRepository}. */
public final class SessionRepositoryOptions {

  private final EntityManagerFactory entityManagerFactory;
  private final SessioneSalvataJpaRepository jpaRepository;
  private final SessionJsonSerializer serializer;
  private final SessioneSalvataSummaryMapper summaryMapper;

  private SessionRepositoryOptions(Builder builder) {
    this.entityManagerFactory =
        Objects.requireNonNull(builder.entityManagerFactory, "entityManagerFactory");
    this.jpaRepository = Objects.requireNonNull(builder.jpaRepository, "jpaRepository");
    this.serializer = Objects.requireNonNull(builder.serializer, "serializer");
    this.summaryMapper = Objects.requireNonNull(builder.summaryMapper, "summaryMapper");
  }

  public static Builder builder() {
    return new Builder();
  }

  public EntityManagerFactory entityManagerFactory() {
    return entityManagerFactory;
  }

  public SessioneSalvataJpaRepository jpaRepository() {
    return jpaRepository;
  }

  public SessionJsonSerializer serializer() {
    return serializer;
  }

  public SessioneSalvataSummaryMapper summaryMapper() {
    return summaryMapper;
  }

  public static final class Builder {

    private EntityManagerFactory entityManagerFactory;
    private SessioneSalvataJpaRepository jpaRepository;
    private SessionJsonSerializer serializer;
    private SessioneSalvataSummaryMapper summaryMapper;

    public Builder entityManagerFactory(EntityManagerFactory entityManagerFactory) {
      this.entityManagerFactory = entityManagerFactory;
      return this;
    }

    public Builder jpaRepository(SessioneSalvataJpaRepository jpaRepository) {
      this.jpaRepository = jpaRepository;
      return this;
    }

    public Builder serializer(SessionJsonSerializer serializer) {
      this.serializer = serializer;
      return this;
    }

    public Builder summaryMapper(SessioneSalvataSummaryMapper summaryMapper) {
      this.summaryMapper = summaryMapper;
      return this;
    }

    public SessionRepositoryOptions build() {
      return new SessionRepositoryOptions(this);
    }
  }
}
