package com.tlincompose.domain.model

data class AppConfigurationData(
    val preferences: AccessibilityPreferences,
    val activityDefinitions: List<ActivityDefinition>,
)

sealed interface AppConfigurationReadResult {
    data class Success(val data: AppConfigurationData) : AppConfigurationReadResult
    data class Failure(val error: AppConfigurationError) : AppConfigurationReadResult
}

sealed interface AppConfigurationError {
    data object InvalidJson : AppConfigurationError
    data object UnsupportedVersion : AppConfigurationError
    data object EmptyContent : AppConfigurationError
    data object PersistenceFailure : AppConfigurationError
}
