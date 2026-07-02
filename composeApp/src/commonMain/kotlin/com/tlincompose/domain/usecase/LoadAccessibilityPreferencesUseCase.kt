package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository

class LoadAccessibilityPreferencesUseCase(
    private val repository: AccessibilityPreferencesRepository,
) {
    suspend operator fun invoke(): AccessibilityPreferences = repository.loadPreferences()
}
