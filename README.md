# TLInCompose

`TLInCompose` e un'app Kotlin Multiplatform con Compose Multiplatform per la gestione di un timesheet mensile, con target Android e Desktop.

L'obiettivo del progetto e offrire un flusso rapido per inserire attivita giornaliere, controllare il mese corrente ed esportare report in formati condivisibili.

## Icone applicazione

| Icona | Anteprima |
| --- | --- |
| Icona principale | ![Icona principale TLInCompose](artwork/icons/tlincompose-app-icon.svg) |
| Foreground icon | ![Foreground icon TLInCompose](artwork/icons/tlincompose-app-icon-foreground.svg) |

Mini didascalie:
- `tlincompose-app-icon.svg`: icona completa usata per branding e distribuzione.
- `tlincompose-app-icon-foreground.svg`: livello foreground utile per pipeline adaptive icon.

## Feature principali

- Calendario mensile con navigazione mese precedente/successivo.
- Griglia mese sempre a 6 settimane (`42` celle), con inizio settimana al lunedi.
- Gestione multi-attivita per giorno.
- Validazioni input coerenti con i tipi di attivita.
- Export su mese visibile o intervallo personalizzato in `CSV`, `XLSX`, `PDF`.
- Raggruppamento automatico export per giorni consecutivi con stessa tripletta (`tipo`, `valore`, `minuti/giorno`).

## Regole funzionali

- Lingua applicativa: italiano.
- Timezone di riferimento: `Europe/Rome`.
- Festivita supportate: nazionali italiane fisse, `Pasqua` e `Pasquetta`.

Tipi di attivita:
- `PROJECT`: richiede descrizione libera obbligatoria.
- `VACATION`: supporta inserimento anche parziale in ore.
- `PERMIT`: supporta inserimento anche parziale in ore.

Regole di inserimento:
- Le righe vuote sono ignorate.
- Le ore devono essere maggiori di zero.
- Le ore sono convertite in minuti per il dominio applicativo.

## Export

Formati supportati:
- `CSV`
- `XLSX`
- `PDF`

Flusso intervallo:
1. Tap su `Seleziona intervallo`.
2. Selezione data iniziale.
3. Selezione data finale.
4. Scelta formato `CSV`, `XLSX` o `PDF`.

Naming file:
- `TLInCompose_YYYY-MM.csv`
- `TLInCompose_YYYY-MM.xlsx`
- `TLInCompose_YYYY-MM.pdf`
- `TLInCompose_YYYY-MM-DD_YYYY-MM-DD.csv`
- `TLInCompose_YYYY-MM-DD_YYYY-MM-DD.xlsx`
- `TLInCompose_YYYY-MM-DD_YYYY-MM-DD.pdf`

Colonne esportate:
- `Data inizio`
- `Data fine`
- `Tipo`
- `Valore`
- `Ore/giorno`
- `Giorni`
- `Ore totali`

## Stato verificato

Build completa eseguita con successo il `2026-05-15` tramite:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew build
```

Output principali:
- APK debug: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`
- APK release unsigned: `composeApp/build/outputs/apk/release/composeApp-release-unsigned.apk`
- JAR Desktop: `composeApp/build/libs/composeApp-desktop.jar`
- Report test aggregato: `composeApp/build/reports/tests/allTests/index.html`
- Report lint Android: `composeApp/build/reports/lint-results-debug.html`

## Stack tecnico

- Kotlin `2.2.0`
- Compose Multiplatform `1.10.3`
- Android Gradle Plugin `8.9.3`
- Kotlin Coroutines `1.10.2`
- kotlinx-datetime `0.7.1`
- kotlinx-serialization JSON `1.9.0`
- Kermit `2.1.0`
- Koin `4.1.1`
- Toolchain Java / JVM target `17`

Target supportati:
- Android (`applicationId`: `com.tlincompose`, `minSdk`: `24`, `targetSdk`: `36`, `compileSdk`: `36`)
- Desktop JVM (main class: `com.tlincompose.desktop.MainKt`, packaging configurato: `DMG`, `MSI`, `DEB`)

Persistenza attuale:
- Android: file interno app `tlincompose-timesheet.json`
- Desktop: `~/.tlincompose/tlincompose-timesheet.json`

## Architettura

Il progetto segue una clean architecture semplice e incrementale:

- `com.tlincompose.presentation`: composable, tema, state holder, dialog, wiring UI.
- `com.tlincompose.presentation.calendar`: `UiState` e mapper da domain a presentation.
- `com.tlincompose.domain.model`: modelli applicativi puri.
- `com.tlincompose.domain.repository`: contratti repository ed export.
- `com.tlincompose.domain.usecase`: use case focalizzati su calendario, validazione, caricamento, salvataggio, export.
- `com.tlincompose.data.local`: storage driver, DTO serializzabili, repository JSON.
- `com.tlincompose.data.mapper`: mapper espliciti tra entity e model di dominio.
- `com.tlincompose.data.export`: raggruppamento export, writer CSV/PDF e encoder XLSX platform-specific.
- `com.tlincompose.core`: date math, formatting, dispatcher condivisi.

Principi applicati:
- Logica business fuori dai composable.
- Controller di presentation usato come coordinatore leggero.
- Separazione esplicita tra model `data`, `domain`, `presentation`.
- Adapter Android/Desktop isolati nei source set di piattaforma.

## Test

Copertura attuale verificata in `commonTest`:
- use case di calendario, caricamento, salvataggio, validazione
- use case di creazione intervallo ed export per intervallo
- mapper entity locali -> model di dominio
- repository JSON con storage in-memory
- regressioni su naming e raggruppamento export

## GitHub Copilot custom agent

Per rendere visibili i profili dedicati nel tool GitHub Copilot, il repository include:

- `.github/copilot-instructions.md` (baseline/autorevole per comportamento Copilot)
- `.github/chatmodes/tlincompose.chatmode.md`
- `.github/chatmodes/tlincompose-testing.chatmode.md`
- `.github/agents/tlincompose-agent.md`
- `.github/agents/tlincompose-testing-agent.md`

Se i nuovi profili non compaiono subito, ricarica la finestra dell'IDE o riapri la sessione Copilot.

Regola pratica di manutenzione:
- aggiorna prima `.github/copilot-instructions.md`
- poi sincronizza `AGENTS.md` e `.aider*.json` quando necessario
- mantieni `agents/` e `chatmodes/` sintetici e coerenti con la baseline

## Setup locale

Prerequisiti:
- JDK `17`
- Android SDK installato
- Device Android collegato o emulatore avviato (solo per installazione/esecuzione Android)

Impostazione consigliata per cache Gradle locale progetto:

```bash
export GRADLE_USER_HOME=$(pwd)/.gradle-local
```

## Build e run

Build completa:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew build
```

Task utili:
- `:composeApp:build`
- `:composeApp:test`
- `:composeApp:allTests`
- `:composeApp:lint`

Run Desktop:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:run
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:desktopRun
```

Packaging Desktop (OS corrente):

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:packageDistributionForCurrentOS
```

Build e install Android:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:assembleDebug
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:installDebug
adb shell am start -n com.tlincompose/.MainActivity
```

Uninstall Android:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:uninstallDebug
```

Test strumentati Android:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:connectedDebugAndroidTest
```

## Roadmap breve

- Aggiungere nome utente all'avvio applicativo, da usare anche in export.
- Aggiungere il nome utente dentro i file esportati.
- Spostare visualizzazione attivita/export su tab dedicate.
- Spostare sezione impostazioni su tab dedicata.
- Aggiungere preferenze default per ore giornaliere, nome utente e timezone.
- Completare revisione stringhe con placeholder dinamici e coerenza ortografica.

## Licenza

Questo progetto e distribuito sotto licenza **Apache License 2.0**.

In sintesi:
- puoi usare, modificare e distribuire il software anche per scopi commerciali;
- devi includere una copia della licenza e mantenere gli avvisi di copyright/attribuzione;
- il software e fornito "AS IS", senza garanzie.

Testo completo della licenza: [`LICENSE`](LICENSE) oppure `http://www.apache.org/licenses/LICENSE-2.0`.
