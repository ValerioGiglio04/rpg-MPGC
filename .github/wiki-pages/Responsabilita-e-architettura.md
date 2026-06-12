# Responsabilità e architettura

← [Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home)

Ho organizzato **GymQuest** in **MVC** (Model–View–Controller): il **model** contiene regole di gioco, servizi e persistenza; la **view** espone FXML e componenti JavaFX; i **controller** gestiscono input utente e navigazione verso il model.

---

## Layer e dipendenze

```mermaid
%%{init: {'flowchart': {'curve': 'linear'}}}%%
flowchart TB
  View[view]
  Controller[controller]
  Model[model]
  Bootstrap[app]
  View --> Controller
  Controller --> Model
  Bootstrap --> View
  Bootstrap --> Controller
  Bootstrap --> Model
```

| Layer | Package | Ruolo |
|-------|---------|-------|
| **app** | `...app` | Bootstrap: JPA, seed catalogo, wiring `GameModel`, avvio JavaFX |
| **model** | `...model` | Entità, combattimento, servizi, persistenza H2 — nessuna dipendenza da JavaFX |
| **view** | `...view` | Componenti UI, tema, mappa overworld, FXML in `resources/fxml` |
| **controller** | `...controller` | Controller FXML, navigazione schermate, coordinamento verso `GameModel` |

Le dipendenze vanno **controller → model** e **view → controller** (callback). Il model non importa JavaFX.

---

## Componenti principali

### Model

- **`GameModel`** — facciata usata dai controller: battaglia, cura, save/load, stato mappa
- **`GameState`** — giocatore, palestre, regole `moveTo`, `canChallengeGym`, `allGymsCompleted`
- **`BattleService`** / **`BattleRoundExecutor`** — ciclo battaglia e calcolo turni
- **`NewGameService`**, **`HealingService`**, **`GymCompletionHandler`** — casi d'uso di gioco
- **`model.persistence`** — Hibernate, entità JPA, JSON sessioni, catalogo su H2

### View

- **FXML** (`MainMenu.fxml`, `Hub.fxml`, `Battle.fxml`, …) in `src/main/resources/fxml/`; path Java in `FxmlPaths`
- **`view.component`** — `CreatureCard`, `BattleArenaView`, `HealthBar`, …
- **`view.overworld`** — `OverworldMap`, tile renderer, modale palestra; helper `OverworldPlayerSpawn`, `OverworldMovement`, `OverworldMapChrome`
- **`view.support`** — `Messages`, `BattleEventTranslator`, `BattleSideMessages`, `BattleLogRenderer`, caricamento FXML

### Controller

- **`ScreenNavigator`** (`navigation.implementations`) — flusso tra schermate e dialoghi save/load
- **`ScreenFactory`** — monta FXML + controller via `FxmlPaths`
- **Controller FXML** (`HubController`, `BattleController`, …) — gestiscono eventi e aggiornano la view
- **Presenter** (`BattlePresenter`, `HubPresenter`, `OverworldPresenter`) — logica schermata estratta dai controller
- **Helper UI** — `SavedSessionSummaryCell`, `HubTeamRowFactory`

### Bootstrap

- **`AppModule`** — crea EMF, repository, delega wiring a **`ServiceGraph`**
- **`CatalogBootstrap`** — seed catalogo H2 e caricamento `GameCatalog` all'avvio
- **`ServiceGraph`** — assembla servizi e **`GameModel`**
- **`RpgApplication`** — avvia JavaFX e passa `GameModel` a `MainView`

---

## Organizzazione package

```
it.unicam.cs.mpgc.rpg125664
├── app/                    Main, RpgApplication, AppModule, CatalogBootstrap, ServiceGraph
├── model/
│   ├── entity/             GameState, Creature, GymRoom, …
│   ├── combat/             BattleRoundExecutor, strategy/
│   ├── catalog/            GameCatalog, template creature
│   ├── service/            GameModel, BattleService, NewGameService, GameStateHolder, …
│   ├── overworld/          GymStatus, layout mappa
│   ├── persistence/        Hibernate, entità JPA, mapper, seed
│   ├── builder/, validation/, event/, session/
├── view/
│   ├── component/          widget riusabili
│   ├── overworld/          mappa, spawn/movimento, modale palestra
│   ├── theme/implementations/
│   └── support/, mapper/   Messages, BattleLogRenderer, BattleEventTranslator, …
└── controller/
    ├── *Controller.java    controller FXML
    ├── *Presenter.java     BattlePresenter, HubPresenter, OverworldPresenter
    ├── SavedSessionSummaryCell, HubTeamRowFactory
    └── navigation/         interfacce *Actions/*Navigation
        ├── implementations/  ScreenNavigator, *ActionsImpl
        └── support/          MainView, ScreenFactory, FxmlPaths, …
```

---

## Flusso tipico (nuova partita → battaglia → save)

### 1. Avvio

1. `Main` → `RpgApplication.start()`
2. `AppModule.bootstrap()` — `CatalogBootstrap` (seed + catalogo), poi `ServiceGraph` → `GameModel`
3. `MainView` + `ScreenNavigator` → menu principale

### 2. Nuova partita

1. `MainMenuController` → `ScreenNavigator.startNewGame()`
2. `NewGameService` costruisce `GameState` iniziale
3. Navigazione verso Hub

### 3. Hub e battaglia

1. `HubController` legge stato da `GameModel`, monta `OverworldMap`
2. Sfida palestra → `ScreenNavigator.showBattle()`
3. `BattleController` → `GameModel.attack()` → eventi tradotti nel log (`BattleEventTranslator` + `BattleLogRenderer`)

### 4. Salvataggio

1. Menu Hub → `ScreenNavigator.saveCurrent()`
2. `GameModel` → `SessionPersistenceFacade` → `HibernateGameStateRepository`

---

## Scelte di design (SOLID nel contesto MVC)

| Principio | Dove nel progetto |
|-----------|-------------------|
| **SRP** | `BattleRoundExecutor` = un round; `BattleController` = UI duello; `GameModel` = API stabile per i controller |
| **OCP** | Nuove strategy in `model.combat.strategy.implementations`; wiring in `ServiceGraph` / `AppModule` |
| **DIP** | Controller dipendono da `GameModel`, non da Hibernate; persistenza dietro `GameStateRepository` in `model.persistence` |

Pattern ricorrenti: **Facade** (`GameModel`), **Strategy** (danno, IA boss, stato mappa), **Repository** (salvataggi), **Factory** (`ScreenFactory`, `HubTeamRowFactory`), **Builder** (parameter object e wiring).

Convenzione package: interfacce nel package padre (`*Navigation`, `Validator`, `UiTheme`), implementazioni in sottocartella `implementations/`.

### Firme metodo e parameter object

Per evitare code smell da troppi parametri, **nessun metodo pubblico supera 3 argomenti**. Se servono più valori:

1. **Builder fluente** (scelta preferita per leggibilità): es. `HealingCheck.builder().creature(...).state(...).build()`, `BattleCommandBindings.builder()`, `GymPlacementRequest.builder()`.
2. **Record** solo per DTO, comandi e eventi immutabili (`SaveSessionCommand`, `BattleEvent`, `MoveDto`, …), non per wiring o helper UI.

**Naming** dei parameter object: per assemblaggio e configurazione usiamo **`Options`** (`GameModelOptions`, `SessionRepositoryOptions`); per contesto condiviso `Context` (`MapGridContext`); per callback UI `Bindings` (`BattleCommandBindings`); per layout `Spec` (`ArenaLayoutSpec`).

Esempi di builder introdotti per raggruppare parametri: `MapGridContext`, `GameModelOptions`, `SessionRepositoryOptions`, `ArenaLayoutSpec`, `TileRenderAssets`, `GymModalUi`, `OverworldTileRenderer.PlayerMarker`.

Vedi anche [Classi e interfacce](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Classi-e-interfacce) ed [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita).
