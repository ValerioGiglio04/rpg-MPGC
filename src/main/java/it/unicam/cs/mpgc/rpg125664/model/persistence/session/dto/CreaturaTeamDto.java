package it.unicam.cs.mpgc.rpg125664.model.persistence.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO JSON HP corrente di una creatura nel salvataggio. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreaturaTeamDto {

  @JsonProperty("id_creatura")
  private long idCreatura;

  @JsonProperty("hp")
  private int hp;

  public CreaturaTeamDto() {}

  public CreaturaTeamDto(long idCreatura, int hp) {
    this.idCreatura = idCreatura;
    this.hp = hp;
  }

  public long getIdCreatura() {
    return idCreatura;
  }

  public void setIdCreatura(long idCreatura) {
    this.idCreatura = idCreatura;
  }

  public int getHp() {
    return hp;
  }

  public void setHp(int hp) {
    this.hp = hp;
  }
}
