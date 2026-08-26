@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.tlincompose.presentation

import co.touchlab.kermit.Logger
import com.tlincompose.TestDispatcherProvider
import com.tlincompose.core.DateMath
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.ActivityWorkLocation
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.model.ExportFormat
import com.tlincompose.domain.model.MonthRange
import com.tlincompose.domain.model.PdfExportStyle
import com.tlincompose.domain.repository.TimesheetExporter
import com.tlincompose.domain.repository.TimesheetRepository
import com.tlincompose.domain.usecase.AddActivityToDayUseCase
import com.tlincompose.domain.usecase.BuildCalendarMonthGridUseCase
import com.tlincompose.domain.usecase.CalculateMonthWorkSummaryUseCase
import com.tlincompose.domain.usecase.CoverIncompleteMonthWithActivityUseCase
import com.tlincompose.domain.usecase.CreateDateRangeUseCase
import com.tlincompose.domain.usecase.CreateMonthRangeUseCase
import com.tlincompose.domain.usecase.ExportMonthRangeReportUseCase
import com.tlincompose.domain.usecase.ExportMonthReportUseCase
import com.tlincompose.domain.usecase.FilterExportEntriesUseCase
import com.tlincompose.domain.usecase.LoadMonthEntriesUseCase
import com.tlincompose.domain.usecase.SaveDailyEntryUseCase
import com.tlincompose.domain.usecase.SaveDateRangeEntriesUseCase
import com.tlincompose.domain.usecase.ValidateDailyEntryUseCase
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimesheetControllerTest {
    @Test
    fun dragSelectionOpensRangeEditorUsingAnchorDayActivities() = runTest {
        val month = CalendarMonth.current()
        val startDate = month.firstDate
        val endDate = DateMath.shiftDate(startDate, 2)
        val repository = FakeTimesheetRepository(
            entries = mutableMapOf(
                startDate to DailyEntry(
                    date = startDate,
                    activities = listOf(
                        Activity(
                            type = EntryType.PROJECT,
                            extCode = "EXT-0001",
                            title = "Apollo",
                            minutes = 480,
                        ),
                    ),
                ),
            ),
        )
        val controller = createController(repository)

        advanceUntilIdle()

        controller.startDayDragSelection(startDate)
        controller.updateDayDragSelection(endDate)
        controller.completeDayDragSelection()

        val editorState = assertNotNull(controller.editorState)
        assertTrue(editorState.target.isRange)
        assertEquals(startDate, editorState.target.sourceDate)
        assertEquals(startDate, editorState.target.startDate)
        assertEquals(endDate, editorState.target.endDate)
        assertEquals(3, editorState.target.affectedDayCount)
        assertEquals("Apollo", editorState.rows.single().title)
        assertNull(controller.dayDragSelectionState.anchorDate)
    }

    @Test
    fun saveEditorCopiesActivitiesAcrossSelectedRange() = runTest {
        val month = CalendarMonth.current()
        val startDate = month.firstDate
        val endDate = DateMath.shiftDate(startDate, 2)
        val activities = listOf(
            Activity(
                type = EntryType.PROJECT,
                extCode = "EXT-0001",
                title = "Apollo",
                minutes = 480,
            ),
        )
        val repository = FakeTimesheetRepository(
            entries = mutableMapOf(
                startDate to DailyEntry(
                    date = startDate,
                    activities = activities,
                ),
            ),
        )
        val controller = createController(repository)

        advanceUntilIdle()

        controller.startDayDragSelection(startDate)
        controller.updateDayDragSelection(endDate)
        controller.completeDayDragSelection()
        controller.saveEditor(
            language = AppLanguage.ITALIAN,
            onValidationError = { error("validation should not fail") },
            onPersistenceError = { error("persistence should not fail") },
        )

        advanceUntilIdle()

        assertNull(controller.editorState)
        assertEquals(activities, repository.entries[startDate]?.activities)
        assertEquals(activities, repository.entries[DateMath.shiftDate(startDate, 1)]?.activities)
        assertEquals(activities, repository.entries[endDate]?.activities)
        assertEquals(1, controller.monthCells.first { it.date == endDate }.activityCount)
    }

    @Test
    fun draggingVisibleActivityCopiesItToAnotherDay() = runTest {
        val month = CalendarMonth.current()
        val sourceDate = month.firstDate
        val targetDate = DateMath.shiftDate(sourceDate, 1)
        val activity = Activity(
            type = EntryType.PROJECT,
            extCode = "EXT-0001",
            title = "Apollo",
            minutes = 480,
        )
        val repository = FakeTimesheetRepository(
            entries = mutableMapOf(
                sourceDate to DailyEntry(
                    date = sourceDate,
                    activities = listOf(activity),
                ),
            ),
        )
        val controller = createController(repository)

        advanceUntilIdle()

        controller.startActivityDrag(sourceDate, activityIndex = 0)
        controller.updateActivityDragTarget(targetDate)
        controller.completeActivityDrag(
            onPersistenceError = { error("copy should not fail") },
        )

        advanceUntilIdle()

        assertEquals(listOf(activity), repository.entries[sourceDate]?.activities)
        assertEquals(listOf(activity), repository.entries[targetDate]?.activities)
        assertEquals(1, controller.monthCells.first { it.date == targetDate }.activityCount)
        assertNull(controller.activityDragState.sourceDate)
    }

    @Test
    fun droppingActivityOnSameDayDoesNotCreateDuplicates() = runTest {
        val month = CalendarMonth.current()
        val sourceDate = month.firstDate
        val activity = Activity(
            type = EntryType.PROJECT,
            extCode = "EXT-0001",
            title = "Apollo",
            minutes = 480,
        )
        val repository = FakeTimesheetRepository(
            entries = mutableMapOf(
                sourceDate to DailyEntry(
                    date = sourceDate,
                    activities = listOf(activity),
                ),
            ),
        )
        val controller = createController(repository)

        advanceUntilIdle()

        controller.startActivityDrag(sourceDate, activityIndex = 0)
        controller.completeActivityDrag(
            onPersistenceError = { error("copy should not fail") },
        )

        advanceUntilIdle()

        assertEquals(listOf(activity), repository.entries[sourceDate]?.activities)
    }

    @Test
    fun exportDocumentPassesAdditionalExportMetadata() = runTest {
        val repository = FakeTimesheetRepository()
        val exporter = RecordingTimesheetExporter()
        val controller = createController(
            repository = repository,
            monthExporter = exporter,
            monthRangeExporter = FakeTimesheetExporter(),
        )
        var exportedDocument: ExportDocument? = null
        var failed = false

        advanceUntilIdle()

        controller.exportDocument(
            language = AppLanguage.ITALIAN,
            format = ExportFormat.CSV,
            exportUserFullName = "Mario Rossi",
            exportOfficeName = "Sede Milano",
            exportEmployeeId = "EMP-123",
            exportPersonId = "P-456",
            onSuccess = { exportedDocument = it },
            onFailure = { failed = true },
        )

        advanceUntilIdle()

        assertFalse(failed)
        assertEquals("Mario Rossi", exporter.exportUserFullName)
        assertEquals("Sede Milano", exporter.exportOfficeName)
        assertEquals("EMP-123", exporter.exportEmployeeId)
        assertEquals("P-456", exporter.exportPersonId)
        assertEquals("month.csv", exportedDocument?.fileName)
    }

    @Test
    fun selectedWorkLocationIsSavedAndRestoredInDayEditor() = runTest {
        val month = CalendarMonth.current()
        val date = month.firstDate
        val repository = FakeTimesheetRepository()
        val controller = createController(repository)
        val definition = ActivityDefinition(
            extCode = "EXT-0001",
            type = EntryType.PROJECT,
            title = "Apollo",
            description = "Sprint planning",
            defaultMinutes = 480,
            createdDate = date,
            updatedDate = date,
        )

        advanceUntilIdle()

        controller.onDayTapped(date)
        controller.updateDraftSelection(index = 0, definition = definition)
        controller.updateDraftWorkLocation(index = 0, workLocation = ActivityWorkLocation.CLIENT_SITE)
        controller.saveEditor(
            language = AppLanguage.ITALIAN,
            onValidationError = { error("validation should not fail") },
            onPersistenceError = { error("persistence should not fail") },
        )

        advanceUntilIdle()

        assertEquals(
            ActivityWorkLocation.CLIENT_SITE,
            repository.entries.getValue(date).activities.single().workLocation,
        )

        controller.onDayTapped(date)

        assertEquals(
            ActivityWorkLocation.CLIENT_SITE,
            controller.editorState?.rows?.single()?.workLocation,
        )
    }

    @Test
    fun openingEmptyDayPrefillsSingleConfiguredDefinition() = runTest {
        val month = CalendarMonth(2026, 7)
        val date = month.firstDate
        val repository = FakeTimesheetRepository()
        val controller = createController(repository)
        val definition = ActivityDefinition(
            extCode = "EXT-UNICA",
            type = EntryType.PROJECT,
            title = "Commessa unica",
            description = "Attività predefinita",
            defaultMinutes = 480,
            createdDate = date,
            updatedDate = date,
        )

        controller.goToMonth(month)
        advanceUntilIdle()
        controller.updateAvailableDefinitions(listOf(definition))

        controller.onDayTapped(date)

        val row = assertNotNull(controller.editorState).rows.single()
        assertEquals("EXT-UNICA", row.extCode)
        assertEquals("Commessa unica", row.title)
        assertEquals("8", row.hoursText)
        assertEquals(EntryType.PROJECT, row.type)
    }

    @Test
    fun coverIncompleteMonthFillsOnlyRemainingWorkdayMinutes() = runTest {
        val month = CalendarMonth(2026, 7)
        val firstWorkday = month.firstDate
        val partialWorkday = LocalDate(2026, 7, 2)
        val completeWorkday = LocalDate(2026, 7, 3)
        val laterWorkday = LocalDate(2026, 7, 6)
        val repository = FakeTimesheetRepository(
            entries = mutableMapOf(
                partialWorkday to DailyEntry(
                    date = partialWorkday,
                    activities = listOf(
                        Activity(
                            type = EntryType.COURSE,
                            extCode = "TRN-01",
                            title = "Formazione",
                            minutes = 240,
                        ),
                    ),
                ),
                completeWorkday to DailyEntry(
                    date = completeWorkday,
                    activities = listOf(
                        Activity(
                            type = EntryType.PROJECT,
                            extCode = "KEEP",
                            title = "Già completo",
                            minutes = 480,
                        ),
                    ),
                ),
            ),
        )
        val controller = createController(repository)
        val definition = ActivityDefinition(
            extCode = "EXT-UNICA",
            type = EntryType.PROJECT,
            title = "Commessa unica",
            description = "Attività predefinita",
            defaultMinutes = 480,
            createdDate = firstWorkday,
            updatedDate = firstWorkday,
        )

        controller.goToMonth(month)
        advanceUntilIdle()
        controller.updateAvailableDefinitions(listOf(definition))
        controller.onDayTapped(firstWorkday)
        controller.coverIncompleteMonth(
            language = AppLanguage.ITALIAN,
            onValidationError = { error("validation should not fail") },
            onPersistenceError = { error("persistence should not fail") },
        )

        advanceUntilIdle()

        assertEquals(0, controller.monthSummary.remainingCompletionMinutes)
        assertEquals(480, repository.entries.getValue(firstWorkday).activities.single().minutes)
        assertEquals(2, repository.entries.getValue(partialWorkday).activities.size)
        assertEquals(240, repository.entries.getValue(partialWorkday).activities.last().minutes)
        assertEquals("EXT-UNICA", repository.entries.getValue(partialWorkday).activities.last().extCode)
        assertEquals("KEEP", repository.entries.getValue(completeWorkday).activities.single().extCode)
        assertEquals(480, repository.entries.getValue(completeWorkday).activities.single().minutes)
        assertEquals(480, repository.entries.getValue(laterWorkday).activities.single().minutes)
        assertNull(repository.entries[LocalDate(2026, 7, 4)])
        assertNull(controller.editorState)
    }

    private fun TestScope.createController(
        repository: FakeTimesheetRepository,
        monthExporter: TimesheetExporter = FakeTimesheetExporter(),
        monthRangeExporter: TimesheetExporter = FakeTimesheetExporter(),
    ): TimesheetController {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val filterExportEntries = FilterExportEntriesUseCase()
        return TimesheetController(
            loadMonthEntries = LoadMonthEntriesUseCase(repository),
            buildCalendarMonthGrid = BuildCalendarMonthGridUseCase(),
            calculateMonthWorkSummary = CalculateMonthWorkSummaryUseCase(),
            validateDailyEntry = ValidateDailyEntryUseCase(),
            addActivityToDay = AddActivityToDayUseCase(repository, SaveDailyEntryUseCase(repository)),
            coverIncompleteMonthWithActivity = CoverIncompleteMonthWithActivityUseCase(
                saveDailyEntry = SaveDailyEntryUseCase(repository),
            ),
            saveDateRangeEntries = SaveDateRangeEntriesUseCase(
                saveDailyEntry = SaveDailyEntryUseCase(repository),
            ),
            exportMonthReport = ExportMonthReportUseCase(monthExporter, filterExportEntries),
            exportMonthRangeReport = ExportMonthRangeReportUseCase(repository, monthRangeExporter, filterExportEntries),
            createDateRange = CreateDateRangeUseCase(),
            createMonthRange = CreateMonthRangeUseCase(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("TimesheetControllerTest"),
        )
    }

    private class FakeTimesheetRepository(
        val entries: MutableMap<LocalDate, DailyEntry> = mutableMapOf(),
    ) : TimesheetRepository {
        override suspend fun loadMonth(month: CalendarMonth): Map<LocalDate, DailyEntry> =
            entries.filterKeys(month::contains)

        override suspend fun loadRange(range: DateRange): Map<LocalDate, DailyEntry> =
            entries.filterKeys(range::contains)

        override suspend fun loadAll(): List<DailyEntry> = entries.values.toList()

        override suspend fun saveEntry(entry: DailyEntry) {
            entries[entry.date] = entry
        }

        override suspend fun deleteEntry(date: LocalDate) {
            entries.remove(date)
        }

        override suspend fun replaceAll(entries: List<DailyEntry>) {
            this.entries.clear()
            entries.forEach { entry ->
                this.entries[entry.date] = entry
            }
        }

        override suspend fun syncActivitiesWithDefinition(
            previousExtCode: String,
            definition: ActivityDefinition,
        ): Int = 0
    }

    private class FakeTimesheetExporter : TimesheetExporter {
        override suspend fun exportMonth(
            month: CalendarMonth,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            exportUserFullName: String?,
            exportOfficeName: String?,
            exportEmployeeId: String?,
            exportPersonId: String?,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument = ExportDocument("month.${format.extension}", format.mimeType, byteArrayOf())

        override suspend fun exportDateRange(
            range: DateRange,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            exportUserFullName: String?,
            exportOfficeName: String?,
            exportEmployeeId: String?,
            exportPersonId: String?,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument = ExportDocument("range.${format.extension}", format.mimeType, byteArrayOf())

        override suspend fun exportMonthRange(
            range: MonthRange,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            exportUserFullName: String?,
            exportOfficeName: String?,
            exportEmployeeId: String?,
            exportPersonId: String?,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument = ExportDocument("months.${format.extension}", format.mimeType, byteArrayOf())
    }

    private class RecordingTimesheetExporter : TimesheetExporter {
        var exportUserFullName: String? = null
        var exportOfficeName: String? = null
        var exportEmployeeId: String? = null
        var exportPersonId: String? = null

        override suspend fun exportMonth(
            month: CalendarMonth,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            exportUserFullName: String?,
            exportOfficeName: String?,
            exportEmployeeId: String?,
            exportPersonId: String?,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument {
            this.exportUserFullName = exportUserFullName
            this.exportOfficeName = exportOfficeName
            this.exportEmployeeId = exportEmployeeId
            this.exportPersonId = exportPersonId
            return ExportDocument("month.csv", format.mimeType, byteArrayOf())
        }

        override suspend fun exportDateRange(
            range: DateRange,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            exportUserFullName: String?,
            exportOfficeName: String?,
            exportEmployeeId: String?,
            exportPersonId: String?,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument = error("exportDateRange should not be called in this test")

        override suspend fun exportMonthRange(
            range: MonthRange,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            exportUserFullName: String?,
            exportOfficeName: String?,
            exportEmployeeId: String?,
            exportPersonId: String?,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument = error("exportMonthRange should not be called in this test")
    }
}
