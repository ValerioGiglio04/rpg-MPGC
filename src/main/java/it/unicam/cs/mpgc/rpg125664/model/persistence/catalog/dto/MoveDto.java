package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** DTO JSON mossa nel file seed catalogo. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MoveDto(String nome, int power, int accuracy, String descrizione) {}
