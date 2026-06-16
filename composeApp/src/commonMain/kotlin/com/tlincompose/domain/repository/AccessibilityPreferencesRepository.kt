package com.tlincompose.domain.repository

import com.tlincompose.domain.model.AccessibilityPreferences

interface AccessibilityPreferencesRepository {
    suspend fun loadPreferences(): AccessibilityPreferences
    suspend fun savePreferences(preferences: AccessibilityPreferences)
}
