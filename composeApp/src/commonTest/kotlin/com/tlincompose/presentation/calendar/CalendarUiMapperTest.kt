package com.tlincompose.presentation.calendar

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarDay
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.EntryType
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class CalendarUiMapperTest {
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
        )

        assertEquals(
            listOf(EntryType.VACATION, EntryType.PERMIT),
            uiModels.single().activityItems.map(CalendarActivityUiModel::entryType),
        )
    }
}
