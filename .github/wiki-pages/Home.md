# Wiki — RPG Palestre e Creature

> **Navigazione** · [Funzionalità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Funzionalita-implementate) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura) · [Classi](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Classi-e-interfacce) · [Persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza) · [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita) · [AI](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dichiarazione-AI)

## Descrizione

Breve descrizione del progetto:

- **Tipologia:** RPG a palestre in stile Pokémon, con combattimenti a turni tra creature
- **Ambientazione:** mappa overworld con palestre collegate; ogni palestra ha un boss e una soglia di punti fama
- **Obiettivo:** completare tutte le palestre sconfiggendo i team dei boss, accumulando gloria e ampliando il proprio team

Il codice è organizzato in **architettura MVC** (model al centro, model.persistence per persistenza, UI JavaFX) per supportare estensioni future su altri dispositivi.

### Struttura package (sintesi)

```
it.unicam.cs.mpgc.rpg125664
├── app/                              Main, RpgApplication, AppModule
├── model.service/                      BattleService, NewGameService, HealingService, …
├── model.service/              GameModel, SessionPersistenceFacade (Facade), GameStateHolder
├── model.overworld/
│   ├── strategy/ + strategy.implementations/    GymStatusStrategy, DefaultGymStatusStrategy
│   └── GymStatus, layout mappa
├── model.persistence/                      GameStateRepository, GameCatalogLoader
├── model.entity|catalog|event|builder/
├── model.validation/                  Validator (abstract), Rules, MoveRules
│   └── implementations/              ValidatorFactory, *Validator
├── model.combat/
│   ├── strategy/                     AttackResolutionStrategy, BossMoveStrategy
│   └── strategy.implementations/                TurnBased*, AccuracyThreshold*
├── model.persistence.catalog/
│   ├── entities/ dto/ mapper/ seed/ support/
│   └── HibernateGameCatalogLoader
├── model.persistence/              AbstractHibernateAdapter
├── model.persistence.session/
│   ├── entities/ dto/ mapper/ serializer/
│   ├── SessioneSalvataJpaRepository
│   ├── SessioneSalvataSummaryMapper
│   └── HibernateGameStateRepository
└── view/                        Messages, UiErrorReporter, BattleEventTranslator
    ├── navigation/                   MainView, ScreenNavigator, ScreenNavigation (+ ISP), PersistenceUiGuard, PersistenceOperation, FxmlScreenLoader, DialogHelper
    ├── actions/                      MainMenuActions, HubActions, …
    │   └── implementations/          MainMenuActionsImpl, HubActionsImpl, …
    ├── controller/ controller/
    ├── component/ (+ component.builder/)
    ├── overworld/
    └── theme/ + theme.implementations/
```

| Cerchi… | Package / ruolo |
|---------|-----------------|
| Porta persistenza | `model.persistence` → impl in `model.persistence.session` / `.catalog` |
| Entity JPA catalogo | `model.persistence.catalog.entities` |
| DTO / seed JSON catalogo | `model.persistence.catalog.dto` |
| Mapper / seed catalogo | `catalog.mapper`, `catalog.seed`, `catalog.support` |
| Entity + DTO + mapper sessione | `session.entities`, `session.dto`, `session.mapper`, `session.serializer` |
| JPQL / mapping slot sessione | `SessioneSalvataJpaRepository`, `SessioneSalvataSummaryMapper` → `HibernateGameStateRepository` |
| Validazione dominio | `model.validation` → `validation.implementations` (`ValidatorFactory`, `*Validator`) |
| Strategy combattimento | `model.combat.strategy` / `.implementations` |
| Strategy mappa | `model.overworld.strategy` / `.implementations` |
| Facade UI | `model.service` (`GameModel`, `SessionPersistenceFacade`) |
| Shell e routing UI | `controller.navigation` |
| Callback schermata | `controller.navigation` (contratto) → `actions.implementations` (`*ActionsImpl`) |
| Controller MVP | `controller` + `controller` |
| Builder widget UI | `view.component.builder` |
| Tema UI | `view.theme` / `theme.implementations` |

### Suffissi nel codice

| Suffisso / nome | Ruolo | Esempio |
|-----------------|-------|---------|
| `*Loader` | Caricamento risorse (FXML, catalogo) | `FxmlScreenLoader`, `HibernateGameCatalogLoader` |
| `Validator<T>` | Classe astratta: `validate(T)` | builder: costruisci → `validator.validate(instance)` → return |
| `*Validator` | Implementazione in `validation.implementations` | `ScoreValidator`, `CreatureHolderValidator`, … |
| `ValidatorFactory` | Registry in `validation.implementations` | `get*Validator()` restituisce `Validator<T>` |
| `*Actions` | Callback schermata (ISP verso controller) | `HubActions` |
| `*ActionsImpl` | Implementazione callback | `HubActionsImpl` in `actions.implementations` |
| `ScreenNavigation` | Unione ISP navigazione per schermata | implementato da `ScreenNavigator` |
| `MainMenuNavigation` / `LoadGameNavigation` / `HubNavigation` / `VictoryNavigation` | Comandi minimi per ogni `*ActionsImpl` | implementati da `ScreenNavigator` |
| `SaveSlotLabels` | Formato data/ora condiviso per nomi slot e lista caricamento | `model.session` |

Pattern documentati senza suffisso dedicato: `GameModel` (Facade), `AppModule` (composition root), `Validator` (Template Method).

---

## Indice della Wiki

| Pagina | Contenuto |
|:-------|:----------|
| [Funzionalità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Funzionalita-implementate) | Feature implementate, schermate e flussi |
| [Architettura e responsabilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura) | Layer, SOLID, diagrammi |
| [Classi e interfacce](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Classi-e-interfacce) | Elenco classi con responsabilità |
| [Persistenza dei dati](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza) | Catalogo, salvataggio, H2 |
| [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita) | Nuove funzionalità e altri dispositivi |
| [Uso dell'AI](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dichiarazione-AI) | Dichiarazione dettagliata strumenti AI |

---

## Avvio rapido

```bash
git clone https://github.com/ValerioGiglio04/rpg-MPGC.git
cd rpg-MPGC
./gradlew build
./gradlew run
```

---

## Informazioni sul progetto

| Campo | Valore |
|:------|:-------|
| **Studente** | Valerio Giglio — matricola `125664` — `valerio.giglio@studenti.unicam.it` |
| **Corso** | Metodologie di Programmazione, Modellazione e Gestione della Conoscenza — AA 2025/26 |
| **Repository** | [rpg-MPGC](https://github.com/ValerioGiglio04/rpg-MPGC) |
| **Linguaggio** | Java 21 |
| **Build** | Gradle |
| **UI** | JavaFX 21 + FXML |
| **Persistenza** | Catalogo e sessioni: Hibernate 6 + H2 (`~/.rpg-palestre-creature/save`); snapshot partita in `sessioni_salvate.dati_salvati_json` |
| **Package** | `it.unicam.cs.mpgc.rpg125664` |

---

## Nota

Questa Wiki sostituisce la relazione scritta richiesta dalla specifica del corso. Qui sono descritti:

- le scelte progettuali e l'organizzazione del codice
- le funzionalità implementate
- responsabilità di classi e interfacce
- persistenza ed estendibilità
- l'uso degli strumenti di AI (solo contributi dell'AI, come da specifica): [Dichiarazione AI](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dichiarazione-AI)

Per build, esecuzione e riepilogo sintetico fare riferimento al [README](https://github.com/ValerioGiglio04/rpg-MPGC) del repository.
