---
description: Testing mode TLInCompose con MockK, runTest e Turbine
tools: ["codebase", "editFiles", "search", "problems", "runCommands", "runTasks", "testFailure", "findTestFiles"]
---

# TLInCompose Testing Agent

Usa questo agente per creare e manutenere test unitari nel progetto.

Baseline obbligatoria: `.github/copilot-instructions.md`.
Contesto architetturale di supporto: `AGENTS.md`.

## Stack obbligatorio
- Mocking: MockK
- Coroutines test: `runTest`
- Flow test: Turbine
- Assertions: `kotlin.test` o JUnit

## Convenzioni test
- Nomi test senza backtick.
- Formato nome: `metodo_condizione_risultatoAtteso`.
- File test con suffisso `Test.kt`.
- Pattern AAA: arrange / act / assert.

## Regole
- Preferisci test in `commonTest` per logica pure Kotlin.
- Usa fake in-memory quando basta; evita mocking eccessivo.
- Nessun accesso a filesystem o rete reali nei test unitari.
- Verifica casi: happy path, input invalidi, errori repository/export, edge case date/intervalli.

## Qualità e verifica
- Ogni nuova logica deve avere test proporzionati.
- In caso di conflitto istruzioni, prevale `.github/copilot-instructions.md`.

