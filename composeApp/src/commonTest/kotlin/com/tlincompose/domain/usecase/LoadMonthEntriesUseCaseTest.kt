package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.repository.TimesheetRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate

class LoadMonthEntriesUseCaseTest {
    @Test
    fun loadMonthDelegatesToRepository() = runTest {
        val expected = mapOf(LocalDate(2026, 5, 2) to DailyEntry(LocalDate(2026, 5, 2), emptyList()))
        val repository = FakeTimesheetRepository(loadResult = expected)

        val result = LoadMonthEntriesUseCase(repository)(CalendarMonth(2026, 5))

        assertEquals(expected, result)
    }

    private class FakeTimesheetRepository(
        private val loadResult: Map<LocalDate, DailyEntry>,
    ) : TimesheetRepository {
        override suspend fun loadMonth(month: CalendarMonth): Map<LocalDate, DailyEntry> = loadResult

        override suspend fun loadRange(range: DateRange): Map<LocalDate, DailyEntry> = emptyMap()

        override suspend fun saveEntry(entry: DailyEntry) = Unit

        override suspend fun deleteEntry(date: LocalDate) = Unit

        override suspend fun syncActivitiesWithDefinition(
            previousExtCode: String,
            definition: ActivityDefinition,
        ): Int = 0
    }
}
