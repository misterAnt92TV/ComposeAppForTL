package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.AppBackupData
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import com.tlincompose.domain.repository.AppBackupRepository
import com.tlincompose.domain.repository.TimesheetRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportAppBackupUseCaseTest {
    @Test
    fun exportCollectsAllDataAndBuildsTimestampedFileName() = runTest {
        val preferences = AccessibilityPreferences(exportUserFullName = "Mario Rossi")
        val definitions = listOf(
            ActivityDefinition(
                extCode = "EXT-001",
                type = EntryType.PROJECT,
                title = "Apollo",
                description = "",
                defaultMinutes = 480,
                createdDate = LocalDate(2026, 5, 1),
                updatedDate = LocalDate(2026, 5, 1),
            ),
        )
        val entries = listOf(
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
        )
        val backupRepository = RecordingAppBackupRepository()
        val useCase = ExportAppBackupUseCase(
            appBackupRepository = backupRepository,
            accessibilityPreferencesRepository = FakeAccessibilityPreferencesRepository(preferences),
            activityDefinitionRepository = FakeActivityDefinitionRepository(definitions),
            timesheetRepository = FakeTimesheetRepository(entries),
            nowProvider = { LocalDateTime(2026, 7, 1, 10, 15, 30) },
        )

        val document = useCase()

        assertEquals("TLInCompose_backup_2026-07-01_10-15-30.json", document.fileName)
        assertEquals(
            AppBackupData(
                preferences = preferences,
                activityDefinitions = definitions,
                timesheetEntries = entries,
            ),
            backupRepository.exportedData,
        )
    }

    private class RecordingAppBackupRepository : AppBackupRepository {
        var exportedData: AppBackupData? = null

        override suspend fun exportBackup(data: AppBackupData, fileName: String): ExportDocument {
            exportedData = data
            return ExportDocument(fileName, "application/json", byteArrayOf(1, 2, 3))
        }

        override suspend fun readBackup(bytes: ByteArray) = error("Not used in export test")
    }

    private class FakeAccessibilityPreferencesRepository(
        private val preferences: AccessibilityPreferences,
    ) : AccessibilityPreferencesRepository {
        override suspend fun loadPreferences(): AccessibilityPreferences = preferences

        override suspend fun savePreferences(preferences: AccessibilityPreferences) = Unit
    }

    private class FakeActivityDefinitionRepository(
        private val definitions: List<ActivityDefinition>,
    ) : ActivityDefinitionRepository {
        override suspend fun loadAll(): List<ActivityDefinition> = definitions

        override suspend fun upsert(definition: ActivityDefinition, previousExtCode: String?) = Unit

        override suspend fun delete(extCode: String) = Unit

        override suspend fun replaceAll(definitions: List<ActivityDefinition>) = Unit
    }

    private class FakeTimesheetRepository(
        private val entries: List<DailyEntry>,
    ) : TimesheetRepository {
        override suspend fun loadMonth(month: com.tlincompose.domain.model.CalendarMonth) = emptyMap<LocalDate, DailyEntry>()

        override suspend fun loadRange(range: com.tlincompose.domain.model.DateRange) = emptyMap<LocalDate, DailyEntry>()

        override suspend fun loadAll(): List<DailyEntry> = entries

        override suspend fun saveEntry(entry: DailyEntry) = Unit

        override suspend fun deleteEntry(date: LocalDate) = Unit

        override suspend fun replaceAll(entries: List<DailyEntry>) = Unit

        override suspend fun syncActivitiesWithDefinition(
            previousExtCode: String,
            definition: ActivityDefinition,
        ): Int = 0
    }
}
