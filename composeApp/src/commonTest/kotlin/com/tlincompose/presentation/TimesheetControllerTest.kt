@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.tlincompose.presentation

import co.touchlab.kermit.Logger
import com.tlincompose.TestDispatcherProvider
import com.tlincompose.core.DateMath
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
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
import com.tlincompose.domain.usecase.BuildCalendarMonthGridUseCase
import com.tlincompose.domain.usecase.CalculateMonthWorkSummaryUseCase
import com.tlincompose.domain.usecase.CreateDateRangeUseCase
import com.tlincompose.domain.usecase.CreateMonthRangeUseCase
import com.tlincompose.domain.usecase.AddActivityToDayUseCase
import com.tlincompose.domain.usecase.ExportMonthRangeReportUseCase
import com.tlincompose.domain.usecase.ExportMonthReportUseCase
import com.tlincompose.domain.usecase.FilterExportEntriesUseCase
import com.tlincompose.domain.usecase.LoadMonthEntriesUseCase
import com.tlincompose.domain.usecase.SaveDailyEntryUseCase
import com.tlincompose.domain.usecase.SaveDateRangeEntriesUseCase
import com.tlincompose.domain.usecase.ValidateDailyEntryUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate

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

    private fun TestScope.createController(
        repository: FakeTimesheetRepository,
    ): TimesheetController {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val filterExportEntries = FilterExportEntriesUseCase()
        return TimesheetController(
            loadMonthEntries = LoadMonthEntriesUseCase(repository),
            buildCalendarMonthGrid = BuildCalendarMonthGridUseCase(),
            calculateMonthWorkSummary = CalculateMonthWorkSummaryUseCase(),
            validateDailyEntry = ValidateDailyEntryUseCase(),
            addActivityToDay = AddActivityToDayUseCase(repository, SaveDailyEntryUseCase(repository)),
            saveDateRangeEntries = SaveDateRangeEntriesUseCase(
                saveDailyEntry = SaveDailyEntryUseCase(repository),
            ),
            exportMonthReport = ExportMonthReportUseCase(FakeTimesheetExporter(), filterExportEntries),
            exportMonthRangeReport = ExportMonthRangeReportUseCase(repository, FakeTimesheetExporter(), filterExportEntries),
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

        override suspend fun saveEntry(entry: DailyEntry) {
            entries[entry.date] = entry
        }

        override suspend fun deleteEntry(date: LocalDate) {
            entries.remove(date)
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
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument = ExportDocument("month.${format.extension}", format.mimeType, byteArrayOf())

        override suspend fun exportDateRange(
            range: DateRange,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument = ExportDocument("range.${format.extension}", format.mimeType, byteArrayOf())

        override suspend fun exportMonthRange(
            range: MonthRange,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument = ExportDocument("months.${format.extension}", format.mimeType, byteArrayOf())
    }
}
