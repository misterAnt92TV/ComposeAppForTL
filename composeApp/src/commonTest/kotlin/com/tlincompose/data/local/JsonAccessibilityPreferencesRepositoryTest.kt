package com.tlincompose.data.local

import co.touchlab.kermit.Logger
import com.tlincompose.TestDispatcherProvider
import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.model.AccessibilityTextSize
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.AppThemeMode
import com.tlincompose.domain.model.PdfExportStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class JsonAccessibilityPreferencesRepositoryTest {
    @Test
    fun repositoryRoundTripKeepsPreferences() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = JsonAccessibilityPreferencesRepository(
            storageDriver = InMemoryStorageDriver(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("JsonAccessibilityPreferencesRepositoryTest"),
        )
        val preferences = AccessibilityPreferences(
            textSize = AccessibilityTextSize.EXTRA_LARGE,
            highContrast = true,
            comfortableSpacing = false,
            focusMode = true,
            themeMode = AppThemeMode.DARK,
            language = AppLanguage.SPANISH,
            standardWorkdayMinutes = 540,
            brandingLogoBase64 = "AQID",
            pdfExportStyle = PdfExportStyle.SIMPLE_TABLE,
        )

        repository.savePreferences(preferences)
        val loaded = repository.loadPreferences()

        assertEquals(preferences, loaded)
    }

    @Test
    fun repositoryReturnsDefaultsWhenFileIsMissing() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = JsonAccessibilityPreferencesRepository(
            storageDriver = InMemoryStorageDriver(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("JsonAccessibilityPreferencesRepositoryTest"),
        )

        val loaded = repository.loadPreferences()

        assertEquals(AccessibilityPreferences(), loaded)
    }
}
