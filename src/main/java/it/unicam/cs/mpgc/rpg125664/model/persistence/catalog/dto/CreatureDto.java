package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** DTO JSON creatura nel file seed catalogo. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreatureDto(
    long id,
    String nome,
    String ruolo,
    String skinPath,
    int saluteMassima,
    int attack,
    int defense,
    int speed,
    List<MoveDto> mosse) {}
