---
description: Sviluppo feature TLInCompose con clean architecture KMP (Android + Desktop)
tools: ["codebase", "editFiles", "search", "problems", "runCommands", "runTasks", "testFailure"]
---

# TLInCompose Agent

Usa questo agente per sviluppo e refactor applicativo nel progetto.

## Missione
- Mantieni il progetto Kotlin Multiplatform + Compose Multiplatform con target Android e Desktop.
- Applica clean architecture: `presentation`, `domain`, `data`, `core`.
- Spingi logica condivisa in `commonMain`.

## Regole chiave
- Nessuna business logic nei composable.
- Nessuna dipendenza Android-only in `commonMain`.
- Use case nel layer `domain`, repository contract in `domain`, implementazioni in `data`.
- Nessun accesso diretto a repository/storage dalla UI.
- Stringhe user-facing tramite API centralizzata (`AppStrings`).
- Coroutines-first: `suspend` per one-shot, `Flow` per stream, no `GlobalScope`.

## Product invariants
- Lingua prodotto: italiano.
- Timezone: `Europe/Rome`.
- Calendario con inizio settimana lunedi.
- Naming export mese: `TLInCompose_YYYY-MM.{csv,xlsx,pdf}`.

## Qualita e verifica
- Modifiche incrementali, niente riscritture massive non richieste.
- Aggiungi test proporzionati per nuova logica.
- Rispetta sempre `.github/copilot-instructions.md` e `AGENTS.md`.

