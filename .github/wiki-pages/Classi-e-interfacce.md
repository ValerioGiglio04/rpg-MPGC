# Classi e interfacce

> [← Indice Wiki](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura) · [Persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza) · [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita)

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
| C | `AppModule` | Composition root: EMF, seed catalogo, strategy combattimento + `BattleRoundExecutor` iniettato in `BattleService`, `SessionJsonSerializer` → `SessioneSalvataSummaryMapper`, `SessioneSalvataJpaRepository` → `HibernateGameStateRepository`, `GameModel`; `close()` rilascia EMF |

---

## Layer `model.entity` — Aggregati di gioco

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `GameState` | Stato del mondo: giocatore, lista palestre, palestra corrente; regole `moveTo`, `canChallengeGym`, `allGymsCompleted` |
| C | `Player` | Nome, gloria (`Score`), skin, `CreatureHolder` del team |
| C | `Creature` | Istanza di creatura in partita: stats, HP, mosse, `catalogId`; subisce danni e cure |
| C | `CreatureHolder` | Team ordinato di creature + indice attivo; switch e cure di massa |
| C | `GymRoom` | Palestra: `id` numerico (`long`), connessioni, soglia punti, flag completata, `GymBoss` |
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
| C | `BattleRoundExecutor` | Esecuzione di un round: turni, attacchi, switch, eventi |
| R | `AttackOutcome` | Esito colpo: hit/miss, danno, difensore KO |

### Strategy combattimento (`model.combat.strategy`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| I | `AttackResolutionStrategy` | Strategy: esegue un attacco e restituisce `AttackOutcome` |
| I | `BossMoveStrategy` | Contratto: sceglie la mossa del boss |

### Implementazioni Strategy (`model.combat.strategy.implementations`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `TurnBasedAttackResolutionStrategy` | Implementazione default: hit roll, formula danno, KO |
| C | `AccuracyThresholdBossMoveStrategy` | IA boss basata su soglia di accuratezza delle mosse |

---

## Layer `model.event` — Eventi di battaglia

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| I | `BattleEvent` | Sealed interface: eventi emessi durante un turno (non persistiti) |
| — | `RoundStarted`, `MoveUsed`, `AttackHit`, `AttackMissed`, `CreatureKnockedOut`, `CreatureSwitched`, `BossDefeated`, `CreaturesAcquired`, `PlayerTeamWiped` | Record annidati in `BattleEvent` per il log UI |
| E | `Side` | Lato in battaglia: giocatore o boss |

---

## Porte del dominio (`model.persistence`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| I | `GameStateRepository` | Multi-save: `listSaves`, `save`, `load`, `delete`, `markLastPlayed` |
| I | `GameCatalogLoader` | Caricamento `GameCatalog` da sorgente esterna |

### Tipi correlati (`model.session` e `model.entity`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| R | `SavedSessionSummary` | Metadati slot per lista UI |
| R | `LoadedSession` | `GameState` + posizione overworld opzionale |
| R | `SaveSessionCommand` | Parametri di salvataggio (stato, slot, nome) |
| C | `SaveSlotLabels` | `defaultSaveName` / `formatSavedAt` — pattern `dd/MM/yyyy HH:mm` condiviso tra model.persistence e UI |
| R | `OverworldPosition` | Coordinate mappa (dominio, senza JavaFX) |
| C | `SessionPersistenceException` | Errore unchecked di persistenza sessione (wrappa I/O nell'model.persistence) |

---

## Layer `model.validation` — Validazione

### Contratti e regole (`model.validation`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| AC | `Validator<T>` | Template Method: `validate(T)`; `validateRules` nelle sottoclassi |
| C | `Rules` | Regole condivise (`requireText`, `requirePositive`, …) |
| C | `MoveRules` | Costanti accuratezza/potenza mosse |

### Implementazioni (`model.validation.implementations`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `ValidatorFactory` | Registry singleton `get*Validator()` → `Validator<T>` |
| C | `ScoreValidator`, `PlayerValidator`, `GameStateValidator`, … | Sottoclassi finali; `GameStateValidator` compone validator su `player` e ogni `GymRoom`; `Score.add` / `Score.spend` ri-validano dopo mutazione |

Pattern builder: `T instance = new …; Validator<T> v = ValidatorFactory.get*Validator(); v.validate(instance); return instance;`

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
| C | `BattleService` | Ciclo battaglia: begin, prepare, attack, switch; riceve `BattleRoundExecutor` dal composition root (DIP) |
| C | `NewGameService` | Nuova partita e build stato iniziale da catalogo |
| C | `HealingService` | Cura a pagamento e calcolo gloria spendibile |
| C | `GymCompletionHandler` | Ricompense al completamento palestra (via `GameCatalog`) |
| E | `HealingError` | Codici errore cura (UI mappa → `Messages`) |
| C | `HealingException` | Eccezione applicativa con `HealingError` |

### Overworld applicativo (`model.overworld`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| E | `GymStatus` | Stato UI di una palestra sulla mappa |
| C | `OverworldGridLayout` | Griglia e celle candidate per layout palestre |
| R | `GymCellPlacement` | Associazione cella ↔ palestra (dominio applicativo) |
| C | `OverworldSpawnPosition` | Posizione di default al primo salvataggio |

#### Strategy overworld (`model.overworld.strategy`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| I | `GymStatusStrategy` | Strategy: calcolo `GymStatus` da `GameState` e `GymRoom` |

#### Implementazioni (`model.overworld.strategy.implementations`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `DefaultGymStatusStrategy` | Implementazione default delle regole overworld |

### Sessione (`model.service`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `GameModel` | Facciata UI: delega a servizi, `SessionPersistenceFacade`, `GymStatusStrategy` |
| C | `GameStateHolder` | `GameState` corrente, `currentSessionId`, `OverworldPosition` |
| C | `SessionPersistenceFacade` | Save/load/delete/list; incapsula `GameStateRepository` |

## Layer `model.persistence` — Infrastruttura

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| AC | `AbstractHibernateAdapter` | `EntityManagerFactory` condiviso; `withEntityManager` / `inTransaction` |

### Catalogo Hibernate (`model.persistence.catalog`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `HibernateGameCatalogLoader` | Implementazione `GameCatalogLoader`; costruisce `GameCatalog` da H2 + `NewGameSettings` già noti |
| C | `CatalogIds` (dominio) | Costanti catalogo (es. `GIOCATORE_UMANO = 1`) |

#### Entity JPA (`model.persistence.catalog.entities`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `GiocatoreEntity` | Tabella `giocatore` |
| C | `CreaturaEntity` | Tabella `creatura` |
| C | `MossaEntity` | Tabella `mosse` |
| C | `PalestraEntity` | Tabella `palestra` |

#### DTO seed (`model.persistence.catalog.dto`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| R | `CatalogSeedBundle` | Dati seed in memoria prima del persist |
| R | `CatalogSeedFileDto`, `CreatureDto`, `GymDto`, … | Forma JSON di `catalog-seed.json` |

#### Mapper catalogo (`model.persistence.catalog.mapper`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `CatalogEntityMapper` | Mapping entità JPA → template di dominio |

#### Seed catalogo (`model.persistence.catalog.seed`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `CatalogSeedJsonLoader` | Lettura `catalog-seed.json` |
| C | `CatalogDatabaseSeeder` | Orchestrazione seed (wipe/insert) |
| C | `CatalogTable` | Operazioni JPA su tabella catalogo |

#### Support catalogo (`model.persistence.catalog.support`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `PalestraCollegamentiSupport` | Collegamenti palestre da `ordine` (catena lineare) |

### Sessione H2 (`model.persistence.session`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `HibernateGameStateRepository` | Implementa `GameStateRepository`: orchestra transazioni, serializzazione e collaboratori |
| C | `SessioneSalvataJpaRepository` | JPQL su `sessioni_salvate` (slot locali, `ultima_giocata`, `requireLocal`) |
| C | `SessioneSalvataSummaryMapper` | `SessioneSalvataEntity` → `SavedSessionSummary` per la UI (legge il JSON via serializer) |

#### Entity JPA (`model.persistence.session.entities`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `SessioneSalvataEntity` | JPA: `dati_salvati_json`, metadati slot, `id_utente` futuro |

#### DTO sessione (`model.persistence.session.dto`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `UltimaSessioneSalvataDto` | Forma del payload JSON in `dati_salvati_json` |

#### Mapper / serializer (`model.persistence.session.mapper` / `.serializer`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `SessioneJsonMapper` | `GameState` ↔ `UltimaSessioneSalvataDto` |
| C | `SessionJsonSerializer` | Serializza/deserializza il DTO nel CLOB; `deserialize()` fa un solo `fromJson` |
| R | `LoadedSessionPayload` | `GameState` + `Optional<OverworldPosition>` da una deserializzazione |

---

## Layer `view` — Presentazione

### Infra UI (`view`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `Messages` | Bundle `messages_it.properties` |
| C | `UiErrorReporter` | Log + `Alert` per errori UI via `Messages` |

### Navigazione (`controller.navigation`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `MainView` | Layout root JavaFX |
| C | `ScreenNavigator` | Routing FXML; delega errori persistenza a `PersistenceUiGuard`; implementa tutte le interfacce navigazione |
| E | `PersistenceOperation` | SAVE / LOAD / DELETE con chiave i18n errore (`persistence.*.failed.title`) |
| C | `PersistenceUiGuard` | `run(Runnable, PersistenceOperation)` — try/catch + dialogo errore |
| I | `ScreenNavigation` | Unione di `MainMenuNavigation`, `LoadGameNavigation`, `HubNavigation`, `VictoryNavigation` |
| I | `MainMenuNavigation` | `startNewGame`, `showLoadGame` |
| I | `LoadGameNavigation` | `loadSession`, `deleteSession`, `showMainMenu` |
| I | `HubNavigation` | `showBattle`, `saveCurrent`, `saveAsNew`, `showMainMenu` |
| I | `VictoryNavigation` | `startNewGame`, `showMainMenu` |
| C | `DialogHelper` | Alert e dialoghi save/load |
| C | `FxmlScreenLoader` | Caricamento FXML e binding controller |

### Callback schermata (`controller.navigation`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| I | `MainMenuActions` | Callback menu (nuova partita, carica, esci) |
| I | `LoadGameActions` | Callback schermata caricamento |
| I | `HubActions` | Callback hub (battaglia, save, menu) |
| I | `VictoryActions` | Callback schermata vittoria |

### Implementazioni callback (`controller.navigation`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `MainMenuActionsImpl` | Delega a `MainMenuNavigation` |
| C | `LoadGameActionsImpl` | Delega a `LoadGameNavigation` |
| C | `HubActionsImpl` | Delega a `HubNavigation` |
| C | `VictoryActionsImpl` | Delega a `VictoryNavigation` |

### Battaglia / log (`view`)
| C | `BattleEventTranslator` | `BattleEvent` → righe log localizzate |
| R | `BattleLogLine` | Riga di log (tipo + testo) |

### Controller

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `MainMenuController` | Logica schermata menu |
| C | `LoadGameController` | Logica schermata caricamento slot |
| C | `HubController` | Logica hub: mappa, team, cura, save |
| C | `BattleController` | Logica battaglia: mosse, log, overlay fine |
| C | `VictoryController` | Logica schermata vittoria |

### Controller (`controller`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `BattleController` | Comandi battaglia, log, overlay fine scontro |
| C | `HubController` | Team hub, cura, tooltip gloria |
| C | `LoadGameController` | Elenco slot salvati, caricamento ed eliminazione |
| C | `OverworldController` | Movimento mappa, sfida palestra, motivo blocco |

I controller FXML restano sottili: binding visivo + delega al controller.

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

#### Builder componenti (`view.component.builder`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `CreatureCardBuilder` | Costruzione card creatura |
| C | `PlayerPortraitBuilder` | Costruzione ritratto giocatore |

### Overworld (`view.overworld`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `OverworldMap` | Rendering mappa + input; delega regole a `OverworldController` |
| C | `OverworldLayoutSupport` | Posizionamento deterministico palestre e decor (seed `LAYOUT_SEED`) |
| C | `OverworldTileRenderer` | Rendering tile mappa |
| C | `OverworldTextures` | Caricamento texture |
| C | `OverworldMapConstants` | Costanti griglia mappa |
| C | `OverworldModalShell` | Modale interazione palestra |
| C | `OverworldDecor` | Tipi decorazione mappa |
| R | `MapOffset` | Delta movimento sulla griglia (usa `OverworldPosition` del dominio) |

### Tema (`view.theme`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| I | `UiTheme` | Contratto colori/stili hub |

### Implementazioni tema (`view.theme`)

| Tipo | Nome | Responsabilità |
|------|------|----------------|
| C | `DuelUiTheme` | Tema schermata battaglia |

---

## Riepilogo interfacce principali

| Interfaccia | Layer | Base astratta | Implementazione/i tipiche |
|-------------|-------|---------------|---------------------------|
| `GameStateRepository` | model.persistence | `AbstractHibernateAdapter` | `HibernateGameStateRepository` (+ `SessioneSalvataJpaRepository`, `SessioneSalvataSummaryMapper`) |
| `GameCatalogLoader` | model.persistence | `AbstractHibernateAdapter` | `HibernateGameCatalogLoader` |
| `AttackResolutionStrategy` | model.combat.strategy | — | `TurnBasedAttackResolutionStrategy` |
| `BossMoveStrategy` | model.combat.strategy | — | `AccuracyThresholdBossMoveStrategy` |
| `GymStatusStrategy` | model.overworld.strategy | — | `DefaultGymStatusStrategy` |
| `Validator<T>` | model.validation | — (classe astratta) | `*Validator`, `ValidatorFactory` in `validation.implementations` |
| `BattleEvent` | model.event | Record sealed annidati |
| `UiTheme` | view.theme | `DuelUiTheme` (`view.theme`); stili hub inline |
| `MainMenuActions`, `HubActions`, `VictoryActions`, `LoadGameActions` | controller.navigation | Implementate da `ScreenNavigator` (`controller.navigation`) |

---

## Conteggio

Circa **120** file sorgente Java nel package principale, più risorse FXML, JSON, immagini e `persistence.xml`.

Per il significato dei layer e le dipendenze vedere [Responsabilità e architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura).
