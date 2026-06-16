package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository

class SaveAccessibilityPreferencesUseCase(
    private val repository: AccessibilityPreferencesRepository,
) {
    suspend operator fun invoke(preferences: AccessibilityPreferences) =
        repository.savePreferences(preferences)
}
