# TLInCompose – Copilot Development & Testing Guidelines

Questo file rende le regole degli agenti (`.aider.json` e `.aider.testing.json`) utilizzabili anche con **GitHub Copilot**.

Copilot deve rispettare queste linee guida quando genera codice o test nel progetto.

---

**[OPTIMIZZAZIONE]**  
Tutti gli strumenti AI/CI (compreso Copilot) devono ignorare i file, le directory e i pattern elencati in `.gitignore` del repository (build, output binari, generati, artefatti temporanei, file personali/locali, ecc.)  
Non proporre né modificare codice in questi file per evitare errori, rallentamenti ed esecuzioni inutili.

Esempi di pattern da escludere:
- `.git/`, `build/`, `composeApp/build/`, `composeApp/bin/`, `*.iml`, `.gradle/`, `.idea/`, `*.class`, `*.jar`, `*.apk`, `*.log`, `.env`, `node_modules/`, `artwork/icons/generated/`, `**/.DS_Store`, ecc.

---

# ✅ Architettura (Clean Architecture KMP)

## Layer obbligatori

- `presentation` → UI Compose + state holder + mapping domain→UI  
- `domain` → model + repository contract + use case + business rules  
- `data` → datasource + repository implementation + DTO/entity + mapper  
- `core` → utilità condivise (dispatchers, time, strings)

## Regole fondamentali

- Nessuna business logic nei composable.
- Nessuna dipendenza Android-only in `commonMain`.
- Preferire `commonMain` per use case, model e logica condivisa.
- Repository definiti in `domain`, implementazioni in `data`.
- Constructor injection come default.

---

# ✅ Coroutines

- Usare `suspend` per operazioni one-shot.
- Usare `Flow` per stream osservabili.
- Nessun `GlobalScope`.
- Non bloccare il main thread.
- Se necessario, usare `DispatcherProvider`.

---

# ✅ UI Compose

- I composable leggono stato e inviano eventi.
- Nessun accesso diretto a repository o storage dalla UI.
- Ogni nuovo componente deve avere preview light/dark.
- Le stringhe devono usare `AppStrings`.

---

# ✅ Product Invariants

- Lingua principale: Italiano.
- Timezone: Europe/Rome.
- Calendario parte da lunedì.
- Target: Android + Desktop.
- Export naming coerente:
  - `TLInCompose_YYYY-MM.csv`
  - `TLInCompose_YYYY-MM.xlsx`
  - `TLInCompose_YYYY-MM.pdf`

---

# ✅ Unit Test Rules (Copilot Testing Mode)

## Stack obbligatorio

- Mocking: **MockK**
- Coroutines test: `runTest`
- Flow test: **Turbine**
- Assertions: `kotlin.test` o JUnit

## Naming Test (OBBLIGATORIO)

❌ Vietati backtick nei nomi test  
✅ Formato:

```
fun saveDailyEntry_whenInputValid_savesSuccessfully()
fun exportMonthReport_whenRepositoryFails_emitsErrorState()
```

## Regole Test

- Arrange / Act / Assert
- MockK per repository/datasource
- Turbine per Flow/StateFlow
- Nessun accesso a filesystem reale
- Test piccoli e focalizzati
- File devono finire con `Test.kt`

---

# ✅ Anti-Pattern da evitare

- Business logic nei composable
- Repository chiamati direttamente dalla UI
- Model condivisi tra layer
- Test con backtick
- Test con delay reali invece di runTest
- Mock eccessivi quando basta un fake

---

# ✅ Definition of Done

Una feature è completa solo se:

- Rispetta i layer
- Usa coroutine correttamente
- Non hardcodifica stringhe
- Include test adeguati
- È compatibile Android + Desktop

---

Copilot deve generare codice conforme a queste regole.
