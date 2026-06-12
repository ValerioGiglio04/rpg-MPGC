package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** DTO JSON palestra nel file seed catalogo. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GymDto(long id, String nome, int ordine, int puntiMinimi, BossDto boss) {}
