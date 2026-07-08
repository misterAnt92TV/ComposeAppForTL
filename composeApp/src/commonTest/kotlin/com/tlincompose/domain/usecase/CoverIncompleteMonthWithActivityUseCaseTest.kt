package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.EntryType
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CoverIncompleteMonthWithActivityUseCaseTest {
    @Test
    fun fillsOnlyIncompleteWorkdaysAndKeepsCompleteDaysUntouched() = runTest {
        val month = CalendarMonth(2026, 7)
        val partialDay = LocalDate(2026, 7, 2)
        val completeDay = LocalDate(2026, 7, 3)
        val weekendDay = LocalDate(2026, 7, 4)
        val repository = RecordingTimesheetRepository()
        val useCase = CoverIncompleteMonthWithActivityUseCase(SaveDailyEntryUseCase(repository))
        val activityTemplate = Activity(
            type = EntryType.PROJECT,
            extCode = "EXT-001",
            title = "Apollo",
            minutes = 480,
        )

        val updatedEntries = useCase(
            month = month,
            existingEntries = mapOf(
                partialDay to DailyEntry(
                    date = partialDay,
                    activities = listOf(
                        Activity(
                            type = EntryType.COURSE,
                            extCode = "TRN-01",
                            title = "Formazione",
                            minutes = 180,
                        ),
                    ),
                ),
                completeDay to DailyEntry(
                    date = completeDay,
                    activities = listOf(
                        Activity(
                            type = EntryType.PROJECT,
                            extCode = "KEEP",
                            title = "Completo",
                            minutes = 480,
                        ),
                    ),
                ),
            ),
            activityTemplate = activityTemplate,
            standardWorkdayMinutes = 480,
        )

        val partialEntry = updatedEntries.first { it.date == partialDay }
        assertEquals(2, partialEntry.activities.size)
        assertEquals(300, partialEntry.activities.last().minutes)
        assertEquals("EXT-001", partialEntry.activities.last().extCode)
        assertEquals(22, updatedEntries.size)
        assertNull(updatedEntries.find { it.date == completeDay })
        assertNull(updatedEntries.find { it.date == weekendDay })
        assertEquals(22, repository.savedEntries.size)
    }

    @Test
    fun mergesWithExistingMatchingActivityInsteadOfDuplicatingRow() = runTest {
        val month = CalendarMonth(2026, 7)
        val date = LocalDate(2026, 7, 1)
        val repository = RecordingTimesheetRepository()
        val useCase = CoverIncompleteMonthWithActivityUseCase(SaveDailyEntryUseCase(repository))
        val template = Activity(
            type = EntryType.PROJECT,
            extCode = "EXT-001",
            title = "Apollo",
            minutes = 480,
        )

        val updatedEntries = useCase(
            month = month,
            existingEntries = mapOf(
                date to DailyEntry(
                    date = date,
                    activities = listOf(
                        template.copy(minutes = 120),
                    ),
                ),
            ),
            activityTemplate = template,
            standardWorkdayMinutes = 480,
        )

        val updatedEntry = updatedEntries.first { it.date == date }
        assertEquals(1, updatedEntry.activities.size)
        assertEquals(480, updatedEntry.activities.single().minutes)
    }

    private class RecordingTimesheetRepository : com.tlincompose.domain.repository.TimesheetRepository {
        val savedEntries = mutableListOf<DailyEntry>()

        override suspend fun loadMonth(month: CalendarMonth): Map<LocalDate, DailyEntry> = emptyMap()

        override suspend fun loadRange(range: com.tlincompose.domain.model.DateRange): Map<LocalDate, DailyEntry> = emptyMap()

        override suspend fun loadAll(): List<DailyEntry> = emptyList()

        override suspend fun saveEntry(entry: DailyEntry) {
            savedEntries += entry
        }

        override suspend fun deleteEntry(date: LocalDate) = Unit

        override suspend fun replaceAll(entries: List<DailyEntry>) = Unit

        override suspend fun syncActivitiesWithDefinition(
            previousExtCode: String,
            definition: com.tlincompose.domain.model.ActivityDefinition,
        ): Int = 0
    }
}
