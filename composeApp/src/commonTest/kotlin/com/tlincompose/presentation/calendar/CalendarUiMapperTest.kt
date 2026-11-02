package com.tlincompose.presentation.calendar

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarDay
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.EntryType
import com.tlincompose.core.appStrings
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalendarUiMapperTest {
    @Test
    fun dayCellContentDescriptionIncludesActivitySummaries() {
        val cell = MonthCellUiModel(
            date = LocalDate(2026, 6, 15),
            inCurrentMonth = true,
            isWeekend = false,
            isToday = false,
            holidayLabel = null,
            activityItems = listOf(
                CalendarActivityUiModel(
                    activityIndex = 0,
                    entryType = EntryType.PROJECT,
                    summary = "Analisi, 4h",
                ),
                CalendarActivityUiModel(
                    activityIndex = 1,
                    entryType = EntryType.VACATION,
                    summary = "Ferie, 2h",
                ),
            ),
            activityCount = 2,
            totalMinutes = 360,
            dailyLimitMinutes = 480,
            isOverDailyLimit = false,
            isRangeStart = false,
            isRangeEnd = false,
            isInSelectedRange = false,
            isActivityDragSource = false,
            isActivityDropTarget = false,
        )

        val description = buildDayCellContentDescription(
            cell = cell,
            strings = appStrings(AppLanguage.ITALIAN),
        )

        assertTrue("Analisi, 4h" in description)
        assertTrue("Ferie, 2h" in description)
    }

    @Test
    fun activityTypesAreKeptInCalendarItemsForDefaultIcons() {
        val date = LocalDate(2026, 6, 15)
        val grid = listOf(
            CalendarDay(
                date = date,
                inCurrentMonth = true,
                isWeekend = false,
                holiday = null,
            ),
        )
        val entriesByDate = mapOf(
            date to DailyEntry(
                date = date,
                activities = listOf(
                    Activity(
                        type = EntryType.VACATION,
                        title = "",
                        minutes = 240,
                    ),
                    Activity(
                        type = EntryType.PERMIT,
                        extCode = "PAR",
                        title = "",
                        minutes = 120,
                    ),
                ),
            ),
        )

        val uiModels = grid.toUiModels(
            entriesByDate = entriesByDate,
            today = date,
            language = AppLanguage.ITALIAN,
            dailyLimitMinutes = 8 * 60,
        )

        assertEquals(
            listOf(EntryType.VACATION, EntryType.PERMIT),
            uiModels.single().activityItems.map(CalendarActivityUiModel::entryType),
        )
    }

    @Test
    fun marksDayAsOverLimitWhenTotalMinutesExceedConfiguredDailyLimit() {
        val date = LocalDate(2026, 6, 16)
        val grid = listOf(
            CalendarDay(
                date = date,
                inCurrentMonth = true,
                isWeekend = false,
                holiday = null,
            ),
        )
        val entriesByDate = mapOf(
            date to DailyEntry(
                date = date,
                activities = listOf(
                    Activity(
                        type = EntryType.PROJECT,
                        title = "Analisi",
                        minutes = 360,
                    ),
                    Activity(
                        type = EntryType.PROJECT,
                        title = "Supporto",
                        minutes = 180,
                    ),
                ),
            ),
        )

        val cell = grid.toUiModels(
            entriesByDate = entriesByDate,
            today = date,
            language = AppLanguage.ITALIAN,
            dailyLimitMinutes = 8 * 60,
        ).single()

        assertEquals(540, cell.totalMinutes)
        assertEquals(8 * 60, cell.dailyLimitMinutes)
        assertEquals(true, cell.isOverDailyLimit)
    }
}
