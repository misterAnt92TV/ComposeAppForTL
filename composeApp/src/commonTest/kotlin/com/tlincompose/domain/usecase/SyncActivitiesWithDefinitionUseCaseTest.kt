package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset
import com.tlincompose.domain.repository.TimesheetRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class SyncActivitiesWithDefinitionUseCaseTest {
    @Test
    fun delegatesSynchronizationToRepository() = runTest {
        val repository = RecordingRepository()
        val definition = ActivityDefinition(
            extCode = "EXT-0042",
            type = EntryType.PROJECT,
            title = "Apollo aggiornato",
            description = "Nuova descrizione",
            defaultMinutes = 480,
            createdDate = LocalDate(2026, 5, 1),
            updatedDate = LocalDate(2026, 5, 10),
            projectUrl = "https://example.com/apollo",
            projectIconPreset = ProjectIconPreset.CODE,
        )

        val updatedCount = SyncActivitiesWithDefinitionUseCase(repository)(
            previousExtCode = "EXT-0001",
            definition = definition,
        )

        assertEquals("EXT-0001", repository.previousExtCode)
        assertEquals(definition, repository.definition)
        assertEquals(3, updatedCount)
    }

    private class RecordingRepository : TimesheetRepository {
        var previousExtCode: String? = null
        var definition: ActivityDefinition? = null

        override suspend fun loadMonth(month: CalendarMonth): Map<LocalDate, DailyEntry> = emptyMap()

        override suspend fun loadRange(range: DateRange): Map<LocalDate, DailyEntry> = emptyMap()

        override suspend fun loadAll(): List<DailyEntry> = emptyList()

        override suspend fun saveEntry(entry: DailyEntry) = Unit

        override suspend fun deleteEntry(date: LocalDate) = Unit

        override suspend fun replaceAll(entries: List<DailyEntry>) = Unit

        override suspend fun syncActivitiesWithDefinition(
            previousExtCode: String,
            definition: ActivityDefinition,
        ): Int {
            this.previousExtCode = previousExtCode
            this.definition = definition
            return 3
        }
    }
}
