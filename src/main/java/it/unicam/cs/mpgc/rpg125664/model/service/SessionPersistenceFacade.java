package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.overworld.OverworldSpawnPosition;
import it.unicam.cs.mpgc.rpg125664.model.persistence.GameStateRepository;
import it.unicam.cs.mpgc.rpg125664.model.session.LoadedSession;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import it.unicam.cs.mpgc.rpg125664.model.session.SaveSessionCommand;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Facciata salvataggio e caricamento sessioni verso il repository. */
public final class SessionPersistenceFacade {

  private final GameStateRepository repository;

  public SessionPersistenceFacade(GameStateRepository repository) {
    this.repository = Objects.requireNonNull(repository, "repository");
  }

  public boolean hasAnySave() {
    return repository.hasAnySave();
  }

  public List<SavedSessionSummary> listSaves() {
    return repository.listSaves();
  }

  public long save(SaveSessionCommand command) {
    OverworldPosition position =
        command
            .overworldPosition()
            .orElseGet(() -> OverworldSpawnPosition.defaultFor(command.state()));
    return repository.save(
        new SaveSessionCommand(
            command.state(), Optional.of(position), command.sessionId(), command.name()));
  }

  public LoadedSession load(long sessionId) {
    return repository.load(sessionId);
  }

  public void delete(long sessionId) {
    repository.delete(sessionId);
  }

  public void markLastPlayed(long sessionId) {
    repository.markLastPlayed(sessionId);
  }

  public Optional<Long> findLastPlayedSessionId() {
    return repository.findLastPlayedSessionId();
  }
}
