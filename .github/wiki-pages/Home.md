# GymQuest Wiki

Questa wiki descrive l'architettura, le funzionalità implementate e le scelte progettuali del progetto `rpg-MPGC` (package `it.unicam.cs.mpgc.rpg125664`).

Il progetto è una demo RPG desktop sviluppata in Java 25 e JavaFX. La struttura del codice separa:

- presentazione JavaFX e navigazione tra schermate;
- persistenza su H2 con Hibernate e snapshot di partita in JSON;
- regole di gioco e servizi applicativi nel model, indipendenti dalla UI.

<p align="center">
  <a href="https://raw.githubusercontent.com/ValerioGiglio04/rpg-MPGC/main/.github/wiki-pages/images/hub-overworld.png">
    <img src="https://raw.githubusercontent.com/ValerioGiglio04/rpg-MPGC/main/.github/wiki-pages/images/hub-overworld.png" alt="Hub di gioco con mappa e team" width="640">
  </a>
</p>

## Premessa

L'applicazione è progettata per favorire l'estensione funzionale grazie alla separazione tra dominio, servizi, persistenza e interfaccia.
Questa organizzazione rende possibile il riuso della logica applicativa in futuri client desktop, web o mobile.
Tuttavia, l'attuale livello di presentazione è implementato con JavaFX ed è quindi specifico per il contesto desktop; il supporto multi-dispositivo richiederebbe lo sviluppo di interfacce dedicate sopra i servizi già esistenti (in particolare `GameModel`).

## Contenuti

- [Funzionalità implementate](Funzionalita-implementate)
- [Responsabilità e architettura](Responsabilita-e-architettura)
- [Classi e interfacce](Classi-e-interfacce)
- [Dati e persistenza](Dati-e-persistenza)
- [Estendibilità](Estendibilita)
- [Dichiarazione uso AI](Dichiarazione-AI)

## Sintesi del progetto

La demo comprende:

- menù principale e schermata di caricamento slot;
- hub con mappa overworld a tile, zoom e gestione del team;
- combattimenti a turni tra creature con log di cronaca in italiano;
- progressione per gloria e palestre collegate;
- cura delle creature a pagamento in hub;
- schermata di vittoria al completamento della campagna;
- persistenza multi-slot su database H2 locale;
- catalogo statico (creature, mosse, palestre) caricato da seed JSON.

## Organizzazione della documentazione

La wiki è organizzata per responsabilità:

- la pagina funzionale descrive cosa può fare il gioco;
- la pagina architetturale descrive perché il codice è stato separato in quel modo;
- la pagina dei tipi elenca i componenti concreti della codebase;
- la pagina persistenza spiega struttura dati, file e regole di ripristino;
- la pagina estendibilità evidenzia i meccanismi predisposti per nuove funzionalità;
- la dichiarazione AI documenta uso e scopo degli strumenti di AI.

## Nota pratica

I sorgenti Markdown si trovano in `.github/wiki-pages/` nel repository principale e possono essere sincronizzati nella repository `rpg-MPGC.wiki` mantenendo gli stessi nomi file.
Istruzioni per build ed esecuzione: [README del repository](https://github.com/ValerioGiglio04/rpg-MPGC).
