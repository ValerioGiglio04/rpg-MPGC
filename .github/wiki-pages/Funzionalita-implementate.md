# Funzionalità implementate

← [Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura) · [Persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza)

Qui descrivo cosa fa **GymQuest nella prima release**. Non ho messo tutto quello che si potrebbe fare in un RPG (inventario, multiplayer, versione web…), ma le funzionalità che ci sono coprono il ciclo di gioco completo: menu → hub → battaglia → salvataggio → vittoria.

---

## Regole di gioco (in breve)

- Il giocatore controlla un **team di creature** con HP, attacco, difesa e velocità.
- Il mondo è fatto di **palestre collegate**: per sfidare un boss serve avere abbastanza **gloria** (`requiredPoints` sulla palestra).
- Se batti tutte le creature del boss, la palestra risulta completata: guadagni gloria e spesso le creature del boss entrano nel tuo team.
- La partita finisce quando **tutte le palestre** sono completate.

---

## Interfaccia grafica

Tutte le schermate sono JavaFX con file FXML in `src/main/resources/fxml/`:

| Schermata | FXML | Controller |
|-----------|------|------------|
| Menu principale | `MainMenu.fxml` | `MainMenuController` |
| Carica partita | `LoadGame.fxml` | `LoadGameController` |
| Hub / mappa | `Hub.fxml` | `HubController` |
| Battaglia | `Battle.fxml` | `BattleController` |
| Vittoria | `Victory.fxml` | `VictoryController` |

I controller non parlano con Hibernate: passano da `GameModel`. La navigazione tra schermate la gestisce `ScreenNavigator`, le schermate le monta `ScreenFactory` usando i path in `FxmlPaths`.

---

## Menu principale

- **Nuova partita** — `NewGameService` crea il primo `GameState` dal catalogo.
- **Carica partita** — apre la lista slot (tabella `sessioni_salvate`); il pulsante è attivo solo se esiste almeno un save.
- **Esci** — chiude l'applicazione.

Se il caricamento fallisce mostro un alert e resto al menu.

---

## Hub (overworld)

È la schermata principale di gioco:

- **Mappa a tile** con frecce da tastiera (`OverworldMovement`); lo spawn iniziale viene da sessione salvata o dalla palestra corrente (`OverworldPlayerSpawn`).
- **Zoom** con +/- e rotella del mouse (`OverworldZoomControls`).
- **Colori/stato palestre** sulla mappa (calcolati da `GameModel.statusOf` + `GymStatusStrategy`):
  - completata, disponibile, corrente, gloria insufficiente, non raggiungibile.
- **Click su una palestra** — modale di conferma; se serve prima `moveTo(gymId)`, poi parte la battaglia.
- **Team** — lista creature con carta cliccabile per cambiare quella attiva; **cura a pagamento** in gloria (`HealingService`, costo spiegato in tooltip da `HubPresenter`).
- **Menu hamburger** — salva, salva come nuovo slot, torna al menu.
- Se hai già finito tutte le palestre, entrando nell'hub vai diretto alla schermata **Vittoria**.

---

## Combattimento a turni

- Puoi sfidare solo se `GameState.canChallengeGym(gym)` è vero (palestra non completata, raggiungibile, punti ok).
- All'inizio di un nuovo tentativo `BattleService` resetta gli HP di entrambi i team e sceglie la prima creatura viva per lato.
- Il giocatore sceglie la mossa; chi colpisce per primo dipende dalla **velocità** delle creature attive nel round.
- Danno e miss: `TurnBasedAttackResolutionStrategy`. Mossa del boss: `AccuracyThresholdBossMoveStrategy`.
- Puoi **cambiare creatura** durante il duello.
- Gli eventi di combattimento (`BattleEvent`) li traduco in italiano con `BattleEventTranslator` e li mostro nel log con `BattleLogRenderer`.
- Se metti KO tutte le creature del boss, `GymCompletionHandler` segna la palestra completata, dà gloria e aggiunge le creature al party.
- Se rientri in una palestra già completata, c'è una modalità "revisione" senza reset HP.

---

## Spostamento tra palestre

Ogni `GymRoom` ha `connectedGymIds`: puoi spostarti solo verso palestre **adiacenti** (`GameState.moveTo`). La gloria decide quali boss puoi affrontare; `currentGymId` tiene traccia di dove sei.

---

## Salvataggi (persistenza in gioco)

- Più **slot** sulla stessa macchina (`sessioni_salvate`).
- **Salva** dall'hub — aggiorna lo slot corrente.
- **Salva come nuovo** — chiede un nome e crea una riga nuova.
- **Elimina slot** dalla schermata Carica.

Nel JSON salvo id di catalogo, HP correnti, gloria, palestre completate e coordinate `{x,y}` sulla mappa. Il dettaglio del formato è in [Persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza).

---

## Catalogo e avvio

All'avvio `CatalogBootstrap` (da `AppModule`) legge `catalog-seed.json`, se serve popola H2 e carica `GameCatalog` in memoria. Da lì `NewGameService` costruisce una nuova partita.

---

## Testi in italiano

Messaggi UI e log battaglia in `messages_it.properties`, letti tramite `Messages`. FXML e controller condividono lo stesso bundle grazie a `FxmlScreenLoader`.

---

## Flusso tipico

```mermaid
%%{init: {"flowchart": {"curve": "linear"}}}%%
flowchart TD
  Boot[Avvio] --> MainMenu[Menu principale]
  MainMenu -->|Nuova partita| Hub[Hub]
  MainMenu -->|Continua| Hub
  MainMenu -->|Esci| End[Fine]
  Hub -->|Salva| Hub
  Hub -->|Menu| MainMenu
  Hub -->|Palestra| Battle[Battaglia]
  Battle --> Hub
  Hub -->|Campagna finita| Victory[Vittoria]
  Victory -->|Menu| MainMenu
```

---

## Cosa non c'è in v1

Multiplayer, mobile/web, negozio, cattura selvatica. Ho lasciato fuori scope volutamente per concentrarmi su un flusso giocabile end-to-end; in [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita) spiego dove aggancierei queste cose nel codice attuale.
