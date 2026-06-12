# Estendibilità

← [Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura)

La specifica dice che **non serve avere tutte le funzionalità nella prima release**, ma che deve essere chiaro come integrarne di nuove e come usare l'app su **più dispositivi** (desktop, mobile, web…). Qui spiego cosa ho lasciato fuori da v1 e **dove agganciare** le estensioni nel codice che ho già scritto.

---

## Idea generale

Ho tenuto **stabile** il package `model` (regole, entità, servizi) e **sostituibile** la UI (`view` + `controller`) e la persistenza concreta (`model.persistence`).

Oggi c'è solo client **JavaFX desktop**. Un client web o mobile dovrebbe chiamare **`GameModel`** (o un wrapper REST sopra di esso) senza importare Hibernate o JavaFX.

```mermaid
flowchart LR
  View[view e controller]
  Model[model]
  Pers[persistence]
  View --> Model
  Pers --> Model
```

---

## Altro dispositivo (web, mobile, …)

Passi che farei:

1. Nuovo modulo/pacchetto presentation (es. API REST o app mobile).
2. Chiamate solo a `GameModel` (o DTO derivati).
3. Nessun import di classi JavaFX o entity JPA nel client.

Regole come `canChallengeGym`, danno, gloria resterebbero identiche perché vivono nel model.

---

## Nuovo backend salvataggi

1. Nuova classe che **implementa `GameStateRepository`**.
2. Registrazione in `AppModule` / `ServiceGraph` al posto di `HibernateGameStateRepository`.
3. I controller non cambiano: continuano a usare `GameModel`.

Oggi salvo JSON in H2 locale. In futuro: PostgreSQL, sync cloud, ecc. — stesso contratto.

---

## Nuove regole di gioco

| Cosa aggiungere | Dove |
|-----------------|------|
| Nuovo calcolo danno / critici | `AttackResolutionStrategy` + impl in `strategy.implementations` |
| IA boss diversa | `BossMoveStrategy` |
| Logica stato palestre mappa | `GymStatusStrategy` |
| Nuovo tipo evento in log | Record in `BattleEvent` + riga in `BattleEventTranslator` |

Le collego in **`ServiceGraph`**: cambio l'istanza strategy senza modificare `BattleService`.

---

## Nuove creature, palestre, mosse

1. Modifico `catalog-seed.json`.
2. Riavvio: `CatalogDatabaseSeeder` aggiorna H2.
3. Se i validator accettano i dati, il dominio non richiede altre modifiche.

---

## Nuova schermata JavaFX

1. FXML in `src/main/resources/fxml/`.
2. Path in `FxmlPaths`.
3. Controller in `controller/`, eventuale presenter.
4. Montaggio in `ScreenFactory`, transizione in `ScreenNavigator`.
5. Se servono callback verso il navigator: interfaccia `*Actions` + `*ActionsImpl`.

---

## Validazione nuovi tipi

Nuovo aggregato → `{Tipo}Validator extends Validator<T>` + registrazione in `ValidatorFactory`. Costruzione con `*Builder` prima della validazione.

---

## Altre idee (non in v1)

- **Autosave** — oggi salvo solo manualmente dall'hub; basterebbe chiamare `GameModel.persistSession()` da un listener.
- **Inventario / negozio** — nuovo servizio in `model.service` + UI hub.
- **Login multi-utente** — filtrare `sessioni_salvate` per `id_utente` (colonna già presente).
- **Schema SQL normalizzato per sessione** — nuove tabelle + mapper; `GameState` invariato.
- **Altra lingua UI** — secondo file `messages_xx.properties` + `Messages.setLocale(...)`.

---

## Convenzioni che aiutano le estensioni

- Max **3 parametri** per metodo pubblico; altrimenti builder (`GameModelOptions`, `HealingCheck`, …).
- Interfacce nel package padre, impl in `implementations/`.
- Record per DTO/eventi, non per wiring.

Organizzazione layer: [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura).
