package it.unicam.cs.mpgc.rpg125664.model.persistence.session.dto;

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
}
