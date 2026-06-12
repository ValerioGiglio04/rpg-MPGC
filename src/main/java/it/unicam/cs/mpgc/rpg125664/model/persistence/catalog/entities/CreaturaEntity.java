package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Entità JPA tabella {@code creatura} del catalogo. */
@Entity
@Table(name = "creatura")
public class CreaturaEntity {

  @Id
  @Column(name = "id_creatura", nullable = false)
  private long idCreatura;

  @Column(name = "id_giocatore")
  private Long idGiocatore;

  @Column(nullable = false, length = 256)
  private String nome;

  @Column(nullable = false, length = 256)
  private String ruolo;

  @Column(name = "percorso_skin", nullable = false, length = 512)
  private String percorsoSkin;

  @Column(nullable = false)
  private int hp;

  @Column(nullable = false)
  private int attacco;

  @Column(nullable = false)
  private int difesa;

  @Column(nullable = false)
  private int velocita;

  protected CreaturaEntity() {}

  public CreaturaEntity(
      long idCreatura,
      Long idGiocatore,
      String nome,
      String ruolo,
      String percorsoSkin,
      int hp,
      int attacco,
      int difesa,
      int velocita) {
    this.idCreatura = idCreatura;
    this.idGiocatore = idGiocatore;
    this.nome = nome;
    this.ruolo = ruolo;
    this.percorsoSkin = percorsoSkin;
    this.hp = hp;
    this.attacco = attacco;
    this.difesa = difesa;
    this.velocita = velocita;
  }

  public long getIdCreatura() {
    return idCreatura;
  }

  public Long getIdGiocatore() {
    return idGiocatore;
  }

  public void setIdGiocatore(Long idGiocatore) {
    this.idGiocatore = idGiocatore;
  }

  public String getNome() {
    return nome;
  }

  public String getRuolo() {
    return ruolo;
  }

  public String getPercorsoSkin() {
    return percorsoSkin;
  }

  public int getHp() {
    return hp;
  }

  public int getAttacco() {
    return attacco;
  }

  public int getDifesa() {
    return difesa;
  }

  public int getVelocita() {
    return velocita;
  }
}
