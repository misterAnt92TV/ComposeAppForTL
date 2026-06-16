# TLInCompose

`TLInCompose` e un'app Kotlin Multiplatform con Compose Multiplatform per la gestione di un timesheet mensile, con target Android e Desktop.

## Stato verificato

Build completa eseguita con successo il `2026-05-15` tramite:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew build
```

Output principali generati:

- APK debug: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`
- APK release unsigned: `composeApp/build/outputs/apk/release/composeApp-release-unsigned.apk`
- JAR Desktop: `composeApp/build/libs/composeApp-desktop.jar`
- Report test aggregato: `composeApp/build/reports/tests/allTests/index.html`
- Report lint Android: `composeApp/build/reports/lint-results-debug.html`

## Specifiche funzionali

- Lingua applicativa: italiano.
- Timezone di riferimento: `Europe/Rome`.
- Schermata principale: calendario del mese corrente.
- Navigazione mese: precedente / successivo.
- Calendario: settimana con partenza da lunedi.
- Griglia mese: sempre 6 settimane (`42` celle), incluse le date del mese precedente/successivo necessarie al completamento della vista.
- Festivita supportate: nazionali italiane fisse piu `Pasqua` e `Pasquetta`.
- Ogni giorno puo contenere piu attività.

Tipi di attività supportati:

- `PROJECT`: usa una descrizione libera obbligatoria.
- `VACATION`: ferie, anche parziali in ore.
- `PERMIT`: permesso, anche parziale in ore.

Regole di inserimento:

- Le righe vuote vengono ignorate.
- Per `PROJECT` la descrizione e obbligatoria.
- Le ore devono essere maggiori di zero.
- Le ore inserite vengono convertite in minuti.

## Specifiche export

Formati supportati:

- `CSV`
- `XLSX`
- `PDF`

Regole export:

- L'export puo lavorare sul mese visibile oppure su un intervallo selezionato.
- Flusso intervallo:
  - tap su `Seleziona intervallo`
  - selezione data iniziale
  - selezione data finale
  - scelta formato `CSV`, `XLSX` o `PDF`
- Il nome file segue questo pattern:
  - `TLInCompose_YYYY-MM.csv`
  - `TLInCompose_YYYY-MM.xlsx`
  - `TLInCompose_YYYY-MM.pdf`
  - `TLInCompose_YYYY-MM-DD_YYYY-MM-DD.csv`
  - `TLInCompose_YYYY-MM-DD_YYYY-MM-DD.xlsx`
  - `TLInCompose_YYYY-MM-DD_YYYY-MM-DD.pdf`
- Le righe vengono raggruppate per giorni consecutivi con stessa tripletta:
  - tipo
  - valore esportato
  - minuti per giorno

Colonne esportate:

- `Data inizio`
- `Data fine`
- `Tipo`
- `Valore`
- `Ore/giorno`
- `Giorni`
- `Ore totali`

## Specifiche tecniche

Stack principale:

- Kotlin `2.2.0`
- Compose Multiplatform `1.10.3`
- Android Gradle Plugin `8.9.3`
- Kotlin Coroutines `1.10.2`
- kotlinx-datetime `0.7.1`
- kotlinx-serialization JSON `1.9.0`
- Kermit `2.1.0`
- Koin `4.1.1`
- Toolchain Java / JVM target: `17`

Target supportati:

- Android
  - `applicationId`: `com.tlincompose`
  - `compileSdk`: `36`
  - `minSdk`: `24`
  - `targetSdk`: `36`
- Desktop JVM
  - main class: `com.tlincompose.desktop.MainKt`
  - formati di packaging configurati: `DMG`, `MSI`, `DEB`

Persistenza attuale:

- Android: file interno app `tlincompose-timesheet.json`
- Desktop: `~/.tlincompose/tlincompose-timesheet.json`

## Architettura

Il progetto e stato rifattorizzato verso una clean architecture semplice e incrementale:

- `com.tlincompose.presentation`
  - composable, theme, state holder, dialog e wiring UI
- `com.tlincompose.presentation.calendar`
  - `UiState` e mapper da domain a presentation
- `com.tlincompose.domain.model`
  - modelli applicativi puri
- `com.tlincompose.domain.repository`
  - contratti di repository ed export
- `com.tlincompose.domain.usecase`
  - use case piccoli e focalizzati per calendario, validazione, caricamento, salvataggio ed export
- `com.tlincompose.data.local`
  - storage driver, DTO serializzabili e repository JSON
- `com.tlincompose.data.mapper`
  - mapper espliciti tra entity e model di dominio
- `com.tlincompose.data.export`
  - raggruppamento export, CSV, PDF e encoder XLSX platform-specific
- `com.tlincompose.core`
  - date math, formatting e dispatcher condivisi

Scelte progettuali:

- il controller di presentation coordina solo state e chiamate ai use case
- la validazione non e nei composable
- i model `data`, `domain` e `presentation` sono separati
- Android e Desktop contengono solo adapter platform-specific
- Koin gestisce il wiring di `core`, `data`, `domain` e `presentation`
- Kermit centralizza il logging applicativo in controller, repository ed export
- il refactor segue il principio `keep simple` ed evita over engineering

## Test presenti

Copertura attuale verificata in `commonTest`:

- use case di calendario, caricamento, salvataggio e validazione
- use case di creazione intervallo ed export per intervallo
- mapper tra entity locali e model di dominio
- repository JSON con storage in-memory
- regressioni su naming e raggruppamento export

## Prerequisiti locali

- JDK `17`
- Android SDK installato per la parte Android
- un device Android collegato oppure un emulatore avviato, se vuoi installare l'app

Comando consigliato per mantenere la cache Gradle nel progetto:

```bash
export GRADLE_USER_HOME=$(pwd)/.gradle-local
```

## Build del progetto

Dalla root del repository:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew build
```

Task utili:

- `:composeApp:build`
- `:composeApp:test`
- `:composeApp:allTests`
- `:composeApp:lint`

## Avvio Desktop

Avvio diretto in sviluppo:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:run
```

In alternativa:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:desktopRun
```

Packaging per il sistema operativo corrente:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:packageDistributionForCurrentOS
```

Altri task desktop disponibili:

- `:composeApp:createDistributable`
- `:composeApp:packageDmg`
- `:composeApp:packageMsi`
- `:composeApp:packageDeb`

## Avvio Android

Build APK debug:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:assembleDebug
```

APK generato:

```text
composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

Installazione su device o emulatore gia avviato:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:installDebug
```

Se vuoi avviare l'app da riga di comando dopo l'installazione:

```bash
adb shell am start -n com.tlincompose/.MainActivity
```

Per rimuoverla dal device:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:uninstallDebug
```

Per test strumentati, con device collegato:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:connectedDebugAndroidTest
```

## Note operative

- Il progetto compila sia Android sia Desktop nella stessa build Gradle.
- Il lint Android e parte della build.
- Il refactor corrente ha spostato la logica in layer `core`, `domain`, `data` e `presentation`, rimuovendo i file flat legacy sotto `com.tlincompose`.

#### TODO:
- Aggiungere nome ad avvio applicativo per utilizzare quando si esporta il file.
- Aggiungere il nome all'interno del file esportato.
- Spostare la visualizzazione attività/EXT su tab invece che su componente a scomparsa.
- Spostare la sezione settings su tab invece che su componente a scomparsa.
- Aggiungere su Impostazioni le preferenze di default per ore giornaliere, nome utente e timezone.
- Verificare le stringhe del progetto e aggiungere quelle mancanti, eventualmente con placeholder dinamici e lo stesso per i file esportati;
  rispetta gli accenti e le maiuscole, ad esempio `Giorni` e non `giorni` o `GIORNI` o l'accento in attività.