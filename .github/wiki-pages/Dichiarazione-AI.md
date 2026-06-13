# Dichiarazione dettagliata di uso di strumenti di AI

## Premessa

Nel corso della realizzazione e dell'evoluzione di questa repository sono stati utilizzati strumenti di AI generativa come supporto tecnico e documentale.

La presente dichiarazione documenta:

- quali attività sono state supportate dall'AI;
- con quale scopo;
- quali limiti e controlli umani sono stati adottati.

Un riepilogo breve è presente anche nel [README del repository](https://github.com/ValerioGiglio04/rpg-MPGC#-uso-di-strumenti-di-ai).

## Strumenti utilizzati

Sono stati utilizzati, in modo non esclusivo:

- **ChatGPT** — domande su MVC, Hibernate, SOLID; bozze diagrammi per la wiki;
- **Claude** — errori JPA, configurazione Gradle e Spotless; revisione testi wiki;
- **Gemini** — dubbi su JavaFX e FXML; alcune texture PNG in `src/main/resources/images/`;
- **GitHub Copilot** — autocompletamento metodi semplici, Javadoc, stringhe in `messages_it.properties`, correzioni Markdown wiki.

## Scopo dell'uso

L'AI è stata usata per accelerare attività che altrimenti avrebbero richiesto più tempo operativo, in particolare:

- produrre bozze iniziali di classi e metodi;
- suggerire organizzazioni architetturali (MVC, Repository, Strategy, Builder);
- proporre soluzioni per scene JavaFX, layout e navigazione;
- velocizzare la scrittura di testi e diagrammi per la documentazione;
- aiutare nella diagnosi di errori o incoerenze tra componenti.

## Ambiti di supporto effettivo

### Modellazione e backend

- definizione iniziale o revisione di classi del dominio;
- stesura o adattamento di servizi applicativi (`GameModel`, `BattleService`, …);
- supporto alla separazione tra logica di gioco, catalogo statico e persistenza sessione.

### Frontend

- supporto alla creazione e modifica di scene JavaFX e FXML;
- rifinitura di componenti mappa, hub e battaglia;
- adattamento messaggi UI e log di cronaca in italiano.

### Persistenza e build

- supporto alla configurazione Hibernate, H2 e mapper JSON;
- bozza plugin Spotless in `build.gradle` (formattazione Prettier via Spotless).

### Documentazione

- organizzazione dei contenuti su responsabilità, persistenza ed estendibilità;
- diagrammi Mermaid (flusso utente, persistenza, architettura) rivisti rispetto al codice effettivo;
- formalizzazione della presente dichiarazione.

## Modalità di utilizzo

L'AI non è stata usata come sostituto del controllo umano, ma come assistente operativo.

Il flusso adottato è stato il seguente:

1. definizione umana del requisito o del problema;
2. generazione di una proposta da parte dell'AI;
3. lettura critica della proposta;
4. modifica e integrazione manuale nel progetto;
5. verifica tramite compilazione (`./gradlew build`) e prova manuale delle schermate;
6. accettazione solo dopo revisione.

## Controlli umani adottati

Per mitigare errori o suggerimenti non coerenti, è stato mantenuto il controllo umano su:

- scelta dell'architettura finale e naming package `rpg125664`;
- responsabilità assegnate ai package e ai servizi;
- correttezza semantica del gameplay (combattimento, gloria, palestre);
- coerenza della UI con i requisiti funzionali;
- correzione di risposte AI non accurate o incomplete prima del commit.

## Limiti riconosciuti

Gli strumenti di AI generativa possono:

- produrre codice formalmente corretto ma concettualmente inadatto;
- introdurre duplicazioni o accoppiamenti non desiderati;
- proporre documentazione non allineata alla versione reale del codice;
- confondere il comportamento effettivo con una descrizione teorica.

Per questo nessun codice o testo dell'AI è stato considerato definitivo senza revisione.

## Responsabilità finale

La responsabilità finale delle scelte progettuali (RPG palestre, regole combattimento, persistenza catalogo H2 + JSON sessione), del codice integrato e della documentazione pubblicata resta umana.

## Sintesi conclusiva

- **Uso dichiarato dell'AI:** sì, come assistente tecnico e documentale;
- **Scopo principale:** accelerare implementazione, revisione e documentazione;
- **Controllo finale:** umano, con verifica di coerenza e build Gradle.

**Valerio Giglio — matricola 125664 — valerio.giglio@studenti.unicam.it**
