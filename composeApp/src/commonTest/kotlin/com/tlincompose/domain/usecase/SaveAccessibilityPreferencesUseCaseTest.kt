package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.model.AccessibilityTextSize
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.AppThemeMode
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class SaveAccessibilityPreferencesUseCaseTest {
    @Test
    fun saveUseCaseDelegatesPreferencesToRepository() = runTest {
        val repository = RecordingAccessibilityPreferencesRepository()
        val preferences = AccessibilityPreferences(
            textSize = AccessibilityTextSize.LARGE,
            highContrast = true,
            comfortableSpacing = true,
            focusMode = false,
            themeMode = AppThemeMode.SYSTEM,
            language = AppLanguage.FRENCH,
            standardWorkdayMinutes = 390,
        )

        SaveAccessibilityPreferencesUseCase(repository).invoke(preferences)

        assertEquals(preferences, repository.savedPreferences)
    }
}

private class RecordingAccessibilityPreferencesRepository : AccessibilityPreferencesRepository {
    var savedPreferences: AccessibilityPreferences? = null

    override suspend fun loadPreferences(): AccessibilityPreferences = AccessibilityPreferences()

    override suspend fun savePreferences(preferences: AccessibilityPreferences) {
        savedPreferences = preferences
    }
}
