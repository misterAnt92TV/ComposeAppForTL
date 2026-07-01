# TLInCompose Agent Guide

## Mission
- Mantieni `TLInCompose` come progetto `Kotlin Multiplatform + Compose Multiplatform` con target Android e Desktop.
- Ogni modifica futura deve spingere il progetto verso una `Clean Architecture` chiara, testabile e coroutine-first.
- Favorisci codice condiviso in `commonMain`; usa `androidMain` e `desktopMain` solo per adapter e integrazioni di piattaforma.
- Keep simple: preferisci la soluzione più semplice che rispetta i vincoli architetturali.
- Evita over engineering: non introdurre layer, astrazioni o pattern se non portano un beneficio concreto al progetto.
- Gestire tutto per step: privilegia refactor incrementali, verificabili e facili da testare invece di riscritture massive.

## Architectural Direction
- Usa questi layer come riferimento obbligatorio per le nuove feature:
  - `presentation`: UI Compose, state holder, eventi, state mapping.
  - `domain`: model, repository contract, use case, regole di business.
  - `data`: datasource, repository implementation, DTO/entity, mapper, persistenza/export.
- Non aggiungere nuova business logic direttamente in `App.kt` o nei composable.
- I state holder principali sono focalizzati per dominio:
  - `TimesheetController`: coordinazione mese, calendario, export (attualmente 623 linee; valutare refactoring se cresce ulteriormente)
  - `AccessibilitySettingsController`: gestione preferenze accessibilità (tema, scale, PDF style, branding)
  - `ActivityCatalogController`: CRUD e validazione definizioni attività
  - Criterio: per ogni nuovo dominio di UI, crea un nuovo controller focalizzato invece di aggiungere logica a `TimesheetController`
- `domain` non deve dipendere da Compose, Android, Swing, file system, serialization o dettagli di persistenza.
- Le interfacce dei repository stanno in `domain`; le implementazioni concrete stanno in `data`.

## Package Layout
- Per il nuovo codice, preferisci questa struttura:
  - `com.tlincompose.presentation`
  - `com.tlincompose.presentation.calendar`
  - `com.tlincompose.presentation.export`
  - `com.tlincompose.domain.model`
  - `com.tlincompose.domain.repository`
  - `com.tlincompose.domain.usecase`
  - `com.tlincompose.data.local`
  - `com.tlincompose.data.export`
  - `com.tlincompose.data.mapper`
  - `com.tlincompose.core`
- Evita di aggiungere nuovi file flat direttamente sotto `com.tlincompose` se appartengono chiaramente a uno di questi layer.
- Se modifichi codice esistente flat, preferisci spostarlo gradualmente nella struttura sopra invece di duplicarlo.

## Domain Rules
- Separa sempre:
  - `data model` o `DTO/entity` per storage/export
  - `domain model` per la logica applicativa
  - `presentation model` o UI state per la schermata
- Non usare gli stessi model per tutti i layer salvo casi davvero banali e dichiarati.
- Ogni comportamento di business non banale deve vivere in un `use case`.
- I use case devono avere responsabilità singola e nome esplicito, per esempio:
  - `LoadMonthEntriesUseCase`
  - `SaveDailyEntryUseCase`
  - `ExportMonthReportUseCase`
  - `ValidateDailyEntryUseCase`
- Preferisci `operator fun invoke(...)` nei use case.
- Le regole di validazione non devono stare nei composable.

## Coroutines Rules
- Uniforma tutta la logica asincrona con `kotlinx.coroutines`.
- Ogni operazione di I/O o computazione non banale deve essere `suspend` o restituire `Flow`.
- Linee guida:
  - usa `suspend` per operazioni one-shot
  - usa `Flow` per stream osservabili o stato persistente
  - non esporre `MutableStateFlow` fuori dal layer owner
- Non usare chiamate bloccanti nel main thread.
- Introduci e usa un `DispatcherProvider` o equivalente quando una logica dipende dal dispatcher, così i test restano deterministici.
- Nei test coroutine usa `runTest`.
- Evita `GlobalScope`.

## Presentation Rules
- I composable devono:
  - leggere state
  - inviare eventi
  - delegare la logica ai use case/state holder
- Evita accesso diretto a repository, storage, export writer o serializer dalla UI.
- **State Holder Pattern**: ogni dominio UI ha il suo controller focalizzato
  - `TimesheetController`: stato calendario, navigazione mese, export
  - `AccessibilitySettingsController`: preferenze tema, scale, PDF styling, branding
  - `ActivityCatalogController`: catalogo e CRUD attività
- Mantieni gli state holder piccoli e focalizzati.
- Mantieni classi, composable e file UI compatti: non lasciare crescere classi troppo lunghe o file monolitici.
- Se una schermata o un componente supera una dimensione ragionevole o contiene più responsabilità, estrai nuove classi/file e usa directory dedicate come `components`, `model`, `state` o equivalenti invece di accumulare tutto nello stesso file.
- **Preview Pattern**: ogni nuovo composable deve includere preview in modalità chiara e scura usando `@PreviewLightDark` (in `androidMain/...presentation/preview/ComponentPreviews.kt`), con state/stub coerenti.
  - Esempio: `ActivityCatalogSection`, `DayEditorDialog`, `ExportDialog` hanno già preview light/dark funzionanti
- **Localization with StringKey**: le stringhe user-facing vanno centralizzate come `StringKey` sealed class in `AppStrings`:
  - Crea `data class KeyWithParams(val param: String)` per stringhe parametriche
  - Usa `AppStrings[StringKey.MyKey]` nei composable tramite `LocalAppStrings` CompositionLocal
  - Implementa traduzioni in `AppStringsCompat` o estensioni dedicate per lingua
  - Evita `when(language)` sparsi nei file feature
- **Informational Item Layout Rule**: per item UI informativi o descrittivi (per esempio librerie di terze parti, metadati, sorgenti, dettagli tecnici), privilegia card/list item full-width o colonne stabili rispetto a chip/flow compatti quando devono mostrare più di una informazione.
  - Nome, versione, URL e metadati principali devono restare sempre leggibili senza che un bottone o una label accessoria rubi la maggior parte dello spazio orizzontale.
  - Evita `fillMaxWidth()` su item figli dentro `FlowRow` o layout simili quando questo può alterare la misura del contenuto e comprimere il testo fino a renderlo illeggibile.
  - Se un item contiene più campi testuali, preferisci stacking verticale del testo e usa CTA secondarie solo se non compromettono la leggibilità delle informazioni principali.
- Se una schermata cresce, estrai:
  - `UiState` (con `data class` per immutabilità)
  - `UiEvent` o callback mirate
  - `UiAction` o callback specifiche
  - mapper da `domain` a `presentation`
- Aggiungi `testTag` ai componenti chiave quando una feature può beneficiare di UI test futuri (already in use: `previous-month-icon-button`, `next-month-icon-button`, `range-selection-button`, `export-button`, ecc.).

## Data Rules
- Tutto ciò che parla con file system, JSON, PDF, CSV, XLSX o API di piattaforma appartiene a `data`.
- Usa mapper espliciti tra `data` e `domain`.
- Le implementazioni concrete dei repository devono nascondere dettagli di storage.
- Le eccezioni di basso livello non devono propagare direttamente alla UI:
  - convertile in `Result`, sealed error type o stato esplicito
  - mantieni coerente la strategia scelta nella feature

## KMP Rules
- `commonMain` è la destinazione preferita per:
  - use case
  - domain model
  - validazione
  - mapping
  - repository contract
  - logica export che non dipende dalla piattaforma
- Usa `expect/actual` solo per confini realmente platform-specific, ad esempio:
  - file picker
  - storage path
  - API native
  - integrazione sistema operativo
- Non introdurre dipendenze Android-only in `commonMain`.

## Product Invariants
- L'app resta in lingua italiana.
- Timezone di riferimento: `Europe/Rome`.
- La schermata principale mostra il mese corrente con navigazione mese precedente/successivo.
- Il calendario parte da lunedì.
- Le festività supportate restano, salvo richieste esplicite diverse, quelle nazionali italiane.
- Un giorno può contenere più attività.
- I tipi attuali del timesheet sono:
  - `PROJECT`
  - `VACATION`
  - `PERMIT`
- `PROJECT` usa descrizione libera.
- `VACATION` e `PERMIT` possono essere parziali in ore.
- L'export standard può lavorare sul mese visibile oppure su un intervallo selezionato dall'utente.
- L'export raggruppa giorni consecutivi con stessa tripletta:
  - tipo
  - valore
  - minuti per giorno
- Mantieni coerenza di naming file export:
  - `TLInCompose_YYYY-MM.csv`
  - `TLInCompose_YYYY-MM.xlsx`
  - `TLInCompose_YYYY-MM.pdf`
  - `TLInCompose_YYYY-MM-DD_YYYY-MM-DD.csv`
  - `TLInCompose_YYYY-MM-DD_YYYY-MM-DD.xlsx`
  - `TLInCompose_YYYY-MM-DD_YYYY-MM-DD.pdf`
- Ogni nuova feature deve preservare il comportamento su Android e Desktop, salvo esplicita eccezione.

## Testing Rules
- Ogni nuova feature deve arrivare con test proporzionati.
- Minimo richiesto:
  - test unitari dei use case
  - test della validazione
  - test dei mapper
  - test dei repository con fake o storage in-memory quando possibile
- Per logica pure Kotlin, usa `commonTest` (preferita per massima compatibilità).
- Sposta i test platform-specific in `androidUnitTest` o `desktopTest` solo se necessario.
- **Test pattern attuale**:
  - `runTest` per coroutine (suspending operations)
  - `TestDispatcherProvider` con singolo dispatcher per determinismo
  - Mock repository con `mockk` e lambda stubbing
  - `runCatching` per error path testing
- Casi da coprire quando applicabili:
  - happy path
  - input invalidi
  - errori del repository o export
  - edge case di date, intervalli, festività, ore parziali
  - regressioni sui raggruppamenti dell'export
- Test esistenti di riferimento:
  - `AccessibilitySettingsControllerTest`: preferenze load/save con mock repository
  - Use case di calendario, caricamento, salvataggio, validazione in `commonTest`

## Migration Guidance For Current Code
- Stato attuale del progetto:
  - la struttura principale è ora organizzata per layer `core`, `domain`, `data` e `presentation`
  - **Tre controller di presentazione**: `TimesheetController` (calendario/export), `AccessibilitySettingsController` (preferenze), `ActivityCatalogController` (attività)
  - repository, export, validazione e mapper sono già separati per responsabilità
  - `core/AppStrings.kt` usa pattern `StringKey` sealed class con `AppLanguage` per localizzazione
  - preview `@PreviewLightDark` ben implementati in `presentation/preview/ComponentPreviews.kt`
  - `testTag` usati nei composable chiave per future UI testing
- Per ogni modifica futura, segui questa priorità:
  1. se è una nuova feature UI con dominio proprio, crea un nuovo controller focalizzato anziché espandere TimesheetController
  2. non aggiungere nuova logica business nei composable
  3. non aggiungere nuova logica business in controller se può vivere in un use case
  4. mantieni separati i model di `data`, `domain` e `presentation`
  5. aggiungi test mentre sposti la logica
  6. includi preview light/dark per ogni nuovo composable
  7. centralizza stringhe user-facing in `StringKey`

## Definition Of Done
- Una modifica è completa solo se:
  - rispetta i layer della clean architecture
  - usa coroutine in modo coerente
  - non introduce logica business nella UI
  - aggiunge preview light/dark per ogni nuovo componente Compose introdotto
  - non lascia stringhe user-facing cablate fuori dal layer di localizzazione
  - include test adeguati
  - mantiene compatibilità Android + Desktop
  - documenta eventuali tradeoff temporanei nel codice o nella PR description

## Preferred Defaults
- Constructor injection per dipendenze.
- Use case piccoli e composti, non classi “manager” generiche.
- `Result` o sealed state per error handling leggibile.
- Storage e export dietro interfacce.
- Naming esplicito e orientato al dominio.
- Keep simple come default operativo.
- Gestire tutto per step durante analisi, refactor e migrazioni.
- Refactor incrementali, non riscritture massive non richieste.

## Anti-Patterns To Avoid
- Business logic nei composable.
- Repository chiamati direttamente dalla UI.
- Model condivisi indiscriminatamente tra `data`, `domain` e `presentation`.
- Utility file enormi con funzioni eterogenee.
- `suspend` mancante su operazioni di I/O.
- Test mancanti per use case o validazione.
- Dipendenze Android/JVM introdotte in `commonMain` senza bisogno reale.
- Over engineering: astrazioni premature, interfacce inutili o moltiplicazione di use case senza una responsabilità chiara.
