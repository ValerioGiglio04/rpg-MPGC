# Dichiarazione dettagliata di uso di strumenti di AI

> [← Indice Wiki](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Repository](https://github.com/ValerioGiglio04/rpg-MPGC)

Questa pagina espande la dichiarazione breve presente nel [README](https://github.com/ValerioGiglio04/rpg-MPGC#-uso-di-strumenti-di-ai) del repository, come richiesto dalla specifica del progetto AA 2025/26.

---

## Premessa

Qui descrivo **solo** i punti in cui ho usato ChatGPT, Claude, Gemini o Copilot: bozze, suggerimenti, pezzi di testo o codice che poi ho rivisto io. Il gioco, l'architettura e la maggior parte della Wiki sono stati scritti e testati da me; in questa pagina trovi cosa mi ha aiutato l'AI e in che modo.

---

## Strumenti utilizzati

| Strumento | Tipologia | Contributo dell'AI |
|-----------|-----------|-------------------|
| **ChatGPT** (OpenAI) | Chat | Spiegazioni (architettura MVC, Hibernate, SOLID), confronto tra alternative di design, bozze **Mermaid** per la Wiki |
| **Claude** (Anthropic) | Chat | Spiegazioni su architettura e JPA, suggerimento configurazione **Spotless** / `spotlessApply`, bozze **Mermaid** per la Wiki |
| **Gemini** (Google) | Chat + immagini | Risposte su JavaFX/FXML; generazione di texture e PNG in `src/main/resources/images/` |
| **GitHub Copilot** | IDE inline | Autocompletamento, bozze di getter/setter, Javadoc, stringhe in `messages_it.properties` e revisione dei Markdown della Wiki |

---

## Contributi dell'AI per area

### Architettura e organizzazione del codice

**Strumenti:** ChatGPT, Claude

- Spiegazione di concetti (Model-View-Controller, separazione catalogo / stato di partita).
- Suggerimenti sulla struttura delle cartelle (`model`, `model.service`, `model.persistence`, `ui`).
- Confronto tra alternative (es. grafo oggetti completo vs `catalogId` + HP nel salvataggio).

### Dominio, combattimento e validazione

**Strumenti:** ChatGPT, Claude, GitHub Copilot

- Spiegazione del pattern Builder e del framework `Validator` / `Validators`.
- Bozze di metodi ripetitivi (getter, costruttori).
- Spiegazione della sintassi delle sealed interface per `BattleEvent`.

### Persistenza (Hibernate + sessione JSON)

**Strumenti:** ChatGPT, Claude

- Spiegazione di errori JPA e mapping (`@ElementCollection`, tabelle catalogo).
- Suggerimenti su `hbm2ddl.auto`, `persistence.xml`, Jackson (`JavaTimeModule`).
- Discussione su seed idempotente e separazione catalogo H2 / sessione JSON.

### Interfaccia JavaFX

**Strumenti:** Gemini, ChatGPT, GitHub Copilot

- Risposte su binding FXML, layout, `ScreenNavigator`.
- Boilerplate di controller e proprietà JavaFX (Copilot).

### Asset grafici

**Strumento:** Gemini

- Generazione di texture e immagini (es. skin in `src/main/resources/images/`).

### Internazionalizzazione

**Strumento:** GitHub Copilot

- Bozze di voci in `messages_it.properties` e messaggi del log di battaglia.

### Formattazione del codice (Spotless)

**Strumento:** Claude (chat)

- Suggerimento del plugin Gradle **Spotless** con **Google Java Format** e comando `spotlessApply`.
- Bozza del blocco `spotless { ... }` in `build.gradle` (anche formattazione JSON del catalogo).

### Documentazione — Wiki e README

**Strumenti:** ChatGPT, Claude, GitHub Copilot

#### Testo e struttura Wiki (GitHub Copilot)

Suggerimenti e completamenti su `.github/wiki-pages/`, ad esempio:

| Intervento dell'AI | File / ambito |
|:-------------------|:--------------|
| Sidebar e footer di navigazione | `_Sidebar.md`, `_Footer.md` |
| Intestazioni con link tra pagine | Pagine Wiki principali |
| Sezioni della dichiarazione AI | `Dichiarazione-AI.md` |
| `curve: stepAfter` nei flowchart Mermaid | Pagine con diagrammi |
| Allineamento nomi classi nei diagrammi | Es. `HibernateGameStateRepository` nei flowchart |

#### Grafici Mermaid nella Wiki (ChatGPT e Claude)

**Alcuni diagrammi di questa Wiki** (flowchart e `erDiagram`) sono stati **creati con l’aiuto di ChatGPT e Claude**: ho descritto a parole il contenuto che volevo (layer, flussi, tabelle del catalogo), ho ricevuto bozze in sintassi Mermaid, le ho **corrette e adattate** al progetto (nomi classi reali, legenda, link tra pagine) e poi le ho incollate nelle pagine sotto.

| Strumento | Ruolo sui diagrammi Wiki |
|:----------|:-------------------------|
| **ChatGPT** | Bozze iniziali di flowchart (architettura a layer, flusso utente, catalogo/sessione) |
| **Claude** | Bozze e revisioni di `erDiagram` (schema catalogo H2, modello sessioni SQL futuro) e affinamento di alcuni flowchart |

Il testo intorno ai diagrammi, la struttura delle pagine e le scelte architetturali restano **mie**; l’AI ha accelerato soprattutto la **scrittura del codice Mermaid**, non la sostituzione del ragionamento sul design.

| Pagina Wiki | Tipo di diagramma | Strumento usato per la bozza Mermaid |
|:------------|:------------------|:-------------------------------------|
| [Responsabilità e architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura) | Flowchart dipendenze tra layer | ChatGPT / Claude |
| [Funzionalità implementate](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Funzionalita-implementate) | Flowchart flusso utente | ChatGPT |
| [Dati e persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza) | Flowchart catalogo/sessione; `erDiagram` catalogo H2 | ChatGPT / Claude |
| [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita) | Flowchart layer; login futuro; `erDiagram` sessioni SQL | Claude |

Nei flowchart compare spesso `curve: stepAfter` (frecce a **angolo retto**): scelta di resa grafica applicata dopo le bozze, in parte con suggerimenti di GitHub Copilot sulla sintassi `%%{init: ...}%%`.

---

## Esempi di output dell'AI

| Richiesta / contesto | Strumento | Output dell'AI |
|----------------------|-----------|----------------|
| Errore JPA su collection eager | ChatGPT / Claude | Spiegazione del messaggio d'errore |
| Logica di cura: service vs dominio | ChatGPT / Claude | Confronto tra alternative |
| Getter ripetitivi su entità catalogo | Copilot | Bozza metodi |
| Texture mappa overworld | Gemini | File PNG |
| Sintassi sealed interface Java 21 | ChatGPT / Claude | Spiegazione sintassi |
| Formattazione uniforme Java/Gradle | Claude | Spiegazione Spotless + bozza `build.gradle` |
| Diagramma dipendenze layer (Wiki) | ChatGPT / Claude | Blocco Mermaid flowchart |
| ER catalogo H2 (Wiki) | Claude | Blocco Mermaid `erDiagram` |
| Formattazione sidebar Wiki | GitHub Copilot | Bozze e correzioni su `.github/wiki-pages/*.md` |
| Linee ortogonali nei flowchart | GitHub Copilot | Aggiunta `%%{init: {'flowchart': {'curve': 'stepAfter'}}}%%` |

---

## Limiti e rischi (mitigazione generale)

| Rischio | Nota |
|---------|------|
| Output errato o non idomatico | Tutto quanto sopra è stato integrato nel progetto solo dopo controllo |
| Allucinazioni su API | Incrociato con documentazione ufficiale e build Gradle |
| Uso eccessivo dell'AI | Limitato ai contributi elencati in questa pagina |

---

## Impegno dello studente

Dichiaro che quanto **non** compare in questa dichiarazione è opera mia (comprensione del codice, scelte progettuali, implementazione e verifica del gioco). L'AI non ha generato l'intero repository in modo automatico.

**Valerio Giglio — matricola 125664**
