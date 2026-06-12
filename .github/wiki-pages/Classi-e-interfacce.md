# Classi e interfacce

← [Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura)

Qui elenco le classi e interfacce più rilevanti per layer; le altre (builder, DTO, entity JPA, componenti UI minori) seguono la stessa organizzazione descritta in [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura).

---

## app

- `Main` — entry point Gradle; avvia `RpgApplication`
- `RpgApplication` — Application JavaFX: bootstrap `AppModule`, `MainView`, chiusura EMF
- `AppModule` — composition root: EMF, repository, wiring verso `GameModel`
- `CatalogBootstrap` — seed catalogo H2 + caricamento `GameCatalog` (usato da `AppModule.bootstrap()`)
- `ServiceGraph` — assembla servizi di gioco e restituisce `GameModel` + `PortraitAssetResolver`

---

## model.entity e model.catalog

- `GameState` — stato mondo: giocatore, palestre, `moveTo`, `canChallengeGym`, `allGymsCompleted`
- `Player`, `Creature`, `CreatureHolder` — giocatore, creature in partita, team e creatura attiva
- `GymRoom`, `GymBoss` — palestra con connessioni, soglia punti, boss e team
- `Move`, `Score` — mossa e punti fama (gloria)
- `GameCatalog` — indice template; lookup e `creatureSkinPath(catalogId)` per asset UI
- `CreatureTemplate`, `GymTemplate`, `BossTemplate`, `MoveTemplate` — definizioni statiche
- `NewGameSettings`, `CatalogIds` — parametri nuova partita e costanti id catalogo

---

## model.combat, model.event, model.persistence

- `BattleRoundExecutor` — esecuzione di un round (turni, attacchi, switch, eventi)
- `AttackOutcome` — esito colpo: hit/miss, danno, KO
- `AttackResolutionStrategy`, `BossMoveStrategy` — contratti strategy combattimento
- `TurnBasedAttackResolutionStrategy`, `AccuracyThresholdBossMoveStrategy` — implementazioni default
- `BattleEvent` — sealed interface: `RoundStarted`, `AttackHit`, `AttackMissed`, `CreatureKnockedOut`, `BossDefeated`, `CreaturesAcquired`, …
- `Side` — lato in battaglia (giocatore o boss)
- `GameStateRepository` — multi-save: `listSaves`, `save`, `load`, `delete`, `markLastPlayed`
- `GameCatalogLoader` — caricamento `GameCatalog`
- `SavedSessionSummary`, `LoadedSession`, `SaveSessionCommand`, `OverworldPosition` — tipi sessione
- `SaveSlotLabels` — formato data condiviso per nomi slot e lista caricamento

---

## model.validation e model.builder

- `Validator<T>` — template method `validate(T)`; implementazioni in `validation.implementations`
- `ScoreValidator`, `PlayerValidator`, `GameStateValidator`, `CreatureValidator`, `CreatureHolderValidator`, `GymRoomValidator`, `GymBossValidator`, `MoveValidator`
- `ValidatorFactory` — registry `get*Validator()` in `validation.support`
- `GameStateBuilder`, `PlayerBuilder`, `CreatureBuilder`, … — costruzione aggregati con validazione

---

## model.service

- `GameModel` — facciata usata dai controller verso servizi e persistenza; `persistSession()` unifica il flusso di salvataggio
- `BattleService`, `NewGameService`, `HealingService` — casi d'uso principali
- `GymCompletionHandler` — ricompense al completamento palestra
- `SessionPersistenceFacade`, `GameStateHolder` — persistenza e stato in memoria
- `GymStatusStrategy`, `DefaultGymStatusStrategy` — stato palestre sulla mappa
- `GymStatus` — enum: `COMPLETED`, `AVAILABLE`, `CURRENT`, `NEEDS_POINTS`, `UNREACHABLE`
- `OverworldGridLayout`, `OverworldSpawnPosition`, `GymCellPlacement` — layout deterministico mappa (seed `LAYOUT_SEED = 42`)
- `MapGridContext`, `GymPlacementRequest` — builder per contesto griglia e assegnazione palestre
- `HealingCheck` — builder condiviso tra `HealingService` e `HubPresenter` (cura abilitata / tooltip)
- `GameModelOptions` — builder opzioni assemblate in `ServiceGraph`

---

## model.persistence (dettaglio)

- `AbstractHibernateAdapter` — base condivisa adapter Hibernate
- `HibernateGameCatalogLoader`, `CatalogEntityMapper`, `CatalogDatabaseSeeder`, `CatalogSeedJsonLoader` — catalogo da JSON → H2 → dominio
- `PalestraCollegamentiSupport`, `CatalogLoadSupport` — collegamenti palestre e helper seed
- `HibernateGameStateRepository`, `SessioneSalvataJpaRepository` — persistenza slot
- `SessionRepositoryOptions` — builder opzioni repository (EMF, JPA, serializer, mapper)
- `SessioneSalvataEntity.SaveRowDraft` — builder campi iniziali nuova riga salvataggio
- `SessioneSalvataSummaryMapper` — metadati slot per lista UI
- `SessioneJsonMapper`, `SessionJsonSerializer`, `LoadedSessionPayload` — serializzazione JSON
- `UltimaSessioneSalvataDto`, `CreaturaTeamDto`, `PalestraProgressoDto`, `PosizioneMappaDto` — DTO sessione
- Entity JPA: `GiocatoreEntity`, `CreaturaEntity`, `MossaEntity`, `PalestraEntity`, `SessioneSalvataEntity`

---

## controller e view

### Navigazione (`controller.navigation`)

Convenzione del progetto: **interfacce nel package padre**, **implementazioni concrete in `implementations/`**, helper in `support/` (come in `model.validation`, `model.combat.strategy`, ecc.).

```
controller/navigation/
├── MainMenuNavigation, HubNavigation, LoadGameNavigation, VictoryNavigation
├── MainMenuActions, HubActions, LoadGameActions, VictoryActions
├── ScreenNavigation
├── implementations/
│   ├── ScreenNavigator
│   └── MainMenuActionsImpl, HubActionsImpl, LoadGameActionsImpl, VictoryActionsImpl
└── support/
    ├── MainView, ScreenFactory, FxmlScreenLoader, FxmlPaths
    ├── RootScreenStack, DialogHelper
    └── PersistenceUiGuard, PersistenceOperation
```

- `MainView` — layout root (`FxmlPaths.MAIN_SHELL`); crea `ScreenNavigator`
- `ScreenNavigator` — routing hub/battaglia/vittoria, save/load/delete; policy `redirectToVictoryIfCompleted()`
- `ScreenFactory` — monta FXML + controller; path centralizzati in `FxmlPaths`
- `*Navigation` / `*Actions` — contratti per schermata; `*ActionsImpl` delegano al navigator

### Presenter, controller e helper schermata

- `BattlePresenter`, `HubPresenter`, `OverworldPresenter` — logica schermata estratta dai controller FXML
- `BattleCommandBindings` — builder binding colonna comandi duello (mosse, switch, callback)
- `MainMenuController`, `LoadGameController`, `HubController`, `BattleController`, `VictoryController`
- `BattleCommandColumnController`, `BattleEndOverlayController` — sotto-pannelli FXML battaglia
- `SavedSessionSummaryCell` — cella lista slot in `LoadGameController`
- `HubTeamRowFactory` — wiring riga team (carta creatura + cura) in `HubController`

### Componenti e overworld

- `OverworldMap`, `OverworldZoomControls`, `OverworldGymModalController`, `OverworldLayoutSupport`, `OverworldTileRenderer`
- `TileRenderAssets`, `GymModalUi`, `OverworldTileRenderer.PlayerMarker` — builder asset tile, UI modale, overlay giocatore
- `ArenaLayoutSpec` — builder layout arena duello
- `OverworldPlayerSpawn`, `OverworldMovement`, `OverworldMapChrome` — spawn, movimento e chrome UI mappa
- `CreaturePortrait`, `CreatureCard`, `BattleArenaView`, `HealthBar`, `HamburgerMenu`
- `PortraitAssetResolver` — path ritratti da `GameCatalog`
- `Messages`, `BattleEventTranslator`, `BattleSideMessages`, `BattleLogRenderer`, `UiErrorReporter` — i18n, log battaglia, rendering cronaca
- `DuelUiTheme` — tema schermata battaglia (`view.theme.implementations`)
