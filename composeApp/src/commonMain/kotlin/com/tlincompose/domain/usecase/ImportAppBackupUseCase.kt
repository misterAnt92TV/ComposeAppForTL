package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.AppBackupImportError
import com.tlincompose.domain.model.AppBackupImportResult
import com.tlincompose.domain.model.AppBackupReadResult
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import com.tlincompose.domain.repository.AppBackupRepository
import com.tlincompose.domain.repository.TimesheetRepository

class ImportAppBackupUseCase(
    private val appBackupRepository: AppBackupRepository,
    private val accessibilityPreferencesRepository: AccessibilityPreferencesRepository,
    private val activityDefinitionRepository: ActivityDefinitionRepository,
    private val timesheetRepository: TimesheetRepository,
) {
    suspend operator fun invoke(bytes: ByteArray): AppBackupImportResult {
        return when (val readResult = appBackupRepository.readBackup(bytes)) {
            is AppBackupReadResult.Failure -> AppBackupImportResult.Failure(readResult.error)
            is AppBackupReadResult.Success -> {
                runCatching {
                    accessibilityPreferencesRepository.savePreferences(readResult.data.preferences)
                    activityDefinitionRepository.replaceAll(readResult.data.activityDefinitions)
                    timesheetRepository.replaceAll(readResult.data.timesheetEntries)
                }.fold(
                    onSuccess = { AppBackupImportResult.Success },
                    onFailure = { AppBackupImportResult.Failure(AppBackupImportError.PersistenceFailure) },
                )
            }
        }
    }
}
