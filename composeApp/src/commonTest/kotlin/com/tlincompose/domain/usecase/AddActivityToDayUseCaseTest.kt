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

class AddActivityToDayUseCaseTest {
    @Test
    fun createsNewEntryWhenTargetDayIsEmpty() = runTest {
        val repository = RecordingRepository()
        val date = LocalDate(2026, 5, 13)
        val activity = sampleActivity()

        val result = AddActivityToDayUseCase(repository, SaveDailyEntryUseCase(repository))(date, activity)

        assertEquals(DailyEntry(date, listOf(activity)), result)
        assertEquals(listOf(DailyEntry(date, listOf(activity))), repository.savedEntries)
    }

    @Test
    fun appendsActivityWhenTargetDayAlreadyHasActivities() = runTest {
        val date = LocalDate(2026, 5, 13)
        val existingActivity = Activity(
            type = EntryType.PERMIT,
            extCode = "PERM",
            title = "Permesso",
            minutes = 120,
        )
        val newActivity = sampleActivity()
        val repository = RecordingRepository(
            entries = mutableMapOf(
                date to DailyEntry(date, listOf(existingActivity)),
            ),
        )

        val result = AddActivityToDayUseCase(repository, SaveDailyEntryUseCase(repository))(date, newActivity)

        assertEquals(
            DailyEntry(date, listOf(existingActivity, newActivity)),
            result,
        )
        assertEquals(
            listOf(DailyEntry(date, listOf(existingActivity, newActivity))),
            repository.savedEntries,
        )
    }

    @Test
    fun doesNotDuplicateAnIdenticalActivity() = runTest {
        val date = LocalDate(2026, 5, 13)
        val activity = sampleActivity()
        val repository = RecordingRepository(
            entries = mutableMapOf(
                date to DailyEntry(date, listOf(activity)),
            ),
        )

        val result = AddActivityToDayUseCase(repository, SaveDailyEntryUseCase(repository))(date, activity)

        assertEquals(DailyEntry(date, listOf(activity)), result)
        assertEquals(emptyList(), repository.savedEntries)
    }

    private fun sampleActivity(): Activity = Activity(
        type = EntryType.PROJECT,
        extCode = "EXT-0001",
        title = "Apollo",
        minutes = 480,
    )

    private class RecordingRepository(
        val entries: MutableMap<LocalDate, DailyEntry> = mutableMapOf(),
    ) : TimesheetRepository {
        val savedEntries = mutableListOf<DailyEntry>()

        override suspend fun loadMonth(month: CalendarMonth): Map<LocalDate, DailyEntry> =
            entries.filterKeys(month::contains)

        override suspend fun loadRange(range: DateRange): Map<LocalDate, DailyEntry> =
            entries.filterKeys(range::contains)

        override suspend fun loadAll(): List<DailyEntry> = entries.values.toList()

        override suspend fun saveEntry(entry: DailyEntry) {
            entries[entry.date] = entry
            savedEntries += entry
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
}
