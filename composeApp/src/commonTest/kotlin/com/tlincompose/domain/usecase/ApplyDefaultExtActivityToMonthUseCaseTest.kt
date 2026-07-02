package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.BuiltInActivityDefinitions
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.DefaultExtWorkMode
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset
import com.tlincompose.domain.repository.TimesheetRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplyDefaultExtActivityToMonthUseCaseTest {
    @Test
    fun appliesDefaultExtOnlyOnEmptyWorkingDays() = runTest {
        val repository = FakeTimesheetRepository(
            mutableMapOf(
                LocalDate(2026, 5, 5) to DailyEntry(
                    date = LocalDate(2026, 5, 5),
                    activities = listOf(
                        Activity(
                            type = EntryType.PROJECT,
                            extCode = "EXT-0001",
                            title = "Attività esistente",
                            minutes = 480,
                        ),
                    ),
                ),
            ),
        )
        val useCase = ApplyDefaultExtActivityToMonthUseCase(
            repository = repository,
            saveDailyEntry = SaveDailyEntryUseCase(repository),
        )

        val appliedDays = useCase(
            month = CalendarMonth(2026, 5),
            workMode = DefaultExtWorkMode.SMART_WORKING,
            definition = defaultDefinition(),
        )

        assertEquals(19, appliedDays)
        assertEquals("Attività esistente", repository.entries.getValue(LocalDate(2026, 5, 5)).activities.single().title)
        assertEquals(
            "Attività EXT di default (Smart working)",
            repository.entries.getValue(LocalDate(2026, 5, 4)).activities.single().title,
        )
        assertTrue(LocalDate(2026, 5, 3) !in repository.entries.keys)
        assertTrue(LocalDate(2026, 5, 1) !in repository.entries.keys)
    }

    @Test
    fun appliesOfficeVariantWhenSelected() = runTest {
        val repository = FakeTimesheetRepository()
        val useCase = ApplyDefaultExtActivityToMonthUseCase(
            repository = repository,
            saveDailyEntry = SaveDailyEntryUseCase(repository),
        )

        useCase(
            month = CalendarMonth(2026, 6),
            workMode = DefaultExtWorkMode.OFFICE,
            definition = defaultDefinition(),
        )

        assertEquals(
            "Attività EXT di default (Ufficio)",
            repository.entries.getValue(LocalDate(2026, 6, 1)).activities.single().title,
        )
    }

    private fun defaultDefinition(): ActivityDefinition = ActivityDefinition(
        extCode = BuiltInActivityDefinitions.DefaultExtCode,
        type = EntryType.PROJECT,
        title = "Attività EXT di default",
        description = "Definizione standard",
        defaultMinutes = 480,
        createdDate = LocalDate(2026, 1, 1),
        updatedDate = LocalDate(2026, 1, 1),
        projectIconPreset = ProjectIconPreset.WORK,
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

    override suspend fun replaceAll(entries: List<DailyEntry>) = Unit

    override suspend fun syncActivitiesWithDefinition(
        previousExtCode: String,
        definition: ActivityDefinition,
    ): Int = 0
}
