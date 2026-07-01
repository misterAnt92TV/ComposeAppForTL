package com.tlincompose.data.local

import co.touchlab.kermit.Logger
import com.tlincompose.TestDispatcherProvider
import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.AppBackupData
import com.tlincompose.domain.model.AppBackupImportError
import com.tlincompose.domain.model.AppBackupReadResult
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.EntryType
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class JsonAppBackupRepositoryTest {
    @Test
    fun exportAndReadBackupPreserveAllData() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = JsonAppBackupRepository(
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("JsonAppBackupRepositoryTest"),
        )
        val backupData = AppBackupData(
            preferences = AccessibilityPreferences(
                exportUserFullName = "Mario Rossi",
                brandingLogoBase64 = "AQID",
            ),
            activityDefinitions = listOf(
                ActivityDefinition(
                    extCode = "EXT-001",
                    type = EntryType.PROJECT,
                    title = "Apollo",
                    description = "Cliente premium",
                    defaultMinutes = 480,
                    createdDate = LocalDate(2026, 5, 1),
                    updatedDate = LocalDate(2026, 5, 2),
                    projectCustomIconBase64 = "BAUG",
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
                            description = "Cliente premium",
                            minutes = 480,
                        ),
                    ),
                ),
            ),
        )

        val document = repository.exportBackup(backupData, "backup.json")
        val readResult = repository.readBackup(document.bytes)

        val success = assertIs<AppBackupReadResult.Success>(readResult)
        assertEquals("application/json", document.mimeType)
        assertEquals(backupData, success.data)
    }

    @Test
    fun readBackupReturnsInvalidJsonForMalformedContent() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = JsonAppBackupRepository(
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("JsonAppBackupRepositoryTest"),
        )

        val result = repository.readBackup("{oops".encodeToByteArray())

        assertEquals(
            AppBackupReadResult.Failure(AppBackupImportError.InvalidJson),
            result,
        )
    }

    @Test
    fun readBackupReturnsUnsupportedVersionWhenSchemaDoesNotMatch() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = JsonAppBackupRepository(
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("JsonAppBackupRepositoryTest"),
        )

        val result = repository.readBackup(
            """
            {"schemaVersion":99,"preferences":{},"activityDefinitions":[],"timesheetEntries":[]}
            """.trimIndent().encodeToByteArray(),
        )

        assertEquals(
            AppBackupReadResult.Failure(AppBackupImportError.UnsupportedVersion),
            result,
        )
    }
}
