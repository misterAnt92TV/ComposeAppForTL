package com.tlincompose.data.local

import co.touchlab.kermit.Logger
import com.tlincompose.TestDispatcherProvider
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonTimesheetRepositoryTest {
    @Test
    fun repositoryRoundTripKeepsEntries() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = JsonTimesheetRepository(
            storageDriver = InMemoryStorageDriver(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("JsonTimesheetRepositoryTest"),
        )
        val entry = DailyEntry(
            date = LocalDate(2026, 5, 12),
            activities = listOf(
                Activity(
                    type = EntryType.PROJECT,
                    extCode = "EXT-0001",
                    title = "Progetto Zeus",
                    description = "Cliente enterprise",
                    projectUrl = "https://example.com/zeus",
                    minutes = 450,
                ),
                Activity(
                    type = EntryType.PERMIT,
                    extCode = "EXT-0002",
                    title = "Permesso",
                    minutes = 30,
                ),
            ),
        )

        repository.saveEntry(entry)
        val loaded = repository.loadMonth(CalendarMonth(2026, 5))

        assertEquals(entry, loaded[entry.date])
    }

    @Test
    fun loadRangeReturnsOnlyEntriesInsideSelectedInterval() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = JsonTimesheetRepository(
            storageDriver = InMemoryStorageDriver(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("JsonTimesheetRepositoryTest"),
        )
        val firstEntry = DailyEntry(
            date = LocalDate(2026, 5, 10),
            activities = listOf(
                Activity(
                    type = EntryType.PROJECT,
                    extCode = "EXT-0001",
                    title = "Apollo",
                    minutes = 480,
                ),
            ),
        )
        val secondEntry = DailyEntry(
            date = LocalDate(2026, 5, 12),
            activities = listOf(
                Activity(
                    type = EntryType.PERMIT,
                    extCode = "EXT-0002",
                    title = "Permesso",
                    minutes = 120,
                ),
            ),
        )
        val outsideEntry = DailyEntry(
            date = LocalDate(2026, 5, 16),
            activities = listOf(
                Activity(
                    type = EntryType.VACATION,
                    extCode = "EXT-0003",
                    title = "Ferie",
                    minutes = 480,
                ),
            ),
        )

        repository.saveEntry(firstEntry)
        repository.saveEntry(secondEntry)
        repository.saveEntry(outsideEntry)

        val loaded = repository.loadRange(
            DateRange(
                startDate = LocalDate(2026, 5, 10),
                endDate = LocalDate(2026, 5, 14),
            ),
        )

        assertEquals(listOf(firstEntry.date, secondEntry.date), loaded.keys.toList())
        assertEquals(firstEntry, loaded[firstEntry.date])
        assertEquals(secondEntry, loaded[secondEntry.date])
    }

    @Test
    fun syncActivitiesWithDefinitionUpdatesStoredSnapshots() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = JsonTimesheetRepository(
            storageDriver = InMemoryStorageDriver(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("JsonTimesheetRepositoryTest"),
        )
        val entry = DailyEntry(
            date = LocalDate(2026, 5, 12),
            activities = listOf(
                Activity(
                    type = EntryType.PROJECT,
                    extCode = "EXT-0001",
                    title = "Apollo",
                    description = "Vecchia descrizione",
                    projectUrl = "https://example.com/old",
                    minutes = 450,
                ),
            ),
        )
        repository.saveEntry(entry)

        val updatedCount = repository.syncActivitiesWithDefinition(
            previousExtCode = "EXT-0001",
            definition = ActivityDefinition(
                extCode = "EXT-0042",
                type = EntryType.PROJECT,
                title = "Apollo aggiornato",
                description = "Nuova descrizione",
                defaultMinutes = 480,
                createdDate = LocalDate(2026, 5, 1),
                updatedDate = LocalDate(2026, 5, 10),
                projectUrl = "https://example.com/new",
                projectIconPreset = ProjectIconPreset.CODE,
            ),
        )

        val loaded = repository.loadMonth(CalendarMonth(2026, 5))
        assertEquals(1, updatedCount)
        assertEquals("EXT-0042", loaded[entry.date]?.activities?.single()?.extCode)
        assertEquals("Apollo aggiornato", loaded[entry.date]?.activities?.single()?.title)
        assertEquals("Nuova descrizione", loaded[entry.date]?.activities?.single()?.description)
        assertEquals("https://example.com/new", loaded[entry.date]?.activities?.single()?.projectUrl)
        assertEquals(450, loaded[entry.date]?.activities?.single()?.minutes)
    }

    @Test
    fun replaceAllAndLoadAllKeepOrderedEntries() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = JsonTimesheetRepository(
            storageDriver = InMemoryStorageDriver(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("JsonTimesheetRepositoryTest"),
        )
        val laterEntry = DailyEntry(
            date = LocalDate(2026, 5, 16),
            activities = listOf(
                Activity(
                    type = EntryType.PROJECT,
                    extCode = "EXT-0100",
                    title = "Athena",
                    minutes = 480,
                ),
            ),
        )
        val earlierEntry = DailyEntry(
            date = LocalDate(2026, 5, 10),
            activities = listOf(
                Activity(
                    type = EntryType.PERMIT,
                    extCode = "EXT-0200",
                    title = "Permesso",
                    minutes = 120,
                ),
            ),
        )

        repository.replaceAll(listOf(laterEntry, earlierEntry))

        assertEquals(listOf(earlierEntry, laterEntry), repository.loadAll())
    }
}
