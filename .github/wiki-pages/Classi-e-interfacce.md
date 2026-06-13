# Classi e interfacce

Questa pagina elenca i tipi principali della repository con la responsabilità associata a ciascuno.

Oltre a classi e interfacce, il progetto usa builder, record e DTO JPA per wiring, eventi e persistenza. I tipi minori seguono le stesse convenzioni descritte in [Responsabilità e architettura](Responsabilita-e-architettura).

Package base: `it.unicam.cs.mpgc.rpg125664`.

## Package `app`

| Tipo | Responsabilità |
| --- | --- |
| `Main` | Entry point Gradle; avvio JavaFX. |
| `RpgApplication` | `Application` JavaFX: bootstrap, `MainView`, chiusura EMF a fine app. |
| `AppModule` | Composition root: EntityManagerFactory, repository, invocazione `ServiceGraph`. |
| `CatalogBootstrap` | Seed catalogo H2 da JSON e caricamento `GameCatalog`. |
| `ServiceGraph` | Crea servizi, strategy e `GameModel`; restituisce anche `PortraitAssetResolver`. |

## Package `model.entity` e `model.catalog`

| Tipo | Responsabilità |
| --- | --- |
| `GameState` | Stato partita: giocatore, palestre, spostamento, check sfida boss, vittoria campagna. |
| `Player` | Giocatore umano: gloria (`Score`), team (`CreatureHolder`), palestra corrente. |
| `Creature` | Creatura in partita: statistiche, HP, mosse, KO. |
| `CreatureHolder` | Team e creatura attiva; switch e prima viva disponibile. |
| `GymRoom` | Palestra: connessioni, soglia gloria, flag completamento. |
| `GymBoss` | Boss di palestra con il suo team. |
| `Move` | Mossa: potenza, precisione, nome. |
| `Score` | Punti fama (gloria) del giocatore. |
| `GameCatalog` | Lookup template creature, palestre e mosse per id; path skin UI. |
| `CreatureTemplate`, `GymTemplate`, `BossTemplate` | Dati statici letti dal catalogo. |
| `NewGameSettings`, `CatalogIds` | Parametri nuova partita e costanti id. |

## Package `model.combat` e `model.event`

| Tipo | Responsabilità |
| --- | --- |
| `BattleRoundExecutor` | Un round: ordine turni, attacchi, KO, switch automatico se creatura attiva KO. |
| `BattleService` | Inizio e fine battaglia; delega i round all'executor. |
| `AttackOutcome` | Risultato colpo: hit o miss, danno, KO difensore. |
| `AttackResolutionStrategy` | Contratto: risolvere danno di una mossa. |
| `BossMoveStrategy` | Contratto: scegliere mossa del boss. |
| `TurnBasedAttackResolutionStrategy` | Implementazione default danno e miss. |
| `AccuracyThresholdBossMoveStrategy` | Implementazione default IA boss. |
| `BattleEvent` | Eventi immutabili del combattimento (sealed interface). |
| `Side` | Lato in battaglia (giocatore o boss). |

## Package `model.service`

| Tipo | Responsabilità |
| --- | --- |
| `GameModel` | Facciata per i controller: battaglia, cura, save/load, stato mappa. |
| `GameStateHolder` | `GameState` corrente in memoria. |
| `NewGameService` | Crea o resetta partita da catalogo. |
| `HealingService` | Cura creatura attiva in hub (costo gloria). |
| `GymCompletionHandler` | Ricompense quando una palestra viene completata. |
| `SessionPersistenceFacade` | Save, load e delete slot verso il repository. |
| `GymStatusStrategy` / `DefaultGymStatusStrategy` | Stato visivo palestra sulla mappa. |
| `GymStatus` | Enum: completata, disponibile, corrente, gloria insufficiente, irraggiungibile. |
| `OverworldGridLayout`, `GymCellPlacement` | Posizionamento deterministico palestre sulla griglia. |
| `GameModelOptions` | Builder opzioni per costruire `GameModel`. |
| `HealingCheck` | Builder condiviso per abilitare e tooltip cura in hub. |

## Package `model.persistence`

| Tipo | Responsabilità |
| --- | --- |
| `GameStateRepository` | Porta save/load multi-slot (contratto). |
| `HibernateGameStateRepository` | Implementazione Hibernate: JSON in `sessioni_salvate`. |
| `GameCatalogLoader` | Porta caricamento catalogo. |
| `HibernateGameCatalogLoader` | Implementazione: catalogo da H2 a `GameCatalog`. |
| `CatalogSeedJsonLoader` | Lettura `catalog-seed.json`. |
| `CatalogDatabaseSeeder` | Scrittura e aggiornamento tabelle catalogo su H2. |
| `CatalogEntityMapper` | Entity JPA → oggetti dominio e catalogo. |
| `SessioneJsonMapper` | JSON sessione ↔ `GameState` + posizione mappa. |
| `SessionJsonSerializer` | Serializza e deserializza payload JSON. |
| `SessioneSalvataJpaRepository` | Query JPQL su tabella slot. |
| `SessionRepositoryOptions` | Builder dipendenze repository sessione. |
| `SavedSessionSummary`, `LoadedSession`, `SaveSessionCommand` | Tipi per lista slot, load e save. |
| Entity JPA (`CreaturaEntity`, `PalestraEntity`, …) | Mapping tabelle H2 catalogo e slot. |

Dettaglio tabelle e JSON: [Dati e persistenza](Dati-e-persistenza).

## Package `model.validation` e `model.builder`

| Tipo | Responsabilità |
| --- | --- |
| `Validator<T>` | Template method: valida un aggregato; implementazioni in `validation.implementations`. |
| `ValidatorFactory` | Restituisce validator per tipo (`getCreatureValidator()`, …). |
| `*Validator` (Creature, Move, GameState, …) | Regole di coerenza su entità e aggregati. |
| `*Builder` (Creature, Player, GameState, …) | Costruzione controllata con validazione. |

## Package `controller`

| Tipo | Responsabilità |
| --- | --- |
| `MainMenuController`, `LoadGameController`, `HubController`, `BattleController`, `VictoryController` | Controller FXML: eventi UI della rispettiva schermata. |
| `BattlePresenter`, `HubPresenter`, `OverworldPresenter` | Logica schermata estratta dal controller FXML. |
| `ScreenNavigator` | Implementa navigazione e save/load tra schermate. |
| `ScreenFactory` | Monta FXML e controller da `FxmlPaths`. |
| `MainView` | Shell root applicazione. |
| `*Actions`, `*Navigation` | Interfacce piccole per azioni e navigazione di ogni schermata. |
| `*ActionsImpl` | Delegano al `ScreenNavigator`. |
| `FxmlPaths`, `FxmlScreenLoader`, `DialogHelper` | Path FXML, caricamento, dialoghi. |
| `SavedSessionSummaryCell` | Cella lista slot in carica partita. |
| `HubTeamRowFactory` | Riga team nell'hub (carta e cura). |
| `BattleCommandBindings` | Builder binding bottoni mosse e switch in battaglia. |

## Package `view`

| Tipo | Responsabilità |
| --- | --- |
| `OverworldMap` | Griglia mappa, input movimento, modale palestra. |
| `OverworldTileRenderer` | Disegno singolo tile (terreno, palestra, giocatore, decor). |
| `OverworldMovement`, `OverworldPlayerSpawn` | Movimento tastiera e posizione iniziale. |
| `OverworldZoomControls` | Zoom mappa. |
| `CreatureCard`, `CreaturePortrait`, `HealthBar` | Widget creature e barra HP. |
| `BattleArenaView`, `ArenaLayoutSpec` | Layout arena duello. |
| `BattleLogRenderer`, `BattleEventTranslator` | Cronaca battaglia in italiano. |
| `Messages` | Accesso a `messages_it.properties`. |
| `PortraitAssetResolver` | Path immagini creature e giocatore da catalogo. |
| `UiTheme` / `DuelUiTheme` | Skin CSS alternativa sulla root FXML. |
