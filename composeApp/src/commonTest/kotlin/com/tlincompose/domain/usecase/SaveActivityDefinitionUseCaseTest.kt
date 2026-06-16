package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset
import com.tlincompose.domain.model.ValidatedActivityDefinitionDraft
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate

class SaveActivityDefinitionUseCaseTest {
    @Test
    fun savesCourseDefinitionWithoutProjectSpecificIcon() = runTest {
        val repository = RecordingActivityDefinitionRepository()
        val useCase = SaveActivityDefinitionUseCase(
            repository = repository,
            todayProvider = { LocalDate(2026, 5, 10) },
        )

        val saved = useCase(
            draft = ValidatedActivityDefinitionDraft(
                extCode = "EXT-C001",
                type = EntryType.COURSE,
                title = "Corso Kotlin",
                description = "Formazione interna",
                defaultMinutes = 240,
                projectUrl = "https://example.com/corso-kotlin",
                projectIconPreset = ProjectIconPreset.SCHOOL,
            ),
            existingDefinition = null,
        )

        assertEquals(EntryType.COURSE, saved.type)
        assertEquals(null, saved.projectIconPreset)
        assertEquals(saved, repository.savedDefinition)
    }

    @Test
    fun preservesCreationDateWhenUpdatingExistingDefinition() = runTest {
        val repository = RecordingActivityDefinitionRepository()
        val existingDefinition = ActivityDefinition(
            extCode = "EXT-0004",
            type = EntryType.PROJECT,
            title = "Apollo",
            description = "Vecchia descrizione",
            defaultMinutes = 480,
            createdDate = LocalDate(2026, 5, 1),
            updatedDate = LocalDate(2026, 5, 1),
        )
        val useCase = SaveActivityDefinitionUseCase(
            repository = repository,
            todayProvider = { LocalDate(2026, 5, 10) },
        )

        val saved = useCase(
            draft = ValidatedActivityDefinitionDraft(
                extCode = "EXT-0004",
                type = EntryType.PROJECT,
                title = "Apollo aggiornato",
                description = "Nuova descrizione",
                defaultMinutes = 420,
                projectUrl = "https://example.com/apollo",
                projectIconPreset = ProjectIconPreset.FOLDER,
            ),
            existingDefinition = existingDefinition,
        )

        assertEquals(LocalDate(2026, 5, 1), saved.createdDate)
        assertEquals(LocalDate(2026, 5, 10), saved.updatedDate)
        assertEquals(ProjectIconPreset.FOLDER, saved.projectIconPreset)
        assertEquals(saved, repository.savedDefinition)
        assertEquals("EXT-0004", repository.previousExtCode)
    }

    @Test
    fun forwardsPreviousExtCodeWhenEntityIsRenamed() = runTest {
        val repository = RecordingActivityDefinitionRepository()
        val existingDefinition = ActivityDefinition(
            extCode = "EXT-0004",
            type = EntryType.PROJECT,
            title = "Apollo",
            description = "Vecchia descrizione",
            defaultMinutes = 480,
            createdDate = LocalDate(2026, 5, 1),
            updatedDate = LocalDate(2026, 5, 1),
        )
        val useCase = SaveActivityDefinitionUseCase(
            repository = repository,
            todayProvider = { LocalDate(2026, 5, 10) },
        )

        val saved = useCase(
            draft = ValidatedActivityDefinitionDraft(
                extCode = "EXT-0042",
                type = EntryType.PROJECT,
                title = "Apollo aggiornato",
                description = "Nuova descrizione",
                defaultMinutes = 420,
                projectUrl = null,
                projectIconPreset = ProjectIconPreset.FOLDER,
            ),
            existingDefinition = existingDefinition,
        )

        assertEquals("EXT-0042", saved.extCode)
        assertEquals("EXT-0004", repository.previousExtCode)
    }
}

private class RecordingActivityDefinitionRepository : ActivityDefinitionRepository {
    var savedDefinition: ActivityDefinition? = null
    var previousExtCode: String? = null

    override suspend fun loadAll(): List<ActivityDefinition> = emptyList()

    override suspend fun upsert(definition: ActivityDefinition, previousExtCode: String?) {
        savedDefinition = definition
        this.previousExtCode = previousExtCode
    }

    override suspend fun delete(extCode: String) = Unit
}
