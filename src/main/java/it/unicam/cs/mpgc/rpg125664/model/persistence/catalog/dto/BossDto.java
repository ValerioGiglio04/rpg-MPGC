package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** DTO JSON del boss di palestra nel seed catalogo. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BossDto(String nome, int ricompensaPunti, List<Long> creatureIds) {}
