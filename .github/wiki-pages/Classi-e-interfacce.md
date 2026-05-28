# Classi e interfacce

> [← Indice Wiki](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura) · [Persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza)

Elenco delle classi e interfacce del package `it.unicam.cs.mpgc.rpg125664`, raggruppate per layer, con la **responsabilità** associata a ciascuna.

| Simbolo | Significato |
|:--------|:------------|
| **C** | Classe |
| **AC** | Classe astratta |
| **I** | Interfaccia |
| **E** | Enum |
| **R** | Record |
| **F** | Classe `final` package-private (dove indicato) |

---

## Layer `app` — Bootstrap

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `Main` | Entry point Gradle; avvia `RpgApplication` |
| C | `RpgApplication` | Application JavaFX: bootstrap `AppModule`, `MainView`, chiusura risorse JPA |
| C | `AppModule` | Composition root: EMF, seed catalogo, repository, servizi, `GameModel`; `close()` rilascia EMF |

---

## Layer `model.entity` — Aggregati di gioco

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `GameState` | Stato del mondo: giocatore, lista palestre, palestra corrente; regole `moveTo`, `canChallengeGym`, `allGymsCompleted` |
| C | `Player` | Nome, gloria (`Score`), skin, `CreatureHolder` del team |
| C | `Creature` | Istanza di creatura in partita: stats, HP, mosse, `catalogId`; subisce danni e cure |
| C | `CreatureHolder` | Team ordinato di creature + indice attivo; switch e cure di massa |
| C | `GymRoom` | Palestra: `id` stringa, connessioni, soglia punti, flag completata, `GymBoss` |
| C | `GymBoss` | Boss di palestra: nome, ricompensa gloria, team creature |
| R | `Move` | Mossa: nome, potenza, accuratezza, descrizione |
| C | `Score` | Punti fama del giocatore (gloria) |

---

## Layer `model.catalog` — Dati statici

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `GameCatalog` | Indice di template; lookup che crea istanze di dominio mutabili |
| R | `CreatureTemplate` | Definizione statica di una creatura |
| R | `GymTemplate` | Definizione statica di una palestra |
| R | `BossTemplate` | Definizione statica di un boss |
| R | `MoveTemplate` | Definizione statica di una mossa |
| R | `NewGameSettings` | Parametri per costruire la prima partita (starter, palestra iniziale) |

---

## Layer `model.combat` — Combattimento

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| I | `CombatEngine` | Contratto: esegue un attacco e restituisce `AttackOutcome` |
| C | `TurnBasedCombatEngine` | Implementazione default: hit roll, formula danno, KO |
| I | `BossMoveStrategy` | Contratto: sceglie la mossa del boss |
| C | `AccuracyThresholdBossMoveStrategy` | IA boss basata su soglia di accuratezza delle mosse |
| R | `AttackOutcome` | Esito colpo: hit/miss, danno, difensore KO |

---

## Layer `model.event` — Eventi di battaglia

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| I | `BattleEvent` | Sealed interface: eventi emessi durante un turno (non persistiti) |
| — | `RoundStarted`, `MoveUsed`, `AttackHit`, `AttackMissed`, `CreatureKnockedOut`, `CreatureSwitched`, `BossDefeated`, `CreaturesAcquired`, `PlayerTeamWiped` | Record annidati in `BattleEvent` per il log UI |
| E | `Side` | Lato in battaglia: giocatore o boss |

---

## Porte del dominio (package `model`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| I | `GameStateRepository` | Multi-save: `listSaves`, `save`, `load`, `delete`, `markLastPlayed` |
| R | `SavedSessionSummary` | Metadati slot per lista UI |
| R | `LoadedSession` | `GameState` + posizione overworld opzionale |
| R | `SaveSessionCommand` | Parametri di salvataggio (stato, slot, nome) |
| R | `OverworldPosition` | Coordinate mappa (dominio, senza JavaFX) |
| I | `GameCatalogLoader` | Caricamento `GameCatalog` da sorgente esterna |

---

## Layer `model.validation` — Validazione

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| I | `Validator<T>` | Contratto validazione generica |
| AC | `AbstractDomainValidator<T>` | Template Method: null-check + `validateRules` nelle sottoclassi |
| C | `Validators` | Factory/registry dei validator di dominio |
| C | `Rules` | Regole condivise (package-private) |
| C | `CreatureValidator` | Invarianti su `Creature` |
| C | `CreatureHolderValidator` | Invarianti su team |
| C | `GameStateValidator` | Invarianti su stato mondo |
| C | `GymBossValidator` | Invarianti su boss |
| C | `GymRoomValidator` | Invarianti su palestra |
| C | `MoveValidator` | Invarianti su mossa |
| C | `PlayerValidator` | Invarianti su giocatore |
| C | `ScoreValidator` | Invarianti su punteggio |
| C | `MoveRules` | Costanti accuratezza/potenza mosse |

---

## Layer `model.builder` — Costruzione validata

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `GameStateBuilder` | Build di `GameState` con validazione |
| C | `PlayerBuilder` | Build di `Player` |
| C | `CreatureBuilder` | Build di `Creature` |
| C | `CreatureHolderBuilder` | Build di `CreatureHolder` |
| C | `GymRoomBuilder` | Build di `GymRoom` |
| C | `GymBossBuilder` | Build di `GymBoss` |
| C | `MoveBuilder` | Build di `Move` |
| C | `ScoreBuilder` | Build di `Score` |

---

## Layer `model.service` — Casi d'uso

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `BattleService` | Ciclo battaglia: begin, prepare, attack, switch |
| C | `NewGameService` | Nuova partita e build stato iniziale da catalogo |
| C | `HealingService` | Cura a pagamento e calcolo gloria spendibile |
| C | `GymCompletionHandler` | Ricompense al completamento palestra |
| E | `GymStatus` | Stato UI di una palestra sulla mappa |

### Sessione (`model.service`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `GameModel` | Entry point per la UI: delega a servizi e repository |
| C | `GameStateHolder` | `GameState` corrente, `currentSessionId`, posizione overworld |

---

## Layer `model.persistence` — Infrastruttura

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| AC | `AbstractHibernateAdapter` | `EntityManagerFactory` condiviso; `withEntityManager` / `inTransaction` |

### Catalogo Hibernate (`model.persistence.catalog`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `HibernateGameCatalogLoader` | Implementazione `GameCatalogLoader`; costruisce `GameCatalog` da H2 |
| C | `CatalogDatabaseSeeder` | Seed idempotente tabelle catalogo da JSON |
| C | `PalestraCollegamentiSupport` | Collegamenti palestre da `ordine` (catena lineare) |
| C | `CatalogIds` (dominio) | Costanti catalogo (es. `GIOCATORE_UMANO = 1`) |
| C | `GiocatoreEntity` | Tabella `giocatore` |
| C | `CreaturaEntity` | Tabella `creatura` |
| C | `MossaEntity` | Tabella `mosse` |
| C | `PalestraEntity` | Tabella `palestra` |
| C | `CatalogSeedJsonLoader` | Lettura `catalog-seed.json` e persistenza entità |
| R | `CatalogSeedBundle` | Dati seed in memoria prima del persist |
| F | `CatalogSeedJsonDtos` | Record DTO Jackson (package-private) |

### Sessione H2 (`model.persistence.session`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `HibernateGameStateRepository` | `GameStateRepository` su tabella `sessioni_salvate` |
| C | `SessioneSalvataEntity` | JPA: `dati_salvati_json`, metadati slot, `id_utente` futuro |
| C | `SessioneJsonMapper` | `GameState` ↔ `UltimaSessioneSalvataDto` |
| C | `SessionJsonSerializer` | Serializza il DTO nel CLOB |
| C | `UltimaSessioneSalvataDto` | Forma del payload JSON in `dati_salvati_json` |

---

## Layer `view` — Presentazione

### Shell e navigazione (`view`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `MainView` | Stage principale JavaFX |
| C | `ScreenNavigator` | Transizioni tra schermate FXML |
| C | `FxmlScreens` | Caricamento FXML e binding controller |
| I | `MainMenuActions` | Callback menu (nuova partita, carica, esci) |
| I | `LoadGameActions` | Callback schermata caricamento (carica, elimina, indietro) |
| I | `HubActions` | Callback hub (battaglia, menu, vittoria) |
| I | `VictoryActions` | Callback schermata vittoria |
| C | `Messages` | Bundle `messages_it.properties` |
| C | `BattleEventTranslator` | `BattleEvent` → righe log localizzate |
| R | `BattleLogLine` | Riga di log (tipo + testo) |

### Controller

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `MainMenuController` | Logica schermata menu |
| C | `HubController` | Logica hub: mappa, team, cura, save |
| C | `BattleController` | Logica battaglia: mosse, log, overlay fine |
| C | `VictoryController` | Logica schermata vittoria |

### Componenti UI (`view.component`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `GamePanel` | Pannello stilizzato riutilizzabile |
| C | `GameButton` | Pulsante stilizzato |
| C | `HealthBar` | Barra HP |
| C | `CreatureCard` | Card creatura nel team |
| C | `CreaturePortrait` | Ritratto creatura |
| C | `PlayerPortrait` | Ritratto giocatore |
| C | `HamburgerMenu` | Menu contestuale hub |
| C | `BattleArenaView` | Layout arena con creature e barre HP |
| C | `BattleCommandColumnView` | Colonna comandi (mosse, switch) |
| C | `BattleEndOverlay` | Overlay fine battaglia / ricompense |
| C | `BattleUiErrorPane` | Messaggio errore (es. palestra non sfidabile) |
| C | `CreatureCardBuilder` | Costruzione card creatura |
| C | `PlayerPortraitBuilder` | Costruzione ritratto giocatore |

### Overworld (`view.overworld`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `OverworldMap` | Mappa overworld interattiva |
| C | `OverworldLayoutSupport` | Posizionamento deterministico palestre e decor (seed `LAYOUT_SEED`) |
| C | `OverworldTileRenderer` | Rendering tile mappa |
| C | `OverworldTextures` | Caricamento texture |
| C | `OverworldMapConstants` | Costanti griglia mappa |
| C | `OverworldModalShell` | Modale interazione palestra |
| C | `OverworldDecor` | Tipi decorazione mappa |
| C | `GymCellAssignment` | Associazione cella ↔ palestra |
| R | `MapCoordinate` | Coordinate riga/colonna |
| R | `MapOffset` | Delta movimento sulla griglia |

### Tema (`view.theme`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| I | `UiTheme` | Contratto colori/stili hub |
| C | `DuelUiTheme` | Tema schermata battaglia |

---

## Riepilogo interfacce principali

| Interfaccia | Layer | Base astratta | Implementazione/i tipiche |
|-------------|-------|---------------|---------------------------|
| `GameStateRepository` | model | `AbstractHibernateAdapter` | `HibernateGameStateRepository` |
| `GameCatalogLoader` | model | `AbstractHibernateAdapter` | `HibernateGameCatalogLoader` |
| `CombatEngine` | model.combat | — | `TurnBasedCombatEngine` |
| `BossMoveStrategy` | model.combat | — | `AccuracyThresholdBossMoveStrategy` |
| `Validator<T>` | model.validation | `AbstractDomainValidator<T>` | `*Validator` concreti |
| `BattleEvent` | model.event | Record sealed annidati |
| `UiTheme` | view.theme | `DuelUiTheme` (battaglia); stili hub inline |
| `MainMenuActions`, `HubActions`, `VictoryActions`, `LoadGameActions` | view | Implementate da `ScreenNavigator` |

---

## Conteggio

Circa **108** file sorgente Java nel package principale, più risorse FXML, JSON, immagini e `persistence.xml`.

Per il significato dei layer e le dipendenze vedere [Responsabilità e architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura).
