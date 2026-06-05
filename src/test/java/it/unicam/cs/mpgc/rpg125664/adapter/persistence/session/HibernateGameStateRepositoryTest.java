package it.unicam.cs.mpgc.rpg125664.model.persistence.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.session.LoadedSession;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import it.unicam.cs.mpgc.rpg125664.model.session.SaveSessionCommand;
import it.unicam.cs.mpgc.rpg125664.support.TestCatalogFactory;
import it.unicam.cs.mpgc.rpg125664.support.TestGameStates;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HibernateGameStateRepositoryTest {

  private static final String PERSISTENCE_UNIT = "rpg-test";
  private static final OverworldPosition DEFAULT_MAP_POSITION = new OverworldPosition(4, 1);

  private EntityManagerFactory emf;
  private HibernateGameStateRepository repository;

  @BeforeEach
  void setUp() {
    emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    repository =
        new HibernateGameStateRepository(emf, new SessioneJsonMapper(TestCatalogFactory.minimal()));
    clearSessions();
  }

  @AfterEach
  void tearDown() {
    if (emf != null && emf.isOpen()) {
      emf.close();
    }
  }

  @Test
  void saveAndLoadRoundTrip() throws IOException {
    GameState state = TestGameStates.challengeableState(TestCatalogFactory.minimal());
    long id =
        repository.save(
            new SaveSessionCommand(
                state, Optional.of(new OverworldPosition(2, 5)), Optional.empty(), Optional.of("Slot A")));

    LoadedSession loaded = repository.load(id);

    assertEquals(state.currentGymId(), loaded.state().currentGymId());
    assertEquals(state.player().score().points(), loaded.state().player().score().points());
    assertTrue(loaded.overworldPosition().isPresent());
    assertEquals(2, loaded.overworldPosition().orElseThrow().row());
    assertEquals(5, loaded.overworldPosition().orElseThrow().column());
  }

  @Test
  void saveUpdatesExistingSession() throws IOException {
    GameState state = TestGameStates.challengeableState(TestCatalogFactory.minimal());
    long id =
        repository.save(
            new SaveSessionCommand(
                state, Optional.of(DEFAULT_MAP_POSITION), Optional.empty(), Optional.of("First")));
    state.player().score().add(5);
    repository.save(
        new SaveSessionCommand(
            state, Optional.of(DEFAULT_MAP_POSITION), Optional.of(id), Optional.of("Renamed")));

    LoadedSession loaded = repository.load(id);

    assertEquals(5, loaded.state().player().score().points());
    assertEquals("Renamed", repository.listSaves().getFirst().name());
  }

  @Test
  void deleteRemovesSession() throws IOException {
    long id =
        repository.save(
            new SaveSessionCommand(
                TestGameStates.challengeableState(TestCatalogFactory.minimal()),
                Optional.of(DEFAULT_MAP_POSITION),
                Optional.empty(),
                Optional.empty()));

    repository.delete(id);

    assertFalse(repository.hasAnySave());
    assertTrue(repository.listSaves().isEmpty());
  }

  @Test
  void markLastPlayedFlagsSession() throws IOException {
    long first =
        repository.save(
            new SaveSessionCommand(
                TestGameStates.challengeableState(TestCatalogFactory.minimal()),
                Optional.of(DEFAULT_MAP_POSITION),
                Optional.empty(),
                Optional.of("One")));
    long second =
        repository.save(
            new SaveSessionCommand(
                TestGameStates.challengeableState(TestCatalogFactory.minimal()),
                Optional.of(DEFAULT_MAP_POSITION),
                Optional.empty(),
                Optional.of("Two")));

    repository.markLastPlayed(first);

    assertEquals(Optional.of(first), repository.findLastPlayedSessionId());
    repository.markLastPlayed(second);
    assertEquals(Optional.of(second), repository.findLastPlayedSessionId());
  }

  private void clearSessions() {
    try (EntityManager em = emf.createEntityManager()) {
      em.getTransaction().begin();
      em.createQuery("delete from SessioneSalvataEntity").executeUpdate();
      em.getTransaction().commit();
    }
  }
}
