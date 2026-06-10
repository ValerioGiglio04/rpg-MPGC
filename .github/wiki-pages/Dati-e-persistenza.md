# Dati e persistenza

← [Home](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Home) · [Architettura](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Responsabilita-e-architettura)

Ho separato **catalogo statico** e **stato di partita**: il primo resta in tabelle H2, il secondo in JSON per slot. Stesso file database, due ruoli distinti.

---

## Catalogo vs sessione

Il **catalogo** (creature, mosse, palestre, boss) viene caricato da `catalog-seed.json` all'avvio, scritto su H2 se serve, e tenuto in RAM come `GameCatalog`. Durante il gioco è solo lettura.

Lo **stato di partita** (gloria, HP del team, palestre completate, posizione mappa) vive in memoria come `GameState` e si salva su richiesta dell'utente in `sessioni_salvate`, colonna `dati_salvati_json`.

---

## Dove sta il database

- File H2: `~/.rpg-palestre-creature/save` (non nel repository)
- Config: `src/main/resources/META-INF/persistence.xml` (unit `rpg-palestre-creature`, Hibernate 6, `hbm2ddl.auto=update`)
- Ispezione: `./gradlew h2Console` (user `sa`, password vuota)

---

## Tabelle catalogo

- `giocatore` — giocatore umano e record boss (`is_boss`, skin)
- `creatura` — statistiche base; `id_giocatore` lega le creature del boss
- `mosse` — mosse per creatura
- `palestra` — nome, ordine, soglia punti, riferimento al boss

I collegamenti tra palestre non sono in H2: al load `PalestraCollegamentiSupport` costruisce una catena lineare da `ordine` (gym-1 → gym-2 → …).

---

## Salvataggi (`sessioni_salvate`)

Ogni riga è uno slot di partita. Colonne principali:

- `id_sessione`, `nome` — identificativo e etichetta in UI
- `data_salvataggio`, `data_creazione`, `ultima_giocata`
- `dati_salvati_json` — snapshot partita
- `format_version` — per migrazioni future del JSON
- `id_utente` — riservato a login futuro (`NULL` = salvataggi locali)

---

## Cosa c'è nel JSON

Il documento radice è `UltimaSessioneSalvataDto`. Salvo:

- gloria (`num_punti_fama`)
- `id_creatura_attiva_selezionata`, `id_palestra_corrente`
- team giocatore: lista `{ id_creatura, hp }`
- progresso palestre: `{ id_palestra, completata }`
- posizione mappa: `{ x, y }`

Non salvo nomi, mosse o statistiche base: al load `SessioneJsonMapper` le rilegge dal catalogo H2 tramite gli `id` numerici.

---

## Perché JSON nel CLOB

- Il payload resta piccolo (solo progresso e coordinate)
- Aggiornare il seed non invalida i save finché gli `id` restano stabili
- Posso cambiare backend sessione (cloud, altro DB) senza toccare le tabelle catalogo — basta una nuova impl di `GameStateRepository`

Dettagli su come estendere persistenza e multi-slot: [Estendibilità](https://github.com/ValerioGiglio04/rpg-MPGC/wiki/Estendibilita).
