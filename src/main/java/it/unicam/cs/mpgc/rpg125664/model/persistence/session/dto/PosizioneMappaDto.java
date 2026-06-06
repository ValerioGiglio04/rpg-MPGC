package it.unicam.cs.mpgc.rpg125664.model.persistence.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class PosizioneMappaDto {

  @JsonProperty("x")
  private int x;

  @JsonProperty("y")
  private int y;

  public PosizioneMappaDto() {}

  public PosizioneMappaDto(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }
}
