package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.repository.TimesheetRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SaveDailyEntryUseCaseTest {
    @Test
    fun savesEntryWhenActivitiesArePresent() = runTest {
        val repository = RecordingRepository()
        val date = LocalDate(2026, 5, 12)
        val activities = listOf(
            Activity(
                type = EntryType.PROJECT,
                extCode = "EXT-0001",
                title = "Apollo",
                minutes = 480,
            ),
        )

        SaveDailyEntryUseCase(repository)(date, activities)

        assertEquals(DailyEntry(date, activities), repository.savedEntry)
        assertNull(repository.deletedDate)
    }

    @Test
    fun deletesEntryWhenActivitiesAreEmpty() = runTest {
        val repository = RecordingRepository()
        val date = LocalDate(2026, 5, 12)

        SaveDailyEntryUseCase(repository)(date, emptyList())

        assertEquals(date, repository.deletedDate)
        assertNull(repository.savedEntry)
    }

    private class RecordingRepository : TimesheetRepository {
        var savedEntry: DailyEntry? = null
        var deletedDate: LocalDate? = null

        override suspend fun loadMonth(month: CalendarMonth): Map<LocalDate, DailyEntry> = emptyMap()

        override suspend fun loadRange(range: DateRange): Map<LocalDate, DailyEntry> = emptyMap()

        override suspend fun loadAll(): List<DailyEntry> = emptyList()

        override suspend fun saveEntry(entry: DailyEntry) {
            savedEntry = entry
        }

        override suspend fun deleteEntry(date: LocalDate) {
            deletedDate = date
        }

        override suspend fun replaceAll(entries: List<DailyEntry>) = Unit

        override suspend fun syncActivitiesWithDefinition(
            previousExtCode: String,
            definition: ActivityDefinition,
        ): Int = 0
    }
}
