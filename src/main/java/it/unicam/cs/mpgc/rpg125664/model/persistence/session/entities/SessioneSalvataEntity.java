package it.unicam.cs.mpgc.rpg125664.model.persistence.session.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** Entità JPA riga tabella {@code sessioni_salvate}. */
@Entity
@Table(name = "sessioni_salvate")
public class SessioneSalvataEntity {

  /** Campi iniziali per una nuova riga di salvataggio locale. */
  public static final class SaveRowDraft {

    private final String nome;
    private final Instant now;
    private final String datiSalvatiJson;
    private final long idGiocatoreCatalogo;
    private final boolean ultimaGiocata;

    private SaveRowDraft(Builder builder) {
      this.nome = builder.nome;
      this.now = builder.now;
      this.datiSalvatiJson = builder.datiSalvatiJson;
      this.idGiocatoreCatalogo = builder.idGiocatoreCatalogo;
      this.ultimaGiocata = builder.ultimaGiocata;
    }

    public static Builder builder() {
      return new Builder();
    }

    public static final class Builder {

      private String nome;
      private Instant now;
      private String datiSalvatiJson;
      private long idGiocatoreCatalogo;
      private boolean ultimaGiocata;

      public Builder nome(String nome) {
        this.nome = nome;
        return this;
      }

      public Builder now(Instant now) {
        this.now = now;
        return this;
      }

      public Builder datiSalvatiJson(String datiSalvatiJson) {
        this.datiSalvatiJson = datiSalvatiJson;
        return this;
      }

      public Builder idGiocatoreCatalogo(long idGiocatoreCatalogo) {
        this.idGiocatoreCatalogo = idGiocatoreCatalogo;
        return this;
      }

      public Builder ultimaGiocata(boolean ultimaGiocata) {
        this.ultimaGiocata = ultimaGiocata;
        return this;
      }

      public SaveRowDraft build() {
        return new SaveRowDraft(this);
      }
    }
  }

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

  public static SessioneSalvataEntity newRow(SaveRowDraft draft) {
    SessioneSalvataEntity row = new SessioneSalvataEntity();
    row.nome = draft.nome;
    row.dataSalvataggio = draft.now;
    row.dataCreazione = draft.now;
    row.datiSalvatiJson = draft.datiSalvatiJson;
    row.formatVersion = 1;
    row.idGiocatoreCatalogo = draft.idGiocatoreCatalogo;
    row.idUtente = null;
    row.ultimaGiocata = draft.ultimaGiocata;
    return row;
  }
}
