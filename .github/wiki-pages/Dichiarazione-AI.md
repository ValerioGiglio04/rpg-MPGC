# Dichiarazione dettagliata di uso di strumenti di AI

← [Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Repository](https://github.com/ValerioGiglio04/rpg-MPGC)

La specifica del corso chiede una dichiarazione **dettagliata** sull'uso di AI, oltre al paragrafo breve nel [README](https://github.com/ValerioGiglio04/rpg-MPGC#-uso-di-strumenti-di-ai).

**GymQuest, le regole di gioco, l'architettura MVC e le scelte di persistenza le ho progettate e implementate io.** Ho usato strumenti di AI come supporto: spiegazioni quando bloccavo su Hibernate o JavaFX, bozze di codice o testi che poi ho modificato, autocompletamento. Prima di tenere qualcosa nel repo l'ho letto, provato con `./gradlew build` e adattato al mio codice.

---

## Strumenti usati

| Strumento | Per cosa l'ho usato |
|-----------|---------------------|
| **ChatGPT** | Domande su MVC, Hibernate, SOLID; bozze diagrammi per la wiki |
| **Claude** | Errori JPA, configurazione Gradle/Spotless; revisione testi wiki |
| **Gemini** | Dubbi su JavaFX/FXML; alcune texture PNG in `src/main/resources/images/` |
| **GitHub Copilot** | Autocompletamento metodi semplici, Javadoc, stringhe in `messages_it.properties`, correzioni Markdown wiki |

---

## Dove l'AI ha aiutato (e cosa ho fatto io dopo)

### Codice e architettura

- **ChatGPT / Claude** — mi hanno spiegato pattern visti a lezione (MVC, Repository, Strategy, Builder). La struttura package, `GameModel`, separazione catalogo/sessione e validator li ho applicati io sul progetto reale.
- **Copilot** — suggerimenti su getter, boilerplate JavaFX, Javadoc su classi semplici. Ho controllato nomi e responsabilità prima di committare.

### Interfaccia

- **Gemini** — alcune immagini overworld e idee layout; ho integrato quelle che encavano col resto del gioco.
- **Copilot** — bozze righe in `messages_it.properties` e messaggi log battaglia, poi rivisti in italiano.

### Documentazione e build

- **ChatGPT / Claude** — bozze iniziali di diagrammi Mermaid (flusso utente, persistenza). Li ho corretti e allineati al codice effettivo.
- **Claude** — bozza plugin Spotless in `build.gradle`; oggi uso Prettier via Spotless (versione aggiornata nel tempo).
- **Copilot** — link tra pagine wiki, sidebar, piccole correzioni di forma.

---

## Esempi puntuali

| Situazione | Strumento | Esito |
|------------|-----------|--------|
| Errore Hibernate su collection | ChatGPT / Claude | Spiegazione del messaggio; fix scritto da me |
| Dove mettere la cura (service vs dominio) | ChatGPT / Claude | Confronto opzioni; scelta `HealingService` |
| Texture mappa / palestra | Gemini | File PNG usati o sostituiti |
| Spotless / formattazione | Claude | Bozza `build.gradle`; io ho scelto regole e quando applicarle |
| Testi wiki e diagrammi | ChatGPT / Claude / Copilot | Bozze riviste manualmente (questa pagina inclusa) |

---

## Cosa non ho delegato

- Scelta del gioco (RPG palestre), regole combattimento, progressione gloria.
- Design persistenza catalogo H2 + JSON sessione.
- Refactoring MVC, navigazione, presenter, naming package `rpg125664`.
- Verifica finale: build Gradle, prova manuale delle schermate, commit Git.

---

**Valerio Giglio — matricola 125664 — valerio.giglio@studenti.unicam.it**
