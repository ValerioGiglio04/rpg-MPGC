# Classi e interfacce

← [Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura)

Il progetto ha circa 160 classi Java. Qui elenco solo le **entry point** per layer; le altre seguono la stessa organizzazione descritta in [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura).

---

## app

- `Main` — entry point Gradle
- `RpgApplication` — avvio JavaFX e chiusura risorse JPA
- `AppModule` — composition root: EMF, seed catalogo, strategy, `GameModel`, `PortraitAssetResolver`

## model

- `GameState` — stato mondo: giocatore, palestre, regole `moveTo` e `canChallengeGym`
- `Player`, `Creature`, `CreatureHolder` — giocatore, creature in partita, team attivo
- `GymRoom`, `GymBoss` — palestra e boss con team
- `GameCatalog` — template statici e lookup; crea istanze mutabili per la partita
- `BattleRoundExecutor` — esecuzione di un round di battaglia
- `AttackResolutionStrategy`, `BossMoveStrategy` — contratti combattimento (impl in `strategy.implementations`)
- `BattleEvent` — eventi sealed per il log UI (colpo, KO, switch, …)
- `Validator<T>` — validazione post-build; registry in `ValidatorFactory` (`validation.support`)
- `GameStateRepository`, `GameCatalogLoader` — porte persistenza e catalogo (`model.persistence`)

## model.service

- `GameModel` — facciata unica per la UI
- `BattleService`, `NewGameService`, `HealingService` — casi d'uso principali
- `GymCompletionHandler` — ricompense al completamento palestra
- `SessionPersistenceFacade` — save/load/delete slot
- `GameStateHolder` — stato corrente in memoria + posizione mappa
- `GymStatusStrategy` — calcolo stato palestre sull'overworld (impl: `DefaultGymStatusStrategy`)

## model.persistence

- `HibernateGameCatalogLoader` — carica `GameCatalog` da H2
- `HibernateGameStateRepository` — implementa `GameStateRepository` (JSON in `sessioni_salvate`)
- `SessioneJsonMapper` — `GameState` ↔ DTO JSON
- `SessionJsonSerializer` — scrittura/lettura del CLOB
- `CatalogDatabaseSeeder` — allinea H2 a `catalog-seed.json` all'avvio

## view

- `MainView` — layout root; crea `ScreenNavigator`
- `ScreenNavigator` — routing tra schermate e save/load con dialoghi
- `ScreenFactory`, `RootScreenStack` — costruzione FXML e swap schermata
- `BattleController`, `HubController`, `OverworldController`, `LoadGameController`, `VictoryController` — logica schermata verso `GameModel`
- `OverworldMap` — mappa hub (movimento, zoom, modale palestra)
- `PortraitAssetResolver` — path ritratti da catalogo, senza leggere `skinPath` dal dominio runtime
- `Messages` — testi UI da `messages_it.properties`

Controller FXML (`MainMenuController`, `BattleController`, …) restano sottili e delegano ai controller.
