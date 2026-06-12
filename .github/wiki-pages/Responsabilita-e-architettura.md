# Responsabilità e architettura

← [Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home)

Ho organizzato **GymQuest** in **MVC** perché mi permette di separare bene regole di gioco, interfaccia e input utente. Il **model** (`model`) contiene entità, servizi e persistenza **senza JavaFX**; la **view** (`view`) disegna FXML e componenti; i **controller** (`controller`) collegano eventi UI al model.

La specifica chiede anche estendibilità su più dispositivi: tenendo il model indipendente dalla UI desktop, in futuro potrei aggiungere un client diverso che chiama sempre `GameModel` (vedi [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita)).

---

## Layer e dipendenze

```mermaid
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

| Layer | Package | Cosa fa |
|-------|---------|---------|
| **app** | `...app` | Avvio: JPA, seed catalogo, wiring `GameModel`, JavaFX |
| **model** | `...model` | Regole di gioco, combattimento, servizi, Hibernate — niente JavaFX |
| **view** | `...view` | Componenti UI, mappa, tema, messaggi |
| **controller** | `...controller` | Controller FXML, navigazione, presenter |

Le frecce vanno **controller → model** e **view → controller**. Il model non importa classi JavaFX.

---

## Chi fa cosa (componenti principali)

### Model

- **`GameModel`** — unico punto d'ingresso per i controller: battaglia, cura, save/load, stato mappa.
- **`GameState`** — mondo di gioco: giocatore, palestre, `moveTo`, `canChallengeGym`.
- **`BattleService` / `BattleRoundExecutor`** — inizio/fine battaglia ed esecuzione di un round.
- **`NewGameService`, `HealingService`, `GymCompletionHandler`** — nuova partita, cura hub, ricompense palestra.
- **`model.persistence`** — Hibernate, JSON sessioni, catalogo H2.

### View

- FXML in `src/main/resources/fxml/`; path Java in `FxmlPaths`.
- **`view.component`** — `CreatureCard`, `BattleArenaView`, `HealthBar`, …
- **`view.overworld`** — mappa, tile, modale palestra, movimento.
- **`view.support`** — `Messages`, traduzione log battaglia, errori UI.

### Controller

- **`ScreenNavigator`** — passa da menu a hub, battaglia, vittoria; save/load.
- **`ScreenFactory`** — carica FXML + controller.
- **Controller FXML** — gestiscono click e input.
- **Presenter** (`BattlePresenter`, `HubPresenter`, …) — logica schermata tolta dai controller FXML per non gonfiarli.

### Bootstrap

- **`AppModule`** — crea EMF e repository.
- **`CatalogBootstrap`** — seed e catalogo all'avvio.
- **`ServiceGraph`** — assembla servizi e `GameModel`.
- **`RpgApplication`** — entry JavaFX.

---

## Organizzazione package

```
it.unicam.cs.mpgc.rpg125664
├── app/
├── model/          entity, combat, service, persistence, validation, …
├── view/           component, overworld, support, theme
└── controller/     *Controller, *Presenter, navigation/
```

Interfacce nel package padre (`Validator`, `*Navigation`, strategy combattimento), implementazioni in sottocartella `implementations/` — stessa idea in più punti del progetto.

---

## Flusso tipico (nuova partita → battaglia → save)

1. **Avvio** — `Main` → `RpgApplication` → `AppModule.bootstrap()` → menu.
2. **Nuova partita** — `MainMenuController` → `NewGameService` → hub.
3. **Hub** — `HubController` + `OverworldMap`; sfida → `ScreenNavigator.showBattle()`.
4. **Battaglia** — `BattleController` → `GameModel.attack()` → eventi nel log.
5. **Salvataggio** — menu hub → `GameModel.persistSession()` → `HibernateGameStateRepository`.

---

## Scelte di design (SOLID nel contesto MVC)

Durante il corso abbiamo visto i principi SOLID; li ho applicati così, legati al MVC che ho scelto.

### S — Single Responsibility

Ogni classe ha un compito preciso. Alcuni esempi:

- `BattleRoundExecutor` — esegue **un round** (ordine attacchi, KO, swap).
- `BattleController` — solo UI del duello (bottoni, pannelli).
- `BattleEventTranslator` — traduce eventi del model in testo per la view.
- `SessionPersistenceFacade` — save/load senza spargere JPQL nei controller.
- `CatalogBootstrap` — solo seed e caricamento catalogo all'avvio.

Se cambio come calcolo il danno modifico `TurnBasedAttackResolutionStrategy`, non il controller. Se cambio come scrivo nel log modifico `BattleLogRenderer`, non `GameState`.

### O — Open/Closed

Per aggiungere comportamenti nuovi implemento interfacce esistenti e le registro in `ServiceGraph`, senza riscrivere `BattleService` o `BattleRoundExecutor`:

- danno / miss → nuova `AttackResolutionStrategy`;
- IA boss → nuova `BossMoveStrategy`;
- colori palestre sulla mappa → nuova `GymStatusStrategy`;
- validazione → nuovo `Validator<T>` registrato in `ValidatorFactory`.

### L — Liskov Substitution

Le implementazioni rispettano il contratto dell'interfaccia. Esempio: `GameModel` dipende da `GymStatusStrategy`, non dalla classe concreta `DefaultGymStatusStrategy`. Stessa cosa per `GameStateRepository` (`HibernateGameStateRepository`) e per le interfacce `*Actions` usate dai controller.

### I — Interface Segregation

Evito un'unica interfaccia gigante "Gioco". Ogni schermata ha le sue azioni:

- `HubActions` per l'hub, `LoadGameActions` per il caricamento, ecc.

`HubController` conosce solo `HubActions`, non i metodi del menu principale. `ScreenNavigator` implementa tutto, ma ogni `*ActionsImpl` resta piccolo.

### D — Dependency Inversion

Controller e presenter dipendono da **`GameModel`**, non da Hibernate. Il model dipende da **`GameStateRepository`**, non dalla tabella SQL. Le implementazioni concrete le creo in **`AppModule` / `ServiceGraph`** e le passo dentro `GameModelOptions` o al repository.

Il model **non importa JavaFX**; i controller **non importano** entità JPA. Così resta chiaro cosa è regola di gioco e cosa è infrastruttura.

---

## Altri pattern che ho usato

- **Facade** — `GameModel` unifica i servizi per la UI.
- **Strategy** — combattimento, stato palestre, tema CSS.
- **Repository** — `GameStateRepository` dietro la persistenza slot.
- **Factory** — `ScreenFactory`, `HubTeamRowFactory`.
- **Builder** — quando servono più di 3 parametri (regola che mi sono dato per leggibilità): `GameModelOptions`, `HealingCheck`, `ArenaLayoutSpec`, …

---

## Firme metodo

Nessun metodo pubblico con più di **3 parametri**. Se ne servono di più uso un builder (`*Options` per wiring, `*Context` per contesto condiviso, `*Bindings` per callback UI). I **record** li tengo per DTO, comandi sessione ed eventi — non per collegare componenti.

Elenco classi con responsabilità punto per punto: [Classi e interfacce](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Classi-e-interfacce). Estensioni future: [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita).
