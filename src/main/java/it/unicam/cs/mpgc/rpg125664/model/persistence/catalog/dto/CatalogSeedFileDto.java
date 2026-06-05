package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** Forma radice JSON di {@code catalog-seed.json}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CatalogSeedFileDto(
    SettingsDto configurazione, List<CreatureDto> creature, List<GymDto> palestre) {}
