package com.tlincompose.domain.model

data class AppBackupData(
    val preferences: AccessibilityPreferences,
    val activityDefinitions: List<ActivityDefinition>,
    val timesheetEntries: List<DailyEntry>,
)

sealed interface AppBackupImportError {
    data object InvalidJson : AppBackupImportError
    data object UnsupportedVersion : AppBackupImportError
    data object EmptyContent : AppBackupImportError
    data object PersistenceFailure : AppBackupImportError
}

sealed interface AppBackupReadResult {
    data class Success(val data: AppBackupData) : AppBackupReadResult
    data class Failure(val error: AppBackupImportError) : AppBackupReadResult
}

sealed interface AppBackupImportResult {
    data object Success : AppBackupImportResult
    data class Failure(val error: AppBackupImportError) : AppBackupImportResult
}
