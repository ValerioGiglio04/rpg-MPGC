package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.mapper;

import it.unicam.cs.mpgc.rpg125664.model.catalog.BossTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.CreatureTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GymTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.MoveTemplate;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.CreaturaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.MossaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.PalestraEntity;
import java.util.List;
import java.util.Map;

/** Mappa le entity JPA del catalogo sui template di dominio. */
public final class CatalogEntityMapper {

  private CatalogEntityMapper() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static MoveTemplate toMove(MossaEntity moveEntity) {
    return new MoveTemplate(
      moveEntity.getNome(),
      moveEntity.getPotenza(),
      moveEntity.getPrecisione(),
      moveEntity.getDescrizione()
    );
  }

  public static CreatureTemplate toCreature(
    CreaturaEntity creatureEntity,
    List<MossaEntity> moveEntities
  ) {
    List<MoveTemplate> moveTemplates = moveEntities
      .stream()
      .map(CatalogEntityMapper::toMove)
      .toList();
    return new CreatureTemplate(
      creatureEntity.getIdCreatura(),
      creatureEntity.getNome(),
      creatureEntity.getRuolo(),
      creatureEntity.getPercorsoSkin(),
      creatureEntity.getHp(),
      creatureEntity.getAttacco(),
      creatureEntity.getDifesa(),
      creatureEntity.getVelocita(),
      moveTemplates
    );
  }

  public static GymTemplate toGym(
    PalestraEntity gymEntity,
    Map<Long, String> bossNameByPlayerId,
    Map<Long, List<Long>> bossTeamCreatureIdsByPlayerId,
    Map<Long, List<Long>> connectedGymIdsByGymId
  ) {
    String bossName = bossNameByPlayerId.getOrDefault(gymEntity.getIdBoss(), "Boss");
    List<Long> bossTeamCreatureIds = List.copyOf(
      bossTeamCreatureIdsByPlayerId.getOrDefault(gymEntity.getIdBoss(), List.of())
    );
    BossTemplate boss = new BossTemplate(
      bossName,
      gymEntity.getPuntiRicompensaBoss(),
      bossTeamCreatureIds
    );
    List<Long> connectedGymIds = List.copyOf(
      connectedGymIdsByGymId.getOrDefault(gymEntity.getIdPalestra(), List.of())
    );
    return new GymTemplate(
      gymEntity.getIdPalestra(),
      gymEntity.getNome(),
      gymEntity.getOrdine(),
      gymEntity.getPuntiRichiesti(),
      connectedGymIds,
      boss
    );
  }
}
