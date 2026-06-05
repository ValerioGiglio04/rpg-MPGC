package it.unicam.cs.mpgc.rpg125664.model.entity;

import it.unicam.cs.mpgc.rpg125664.model.builder.CreatureHolderBuilder;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.CreatureHolderValidator;
import it.unicam.cs.mpgc.rpg125664.model.validation.implementations.Validators;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CreatureHolder implements Serializable {

  private final List<Creature> creatures;
  private long activeCatalogId;

  public static CreatureHolderBuilder builder() {
    return new CreatureHolderBuilder();
  }

  public CreatureHolder(List<Creature> creatures, long activeCatalogId) {
    this.creatures = new ArrayList<>(creatures);
    this.activeCatalogId = activeCatalogId;
    Validators.getCreatureHolderValidator().validate(this);
  }

  public List<Creature> creatures() {
    return Collections.unmodifiableList(creatures);
  }

  public long activeCatalogId() {
    return activeCatalogId;
  }

  public Creature activeCreature() {
    return findCreature(activeCatalogId);
  }

  public boolean isActive(Creature creature) {
    return creature.catalogId() == activeCatalogId;
  }

  public void switchTo(long catalogId) {
    CreatureHolderValidator.validateSwitch(creatures, catalogId);
    activeCatalogId = catalogId;
  }

  /** Se la creatura attiva e' KO, passa alla prima ancora in piedi nel team. */
  public void switchToFirstAliveIfNeeded() {
    if (!activeCreature().isKnockedOut()) {
      return;
    }
    creatures.stream()
        .filter(creature -> !creature.isKnockedOut())
        .findFirst()
        .ifPresent(creature -> switchTo(creature.catalogId()));
  }

  public boolean canSwitchTo(long catalogId) {
    return CreatureHolderValidator.canSwitchTo(creatures, catalogId);
  }

  public boolean allKnockedOut() {
    return creatures.stream().allMatch(Creature::isKnockedOut);
  }

  public void addCreature(Creature creature) {
    CreatureHolderValidator.validateNewCreature(creature);
    creatures.add(creature);
  }

  public void healAll() {
    creatures.forEach(Creature::healToFull);
    activeCatalogId = creatures.getFirst().catalogId();
  }

  /**
   * Cura piena per ogni creatura senza cambiare la creatura attiva. Usato all'inizio di un duello
   * di palestra cosi' la squadra rispetta la regola "riparti a salute piena" dopo un wipe, senza
   * resettare quale creatura guida.
   */
  public void healAllToFullPreservingActive() {
    creatures.forEach(Creature::healToFull);
  }

  private Creature findCreature(long catalogId) {
    for (Creature creature : creatures) {
      if (creature.catalogId() == catalogId) {
        return creature;
      }
    }
    throw new IllegalStateException("Active creature not in team: catalogId=" + catalogId);
  }
}
