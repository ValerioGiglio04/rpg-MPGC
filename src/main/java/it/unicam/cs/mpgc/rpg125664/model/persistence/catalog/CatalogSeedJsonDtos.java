package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** Forma JSON di {@code catalog-seed.json} (nomi campi in italiano come sul file). */
@JsonIgnoreProperties(ignoreUnknown = true)
record CatalogSeedFileDto(
    SettingsDto configurazione, List<CreatureDto> creature, List<GymDto> palestre) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record SettingsDto(
    String nomeGiocatore,
    long idPalestraIniziale,
    String percorsoSkinGiocatore,
    List<Long> starterTeamIds) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record MoveDto(String nome, int power, int accuracy, String descrizione) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record CreatureDto(
    long id,
    String nome,
    String ruolo,
    String skinPath,
    int saluteMassima,
    int attack,
    int defense,
    int speed,
    List<MoveDto> mosse) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record BossDto(String nome, int ricompensaPunti, List<Long> creatureIds) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record GymDto(long id, String nome, int ordine, int puntiMinimi, BossDto boss) {}
