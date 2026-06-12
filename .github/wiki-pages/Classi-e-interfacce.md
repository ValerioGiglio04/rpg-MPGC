# Classi e interfacce

← [Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura)

La specifica chiede l'elenco delle classi e interfacce con la **responsabilità** di ciascuna. Qui metto quelle centrali del progetto; builder, DTO JPA e widget minori seguono la stessa logica descritta in [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura).

Package base: `it.unicam.cs.mpgc.rpg125664`.

---

## app — avvio applicazione

| Classe | Responsabilità |
|--------|----------------|
| `Main` | Entry point Gradle; lancia JavaFX. |
| `RpgApplication` | `Application` JavaFX: bootstrap, `MainView`, chiusura EMF a fine app. |
| `AppModule` | Composition root: EntityManagerFactory, repository, chiama `ServiceGraph`. |
| `CatalogBootstrap` | Seed catalogo H2 da JSON + caricamento `GameCatalog`. |
| `ServiceGraph` | Crea servizi, strategy e `GameModel`; restituisce anche `PortraitAssetResolver`. |

---

## model.entity e model.catalog — dominio di gioco

| Classe / interfaccia | Responsabilità |
|----------------------|----------------|
| `GameState` | Stato partita: giocatore, palestre, spostamento, check sfida boss, vittoria campagna. |
| `Player` | Giocatore umano: gloria (`Score`), team (`CreatureHolder`), palestra corrente. |
| `Creature` | Creatura in partita: statistiche, HP, mosse, KO. |
| `CreatureHolder` | Team e creatura attiva; switch e prima viva disponibile. |
| `GymRoom` | Palestra: connessioni, soglia gloria, flag completamento. |
| `GymBoss` | Boss di palestra con il suo team. |
| `Move` | Mossa: potenza, precisione, nome. |
| `Score` | Punti fama (gloria) del giocatore. |
| `GameCatalog` | Lookup template creature/palestre/mosse per id; path skin UI. |
| `CreatureTemplate`, `GymTemplate`, `BossTemplate` | Dati statici letti dal catalogo. |
| `NewGameSettings`, `CatalogIds` | Parametri nuova partita e costanti id. |

---

## model.combat e model.event — battaglia

| Classe / interfaccia | Responsabilità |
|----------------------|----------------|
| `BattleRoundExecutor` | Un round: ordine turni, attacchi, KO, switch automatico se creatura attiva KO. |
| `BattleService` | Inizio/fine battaglia; delega i round all'executor. |
| `AttackOutcome` | Risultato colpo: hit/miss, danno, KO difensore. |
| `AttackResolutionStrategy` | Contratto: risolvere danno di una mossa. |
| `BossMoveStrategy` | Contratto: scegliere mossa del boss. |
| `TurnBasedAttackResolutionStrategy` | Impl default danno/miss. |
| `AccuracyThresholdBossMoveStrategy` | Impl default IA boss. |
| `BattleEvent` | Eventi immutabili del combattimento (sealed interface). |
| `Side` | Lato in battaglia (giocatore o boss). |

---

## model.service — casi d'uso

| Classe | Responsabilità |
|--------|----------------|
| `GameModel` | Facciata per i controller: battaglia, cura, save/load, stato mappa. |
| `GameStateHolder` | `GameState` corrente in memoria. |
| `NewGameService` | Crea/resetta partita da catalogo. |
| `HealingService` | Cura creatura attiva in hub (costo gloria). |
| `GymCompletionHandler` | Ricompense quando una palestra viene completata. |
| `SessionPersistenceFacade` | Save/load/delete slot verso il repository. |
| `GymStatusStrategy` / `DefaultGymStatusStrategy` | Stato visivo palestra sulla mappa. |
| `GymStatus` | Enum: completata, disponibile, corrente, gloria insufficiente, irraggiungibile. |
| `OverworldGridLayout`, `GymCellPlacement` | Posizionamento deterministico palestre sulla griglia. |
| `GameModelOptions` | Builder opzioni per costruire `GameModel`. |
| `HealingCheck` | Builder condiviso per abilitare/tooltip cura in hub. |

---

## model.persistence — dati su disco

| Classe / interfaccia | Responsabilità |
|----------------------|----------------|
| `GameStateRepository` | Porta save/load multi-slot (contratto). |
| `HibernateGameStateRepository` | Impl Hibernate: JSON in `sessioni_salvate`. |
| `GameCatalogLoader` | Porta caricamento catalogo. |
| `HibernateGameCatalogLoader` | Impl: catalogo da H2 a `GameCatalog`. |
| `CatalogSeedJsonLoader` | Legge `catalog-seed.json`. |
| `CatalogDatabaseSeeder` | Scrive/aggiorna tabelle catalogo su H2. |
| `CatalogEntityMapper` | Entity JPA → oggetti dominio/catalogo. |
| `SessioneJsonMapper` | JSON sessione ↔ `GameState` + posizione mappa. |
| `SessionJsonSerializer` | Serializza/deserializza payload JSON. |
| `SessioneSalvataJpaRepository` | Query JPQL su tabella slot. |
| `SessionRepositoryOptions` | Builder dipendenze repository sessione. |
| `SavedSessionSummary`, `LoadedSession`, `SaveSessionCommand` | Tipi per lista slot, load e save. |
| Entity JPA (`CreaturaEntity`, `PalestraEntity`, …) | Mapping tabelle H2 catalogo e slot. |

Dettaglio tabelle e JSON: [Persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza).

---

## model.validation e model.builder

| Classe | Responsabilità |
|--------|----------------|
| `Validator<T>` | Template method: valida un aggregato; impl in `validation.implementations`. |
| `ValidatorFactory` | Restituisce validator per tipo (`getCreatureValidator()`, …). |
| `*Validator` (Creature, Move, GameState, …) | Regole di coerenza su entità e aggregati. |
| `*Builder` (Creature, Player, GameState, …) | Costruzione controllata con validazione. |

---

## controller — schermate e navigazione

| Classe / interfaccia | Responsabilità |
|----------------------|----------------|
| `MainMenuController`, `LoadGameController`, `HubController`, `BattleController`, `VictoryController` | Controller FXML: eventi UI della rispettiva schermata. |
| `BattlePresenter`, `HubPresenter`, `OverworldPresenter` | Logica schermata estratta dal controller FXML. |
| `ScreenNavigator` | Implementa navigazione e save/load tra schermate. |
| `ScreenFactory` | Monta FXML + controller da `FxmlPaths`. |
| `MainView` | Shell root applicazione. |
| `*Actions`, `*Navigation` | Interfacce piccole per azioni/navigazione di ogni schermata. |
| `*ActionsImpl` | Delegano al `ScreenNavigator`. |
| `FxmlPaths`, `FxmlScreenLoader`, `DialogHelper` | Path FXML, caricamento, dialoghi. |
| `SavedSessionSummaryCell` | Cella lista slot in carica partita. |
| `HubTeamRowFactory` | Riga team nell'hub (carta + cura). |
| `BattleCommandBindings` | Builder binding bottoni mosse/switch in battaglia. |

---

## view — interfaccia grafica

| Classe | Responsabilità |
|--------|----------------|
| `OverworldMap` | Griglia mappa, input movimento, modale palestra. |
| `OverworldTileRenderer` | Disegno singolo tile (terreno, palestra, giocatore, decor). |
| `OverworldMovement`, `OverworldPlayerSpawn` | Movimento tastiera e posizione iniziale. |
| `OverworldZoomControls` | Zoom mappa. |
| `CreatureCard`, `CreaturePortrait`, `HealthBar` | Widget creature e barra HP. |
| `BattleArenaView`, `ArenaLayoutSpec` | Layout arena duello. |
| `BattleLogRenderer`, `BattleEventTranslator` | Cronaca battaglia in italiano. |
| `Messages` | Accesso a `messages_it.properties`. |
| `PortraitAssetResolver` | Path immagini creature/giocatore da catalogo. |
| `UiTheme` / `DuelUiTheme` | Skin CSS alternativa sulla root FXML. |
