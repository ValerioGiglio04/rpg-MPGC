# Dichiarazione dettagliata di uso di strumenti di AI

← [Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Repository](https://github.com/ValerioGiglio04/rpg-MPGC)

Espansione della dichiarazione breve nel [README](https://github.com/ValerioGiglio04/rpg-MPGC#-uso-di-strumenti-di-ai), come richiesto dalla specifica AA 2025/26.

Qui descrivo **solo** dove ho usato ChatGPT, Claude, Gemini o Copilot. Gioco, architettura e wiki li ho scritti e verificati io; l'AI ha accelerato spiegazioni, bozze e autocompletamento.

---

## Strumenti utilizzati

| Strumento | Uso principale |
|-----------|----------------|
| **ChatGPT** | Spiegazioni (architettura MVC, Hibernate, SOLID); bozze diagrammi wiki |
| **Claude** | JPA, Spotless 7.0.4 + google-java-format 1.28.0; bozze diagrammi wiki |
| **Gemini** | JavaFX/FXML; texture PNG in `src/main/resources/images/` |
| **GitHub Copilot** | Autocompletamento, getter/setter, Javadoc, `messages_it.properties`, revisione Markdown wiki |

---

## Contributi per area

### Architettura e codice

**ChatGPT / Claude:** Model-View-Controller, separazione catalogo/sessione, struttura cartelle, Builder, `Validator`, sealed `BattleEvent`, errori JPA, seed idempotente, Jackson.

**GitHub Copilot:** boilerplate JavaFX, getter su entità catalogo, Javadoc.

### Interfaccia e asset

**Gemini:** binding FXML, layout, texture e PNG in `src/main/resources/images/`.

**Copilot:** stringhe in `messages_it.properties`, messaggi log battaglia.

### Documentazione e build

**ChatGPT / Claude:** bozze Mermaid (MVC e flusso utente), revisione wiki.

**Claude:** Spotless 7.0.4 + google-java-format 1.28.0 in `build.gradle`.

**Copilot:** sidebar/footer wiki, link tra pagine, sintassi Mermaid (`curve: linear`).

---

## Esempi concreti

| Contesto | Strumento | Cosa ha prodotto l'AI |
|----------|-----------|------------------------|
| Errore JPA su collection | ChatGPT / Claude | Spiegazione messaggio |
| Cura: service vs dominio | ChatGPT / Claude | Confronto alternative |
| Getter su entità catalogo | Copilot | Bozza metodi |
| Texture overworld | Gemini | File PNG |
| Spotless in `build.gradle` | Claude | Bozza plugin + `spotlessApply` |
| Flowchart architettura, flusso utente, persistenza DB | ChatGPT / Claude | Bozza Mermaid nella wiki |
| Revisione testi wiki | Copilot | Correzioni su `.github/wiki-pages/` |

---

## Limiti

Tutto quanto sopra è entrato nel progetto solo dopo controllo manuale. Per API e configurazioni ho incrociato con documentazione ufficiale e build Gradle.

---

**Valerio Giglio — matricola 125664**
