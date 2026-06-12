package it.unicam.cs.mpgc.rpg125664.model.catalog;

import it.unicam.cs.mpgc.rpg125664.model.entity.Creature;
import it.unicam.cs.mpgc.rpg125664.model.entity.CreatureHolder;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymBoss;
import it.unicam.cs.mpgc.rpg125664.model.entity.GymRoom;
import it.unicam.cs.mpgc.rpg125664.model.entity.Move;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Dati statici di gioco (creature, palestre, configurazione iniziale). Sta nel layer di dominio
 * cosi' gli adapter (Hibernate, codice di seed, ecc.) possono fornirli senza invertire la regola di
 * dipendenza. I metodi di lookup producono nuove istanze di dominio mutabili cosi' i chiamanti
 * possono danneggiarle / curarle senza intaccare il catalogo.
 */
public final class GameCatalog {

  private final NewGameSettings settings;
  private final Map<Long, CreatureTemplate> creaturesById;
  private final Map<Long, GymTemplate> gymsById;
  private final List<Long> orderedGymIds;

  public GameCatalog(
    NewGameSettings settings,
    List<CreatureTemplate> creatures,
    List<GymTemplate> gyms
  ) {
    this.settings = Objects.requireNonNull(settings, "settings");
    Objects.requireNonNull(creatures, "creatures");
    Objects.requireNonNull(gyms, "gyms");
    this.creaturesById = indexById(creatures, CreatureTemplate::id, "creature");
    this.gymsById = indexById(gyms, GymTemplate::id, "gym");
    this.orderedGymIds = gyms
      .stream()
      .sorted((a, b) -> Integer.compare(a.order(), b.order()))
      .map(GymTemplate::id)
      .toList();
  }

  public NewGameSettings settings() {
    return settings;
  }

  public Creature buildCreature(long catalogId) {
    return buildCreature(catalogId, null);
  }

  public Creature buildCreature(long catalogId, Integer currentHealth) {
    CreatureTemplate template = requireCreature(catalogId);
    List<Move> moves = template.moves().stream().map(this::toMove).toList();
    return Creature.builder()
      .catalogId(template.id())
      .name(template.name())
      .role(template.role())
      .skinPath(template.skinPath())
      .maxHealth(template.maxHealth())
      .currentHealth(currentHealth != null ? currentHealth : template.maxHealth())
      .attack(template.attack())
      .defense(template.defense())
      .speed(template.speed())
      .moves(moves)
      .build();
  }

  public GymRoom buildGym(long gymId, boolean completed) {
    GymTemplate template = requireGym(gymId);
    List<Creature> bossTeam = template
      .boss()
      .creatureIds()
      .stream()
      .map(this::buildCreature)
      .toList();
    GymBoss boss = GymBoss.builder()
      .name(template.boss().name())
      .holder(CreatureHolder.builder().creatures(bossTeam).build())
      .pointsReward(template.boss().pointsReward())
      .build();
    return GymRoom.builder()
      .id(template.id())
      .name(template.name())
      .connectedGymIds(template.connectedGymIds())
      .boss(boss)
      .requiredPoints(template.requiredPoints())
      .completed(completed)
      .build();
  }

  public List<GymRoom> buildAllGyms(Map<Long, Boolean> completionByGymId) {
    Map<Long, Boolean> completion = completionByGymId == null ? Map.of() : completionByGymId;
    return orderedGymIds
      .stream()
      .map(gymId -> buildGym(gymId, completion.getOrDefault(gymId, false)))
      .collect(Collectors.toCollection(ArrayList::new));
  }

  public List<Long> orderedGymIds() {
    return Collections.unmodifiableList(orderedGymIds);
  }

  public boolean hasCreature(long catalogId) {
    return creaturesById.containsKey(catalogId);
  }

  public boolean hasGym(long gymId) {
    return gymsById.containsKey(gymId);
  }

  public String creatureSkinPath(long catalogId) {
    return requireCreature(catalogId).skinPath();
  }

  private Move toMove(MoveTemplate template) {
    return Move.builder()
      .name(template.name())
      .power(template.power())
      .accuracy(template.accuracy())
      .description(template.description())
      .build();
  }

  private CreatureTemplate requireCreature(long catalogId) {
    CreatureTemplate template = creaturesById.get(catalogId);
    if (template == null) {
      throw new IllegalArgumentException("Unknown creature catalog id: " + catalogId);
    }
    return template;
  }

  private GymTemplate requireGym(long gymId) {
    GymTemplate template = gymsById.get(gymId);
    if (template == null) {
      throw new IllegalArgumentException("Unknown gym catalog id: " + gymId);
    }
    return template;
  }

  private static <T> Map<Long, T> indexById(
    List<T> values,
    java.util.function.ToLongFunction<T> idExtractor,
    String label
  ) {
    Map<Long, T> index = values.stream().collect(
      LinkedHashMap::new,
      (map, value) -> {
        long id = idExtractor.applyAsLong(value);
        if (id <= 0) {
          throw new IllegalArgumentException("Catalog " + label + " is missing id");
        }
        if (map.put(id, value) != null) {
          throw new IllegalArgumentException("Duplicate catalog " + label + " id: " + id);
        }
      },
      LinkedHashMap::putAll
    );
    return Collections.unmodifiableMap(index);
  }
}
