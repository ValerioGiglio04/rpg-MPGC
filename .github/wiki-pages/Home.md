# Wiki — GymQuest

**Valerio Giglio** — matricola `125664` — Metodologie di Programmazione, Modellazione e Gestione della Conoscenza (AA 2025/26)

← [Funzionalità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Funzionalita-implementate) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura) · [Classi](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Classi-e-interfacce) · [Persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza) · [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita) · [AI](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dichiarazione-AI)

---

## Cos'è GymQuest

Ho scelto di fare un RPG a palestre in stile Pokémon: si esplora una mappa, si sfidano i boss delle palestre con combattimenti a turni tra creature e si accumula gloria per sbloccare le palestre successive. L'obiettivo di campagna è completarle tutte.

L'applicazione ha **interfaccia grafica** (JavaFX + FXML) e **persistenza** dei salvataggi su file H2 locale. Il codice sta nel package `it.unicam.cs.mpgc.rpg125664`, come richiesto dalla specifica.

---

## Cosa trovi in questa wiki

La specifica del corso chiede di documentare il progetto al posto di una relazione scritta. Ho organizzato la wiki così:

| Requisito specifica | Pagina wiki |
|---------------------|-------------|
| Funzionalità implementate | [Funzionalità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Funzionalita-implementate) |
| Responsabilità delle classi | [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura) |
| Classi e interfacce (con responsabilità) | [Classi e interfacce](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Classi-e-interfacce) |
| Dati e persistenza | [Persistenza](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dati-e-persistenza) |
| Integrazione di nuove funzionalità / altri dispositivi | [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita) |
| Dichiarazione uso AI | [Dichiarazione AI](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Dichiarazione-AI) |

Per il codice vero e proprio il riferimento resta il repository; qui spiego le scelte che ho fatto.

---

## Avvio rapido

Prerequisiti: **Java 25** (Gradle arriva col Wrapper).

```bash
git clone https://github.com/ValerioGiglio04/rpg-MPGC.git
cd rpg-MPGC
./gradlew build
./gradlew run
```

Su Windows: `.\gradlew.bat build` e `.\gradlew.bat run`. Nel [README](https://github.com/ValerioGiglio04/rpg-MPGC) c'è la stessa cosa più la dichiarazione sintetica sull'uso di AI.

---

## Stack tecnico

| | |
|:--|:--|
| Linguaggio | Java 25 |
| Build | Gradle 8.14.4 (Wrapper) |
| UI | JavaFX 25.0.1 + FXML |
| Persistenza | Hibernate 6 + H2 (`~/.rpg-palestre-creature/save`) |
| Formattazione | Spotless + Prettier (opzionale in sviluppo) |
| Repository | [github.com/ValerioGiglio04/rpg-MPGC](https://github.com/ValerioGiglio04/rpg-MPGC) |

Ho usato **MVC**: il `model` non dipende da JavaFX, così in futuro potrei cambiare solo la parte grafica senza riscrivere le regole di gioco (lo approfondisco in [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita)).
