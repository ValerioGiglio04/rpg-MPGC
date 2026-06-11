# Wiki — GymQuest

← [Funzionalità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Funzionalita-implementate) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura) · [Classi](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Classi-e-interfacce) · [Persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza) · [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita) · [AI](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dichiarazione-AI)

## Il gioco

RPG a palestre in stile Pokémon: il giocatore esplora una mappa overworld, sfida i boss delle palestre con combattimenti a turni tra creature e accumula punti fama (gloria) per sbloccare le palestre successive.

- Ogni palestra ha un **boss** con un team di creature e una **soglia minima di gloria** per essere sfidata
- Completando una palestra si ottengono ricompense e, spesso, le creature del boss nel proprio team
- L'obiettivo della campagna è **completare tutte le palestre**

Il codice segue il pattern **MVC**: `model` (regole + persistenza), `view` (FXML e componenti JavaFX), `controller` (schermate e navigazione). Dettagli in [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura).

---

## Indice

| Pagina | Contenuto |
|--------|-----------|
| [Funzionalità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Funzionalita-implementate) | Schermate, hub, battaglia, salvataggi |
| [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura) | Layer, componenti, pattern |
| [Classi e interfacce](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Classi-e-interfacce) | Classi principali per layer |
| [Persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza) | Catalogo H2, sessioni JSON, save multipli |
| [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita) | Nuove feature, altri dispositivi |
| [Uso dell'AI](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dichiarazione-AI) | Dichiarazione strumenti AI |

---

## Avvio rapido

Prerequisiti: Java 25, Gradle via Wrapper.

```bash
git clone https://github.com/ValerioGiglio04/rpg-MPGC.git
cd rpg-MPGC
./gradlew build
./gradlew run
```

Su Windows: `.\gradlew.bat build` e `.\gradlew.bat run`. Dettagli nel [README](https://github.com/ValerioGiglio04/rpg-MPGC).

---

## Informazioni sul progetto

| Campo | Valore |
|:------|:-------|
| **Studente** | Valerio Giglio — matricola `125664` — `valerio.giglio@studenti.unicam.it` |
| **Corso** | Metodologie di Programmazione, Modellazione e Gestione della Conoscenza — AA 2025/26 |
| **Repository** | [rpg-MPGC](https://github.com/ValerioGiglio04/rpg-MPGC) |
| **Linguaggio** | Java 25 (toolchain in `build.gradle`) |
| **Build** | Gradle 8.14.4 (Wrapper) |
| **UI** | JavaFX 25.0.1 + FXML |
| **Qualità codice** | Spotless 7.0.4 + google-java-format 1.28.0 |
| **Persistenza** | Hibernate 6 + H2 in `~/.rpg-palestre-creature/save`; snapshot partita in `sessioni_salvate.dati_salvati_json` |
| **Package** | `it.unicam.cs.mpgc.rpg125664` |

---

## Nota

Questa wiki sostituisce la relazione scritta del corso. Qui trovi scelte progettuali, funzionalità, classi principali, persistenza, estendibilità e [dichiarazione uso AI](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dichiarazione-AI). Per il dettaglio implementativo il riferimento resta il codice sorgente.
