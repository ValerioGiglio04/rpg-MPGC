# Wiki — RPG Palestre e Creature

← [Funzionalità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Funzionalita-implementate) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura) · [Classi](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Classi-e-interfacce) · [Persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza) · [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita) · [AI](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dichiarazione-AI)

## Il gioco

- RPG a palestre in stile Pokémon: combattimenti a turni tra creature
- Mappa overworld con palestre collegate; ogni boss ha una soglia di punti fama
- Obiettivo: completare tutte le palestre, accumulare gloria e ampliare il team

Il codice segue un'**architettura MVC** (model al centro, model.persistence per H2, UI JavaFX). Dettagli in [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura).

---

## Informazioni sul progetto

| Campo | Valore |
|:------|:-------|
| **Studente** | Valerio Giglio — matricola `125664` — `valerio.giglio@studenti.unicam.it` |
| **Corso** | Metodologie di Programmazione, Modellazione e Gestione della Conoscenza — AA 2025/26 |
| **Repository** | [rpg-MPGC](https://github.com/ValerioGiglio04/rpg-MPGC) |
| **Linguaggio** | Java 25 |
| **Build** | Gradle 8.14.4 (Wrapper) |
| **UI** | JavaFX 25.0.1 + FXML |
| **Persistenza** | Hibernate 6 + H2 in `~/.rpg-palestre-creature/save` |
| **Package** | `it.unicam.cs.mpgc.rpg125664` |

---

## Nota

Questa wiki sostituisce la relazione scritta del corso. Qui trovi:

- scelte progettuali e organizzazione del codice
- funzionalità implementate
- responsabilità delle classi principali
- persistenza ed estendibilità
- [dichiarazione uso AI](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dichiarazione-AI)

Per build ed esecuzione vedi il [README](https://github.com/ValerioGiglio04/rpg-MPGC) del repository.
