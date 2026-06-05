package it.unicam.cs.mpgc.rpg125664.model.persistence.session.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "sessioni_salvate")
public class SessioneSalvataEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_sessione", nullable = false)
  private Long idSessione;

  @Column(nullable = false, length = 128)
  private String nome;

  @Column(name = "data_salvataggio", nullable = false)
  private Instant dataSalvataggio;

  @Column(name = "data_creazione", nullable = false)
  private Instant dataCreazione;

  @Column(name = "dati_salvati_json", nullable = false, columnDefinition = "CLOB")
  private String datiSalvatiJson;

  @Column(name = "format_version", nullable = false)
  private int formatVersion = 1;

  @Column(name = "id_giocatore_catalogo")
  private Long idGiocatoreCatalogo;

  @Column(name = "id_utente")
  private Long idUtente;

  @Column(name = "ultima_giocata", nullable = false)
  private boolean ultimaGiocata;

  protected SessioneSalvataEntity() {}

  public Long getIdSessione() {
    return idSessione;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public Instant getDataSalvataggio() {
    return dataSalvataggio;
  }

  public void setDataSalvataggio(Instant dataSalvataggio) {
    this.dataSalvataggio = dataSalvataggio;
  }

  public Instant getDataCreazione() {
    return dataCreazione;
  }

  public String getDatiSalvatiJson() {
    return datiSalvatiJson;
  }

  public void setDatiSalvatiJson(String datiSalvatiJson) {
    this.datiSalvatiJson = datiSalvatiJson;
  }

  public int getFormatVersion() {
    return formatVersion;
  }

  public Long getIdGiocatoreCatalogo() {
    return idGiocatoreCatalogo;
  }

  public void setIdGiocatoreCatalogo(Long idGiocatoreCatalogo) {
    this.idGiocatoreCatalogo = idGiocatoreCatalogo;
  }

  public Long getIdUtente() {
    return idUtente;
  }

  public boolean isUltimaGiocata() {
    return ultimaGiocata;
  }

  public void setUltimaGiocata(boolean ultimaGiocata) {
    this.ultimaGiocata = ultimaGiocata;
  }

  public static SessioneSalvataEntity newRow(
      String nome,
      Instant now,
      String datiSalvatiJson,
      long idGiocatoreCatalogo,
      boolean ultimaGiocata) {
    SessioneSalvataEntity row = new SessioneSalvataEntity();
    row.nome = nome;
    row.dataSalvataggio = now;
    row.dataCreazione = now;
    row.datiSalvatiJson = datiSalvatiJson;
    row.formatVersion = 1;
    row.idGiocatoreCatalogo = idGiocatoreCatalogo;
    row.idUtente = null;
    row.ultimaGiocata = ultimaGiocata;
    return row;
  }
}
