package it.unicam.cs.mpgc.rpg125664.model.persistence.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mosse")
public class MossaEntity {

  @Id
  @Column(name = "id_mossa", nullable = false)
  private long idMossa;

  @Column(name = "id_creatura", nullable = false)
  private long idCreatura;

  @Column(name = "ordine", nullable = false)
  private int ordine;

  @Column(nullable = false, length = 256)
  private String nome;

  @Column(nullable = false)
  private int potenza;

  @Column(nullable = false)
  private int precisione;

  @Column(nullable = false, length = 512)
  private String descrizione;

  protected MossaEntity() {}

  public MossaEntity(
      long idMossa,
      long idCreatura,
      int ordine,
      String nome,
      int potenza,
      int precisione,
      String descrizione) {
    this.idMossa = idMossa;
    this.idCreatura = idCreatura;
    this.ordine = ordine;
    this.nome = nome;
    this.potenza = potenza;
    this.precisione = precisione;
    this.descrizione = descrizione;
  }

  public long getIdMossa() {
    return idMossa;
  }

  public long getIdCreatura() {
    return idCreatura;
  }

  public int getOrdine() {
    return ordine;
  }

  public String getNome() {
    return nome;
  }

  public int getPotenza() {
    return potenza;
  }

  public int getPrecisione() {
    return precisione;
  }

  public String getDescrizione() {
    return descrizione;
  }
}
