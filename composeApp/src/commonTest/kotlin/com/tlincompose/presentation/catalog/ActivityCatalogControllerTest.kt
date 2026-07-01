package com.tlincompose.presentation.catalog

import co.touchlab.kermit.Logger
import com.tlincompose.TestDispatcherProvider
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import com.tlincompose.domain.repository.TimesheetRepository
import com.tlincompose.domain.usecase.DeleteActivityDefinitionUseCase
import com.tlincompose.domain.usecase.GenerateNextExtCodeUseCase
import com.tlincompose.domain.usecase.LoadActivityDefinitionsUseCase
import com.tlincompose.domain.usecase.SaveActivityDefinitionUseCase
import com.tlincompose.domain.usecase.SyncActivitiesWithDefinitionUseCase
import com.tlincompose.domain.usecase.ValidateActivityDefinitionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityCatalogControllerTest {
    @Test
    fun controllerAllowsMultipleCourseDefinitionsWithDifferentExtCodes() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = FakeActivityDefinitionRepository(
            definitions = mutableListOf(
                ActivityDefinition(
                    extCode = "EXT-C001",
                    type = EntryType.COURSE,
                    title = "Corso sicurezza",
                    description = "",
                    defaultMinutes = 240,
                    createdDate = LocalDate(2026, 5, 1),
                    updatedDate = LocalDate(2026, 5, 1),
                ),
            ),
        )
        val controller = ActivityCatalogController(
            loadActivityDefinitions = LoadActivityDefinitionsUseCase(repository),
            generateNextExtCode = GenerateNextExtCodeUseCase(),
            validateActivityDefinition = ValidateActivityDefinitionUseCase(),
            saveActivityDefinition = SaveActivityDefinitionUseCase(
                repository = repository,
                todayProvider = { LocalDate(2026, 5, 10) },
            ),
            syncActivitiesWithDefinition = SyncActivitiesWithDefinitionUseCase(FakeTimesheetRepository()),
            deleteActivityDefinition = DeleteActivityDefinitionUseCase(repository),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("ActivityCatalogControllerTest"),
        )

        advanceUntilIdle()

        controller.openCreateEditor(defaultMinutes = 240)
        controller.updateDraftProjectIconPreset(ProjectIconPreset.SCHOOL)
        controller.updateDraftType(EntryType.COURSE)
        controller.updateDraftExtCode("EXT-C002")
        controller.updateDraftTitle("Corso Kotlin")
        controller.updateDraftDuration("4")
        controller.saveEditor(
            language = AppLanguage.ITALIAN,
            onValidationError = {},
            onPersistenceError = {},
        )

        advanceUntilIdle()

        assertEquals(
            setOf("EXT-C001", "EXT-C002"),
            controller.definitions.filter { it.type == EntryType.COURSE }.map(ActivityDefinition::extCode).toSet(),
        )
        assertEquals(
            null,
            controller.definitions.first { it.extCode == "EXT-C002" }.projectIconPreset,
        )
        controller.dispose()
    }

    @Test
    fun controllerLoadsCatalogAndSavesNewDefinition() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = FakeActivityDefinitionRepository(
            definitions = mutableListOf(
                ActivityDefinition(
                    extCode = "EXT-0001",
                    type = EntryType.PROJECT,
                    title = "Apollo",
                    description = "",
                    defaultMinutes = 480,
                    createdDate = LocalDate(2026, 5, 1),
                    updatedDate = LocalDate(2026, 5, 1),
                ),
            ),
        )
        val timesheetRepository = FakeTimesheetRepository()
        val controller = ActivityCatalogController(
            loadActivityDefinitions = LoadActivityDefinitionsUseCase(repository),
            generateNextExtCode = GenerateNextExtCodeUseCase(),
            validateActivityDefinition = ValidateActivityDefinitionUseCase(),
            saveActivityDefinition = SaveActivityDefinitionUseCase(
                repository = repository,
                todayProvider = { LocalDate(2026, 5, 10) },
            ),
            syncActivitiesWithDefinition = SyncActivitiesWithDefinitionUseCase(timesheetRepository),
            deleteActivityDefinition = DeleteActivityDefinitionUseCase(repository),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("ActivityCatalogControllerTest"),
        )

        advanceUntilIdle()
        assertEquals(1, controller.definitions.size)

        controller.openCreateEditor(defaultMinutes = 450)
        assertEquals("EXT-0002", controller.editorState?.extCode)
        assertEquals("7.5", controller.editorState?.durationHoursText)

        controller.updateDraftTitle("Portale clienti")
        controller.updateDraftDescription("Refactoring backlog")
        controller.updateDraftDuration("8")
        controller.updateDraftProjectUrl("https://example.com/portal")
        controller.updateDraftProjectIconPreset(ProjectIconPreset.CODE)
        controller.saveEditor(
            language = AppLanguage.ENGLISH,
            onValidationError = {},
            onPersistenceError = {},
        )

        advanceUntilIdle()

        assertEquals(2, controller.definitions.size)
        assertEquals("EXT-0002", controller.definitions.last().extCode)
        assertEquals(EntryType.PROJECT, controller.definitions.last().type)
        assertEquals(ProjectIconPreset.CODE, controller.definitions.last().projectIconPreset)
        assertEquals(null, controller.editorState)

        controller.requestDelete(controller.definitions.first())
        assertNotNull(controller.definitionPendingDelete)
        controller.confirmDelete(onFailure = {})
        advanceUntilIdle()

        assertEquals(listOf("EXT-0002"), controller.definitions.map(ActivityDefinition::extCode))
        controller.dispose()
    }

    @Test
    fun controllerRenamesExistingExtCodeWhenEditingDefinition() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = FakeActivityDefinitionRepository(
            definitions = mutableListOf(
                ActivityDefinition(
                    extCode = "EXT-0001",
                    type = EntryType.PROJECT,
                    title = "Apollo",
                    description = "",
                    defaultMinutes = 480,
                    createdDate = LocalDate(2026, 5, 1),
                    updatedDate = LocalDate(2026, 5, 1),
                ),
            ),
        )
        val timesheetRepository = FakeTimesheetRepository(
            entries = mutableMapOf(
                LocalDate(2026, 5, 12) to DailyEntry(
                    date = LocalDate(2026, 5, 12),
                    activities = listOf(
                        Activity(
                            type = EntryType.PROJECT,
                            extCode = "EXT-0001",
                            title = "Apollo",
                            description = "Vecchia descrizione",
                            projectUrl = "https://example.com/old",
                            minutes = 480,
                        ),
                    ),
                ),
            ),
        )
        val controller = ActivityCatalogController(
            loadActivityDefinitions = LoadActivityDefinitionsUseCase(repository),
            generateNextExtCode = GenerateNextExtCodeUseCase(),
            validateActivityDefinition = ValidateActivityDefinitionUseCase(),
            saveActivityDefinition = SaveActivityDefinitionUseCase(
                repository = repository,
                todayProvider = { LocalDate(2026, 5, 10) },
            ),
            syncActivitiesWithDefinition = SyncActivitiesWithDefinitionUseCase(timesheetRepository),
            deleteActivityDefinition = DeleteActivityDefinitionUseCase(repository),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("ActivityCatalogControllerTest"),
        )

        advanceUntilIdle()
        controller.openEditEditor(controller.definitions.first())
        controller.updateDraftExtCode("EXT-0042")
        controller.updateDraftTitle("Apollo aggiornato")
        controller.saveEditor(
            language = AppLanguage.ITALIAN,
            onValidationError = {},
            onPersistenceError = {},
        )

        advanceUntilIdle()

        assertEquals(listOf("EXT-0042"), controller.definitions.map(ActivityDefinition::extCode))
        assertEquals("EXT-0042", repository.definitions.single().extCode)
        assertEquals("EXT-0042", timesheetRepository.entries.values.single().activities.single().extCode)
        assertEquals("Apollo aggiornato", timesheetRepository.entries.values.single().activities.single().title)
        controller.dispose()
    }
}

private class FakeActivityDefinitionRepository(
    val definitions: MutableList<ActivityDefinition>,
) : ActivityDefinitionRepository {
    override suspend fun loadAll(): List<ActivityDefinition> = definitions.toList()

    override suspend fun upsert(definition: ActivityDefinition, previousExtCode: String?) {
        previousExtCode
            ?.takeIf { it != definition.extCode }
            ?.let { code -> definitions.removeAll { it.extCode == code } }
        definitions.removeAll { it.extCode == definition.extCode }
        definitions += definition
    }

    override suspend fun delete(extCode: String) {
        definitions.removeAll { it.extCode == extCode }
    }

    override suspend fun replaceAll(definitions: List<ActivityDefinition>) {
        this.definitions.clear()
        this.definitions += definitions
    }
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

    override suspend fun replaceAll(entries: List<DailyEntry>) {
        this.entries.clear()
        entries.forEach { entry ->
            this.entries[entry.date] = entry
        }
    }

    override suspend fun syncActivitiesWithDefinition(
        previousExtCode: String,
        definition: ActivityDefinition,
    ): Int {
        var updatedActivities = 0
        entries.replaceAll { _, entry ->
            entry.copy(
                activities = entry.activities.map { activity ->
                    if (activity.extCode != previousExtCode) {
                        activity
                    } else {
                        updatedActivities += 1
                        activity.copy(
                            type = definition.type,
                            extCode = definition.extCode,
                            title = definition.title,
                            description = definition.description,
                            projectUrl = definition.projectUrl,
                        )
                    }
                },
            )
        }
        return updatedActivities
    }
}
