package com.tlincompose.presentation.accessibility

import co.touchlab.kermit.Logger
import com.tlincompose.TestDispatcherProvider
import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.model.AppBackupData
import com.tlincompose.domain.model.AppBackupImportError
import com.tlincompose.domain.model.AppBackupImportResult
import com.tlincompose.domain.model.AppBackupReadResult
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import com.tlincompose.domain.repository.AppBackupRepository
import com.tlincompose.domain.repository.TimesheetRepository
import com.tlincompose.domain.usecase.ExportAppBackupUseCase
import com.tlincompose.domain.usecase.ImportAppBackupUseCase
import com.tlincompose.presentation.JsonFileSelection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsBackupControllerTest {
    @Test
    fun prepareImportShowsConfirmationAndDismissClearsIt() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val controller = SettingsBackupController(
            exportAppBackup = createExportUseCase(),
            importAppBackup = createImportUseCase(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("SettingsBackupControllerTest"),
        )

        controller.prepareImport(JsonFileSelection("backup.json", byteArrayOf(1, 2, 3)))

        assertTrue(controller.uiState.isImportConfirmationVisible)
        assertEquals("backup.json", controller.uiState.pendingImportFileName)

        controller.dismissImportConfirmation()

        assertFalse(controller.uiState.isImportConfirmationVisible)
        assertNull(controller.uiState.pendingImportFileName)
        controller.dispose()
    }

    @Test
    fun confirmImportCallsSuccessCallbackAndClearsBusyState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val backupRepository = RecordingAppBackupRepository(
            readResult = AppBackupReadResult.Success(AppBackupData(AccessibilityPreferences(), emptyList(), emptyList())),
        )
        val controller = SettingsBackupController(
            exportAppBackup = createExportUseCase(),
            importAppBackup = createImportUseCase(backupRepository),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("SettingsBackupControllerTest"),
        )
        var successCalled = false

        controller.prepareImport(JsonFileSelection("backup.json", byteArrayOf(9, 9, 9)))
        controller.confirmImport(
            onSuccess = { successCalled = true },
            onFailure = { error("import should succeed") },
        )
        advanceUntilIdle()

        assertTrue(successCalled)
        assertEquals(byteArrayOf(9, 9, 9).toList(), backupRepository.readBytes?.toList())
        assertFalse(controller.uiState.isBusy)
        assertFalse(controller.uiState.isImportConfirmationVisible)
        controller.dispose()
    }

    @Test
    fun confirmImportMapsDomainErrorsToUiReasons() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val controller = SettingsBackupController(
            exportAppBackup = createExportUseCase(),
            importAppBackup = createImportUseCase(
                RecordingAppBackupRepository(
                    readResult = AppBackupReadResult.Failure(AppBackupImportError.UnsupportedVersion),
                ),
            ),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("SettingsBackupControllerTest"),
        )
        var failureReason: SettingsBackupFailureReason? = null

        controller.prepareImport(JsonFileSelection("backup.json", byteArrayOf(7)))
        controller.confirmImport(
            onSuccess = { error("import should fail") },
            onFailure = { failureReason = it },
        )
        advanceUntilIdle()

        assertEquals(SettingsBackupFailureReason.UNSUPPORTED_VERSION, failureReason)
        assertFalse(controller.uiState.isBusy)
        controller.dispose()
    }

    @Test
    fun exportBackupReturnsDocumentAndResetsState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val backupRepository = RecordingAppBackupRepository(
            document = ExportDocument("backup.json", "application/json", byteArrayOf(1, 2, 3)),
        )
        val controller = SettingsBackupController(
            exportAppBackup = createExportUseCase(backupRepository),
            importAppBackup = createImportUseCase(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("SettingsBackupControllerTest"),
        )
        var exportedDocument: ExportDocument? = null

        controller.exportBackup(
            onSuccess = { exportedDocument = it },
            onFailure = { error("export should succeed") },
        )
        advanceUntilIdle()

        assertEquals("backup.json", exportedDocument?.fileName)
        assertFalse(controller.uiState.isBusy)
        controller.dispose()
    }

    private fun createExportUseCase(
        backupRepository: RecordingAppBackupRepository = RecordingAppBackupRepository(),
    ): ExportAppBackupUseCase = ExportAppBackupUseCase(
        appBackupRepository = backupRepository,
        accessibilityPreferencesRepository = FakeAccessibilityPreferencesRepository(),
        activityDefinitionRepository = FakeActivityDefinitionRepository(),
        timesheetRepository = FakeTimesheetRepository(),
        nowProvider = { kotlinx.datetime.LocalDateTime(2026, 7, 1, 10, 15, 30) },
    )

    private fun createImportUseCase(
        backupRepository: RecordingAppBackupRepository = RecordingAppBackupRepository(
            readResult = AppBackupReadResult.Success(AppBackupData(AccessibilityPreferences(), emptyList(), emptyList())),
        ),
    ): ImportAppBackupUseCase = ImportAppBackupUseCase(
        appBackupRepository = backupRepository,
        accessibilityPreferencesRepository = FakeAccessibilityPreferencesRepository(),
        activityDefinitionRepository = FakeActivityDefinitionRepository(),
        timesheetRepository = FakeTimesheetRepository(),
    )

    private class RecordingAppBackupRepository(
        private val document: ExportDocument = ExportDocument("backup.json", "application/json", byteArrayOf()),
        private val readResult: AppBackupReadResult = AppBackupReadResult.Success(
            AppBackupData(AccessibilityPreferences(), emptyList(), emptyList()),
        ),
    ) : AppBackupRepository {
        var readBytes: ByteArray? = null

        override suspend fun exportBackup(data: AppBackupData, fileName: String): ExportDocument = document

        override suspend fun readBackup(bytes: ByteArray): AppBackupReadResult {
            readBytes = bytes
            return readResult
        }
    }

    private class FakeAccessibilityPreferencesRepository : AccessibilityPreferencesRepository {
        override suspend fun loadPreferences(): AccessibilityPreferences = AccessibilityPreferences()

        override suspend fun savePreferences(preferences: AccessibilityPreferences) = Unit
    }

    private class FakeActivityDefinitionRepository : ActivityDefinitionRepository {
        override suspend fun loadAll() = emptyList<com.tlincompose.domain.model.ActivityDefinition>()

        override suspend fun upsert(
            definition: com.tlincompose.domain.model.ActivityDefinition,
            previousExtCode: String?,
        ) = Unit

        override suspend fun delete(extCode: String) = Unit

        override suspend fun replaceAll(definitions: List<com.tlincompose.domain.model.ActivityDefinition>) = Unit
    }

    private class FakeTimesheetRepository : TimesheetRepository {
        override suspend fun loadMonth(month: com.tlincompose.domain.model.CalendarMonth) = emptyMap<kotlinx.datetime.LocalDate, com.tlincompose.domain.model.DailyEntry>()

        override suspend fun loadRange(range: com.tlincompose.domain.model.DateRange) = emptyMap<kotlinx.datetime.LocalDate, com.tlincompose.domain.model.DailyEntry>()

        override suspend fun loadAll() = emptyList<com.tlincompose.domain.model.DailyEntry>()

        override suspend fun saveEntry(entry: com.tlincompose.domain.model.DailyEntry) = Unit

        override suspend fun deleteEntry(date: kotlinx.datetime.LocalDate) = Unit

        override suspend fun replaceAll(entries: List<com.tlincompose.domain.model.DailyEntry>) = Unit

        override suspend fun syncActivitiesWithDefinition(
            previousExtCode: String,
            definition: com.tlincompose.domain.model.ActivityDefinition,
        ): Int = 0
    }
}
