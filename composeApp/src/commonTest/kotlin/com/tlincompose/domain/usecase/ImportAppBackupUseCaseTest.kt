package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.AppBackupData
import com.tlincompose.domain.model.AppBackupImportError
import com.tlincompose.domain.model.AppBackupImportResult
import com.tlincompose.domain.model.AppBackupReadResult
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import com.tlincompose.domain.repository.AppBackupRepository
import com.tlincompose.domain.repository.TimesheetRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportAppBackupUseCaseTest {
    @Test
    fun importReplacesAllRepositoriesOnSuccess() = runTest {
        val backupData = sampleBackupData()
        val backupRepository = FakeAppBackupRepository(AppBackupReadResult.Success(backupData))
        val accessibilityRepository = RecordingAccessibilityPreferencesRepository()
        val activityRepository = RecordingActivityDefinitionRepository()
        val timesheetRepository = RecordingTimesheetRepository()
        val useCase = ImportAppBackupUseCase(
            appBackupRepository = backupRepository,
            accessibilityPreferencesRepository = accessibilityRepository,
            activityDefinitionRepository = activityRepository,
            timesheetRepository = timesheetRepository,
        )

        val result = useCase("backup".encodeToByteArray())

        assertEquals(AppBackupImportResult.Success, result)
        assertEquals(backupData.preferences, accessibilityRepository.savedPreferences)
        assertEquals(backupData.activityDefinitions, activityRepository.replacedDefinitions)
        assertEquals(backupData.timesheetEntries, timesheetRepository.replacedEntries)
    }

    @Test
    fun importReturnsFailureForInvalidJson() = runTest {
        val useCase = ImportAppBackupUseCase(
            appBackupRepository = FakeAppBackupRepository(AppBackupReadResult.Failure(AppBackupImportError.InvalidJson)),
            accessibilityPreferencesRepository = RecordingAccessibilityPreferencesRepository(),
            activityDefinitionRepository = RecordingActivityDefinitionRepository(),
            timesheetRepository = RecordingTimesheetRepository(),
        )

        val result = useCase("broken".encodeToByteArray())

        assertEquals(
            AppBackupImportResult.Failure(AppBackupImportError.InvalidJson),
            result,
        )
    }

    @Test
    fun importReturnsFailureForEmptyContent() = runTest {
        val useCase = ImportAppBackupUseCase(
            appBackupRepository = FakeAppBackupRepository(AppBackupReadResult.Failure(AppBackupImportError.EmptyContent)),
            accessibilityPreferencesRepository = RecordingAccessibilityPreferencesRepository(),
            activityDefinitionRepository = RecordingActivityDefinitionRepository(),
            timesheetRepository = RecordingTimesheetRepository(),
        )

        val result = useCase(byteArrayOf())

        assertEquals(
            AppBackupImportResult.Failure(AppBackupImportError.EmptyContent),
            result,
        )
    }

    @Test
    fun importReturnsFailureWhenPersistenceFails() = runTest {
        val backupData = sampleBackupData()
        val useCase = ImportAppBackupUseCase(
            appBackupRepository = FakeAppBackupRepository(AppBackupReadResult.Success(backupData)),
            accessibilityPreferencesRepository = RecordingAccessibilityPreferencesRepository(),
            activityDefinitionRepository = object : RecordingActivityDefinitionRepository() {
                override suspend fun replaceAll(definitions: List<ActivityDefinition>) {
                    error("disk full")
                }
            },
            timesheetRepository = RecordingTimesheetRepository(),
        )

        val result = useCase("backup".encodeToByteArray())

        assertEquals(
            AppBackupImportResult.Failure(AppBackupImportError.PersistenceFailure),
            result,
        )
    }

    private fun sampleBackupData(): AppBackupData = AppBackupData(
        preferences = AccessibilityPreferences(exportUserFullName = "Mario Rossi"),
        activityDefinitions = listOf(
            ActivityDefinition(
                extCode = "EXT-001",
                type = EntryType.PROJECT,
                title = "Apollo",
                description = "",
                defaultMinutes = 480,
                createdDate = LocalDate(2026, 5, 1),
                updatedDate = LocalDate(2026, 5, 1),
            ),
        ),
        timesheetEntries = listOf(
            DailyEntry(
                date = LocalDate(2026, 5, 12),
                activities = listOf(
                    Activity(
                        type = EntryType.PROJECT,
                        extCode = "EXT-001",
                        title = "Apollo",
                        minutes = 480,
                    ),
                ),
            ),
        ),
    )

    private class FakeAppBackupRepository(
        private val result: AppBackupReadResult,
    ) : AppBackupRepository {
        override suspend fun exportBackup(data: AppBackupData, fileName: String) = error("Not used in import test")

        override suspend fun readBackup(bytes: ByteArray): AppBackupReadResult = result
    }

    private class RecordingAccessibilityPreferencesRepository : AccessibilityPreferencesRepository {
        var savedPreferences: AccessibilityPreferences? = null

        override suspend fun loadPreferences(): AccessibilityPreferences = AccessibilityPreferences()

        override suspend fun savePreferences(preferences: AccessibilityPreferences) {
            savedPreferences = preferences
        }
    }

    private open class RecordingActivityDefinitionRepository : ActivityDefinitionRepository {
        var replacedDefinitions: List<ActivityDefinition> = emptyList()

        override suspend fun loadAll(): List<ActivityDefinition> = emptyList()

        override suspend fun upsert(definition: ActivityDefinition, previousExtCode: String?) = Unit

        override suspend fun delete(extCode: String) = Unit

        override suspend fun replaceAll(definitions: List<ActivityDefinition>) {
            replacedDefinitions = definitions
        }
    }

    private class RecordingTimesheetRepository : TimesheetRepository {
        var replacedEntries: List<DailyEntry> = emptyList()

        override suspend fun loadMonth(month: com.tlincompose.domain.model.CalendarMonth) = emptyMap<LocalDate, DailyEntry>()

        override suspend fun loadRange(range: com.tlincompose.domain.model.DateRange) = emptyMap<LocalDate, DailyEntry>()

        override suspend fun loadAll(): List<DailyEntry> = emptyList()

        override suspend fun saveEntry(entry: DailyEntry) = Unit

        override suspend fun deleteEntry(date: LocalDate) = Unit

        override suspend fun replaceAll(entries: List<DailyEntry>) {
            replacedEntries = entries
        }

        override suspend fun syncActivitiesWithDefinition(
            previousExtCode: String,
            definition: ActivityDefinition,
        ): Int = 0
    }
}
