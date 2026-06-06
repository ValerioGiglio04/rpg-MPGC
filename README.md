# RPG Palestre e Creature

L'idea è un RPG a stanze/palestre in stile Pokémon: il giocatore parte con un team di creature, gira la mappa, sfida i boss delle varie palestre e accumula punti fama man mano che avanza.

Come funziona il gioco
Ogni palestra ha una soglia minima di punti fama: non puoi sfidare il boss se non hai ancora l'esperienza sufficiente. La sfida al boss è una serie di scontri a turni — la palestra si considera completata solo quando tutte le creature del boss sono KO. Vinci punti fama, li usi per sbloccare la palestra successiva.

**Studente**: Valerio Giglio — matricola `125664` — `valerio.giglio@studenti.unicam.it`

## Documentazione

Documentazione completa del progetto (funzionalità, architettura, classi, persistenza, estendibilità, dichiarazione AI): **[Wiki del repository](https://github.com/ValerioGiglio04/rpg-MPGC/wiki)**.

**Persistenza:** catalogo su H2 locale; le partite salvate (più slot) nella tabella `sessioni_salvate` con snapshot JSON in `dati_salvati_json`. Dettagli in [Wiki — Dati e persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza).

**Codice:** package root `it.unicam.cs.mpgc.rpg125664` — dominio e porte in `domain`, casi d’uso in `application` / `application.session`, catalogo H2 e sessione JSON in `adapter.persistence.*`, UI JavaFX in `ui.javafx` (controller, component, overworld, theme). Albero completo nella [Wiki — Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home).

---

## 🚀 Come eseguire il progetto

### Prerequisiti

- Java 21+

### Istruzioni

```bash
git clone https://github.com/ValerioGiglio04/rpg-MPGC.git
cd rpg-MPGC
```

### Build del progetto

Linux, macOS o Git Bash:

```bash
./gradlew build
```

Windows (PowerShell o CMD):

```powershell
.\gradlew.bat build
```

### Esecuzione

Linux, macOS o Git Bash:

```bash
./gradlew run
```

Windows (PowerShell o CMD):

```powershell
.\gradlew.bat run
```

---

## 🤖 Uso di strumenti di AI

Come richiesto dalla specifica, qui sotto indico **dove** ho usato strumenti di AI durante il lavoro (spiegazioni, bozze, autocompletamento). Non è un elenco di tutto il progetto.

- **ChatGPT** e **Claude**: spiegazioni su architettura, Hibernate e design; **creazione di alcuni grafici Mermaid** nella Wiki (flowchart e diagrammi ER, poi rivisti da me); mi hanno suggerito il plugin **Spotless** e `spotlessApply`
- **Gemini**: domande su JavaFX/FXML; alcune immagini e texture
- **GitHub Copilot**: autocompletamento su classi Java, su `messages_it.properties` e bozze sui file Markdown della Wiki (formattazione e link tra le pagine)

Per il dettaglio (file, prompt, cosa ho tenuto o cambiato) vedi la [Wiki — Dichiarazione AI](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dichiarazione-AI).
