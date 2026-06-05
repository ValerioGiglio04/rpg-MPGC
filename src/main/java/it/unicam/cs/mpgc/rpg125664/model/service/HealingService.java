package it.unicam.cs.mpgc.rpg125664.model.service;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import java.util.Objects;

/**
 * Cura piena a pagamento per una singola creatura del party: il costo scala col HP mancante,
 * limitato dalla gloria spendibile cosi' il giocatore non scende sotto le soglie necessarie per le
 * palestre attualmente sfidabili.
 */
public final class HealingService {

  /** Punti gloria per HP mancante (minimo un punto per cura). */
  private static final double GLORY_PER_MISSING_HP = 1.0;

  private static final int MIN_HEAL_COST = 1;

  public int healCostForMissingHp(int missingHp) {
    if (missingHp <= 0) {
      return 0;
    }
    return Math.max(MIN_HEAL_COST, (int) Math.ceil(missingHp * GLORY_PER_MISSING_HP));
  }

  /**
   * Gloria che il giocatore puo' spendere senza perdere l'accesso ad una palestra che sia
   * attualmente {@link GameState#canChallengeGym(GymRoom)}.
   */
  public int spendableGlory(GameState state) {
    Objects.requireNonNull(state, "state");
    int points = state.player().score().points();
    int reserve =
        state.gyms().stream()
            .filter(g -> state.canChallengeGym(g))
            .mapToInt(GymRoom::requiredPoints)
            .max()
            .orElse(0);
    return Math.max(0, points - reserve);
  }

  public int healCostForCreature(GameState state, long creatureCatalogId) {
    Creature creature = creatureByCatalogId(state, creatureCatalogId);
    return healCostForMissingHp(creature.maxHealth() - creature.currentHealth());
  }

  public void healCreature(GameState state, long creatureCatalogId) {
    Objects.requireNonNull(state, "state");
    Creature creature = creatureByCatalogId(state, creatureCatalogId);
    int missingHp = creature.maxHealth() - creature.currentHealth();
    if (missingHp <= 0) {
      throw new HealingException(HealingError.FULL_HP);
    }
    int cost = healCostForMissingHp(missingHp);
    int spendable = spendableGlory(state);
    if (cost > state.player().score().points()) {
      throw new HealingException(HealingError.INSUFFICIENT_GLORY);
    }
    if (cost > spendable) {
      throw new HealingException(HealingError.GYM_FLOOR);
    }
    state.player().score().spend(cost);
    creature.healToFull();
  }

  private static Creature creatureByCatalogId(GameState state, long creatureCatalogId) {
    CreatureHolder holder = state.player().holder();
    return holder.creatures().stream()
        .filter(creature -> creature.catalogId() == creatureCatalogId)
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Creature catalog id not in team: " + creatureCatalogId));
  }
}
