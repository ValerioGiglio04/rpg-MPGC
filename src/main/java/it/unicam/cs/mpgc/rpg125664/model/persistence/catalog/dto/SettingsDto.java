package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SettingsDto(
    String nomeGiocatore,
    long idPalestraIniziale,
    String percorsoSkinGiocatore,
    List<Long> starterTeamIds) {}
