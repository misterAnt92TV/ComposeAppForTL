# TLInCompose – Copilot Development & Testing Guidelines

Questo file rende le regole degli agenti (`.aider.json` e `.aider.testing.json`) utilizzabili anche con **GitHub Copilot**.

## Fonte autorevole per Copilot (Android Studio)

Per il plugin Copilot in Android Studio, questo file è la baseline di comportamento.

- Aggiorna prima questo file quando cambia una regola di sviluppo/testing.
- Mantieni `AGENTS.md`, `.aider.json` e `.aider.testing.json` allineati a quanto definito qui.
- I file in `.github/agents/` e `.github/chatmodes/` devono restare sintetici e rimandare a questa baseline, evitando duplicazioni estese.

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

# ✅ Uso operativo nel plugin Copilot (Android Studio)

- Profili disponibili nel repository:
  - `.github/agents/tlincompose-agent.md`
  - `.github/agents/tlincompose-testing-agent.md`
  - `.github/chatmodes/tlincompose.chatmode.md`
  - `.github/chatmodes/tlincompose-testing.chatmode.md`
- Se i profili non compaiono subito nel plugin, ricaricare la finestra IDE o riaprire la sessione Copilot.
- In caso di conflitto tra file istruzioni, questo file prevale per i comportamenti Copilot.

---

# ✅ Manutenzione istruzioni (checklist rapida)

Quando modifichi regole di progetto:

1. Aggiorna `.github/copilot-instructions.md` (fonte principale).
2. Sincronizza `AGENTS.md` se la regola è architetturale/prodotto.
3. Sincronizza `.aider.json` e/o `.aider.testing.json` se la regola impatta i rispettivi agenti.
4. Verifica che `.github/agents/*.md` e `.github/chatmodes/*.md` restino coerenti e sintetici.

---

Copilot deve generare codice conforme a queste regole.
