package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.repository.TimesheetRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate

class SaveDateRangeEntriesUseCaseTest {
    @Test
    fun savesActivitiesOnEveryDayInsideRange() = runTest {
        val repository = RecordingRepository()
        val activities = listOf(
            Activity(
                type = EntryType.PROJECT,
                extCode = "EXT-0001",
                title = "Apollo",
                minutes = 480,
            ),
        )

        SaveDateRangeEntriesUseCase(
            saveDailyEntry = SaveDailyEntryUseCase(repository),
        )(
            range = DateRange(
                startDate = LocalDate(2026, 5, 12),
                endDate = LocalDate(2026, 5, 14),
            ),
            activities = activities,
        )

        assertEquals(
            listOf(
                DailyEntry(LocalDate(2026, 5, 12), activities),
                DailyEntry(LocalDate(2026, 5, 13), activities),
                DailyEntry(LocalDate(2026, 5, 14), activities),
            ),
            repository.savedEntries,
        )
        assertEquals(emptyList(), repository.deletedDates)
    }

    @Test
    fun deletesActivitiesOnEveryDayInsideRangeWhenListIsEmpty() = runTest {
        val repository = RecordingRepository()

        SaveDateRangeEntriesUseCase(
            saveDailyEntry = SaveDailyEntryUseCase(repository),
        )(
            range = DateRange(
                startDate = LocalDate(2026, 5, 12),
                endDate = LocalDate(2026, 5, 14),
            ),
            activities = emptyList(),
        )

        assertEquals(
            listOf(
                LocalDate(2026, 5, 12),
                LocalDate(2026, 5, 13),
                LocalDate(2026, 5, 14),
            ),
            repository.deletedDates,
        )
        assertNull(repository.savedEntries.firstOrNull())
    }

    private class RecordingRepository : TimesheetRepository {
        val savedEntries = mutableListOf<DailyEntry>()
        val deletedDates = mutableListOf<LocalDate>()

        override suspend fun loadMonth(month: CalendarMonth): Map<LocalDate, DailyEntry> = emptyMap()

        override suspend fun loadRange(range: DateRange): Map<LocalDate, DailyEntry> = emptyMap()

        override suspend fun saveEntry(entry: DailyEntry) {
            savedEntries += entry
        }

        override suspend fun deleteEntry(date: LocalDate) {
            deletedDates += date
        }

        override suspend fun syncActivitiesWithDefinition(
            previousExtCode: String,
            definition: ActivityDefinition,
        ): Int = 0
    }
}
