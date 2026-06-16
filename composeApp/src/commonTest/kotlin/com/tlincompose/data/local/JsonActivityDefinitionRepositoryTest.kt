package com.tlincompose.data.local

import co.touchlab.kermit.Logger
import com.tlincompose.TestDispatcherProvider
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate

class JsonActivityDefinitionRepositoryTest {
    @Test
    fun repositoryRoundTripKeepsDefinitions() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = JsonActivityDefinitionRepository(
            storageDriver = InMemoryStorageDriver(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("JsonActivityDefinitionRepositoryTest"),
        )
        val definition = ActivityDefinition(
            extCode = "EXT-0001",
            type = EntryType.PROJECT,
            title = "Apollo",
            description = "Cliente premium",
            defaultMinutes = 480,
            createdDate = LocalDate(2026, 5, 1),
            updatedDate = LocalDate(2026, 5, 1),
            projectUrl = "https://example.com/apollo",
            projectIconPreset = ProjectIconPreset.BUILD,
        )

        repository.upsert(definition)
        val loaded = repository.loadAll()

        assertEquals(listOf(definition), loaded)
    }

    @Test
    fun deleteRemovesDefinitionFromCatalog() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = JsonActivityDefinitionRepository(
            storageDriver = InMemoryStorageDriver(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("JsonActivityDefinitionRepositoryTest"),
        )
        val definition = ActivityDefinition(
            extCode = "EXT-0001",
            type = EntryType.PROJECT,
            title = "Apollo",
            description = "",
            defaultMinutes = 480,
            createdDate = LocalDate(2026, 5, 1),
            updatedDate = LocalDate(2026, 5, 1),
        )

        repository.upsert(definition)
        repository.delete("EXT-0001")

        assertEquals(emptyList(), repository.loadAll())
    }

    @Test
    fun upsertWithPreviousExtCodeRenamesDefinitionAtomically() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = JsonActivityDefinitionRepository(
            storageDriver = InMemoryStorageDriver(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("JsonActivityDefinitionRepositoryTest"),
        )
        repository.upsert(
            ActivityDefinition(
                extCode = "EXT-0001",
                type = EntryType.PROJECT,
                title = "Apollo",
                description = "",
                defaultMinutes = 480,
                createdDate = LocalDate(2026, 5, 1),
                updatedDate = LocalDate(2026, 5, 1),
            ),
        )

        repository.upsert(
            definition = ActivityDefinition(
                extCode = "EXT-0042",
                type = EntryType.PROJECT,
                title = "Apollo",
                description = "",
                defaultMinutes = 480,
                createdDate = LocalDate(2026, 5, 1),
                updatedDate = LocalDate(2026, 5, 10),
            ),
            previousExtCode = "EXT-0001",
        )

        assertEquals(listOf("EXT-0042"), repository.loadAll().map(ActivityDefinition::extCode))
    }
}
