package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.AppConfigurationError
import com.tlincompose.domain.model.AppConfigurationReadResult
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import com.tlincompose.domain.repository.AppConfigurationRepository

class ImportAppConfigurationUseCase(
    private val repository: AppConfigurationRepository,
    private val preferencesRepository: AccessibilityPreferencesRepository,
    private val activityRepository: ActivityDefinitionRepository,
) {
    suspend operator fun invoke(bytes: ByteArray): AppConfigurationError? {
        val data = when (val result = repository.readConfiguration(bytes)) {
            is AppConfigurationReadResult.Failure -> return result.error
            is AppConfigurationReadResult.Success -> result.data
        }
        val oldPreferences = preferencesRepository.loadPreferences()
        val oldDefinitions = activityRepository.loadAll()
        return runCatching {
            preferencesRepository.savePreferences(data.preferences)
            activityRepository.replaceAll(data.activityDefinitions)
        }.fold(
            onSuccess = { null },
            onFailure = {
                runCatching {
                    preferencesRepository.savePreferences(oldPreferences)
                    activityRepository.replaceAll(oldDefinitions)
                }
                AppConfigurationError.PersistenceFailure
            },
        )
    }
}
