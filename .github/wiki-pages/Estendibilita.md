# Estendibilità

← [Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura)

La specifica chiede che sia chiaro come il progetto possa crescere (nuove feature, altri dispositivi) anche se non tutto è nella v1. Qui descrivo i punti di aggancio già presenti nel codice.

---

## Layer sostituibili

Oggi c'è solo UI JavaFX. Dominio e `model.service` restano stabili; si possono sostituire `view` e, se serve, gli model.persistence di persistenza. Un client web o mobile dovrebbe chiamare **`GameModel`** (o un'API equivalente) senza importare Hibernate, JPA o JavaFX.

---

## Nuova UI o nuovo backend

- **Altro client** — nuovo package presentation che usa `GameModel`; opzionale wrapper REST che serializza DTO.
- **Altro storage** — nuova classe che implementa `GameStateRepository`; wiring in `AppModule`. Stesso approccio per `GameCatalogLoader` se il catalogo non resta su H2.

---

## Nuove regole di gioco

- **Combattimento** — nuova impl di `AttackResolutionStrategy` o `BossMoveStrategy`, registrata in `AppModule`.
- **Mappa** — nuova impl di `GymStatusStrategy` in `model.overworld.strategy.implementations`.
- **Eventi battaglia** — nuovi record in `BattleEvent` (sealed) + aggiornamento di `BattleEventTranslator`.

---

## Nuove creature, palestre, mosse

1. Modificare `src/main/resources/game-data/catalog-seed.json`
2. Riavviare l'app: `CatalogDatabaseSeeder` riallinea H2 se mancano dati

Mapping entità → dominio in `CatalogEntityMapper`. Se i nuovi dati rispettano i validator esistenti, il dominio non va toccato.

---

## Nuova schermata JavaFX

1. Aggiungere `NuovaSchermata.fxml` in `src/main/resources/fxml/`
2. Creare controller in `controller` e controller sottile
3. Registrare costruzione in `ScreenFactory` e transizione in `ScreenNavigator`
4. Eventuale callback in `controller.navigation` + `*ActionsImpl`

---

## Altre estensioni possibili

- **Autosave** — chiamare `GameModel.saveCurrent()` da un listener (oggi solo save manuale dall'Hub)
- **Inventario, negozio, achievement** — nuovo servizio in `model.service` + modello in `model`
- **Lingua UI** — `messages_it.properties` e classe `Messages`; FXML usa lo stesso bundle via `FxmlScreenLoader`
- **Login multi-utente** — filtrare `sessioni_salvate` per `id_utente` negli model.persistence, senza cambiare `GameModel`
- **Schema SQL normalizzato al posto del JSON** — nuovo mapper in model.persistence; `GameState` invariato

Per l'organizzazione dei layer vedi [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura).
