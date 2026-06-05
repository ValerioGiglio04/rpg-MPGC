package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.seed;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto.BossDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto.CatalogSeedBundle;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto.CatalogSeedFileDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto.CreatureDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto.GymDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto.MoveDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto.SettingsDto;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.CreaturaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.GiocatoreEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.MossaEntity;
import it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities.PalestraEntity;
import it.unicam.cs.mpgc.rpg125664.model.catalog.CatalogIds;
import it.unicam.cs.mpgc.rpg125664.model.catalog.NewGameSettings;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Legge {@code catalog-seed.json} e costruisce le entity per le quattro tabelle catalogo. */
public final class CatalogSeedJsonLoader {

  public static final String DEFAULT_RESOURCE = "/game-data/catalog-seed.json";

  private static final ObjectMapper MAPPER =
      new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private CatalogSeedJsonLoader() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static CatalogSeedBundle load() {
    return load(DEFAULT_RESOURCE);
  }

  public static CatalogSeedBundle load(String classpathResource) {
    Objects.requireNonNull(classpathResource, "classpathResource");
    try (InputStream in = CatalogSeedJsonLoader.class.getResourceAsStream(classpathResource)) {
      if (in == null) {
        throw new IllegalStateException("Seed resource missing: " + classpathResource);
      }
      CatalogSeedFileDto dto = MAPPER.readValue(in, CatalogSeedFileDto.class);
      return toBundle(dto);
    } catch (IllegalStateException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException("Cannot parse catalog seed: " + classpathResource, e);
    }
  }

  private static CatalogSeedBundle toBundle(CatalogSeedFileDto dto) {
    validateRoot(dto);
    SettingsDto settings = dto.configurazione();
    List<GiocatoreEntity> giocatori = new ArrayList<>();
    giocatori.add(
        new GiocatoreEntity(
            CatalogIds.GIOCATORE_UMANO,
            settings.nomeGiocatore(),
            false,
            settings.percorsoSkinGiocatore()));

    Map<Long, CreaturaEntity> creatureById = new HashMap<>();
    List<MossaEntity> mosse = new ArrayList<>();
    long nextMossaId = 1L;
    for (CreatureDto creatureDto : dto.creature()) {
      CreaturaEntity row =
          new CreaturaEntity(
              creatureDto.id(),
              null,
              creatureDto.nome(),
              creatureDto.ruolo(),
              creatureDto.skinPath(),
              creatureDto.saluteMassima(),
              creatureDto.attack(),
              creatureDto.defense(),
              creatureDto.speed());
      creatureById.put(creatureDto.id(), row);
      List<MoveDto> rawMoves = creatureDto.mosse() != null ? creatureDto.mosse() : List.of();
      int moveOrder = 0;
      for (MoveDto moveDto : rawMoves) {
        mosse.add(
            new MossaEntity(
                nextMossaId++,
                creatureDto.id(),
                moveOrder++,
                moveDto.nome(),
                moveDto.power(),
                moveDto.accuracy(),
                moveDto.descrizione()));
      }
    }

    List<PalestraEntity> palestre = new ArrayList<>();
    long nextBossGiocatoreId = 2L;
    for (GymDto gymDto : dto.palestre()) {
      BossDto boss = gymDto.boss();
      if (boss == null) {
        throw new IllegalStateException("Seed gym has no boss: " + gymDto.id());
      }
      long bossGiocatoreId = nextBossGiocatoreId++;
      giocatori.add(new GiocatoreEntity(bossGiocatoreId, boss.nome(), true, null));
      List<Long> bossTeam = boss.creatureIds() != null ? boss.creatureIds() : List.of();
      for (Long creatureId : bossTeam) {
        CreaturaEntity creature = creatureById.get(creatureId);
        if (creature == null) {
          throw new IllegalStateException(
              "Boss creature not in catalog: " + creatureId + " gym=" + gymDto.id());
        }
        creature.setIdGiocatore(bossGiocatoreId);
      }
      palestre.add(
          new PalestraEntity(
              gymDto.id(),
              gymDto.nome(),
              gymDto.ordine(),
              gymDto.puntiMinimi(),
              boss.ricompensaPunti(),
              bossGiocatoreId));
    }

    NewGameSettings newGame =
        new NewGameSettings(
            settings.nomeGiocatore(),
            settings.idPalestraIniziale(),
            settings.percorsoSkinGiocatore(),
            settings.starterTeamIds() != null ? settings.starterTeamIds() : List.of());

    return new CatalogSeedBundle(
        giocatori, new ArrayList<>(creatureById.values()), mosse, palestre, newGame);
  }

  private static void validateRoot(CatalogSeedFileDto dto) {
    if (dto.creature() == null || dto.creature().isEmpty()) {
      throw new IllegalStateException("Seed has no creatures");
    }
    if (dto.palestre() == null || dto.palestre().isEmpty()) {
      throw new IllegalStateException("Seed has no gyms");
    }
    if (dto.configurazione() == null) {
      throw new IllegalStateException("Seed has no configurazione");
    }
  }
}
