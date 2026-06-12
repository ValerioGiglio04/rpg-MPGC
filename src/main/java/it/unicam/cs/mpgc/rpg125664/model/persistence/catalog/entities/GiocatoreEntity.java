package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Entità JPA tabella {@code giocatore} (umano o boss). */
@Entity
@Table(name = "giocatore")
public class GiocatoreEntity {

  @Id
  @Column(name = "id_giocatore", nullable = false)
  private long idGiocatore;

  @Column(nullable = false, length = 256)
  private String nome;

  @Column(name = "is_boss", nullable = false)
  private boolean boss;

  @Column(name = "percorso_skin", length = 512)
  private String percorsoSkin;

  protected GiocatoreEntity() {}

  public GiocatoreEntity(long idGiocatore, String nome, boolean boss, String percorsoSkin) {
    this.idGiocatore = idGiocatore;
    this.nome = nome;
    this.boss = boss;
    this.percorsoSkin = percorsoSkin;
  }

  public long getIdGiocatore() {
    return idGiocatore;
  }

  public String getNome() {
    return nome;
  }

  public boolean isBoss() {
    return boss;
  }

  public String getPercorsoSkin() {
    return percorsoSkin;
  }
}
