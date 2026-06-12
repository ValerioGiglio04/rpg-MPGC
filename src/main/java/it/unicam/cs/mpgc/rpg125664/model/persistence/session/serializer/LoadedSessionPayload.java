package it.unicam.cs.mpgc.rpg125664.model.persistence.session.serializer;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.session.OverworldPosition;
import java.util.Optional;

/** Risultato di una singola deserializzazione del JSON in {@code dati_salvati_json}. */
public record LoadedSessionPayload(
  GameState gameState,
  Optional<OverworldPosition> overworldPosition
) {}
