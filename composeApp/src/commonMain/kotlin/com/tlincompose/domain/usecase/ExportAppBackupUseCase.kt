package com.tlincompose.domain.usecase

import com.tlincompose.core.AppTimeZone
import com.tlincompose.core.toTwoDigits
import com.tlincompose.domain.model.AppBackupData
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import com.tlincompose.domain.repository.AppBackupRepository
import com.tlincompose.domain.repository.TimesheetRepository
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ExportAppBackupUseCase(
    private val appBackupRepository: AppBackupRepository,
    private val accessibilityPreferencesRepository: AccessibilityPreferencesRepository,
    private val activityDefinitionRepository: ActivityDefinitionRepository,
    private val timesheetRepository: TimesheetRepository,
    private val nowProvider: () -> LocalDateTime = currentDateTimeProvider(),
) {
    suspend operator fun invoke(): ExportDocument {
        val backupData = AppBackupData(
            preferences = accessibilityPreferencesRepository.loadPreferences(),
            activityDefinitions = activityDefinitionRepository.loadAll(),
            timesheetEntries = timesheetRepository.loadAll(),
        )
        return appBackupRepository.exportBackup(
            data = backupData,
            fileName = buildFileName(nowProvider()),
        )
    }

    private fun buildFileName(dateTime: LocalDateTime): String = buildString {
        append("TLInCompose_backup_")
        append(dateTime.year)
        append("-")
        append((dateTime.month.ordinal + 1).toTwoDigits())
        append("-")
        append(dateTime.day.toTwoDigits())
        append("_")
        append(dateTime.hour.toTwoDigits())
        append("-")
        append(dateTime.minute.toTwoDigits())
        append("-")
        append(dateTime.second.toTwoDigits())
        append(".json")
    }

    companion object {
        @OptIn(ExperimentalTime::class)
        private fun currentDateTimeProvider(
            timeZone: TimeZone = AppTimeZone,
        ): () -> LocalDateTime = {
            Clock.System.now().toLocalDateTime(timeZone)
        }
    }
}
