package com.tlincompose.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.tlincompose.core.AppStrings
import com.tlincompose.core.AppTimeZone
import com.tlincompose.core.DateMath
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.core.LocalDateComparator
import com.tlincompose.core.appStrings
import com.tlincompose.core.displayLabel
import com.tlincompose.core.exportPeriodTitle
import com.tlincompose.core.exportSelectedMonthsDescription
import com.tlincompose.core.exportVisibleMonthDescription
import com.tlincompose.core.formatHours
import com.tlincompose.core.message
import com.tlincompose.core.selectFirstMonthInstruction
import com.tlincompose.core.selectLastMonthInstruction
import com.tlincompose.core.selectedMonthsReadyMessage
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.DefaultWorkdayMinutes
import com.tlincompose.domain.model.ExportActivityTypeFilter
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.model.ExportFormat
import com.tlincompose.domain.model.MonthRange
import com.tlincompose.domain.model.PdfExportStyle
import com.tlincompose.domain.usecase.AddActivityToDayUseCase
import com.tlincompose.domain.usecase.BuildCalendarMonthGridUseCase
import com.tlincompose.domain.usecase.CalculateMonthWorkSummaryUseCase
import com.tlincompose.domain.usecase.CreateDateRangeUseCase
import com.tlincompose.domain.usecase.CreateMonthRangeUseCase
import com.tlincompose.domain.usecase.ExportMonthRangeReportUseCase
import com.tlincompose.domain.usecase.ExportMonthReportUseCase
import com.tlincompose.domain.usecase.LoadMonthEntriesUseCase
import com.tlincompose.domain.usecase.SaveDateRangeEntriesUseCase
import com.tlincompose.domain.usecase.ValidateDailyEntryUseCase
import com.tlincompose.presentation.calendar.ActivityDraftUiState
import com.tlincompose.presentation.calendar.ActivityDragUiState
import com.tlincompose.presentation.calendar.DayDragSelectionUiState
import com.tlincompose.presentation.calendar.DayEditTargetUiState
import com.tlincompose.presentation.calendar.DayEditorUiState
import com.tlincompose.presentation.calendar.RangeSelectionUiState
import com.tlincompose.presentation.calendar.toDayEditorUiState
import com.tlincompose.presentation.calendar.toDomainInput
import com.tlincompose.presentation.calendar.toUiModels
import com.tlincompose.presentation.calendar.toUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TimesheetController(
    private val loadMonthEntries: LoadMonthEntriesUseCase,
    private val buildCalendarMonthGrid: BuildCalendarMonthGridUseCase,
    private val calculateMonthWorkSummary: CalculateMonthWorkSummaryUseCase,
    private val validateDailyEntry: ValidateDailyEntryUseCase,
    private val addActivityToDay: AddActivityToDayUseCase,
    private val saveDateRangeEntries: SaveDateRangeEntriesUseCase,
    private val exportMonthReport: ExportMonthReportUseCase,
    private val exportMonthRangeReport: ExportMonthRangeReportUseCase,
    private val createDateRange: CreateDateRangeUseCase,
    private val createMonthRange: CreateMonthRangeUseCase,
    dispatcherProvider: DispatcherProvider,
    logger: Logger,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
    private val today = Clock.System.todayIn(AppTimeZone)
    private val log = logger.withTag("TimesheetController")
    private var currentLanguage: AppLanguage = AppLanguage.ENGLISH
    private var standardWorkdayMinutes: Int = DefaultWorkdayMinutes
    private val currentStrings: AppStrings
        get() = appStrings(currentLanguage)

    var currentMonth by mutableStateOf(CalendarMonth.current())
        private set

    private var entriesByDate by mutableStateOf<Map<LocalDate, DailyEntry>>(emptyMap())

    var rangeSelectionState by mutableStateOf(RangeSelectionUiState())
        private set

    var dayDragSelectionState by mutableStateOf(DayDragSelectionUiState())
        private set

    var activityDragState by mutableStateOf(ActivityDragUiState())
        private set

    var monthCells by mutableStateOf(
        buildCalendarMonthGrid(currentMonth).toUiModels(
            entriesByDate = entriesByDate,
            today = today,
            language = currentLanguage,
        ),
    )
        private set

    var monthSummary by mutableStateOf(
        calculateMonthWorkSummary(currentMonth, entriesByDate.values, standardWorkdayMinutes).toUiState(),
    )
        private set

    var editorState by mutableStateOf<DayEditorUiState?>(null)
        private set

    var isExportDialogVisible by mutableStateOf(false)
        private set

    val intervalSelectionMessage: String?
        get() = when {
            !rangeSelectionState.isSelecting -> null
            rangeSelectionState.selectedRange != null -> {
                val range = rangeSelectionState.selectedRange ?: return null
                currentStrings.selectedMonthsReadyMessage(range.displayLabel(currentLanguage))
            }

            rangeSelectionState.startMonth == null -> currentStrings.selectFirstMonthInstruction
            else -> currentStrings.selectLastMonthInstruction
        }

    val isExportEnabled: Boolean
        get() = !rangeSelectionState.isSelecting || rangeSelectionState.selectedRange != null

    val exportDialogTitle: String
        get() = rangeSelectionState.selectedRange?.let {
            currentStrings.exportPeriodTitle(it.displayLabel(currentLanguage))
        } ?: currentStrings.exportPeriodTitle(currentMonth.displayLabel(currentLanguage))

    val exportDialogDescription: String
        get() = rangeSelectionState.selectedRange?.let {
            currentStrings.exportSelectedMonthsDescription
        } ?: currentStrings.exportVisibleMonthDescription

    init {
        log.i { "Inizializzazione controller sul mese ${currentMonth.fileStamp}." }
        reloadMonth()
    }

    fun dispose() {
        log.d { "Chiusura controller e cancellazione scope." }
        scope.cancel()
    }

    fun loadPreviousMonth() {
        currentMonth = currentMonth.plusMonths(-1)
        log.i { "Navigazione mese precedente: ${currentMonth.fileStamp}." }
        dayDragSelectionState = DayDragSelectionUiState()
        activityDragState = ActivityDragUiState()
        editorState = null
        entriesByDate = emptyMap()
        updateMonthCells()
        reloadMonth()
    }

    fun loadNextMonth() {
        currentMonth = currentMonth.plusMonths(1)
        log.i { "Navigazione mese successivo: ${currentMonth.fileStamp}." }
        dayDragSelectionState = DayDragSelectionUiState()
        activityDragState = ActivityDragUiState()
        editorState = null
        entriesByDate = emptyMap()
        updateMonthCells()
        reloadMonth()
    }

    fun goToMonth(month: CalendarMonth) {
        if (currentMonth == month) return

        currentMonth = month
        log.i { "Navigazione diretta al mese ${currentMonth.fileStamp}." }

        dayDragSelectionState = DayDragSelectionUiState()
        activityDragState = ActivityDragUiState()
        editorState = null
        entriesByDate = emptyMap()

        updateMonthCells()
        reloadMonth()
    }

    fun onDayTapped(date: LocalDate) {
        if (rangeSelectionState.isSelecting) {
            selectRangeDate(date)
        } else {
            openEditor(date)
        }
    }

    fun toggleRangeSelection() {
        if (rangeSelectionState.isSelecting) {
            cancelRangeSelection()
        } else {
            editorState = null
            dayDragSelectionState = DayDragSelectionUiState()
            activityDragState = ActivityDragUiState()
            rangeSelectionState = RangeSelectionUiState(isSelecting = true)
            updateMonthCells()
            log.i { "Modalita selezione intervallo attivata." }
        }
    }

    fun dismissEditor() {
        editorState = null
        updateMonthCells()
    }

    fun startDayDragSelection(date: LocalDate) {
        if (rangeSelectionState.isSelecting || activityDragState.isDragging) return
        val anchorDate = clampToCurrentMonth(date)
        editorState = null
        dayDragSelectionState = DayDragSelectionUiState(
            anchorDate = anchorDate,
            selectedRange = createDateRange(anchorDate, anchorDate),
        )
        updateMonthCells()
        log.d { "Selezione drag attivata dal giorno $anchorDate." }
    }

    fun updateDayDragSelection(date: LocalDate) {
        if (activityDragState.isDragging) return
        val anchorDate = dayDragSelectionState.anchorDate ?: return
        val selectedRange = createDateRange(anchorDate, clampToCurrentMonth(date))
        if (selectedRange == dayDragSelectionState.selectedRange) return
        dayDragSelectionState = dayDragSelectionState.copy(selectedRange = selectedRange)
        updateMonthCells()
    }

    fun completeDayDragSelection() {
        if (activityDragState.isDragging) return
        val anchorDate = dayDragSelectionState.anchorDate ?: return
        val selectedRange = dayDragSelectionState.selectedRange ?: return
        dayDragSelectionState = DayDragSelectionUiState()
        if (selectedRange.startDate == selectedRange.endDate) {
            openEditor(anchorDate)
        } else {
            openRangeEditor(anchorDate, selectedRange)
        }
        updateMonthCells()
    }

    fun cancelDayDragSelection() {
        if (!dayDragSelectionState.isDragging) return
        dayDragSelectionState = DayDragSelectionUiState()
        updateMonthCells()
        log.d { "Selezione drag annullata." }
    }

    fun startActivityDrag(date: LocalDate, activityIndex: Int) {
        if (rangeSelectionState.isSelecting) return
        val sourceDate = date.takeIf(currentMonth::contains) ?: return
        val sourceActivity = entriesByDate[sourceDate]?.activities?.getOrNull(activityIndex) ?: return
        editorState = null
        dayDragSelectionState = DayDragSelectionUiState()
        activityDragState = ActivityDragUiState(
            sourceDate = sourceDate,
            sourceActivityIndex = activityIndex,
            targetDate = sourceDate,
        )
        updateMonthCells()
        log.d { "Drag attività avviato dal giorno $sourceDate per ${sourceActivity.displayLabel}." }
    }

    fun updateActivityDragTarget(date: LocalDate) {
        val currentDrag = activityDragState
        if (!currentDrag.isDragging) return
        val targetDate = date.takeIf(currentMonth::contains) ?: return
        if (targetDate == currentDrag.targetDate) return
        activityDragState = currentDrag.copy(targetDate = targetDate)
        updateMonthCells()
    }

    fun completeActivityDrag(onPersistenceError: () -> Unit) {
        val currentDrag = activityDragState
        val sourceDate = currentDrag.sourceDate
        val activityIndex = currentDrag.sourceActivityIndex
        val targetDate = currentDrag.targetDate
        activityDragState = ActivityDragUiState()
        updateMonthCells()

        if (sourceDate == null || activityIndex == null || targetDate == null || sourceDate == targetDate) return

        val activity = entriesByDate[sourceDate]?.activities?.getOrNull(activityIndex) ?: return
        scope.launch {
            runCatching {
                addActivityToDay(targetDate, activity)
            }.onSuccess { updatedEntry ->
                log.i { "Attività copiata da $sourceDate a $targetDate." }
                val updatedEntries = entriesByDate.toMutableMap()
                updatedEntries[targetDate] = updatedEntry
                entriesByDate = updatedEntries.toSortedMap(LocalDateComparator)
                updateMonthCells()
            }.onFailure {
                log.e(it) { "Errore durante la copia attività da $sourceDate a $targetDate." }
                onPersistenceError()
            }
        }
    }

    fun cancelActivityDrag() {
        if (!activityDragState.isDragging) return
        activityDragState = ActivityDragUiState()
        updateMonthCells()
        log.d { "Drag attività annullato." }
    }

    fun updateLanguage(language: AppLanguage) {
        if (currentLanguage == language) return
        currentLanguage = language
        updateMonthCells()
    }

    fun updateStandardWorkdayMinutes(minutes: Int) {
        if (standardWorkdayMinutes == minutes) return
        standardWorkdayMinutes = minutes
        updateMonthCells()
    }

    fun refreshCurrentMonth() {
        log.d { "Ricarica richiesta per il mese ${currentMonth.fileStamp}." }
        reloadMonth()
    }

    fun addDraftRow() {
        updateEditorRows { rows -> rows + ActivityDraftUiState() }
    }

    fun removeDraftRow(index: Int) {
        updateEditorRows { rows -> rows.filterIndexed { itemIndex, _ -> itemIndex != index } }
    }

    fun updateDraftType(index: Int, type: com.tlincompose.domain.model.EntryType) {
        updateEditorRows { rows ->
            rows.toMutableList().apply {
                this[index] = this[index].copy(
                    type = type,
                    extCode = null,
                    title = "",
                    description = "",
                    projectUrl = "",
                    hoursText = "",
                )
            }
        }
    }

    fun updateDraftSelection(index: Int, definition: ActivityDefinition) {
        updateEditorRows { rows ->
            rows.toMutableList().apply {
                this[index] = this[index].copy(
                    type = definition.type,
                    extCode = definition.extCode,
                    title = definition.title,
                    description = definition.description,
                    projectUrl = definition.projectUrl.orEmpty(),
                    hoursText = formatHours(definition.defaultMinutes),
                )
            }
        }
    }

    fun updateDraftHours(index: Int, hoursText: String) {
        updateEditorRows { rows ->
            rows.toMutableList().apply {
                this[index] = this[index].copy(hoursText = hoursText)
            }
        }
    }

    fun saveEditor(
        language: AppLanguage,
        onValidationError: () -> Unit,
        onPersistenceError: () -> Unit,
    ) {
        val currentEditor = editorState ?: return
        currentLanguage = language
        val validation = validateDailyEntry(currentEditor.rows.map(ActivityDraftUiState::toDomainInput))
        if (validation.hasErrors) {
            log.w { "Validazione fallita per il target ${currentEditor.target.startDate} - ${currentEditor.target.endDate}." }
            editorState = currentEditor.copy(errors = validation.errors.map { it?.message(language) })
            onValidationError()
            return
        }

        scope.launch {
            runCatching {
                saveDateRangeEntries(currentEditor.target.selectedRange, validation.activities)
            }.onSuccess {
                log.i {
                    "Salvataggio completato per ${currentEditor.target.affectedDayCount} giorno/i " +
                        "(${currentEditor.target.startDate} - ${currentEditor.target.endDate})."
                }
                val updatedEntries = entriesByDate.toMutableMap()
                applyActivitiesToEntries(
                    entries = updatedEntries,
                    range = currentEditor.target.selectedRange,
                    activities = validation.activities,
                )
                entriesByDate = updatedEntries.toSortedMap(LocalDateComparator)
                editorState = null
                updateMonthCells()
            }.onFailure {
                log.e(it) {
                    "Errore durante il salvataggio del target " +
                        "${currentEditor.target.startDate} - ${currentEditor.target.endDate}."
                }
                onPersistenceError()
            }
        }
    }

    fun openExportDialog() {
        if (!isExportEnabled) return
        isExportDialogVisible = true
    }

    fun dismissExportDialog() {
        isExportDialogVisible = false
    }

    fun exportDocument(
        language: AppLanguage,
        format: ExportFormat,
        filter: ExportActivityTypeFilter = ExportActivityTypeFilter(),
        brandingLogoBase64: String? = null,
        pdfExportStyle: PdfExportStyle = PdfExportStyle.RETRO,
        onSuccess: (ExportDocument) -> Unit,
        onFailure: () -> Unit,
    ) {
        currentLanguage = language
        scope.launch {
            runCatching {
                rangeSelectionState.selectedRange?.let { range ->
                    exportMonthRangeReport(
                        range = range,
                        format = format,
                        language = language,
                        filter = filter,
                        brandingLogoBase64 = brandingLogoBase64,
                        pdfExportStyle = pdfExportStyle,
                    )
                } ?: exportMonthReport(
                    month = currentMonth,
                    entries = entriesByDate.values.sortedByDate(),
                    format = format,
                    language = language,
                    filter = filter,
                    brandingLogoBase64 = brandingLogoBase64,
                    pdfExportStyle = pdfExportStyle,
                )
            }.onSuccess {
                val exportLabel = rangeSelectionState.selectedRange?.let { "mesi ${it.startMonth.fileStamp} - ${it.endMonth.fileStamp}" }
                    ?: "mese ${currentMonth.fileStamp}"
                log.i { "Export ${format.name} pronto per $exportLabel." }
                onSuccess(it)
            }.onFailure {
                log.e(it) { "Errore durante l'export ${format.name}." }
                onFailure()
            }
        }
    }

    private fun openEditor(date: LocalDate) {
        log.d { "Apertura editor per il giorno $date." }
        editorState = entriesByDate[date].toDayEditorUiState(date)
    }

    private fun openRangeEditor(sourceDate: LocalDate, selectedRange: DateRange) {
        log.d { "Apertura editor drag per l'intervallo ${selectedRange.startDate} - ${selectedRange.endDate}." }
        editorState = entriesByDate[sourceDate].toDayEditorUiState(
            DayEditTargetUiState(
                sourceDate = sourceDate,
                startDate = selectedRange.startDate,
                endDate = selectedRange.endDate,
                includesWeekend = rangeIncludesWeekend(selectedRange),
            ),
        )
    }

    private fun cancelRangeSelection() {
        rangeSelectionState = RangeSelectionUiState()
        updateMonthCells()
        log.i { "Modalita selezione mesi annullata." }
    }

    private fun selectRangeDate(date: LocalDate) {
        val selectedMonth = CalendarMonth(date.year, date.month.ordinal + 1)
        val currentState = rangeSelectionState
        val nextState = when {
            currentState.startMonth == null -> {
                log.d { "Primo mese export selezionato: ${selectedMonth.fileStamp}." }
                currentState.copy(startMonth = selectedMonth, selectedRange = null)
            }

            currentState.selectedRange == null -> {
                val startMonth = currentState.startMonth ?: return
                val range = createMonthRange(startMonth, selectedMonth)
                log.d { "Intervallo mesi completato: ${range.startMonth.fileStamp} - ${range.endMonth.fileStamp}." }
                currentState.copy(
                    startMonth = range.startMonth,
                    selectedRange = range,
                )
            }

            else -> {
                log.d { "Nuova selezione mesi avviata dal mese ${selectedMonth.fileStamp}." }
                RangeSelectionUiState(
                    isSelecting = true,
                    startMonth = selectedMonth,
                )
            }
        }

        rangeSelectionState = nextState
        updateMonthCells()
    }

    private fun reloadMonth() {
        val requestedMonth = currentMonth
        scope.launch {
            val loadedEntries = runCatching {
                loadMonthEntries(requestedMonth)
            }.onFailure {
                log.e(it) { "Errore durante il caricamento del mese ${requestedMonth.fileStamp}." }
            }.getOrDefault(emptyMap())

            if (currentMonth != requestedMonth) return@launch

            entriesByDate = loadedEntries
            updateMonthCells()
            log.d { "UI aggiornata per il mese ${requestedMonth.fileStamp} con ${loadedEntries.size} giorni." }
        }
    }

    private fun updateMonthCells() {
        val editorRange = editorState?.target
            ?.takeIf(DayEditTargetUiState::isRange)
            ?.selectedRange
        val activeDragRange = dayDragSelectionState.selectedRange
        monthCells = buildCalendarMonthGrid(currentMonth).toUiModels(
            entriesByDate = entriesByDate,
            today = today,
            language = currentLanguage,
            selectedRange = editorRange ?: activeDragRange ?: rangeSelectionState.selectedRange?.toDateRange(),
            pendingRange = if (editorRange == null && activeDragRange == null) {
                rangeSelectionState.startMonth?.toDateRange()
            } else {
                null
            },
        ).map { cell ->
            cell.copy(
                isActivityDragSource = cell.date == activityDragState.sourceDate,
                isActivityDropTarget = cell.date == activityDragState.targetDate &&
                    activityDragState.sourceDate != null &&
                    activityDragState.targetDate != activityDragState.sourceDate,
            )
        }
        monthSummary = calculateMonthWorkSummary(
            currentMonth,
            entriesByDate.values,
            standardWorkdayMinutes,
        ).toUiState()
    }

    private fun updateEditorRows(transform: (List<ActivityDraftUiState>) -> List<ActivityDraftUiState>) {
        val currentEditor = editorState ?: return
        val updatedRows = transform(currentEditor.rows)
        editorState = currentEditor.copy(
            rows = updatedRows,
            errors = List(updatedRows.size) { null },
        )
    }

    private fun Collection<DailyEntry>.sortedByDate(): List<DailyEntry> =
        sortedWith(compareBy(LocalDateComparator) { it.date })

    private fun clampToCurrentMonth(date: LocalDate): LocalDate = when {
        LocalDateComparator.compare(date, currentMonth.firstDate) < 0 -> currentMonth.firstDate
        LocalDateComparator.compare(date, currentMonth.lastDate) > 0 -> currentMonth.lastDate
        else -> date
    }

    private fun rangeIncludesWeekend(range: DateRange): Boolean {
        var cursor = range.startDate
        while (LocalDateComparator.compare(cursor, range.endDate) <= 0) {
            if (DateMath.isWeekend(cursor)) return true
            cursor = DateMath.nextDate(cursor)
        }
        return false
    }

    private fun applyActivitiesToEntries(
        entries: MutableMap<LocalDate, DailyEntry>,
        range: DateRange,
        activities: List<com.tlincompose.domain.model.Activity>,
    ) {
        var cursor = range.startDate
        while (LocalDateComparator.compare(cursor, range.endDate) <= 0) {
            if (activities.isEmpty()) {
                entries.remove(cursor)
            } else {
                entries[cursor] = DailyEntry(cursor, activities)
            }
            cursor = DateMath.nextDate(cursor)
        }
    }

    private fun CalendarMonth.toDateRange(): DateRange = DateRange(firstDate, lastDate)

    private fun MonthRange.toDateRange(): DateRange = DateRange(startDate, endDate)
}
