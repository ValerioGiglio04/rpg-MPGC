package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.service.HealingException;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.GameState;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import it.unicam.cs.mpgc.rpg125664.view.support.UiErrorReporter;
import java.util.Objects;

public final class HubPresenter {

  private final GameModel gameModel;

  public HubPresenter(GameModel gameModel) {
    this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
  }

  public GameState state() {
    return gameModel.gameState();
  }

  public String subtitle() {
    GameState state = gameModel.gameState();
    return Messages.format("hub.subtitle", state.player().name(), state.player().score().points());
  }

  public int spendableGlory() {
    return gameModel.spendableGloryForHealing();
  }

  public TeamRowViewModel teamRow(Creature creature, boolean active, int spendable) {
    long catalogId = creature.catalogId();
    int cost = gameModel.healCostForCreature(catalogId);
    GameState state = gameModel.gameState();
    boolean fullHp = creature.currentHealth() >= creature.maxHealth();
    int playerPoints = state.player().score().points();
    boolean healEnabled = !fullHp && cost <= playerPoints && cost <= spendable;
    return new TeamRowViewModel(
        creature,
        catalogId,
        active,
        creature.isKnockedOut(),
        cost,
        healEnabled,
        healTooltip(state, creature, cost, spendable));
  }

  public void healCreature(long catalogId) {
    try {
      gameModel.healPlayerCreature(catalogId);
    } catch (HealingException ex) {
      UiErrorReporter.reportActionError("heal failed: " + ex.error(), ex);
    } catch (RuntimeException ex) {
      UiErrorReporter.reportActionError("heal failed", ex);
    }
  }

  public void selectCreature(long catalogId) {
    try {
      gameModel.switchPlayerCreature(catalogId);
    } catch (RuntimeException ex) {
      UiErrorReporter.reportActionError("creature switch failed", ex);
    }
  }

  private static String healTooltip(GameState state, Creature creature, int cost, int spendable) {
    if (creature.currentHealth() >= creature.maxHealth()) {
      return Messages.get("hub.heal.tooltip.fullHp");
    }
    if (cost > state.player().score().points()) {
      return Messages.format("hub.heal.tooltip.noGlory", cost);
    }
    if (cost > spendable) {
      return Messages.format("hub.heal.tooltip.gymFloor", cost, spendable);
    }
    return Messages.format("hub.heal.tooltip.ok", cost);
  }

  public record TeamRowViewModel(
      Creature creature,
      long catalogId,
      boolean active,
      boolean knockedOut,
      int healCost,
      boolean healEnabled,
      String healTooltip) {}
}
