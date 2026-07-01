package com.tlincompose.data.local

import kotlinx.serialization.Serializable

@Serializable
data class AppBackupStore(
    val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
    val preferences: AccessibilityPreferencesStore = AccessibilityPreferencesStore(),
    val activityDefinitions: List<ActivityDefinitionEntity> = emptyList(),
    val timesheetEntries: List<DailyEntryEntity> = emptyList(),
) {
    companion object {
        const val CURRENT_SCHEMA_VERSION: Int = 1
    }
}
