package com.tlincompose.domain.usecase

import com.tlincompose.core.AppTimeZone
import com.tlincompose.core.toTwoDigits
import com.tlincompose.domain.model.AppConfigurationData
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import com.tlincompose.domain.repository.AppConfigurationRepository
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ExportAppConfigurationUseCase(
    private val repository: AppConfigurationRepository,
    private val preferencesRepository: AccessibilityPreferencesRepository,
    private val activityRepository: ActivityDefinitionRepository,
    private val nowProvider: () -> LocalDateTime = currentDateTimeProvider(),
) {
    suspend operator fun invoke(): ExportDocument = repository.exportConfiguration(
        AppConfigurationData(preferencesRepository.loadPreferences(), activityRepository.loadAll()),
        buildFileName(nowProvider()),
    )

    private fun buildFileName(dateTime: LocalDateTime) = buildString {
        append("TLInCompose_configuration_")
        append(dateTime.year).append('-').append((dateTime.month.ordinal + 1).toTwoDigits()).append('-').append(dateTime.day.toTwoDigits())
        append('_').append(dateTime.hour.toTwoDigits()).append('-').append(dateTime.minute.toTwoDigits()).append('-').append(dateTime.second.toTwoDigits()).append(".json")
    }

    companion object {
        @OptIn(ExperimentalTime::class)
        private fun currentDateTimeProvider(timeZone: TimeZone = AppTimeZone): () -> LocalDateTime = { Clock.System.now().toLocalDateTime(timeZone) }
    }
}
