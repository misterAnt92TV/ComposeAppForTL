package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.model.AccessibilityTextSize
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.AppThemeMode
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LoadAccessibilityPreferencesUseCaseTest {
    @Test
    fun loadUseCaseReturnsRepositoryPreferences() = runTest {
        val expected = AccessibilityPreferences(
            textSize = AccessibilityTextSize.EXTRA_LARGE,
            highContrast = true,
            comfortableSpacing = false,
            focusMode = true,
            themeMode = AppThemeMode.DARK,
            language = AppLanguage.GERMAN,
            standardWorkdayMinutes = 420,
        )
        val repository = FakeAccessibilityPreferencesRepository(
            preferencesToLoad = expected,
        )

        val loaded = LoadAccessibilityPreferencesUseCase(repository).invoke()

        assertEquals(expected, loaded)
    }
}

private class FakeAccessibilityPreferencesRepository(
    private val preferencesToLoad: AccessibilityPreferences = AccessibilityPreferences(),
) : AccessibilityPreferencesRepository {
    override suspend fun loadPreferences(): AccessibilityPreferences = preferencesToLoad

    override suspend fun savePreferences(preferences: AccessibilityPreferences) = Unit
}
