package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Entità JPA tabella {@code palestra} del catalogo. */
@Entity
@Table(name = "palestra")
public class PalestraEntity {

  @Id
  @Column(name = "id_palestra", nullable = false)
  private long idPalestra;

  @Column(nullable = false, length = 256)
  private String nome;

  @Column(nullable = false)
  private int ordine;

  @Column(name = "punti_richiesti", nullable = false)
  private int puntiRichiesti;

  @Column(name = "punti_ricompensa_boss", nullable = false)
  private int puntiRicompensaBoss;

  @Column(name = "id_boss", nullable = false)
  private long idBoss;

  protected PalestraEntity() {}

  public PalestraEntity(
      long idPalestra,
      String nome,
      int ordine,
      int puntiRichiesti,
      int puntiRicompensaBoss,
      long idBoss) {
    this.idPalestra = idPalestra;
    this.nome = nome;
    this.ordine = ordine;
    this.puntiRichiesti = puntiRichiesti;
    this.puntiRicompensaBoss = puntiRicompensaBoss;
    this.idBoss = idBoss;
  }

  public long getIdPalestra() {
    return idPalestra;
  }

  public String getNome() {
    return nome;
  }

  public int getOrdine() {
    return ordine;
  }

  public int getPuntiRichiesti() {
    return puntiRichiesti;
  }

  public int getPuntiRicompensaBoss() {
    return puntiRicompensaBoss;
  }

  public long getIdBoss() {
    return idBoss;
  }
}
