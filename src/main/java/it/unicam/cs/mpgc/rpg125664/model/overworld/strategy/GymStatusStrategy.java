package it.unicam.cs.mpgc.rpg125664.model.overworld.strategy;

import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.overworld.GymStatus;

/** Strategy per calcolare lo stato visuale di una palestra sulla mappa overworld. */
public interface GymStatusStrategy {
  GymStatus resolve(GameState state, GymRoom gym);
}
