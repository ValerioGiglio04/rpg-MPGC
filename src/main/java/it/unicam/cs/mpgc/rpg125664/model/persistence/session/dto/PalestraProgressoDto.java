package it.unicam.cs.mpgc.rpg125664.model.persistence.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO JSON stato completamento di una palestra salvata. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PalestraProgressoDto {

  @JsonProperty("id_palestra")
  private long idPalestra;

  @JsonProperty("completata")
  private boolean completata;

  public PalestraProgressoDto() {}

  public PalestraProgressoDto(long idPalestra, boolean completata) {
    this.idPalestra = idPalestra;
    this.completata = completata;
  }

  public long getIdPalestra() {
    return idPalestra;
  }

  public void setIdPalestra(long idPalestra) {
    this.idPalestra = idPalestra;
  }

  public boolean isCompletata() {
    return completata;
  }

  public void setCompletata(boolean completata) {
    this.completata = completata;
  }
}
