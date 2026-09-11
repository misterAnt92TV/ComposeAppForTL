package com.tlincompose.data.local

import kotlinx.serialization.Serializable

@Serializable
data class AppConfigurationStore(
    val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
    val preferences: AccessibilityPreferencesStore = AccessibilityPreferencesStore(),
    val activityDefinitions: List<ActivityDefinitionEntity> = emptyList(),
) {
    companion object { const val CURRENT_SCHEMA_VERSION = 1 }
}
