package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.mapper;

import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.CreaturaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.MossaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.PalestraEntity;
import it.unicam.cs.mpgc.rpg125664.model.catalog.BossTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.CreatureTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GymTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.MoveTemplate;
import java.util.List;
import java.util.Map;

/** Mappa le entity JPA del catalogo sui template di dominio. */
public final class CatalogEntityMapper {

  private CatalogEntityMapper() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static MoveTemplate toMove(MossaEntity row) {
    return new MoveTemplate(
        row.getNome(), row.getPotenza(), row.getPrecisione(), row.getDescrizione());
  }

  public static CreatureTemplate toCreature(CreaturaEntity row, List<MossaEntity> moves) {
    List<MoveTemplate> moveTemplates = moves.stream().map(CatalogEntityMapper::toMove).toList();
    return new CreatureTemplate(
        row.getIdCreatura(),
        row.getNome(),
        row.getRuolo(),
        row.getPercorsoSkin(),
        row.getHp(),
        row.getAttacco(),
        row.getDifesa(),
        row.getVelocita(),
        moveTemplates);
  }

  public static GymTemplate toGym(
      PalestraEntity row,
      Map<Long, String> bossNamesById,
      Map<Long, List<Long>> bossCreatureIdsByGiocatoreId,
      Map<Long, List<Long>> collegamentiByPalestraId) {
    String bossName = bossNamesById.getOrDefault(row.getIdBoss(), "Boss");
    List<Long> bossTeam =
        List.copyOf(bossCreatureIdsByGiocatoreId.getOrDefault(row.getIdBoss(), List.of()));
    BossTemplate boss = new BossTemplate(bossName, row.getPuntiRicompensaBoss(), bossTeam);
    List<Long> collegamenti =
        List.copyOf(collegamentiByPalestraId.getOrDefault(row.getIdPalestra(), List.of()));
    return new GymTemplate(
        row.getIdPalestra(),
        row.getNome(),
        row.getOrdine(),
        row.getPuntiRichiesti(),
        collegamenti,
        boss);
  }
}
