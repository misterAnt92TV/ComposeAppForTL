package com.tlincompose.data.mapper

import com.tlincompose.data.local.AppConfigurationStore
import com.tlincompose.data.local.AppLanguageEntity
import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.model.AppConfigurationData
import com.tlincompose.domain.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

class AppConfigurationMapperTest {
    @Test
    fun configurationRoundTripsPreferencesAndEmptyDefinitions() {
        val configuration = AppConfigurationData(
            preferences = AccessibilityPreferences(language = AppLanguage.ITALIAN, reduceMotion = true),
            activityDefinitions = emptyList(),
        )

        val decoded = configuration.toStore().toDomain()

        assertEquals(configuration, decoded)
        assertEquals(AppLanguageEntity.ITALIAN, configuration.toStore().preferences.language)
        assertEquals(AppConfigurationStore.CURRENT_SCHEMA_VERSION, configuration.toStore().schemaVersion)
    }
}
