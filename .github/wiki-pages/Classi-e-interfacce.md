# Classi e interfacce

← [Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura)

Il progetto ha circa **160 classi** in `src/main/java`. Qui elenco le classi e interfacce più rilevanti per layer; le altre (builder, DTO, entity JPA, componenti UI minori) seguono la stessa organizzazione descritta in [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura).

---

## app

- `Main` — entry point Gradle; avvia `RpgApplication`
- `RpgApplication` — Application JavaFX: bootstrap `AppModule`, `MainView`, chiusura EMF
- `AppModule` — composition root: EMF, seed catalogo, strategy combattimento, `BattleRoundExecutor`, `PortraitAssetResolver`, `GameModel`

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

- `GameModel` — facciata UI verso tutti i servizi e la persistenza
- `BattleService`, `NewGameService`, `HealingService` — casi d'uso principali
- `GymCompletionHandler` — ricompense al completamento palestra
- `SessionPersistenceFacade`, `GameStateHolder` — persistenza e stato in memoria
- `GymStatusStrategy`, `DefaultGymStatusStrategy` — stato palestre sulla mappa
- `GymStatus` — enum: `COMPLETED`, `AVAILABLE`, `CURRENT`, `NEEDS_POINTS`, `UNREACHABLE`
- `OverworldGridLayout`, `OverworldSpawnPosition` — layout deterministico mappa (seed `LAYOUT_SEED = 42`)

---

## model.persistence

- `AbstractHibernateAdapter` — base condivisa model.persistence Hibernate
- `HibernateGameCatalogLoader`, `CatalogEntityMapper`, `CatalogDatabaseSeeder`, `CatalogSeedJsonLoader` — catalogo da JSON → H2 → dominio
- `PalestraCollegamentiSupport`, `CatalogLoadSupport` — collegamenti palestre e helper seed
- `HibernateGameStateRepository`, `SessioneSalvataJpaRepository` — persistenza slot
- `SessioneSalvataSummaryMapper` — metadati slot per lista UI
- `SessioneJsonMapper`, `SessionJsonSerializer`, `LoadedSessionPayload` — serializzazione JSON
- `UltimaSessioneSalvataDto`, `CreaturaTeamDto`, `PalestraProgressoDto`, `PosizioneMappaDto` — DTO sessione
- Entity JPA: `GiocatoreEntity`, `CreaturaEntity`, `MossaEntity`, `PalestraEntity`, `SessioneSalvataEntity`

---

## view

### Navigazione e azioni

- `MainView` — layout root; crea `ScreenNavigator` con `GameModel` e `PortraitAssetResolver`
- `ScreenNavigator` — routing hub/battaglia/vittoria e save/load/delete
- `ScreenFactory`, `RootScreenStack`, `FxmlScreenLoader`, `DialogHelper`
- `PersistenceUiGuard`, `PersistenceOperation` — gestione errori persistenza in UI
- `MainMenuNavigation`, `HubNavigation`, `LoadGameNavigation`, `VictoryNavigation`, `ScreenNavigation`
- `MainMenuActions`, `HubActions`, … + `*ActionsImpl` in `actions.implementations`

### Controller e controller

- `BattleController`, `HubController`, `OverworldController`, `LoadGameController`, `VictoryController`
- `MainMenuController`, `LoadGameController`, `HubController`, `BattleController`, `VictoryController`
- `BattleCommandColumnController`, `BattleEndOverlayController` — sotto-pannelli FXML battaglia

### Componenti e overworld

- `OverworldMap`, `OverworldZoomControls`, `OverworldGymModalController`, `OverworldLayoutSupport`, `OverworldTileRenderer`
- `CreaturePortrait`, `CreatureCard`, `BattleArenaView`, `HealthBar`, `HamburgerMenu`
- `PortraitAssetResolver` — path ritratti da `GameCatalog`
- `Messages`, `BattleEventTranslator`, `UiErrorReporter` — i18n e messaggi UI
- `DuelUiTheme` — tema schermata battaglia
