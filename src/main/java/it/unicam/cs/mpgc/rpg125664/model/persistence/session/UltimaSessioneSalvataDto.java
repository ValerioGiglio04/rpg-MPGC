package it.unicam.cs.mpgc.rpg125664.model.persistence.session;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Snapshot JSON della partita (id numerici allineati al catalogo). */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class UltimaSessioneSalvataDto {

  @JsonProperty("data_salvataggio")
  private Instant dataSalvataggio;

  @JsonProperty("num_punti_fama")
  private int numPuntiFama;

  @JsonProperty("id_creatura_attiva_selezionata")
  private long idCreaturaAttivaSelezionata;

  @JsonProperty("id_palestra_corrente")
  private long idPalestraCorrente;

  @JsonProperty("lista_creature_team_giocatore")
  private List<CreaturaTeamDto> listaCreatureTeamGiocatore = new ArrayList<>();

  @JsonProperty("palestre_completate")
  private List<PalestraProgressoDto> palestreCompletate = new ArrayList<>();

  @JsonProperty("posizione_giocatore_mappa")
  private PosizioneMappaDto posizioneGiocatoreMappa;

  public UltimaSessioneSalvataDto() {}

  public Instant getDataSalvataggio() {
    return dataSalvataggio;
  }

  public void setDataSalvataggio(Instant dataSalvataggio) {
    this.dataSalvataggio = dataSalvataggio;
  }

  public int getNumPuntiFama() {
    return numPuntiFama;
  }

  public void setNumPuntiFama(int numPuntiFama) {
    this.numPuntiFama = numPuntiFama;
  }

  public long getIdCreaturaAttivaSelezionata() {
    return idCreaturaAttivaSelezionata;
  }

  public void setIdCreaturaAttivaSelezionata(long idCreaturaAttivaSelezionata) {
    this.idCreaturaAttivaSelezionata = idCreaturaAttivaSelezionata;
  }

  public long getIdPalestraCorrente() {
    return idPalestraCorrente;
  }

  public void setIdPalestraCorrente(long idPalestraCorrente) {
    this.idPalestraCorrente = idPalestraCorrente;
  }

  public List<CreaturaTeamDto> getListaCreatureTeamGiocatore() {
    return listaCreatureTeamGiocatore;
  }

  public void setListaCreatureTeamGiocatore(List<CreaturaTeamDto> listaCreatureTeamGiocatore) {
    this.listaCreatureTeamGiocatore = listaCreatureTeamGiocatore;
  }

  public List<PalestraProgressoDto> getPalestreCompletate() {
    return palestreCompletate;
  }

  public void setPalestreCompletate(List<PalestraProgressoDto> palestreCompletate) {
    this.palestreCompletate = palestreCompletate;
  }

  public PosizioneMappaDto getPosizioneGiocatoreMappa() {
    return posizioneGiocatoreMappa;
  }

  public void setPosizioneGiocatoreMappa(PosizioneMappaDto posizioneGiocatoreMappa) {
    this.posizioneGiocatoreMappa = posizioneGiocatoreMappa;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class CreaturaTeamDto {

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

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class PalestraProgressoDto {

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

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class PosizioneMappaDto {

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
}
