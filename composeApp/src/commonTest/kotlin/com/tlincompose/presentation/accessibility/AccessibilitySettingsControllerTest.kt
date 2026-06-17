package com.tlincompose.presentation.accessibility

import co.touchlab.kermit.Logger
import com.tlincompose.TestDispatcherProvider
import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.model.AccessibilityTextSize
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.AppThemeMode
import com.tlincompose.domain.model.PdfExportStyle
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import com.tlincompose.domain.usecase.LoadAccessibilityPreferencesUseCase
import com.tlincompose.domain.usecase.SaveAccessibilityPreferencesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AccessibilitySettingsControllerTest {
    @Test
    fun controllerLoadsInitialPreferencesAndPersistsUpdates() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = FakeAccessibilityPreferencesRepository(
            initialPreferences = AccessibilityPreferences(
                textSize = AccessibilityTextSize.LARGE,
                highContrast = true,
                comfortableSpacing = false,
                focusMode = false,
                themeMode = AppThemeMode.SYSTEM,
                language = AppLanguage.ITALIAN,
                standardWorkdayMinutes = 450,
                exportUserFullName = "Mario Rossi",
                brandingLogoBase64 = "AQID",
                pdfExportStyle = PdfExportStyle.SIMPLE_TABLE,
            ),
        )
        val controller = AccessibilitySettingsController(
            loadAccessibilityPreferences = LoadAccessibilityPreferencesUseCase(repository),
            saveAccessibilityPreferences = SaveAccessibilityPreferencesUseCase(repository),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("AccessibilitySettingsControllerTest"),
        )

        advanceUntilIdle()

        assertEquals(AccessibilityTextScaleUiState.LARGE, controller.uiState.textScale)
        assertEquals(true, controller.uiState.highContrast)
        assertEquals(false, controller.uiState.comfortableSpacing)
        assertEquals(ThemeModeUiState.SYSTEM, controller.uiState.themeMode)
        assertEquals(AppLanguage.ITALIAN, controller.uiState.language)
        assertEquals(450, controller.uiState.standardWorkdayMinutes)
        assertEquals("Mario Rossi", controller.uiState.exportUserFullName)
        assertEquals("AQID", controller.uiState.brandingLogoBase64)
        assertEquals(PdfExportStyleUiState.SIMPLE_TABLE, controller.uiState.pdfExportStyle)

        controller.updateThemeMode(ThemeModeUiState.DARK)
        controller.updateFocusMode(true)
        controller.updateLanguage(AppLanguage.GERMAN)
        controller.updateStandardWorkdayMinutes(510)
        controller.updateExportUserFullName("  Mario   Rossi  ")
        controller.updatePdfExportStyle(PdfExportStyleUiState.DETAIL_BLOCKS)
        advanceUntilIdle()

        assertEquals(true, controller.uiState.focusMode)
        assertEquals(ThemeModeUiState.DARK, controller.uiState.themeMode)
        assertEquals(AppLanguage.GERMAN, controller.uiState.language)
        assertEquals(
            AccessibilityPreferences(
                textSize = AccessibilityTextSize.LARGE,
                highContrast = true,
                comfortableSpacing = false,
                focusMode = true,
                themeMode = AppThemeMode.DARK,
                language = AppLanguage.GERMAN,
                standardWorkdayMinutes = 510,
                exportUserFullName = "Mario Rossi",
                brandingLogoBase64 = "AQID",
                pdfExportStyle = PdfExportStyle.DETAIL_BLOCKS,
            ),
            repository.savedPreferences,
        )

        controller.dispose()
    }

    @Test
    fun controllerStoresAndClearsBrandingLogo() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = FakeAccessibilityPreferencesRepository(
            initialPreferences = AccessibilityPreferences(),
        )
        val controller = AccessibilitySettingsController(
            loadAccessibilityPreferences = LoadAccessibilityPreferencesUseCase(repository),
            saveAccessibilityPreferences = SaveAccessibilityPreferencesUseCase(repository),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = Logger.withTag("AccessibilitySettingsControllerTest"),
        )

        advanceUntilIdle()

        controller.updateBrandingLogo(
            imageBytes = byteArrayOf(1, 2, 3),
            onLogoTooLarge = { error("logo should fit") },
            onInvalidLogo = { error("logo should be valid") },
        )
        advanceUntilIdle()

        assertEquals("AQID", controller.uiState.brandingLogoBase64)
        assertEquals("AQID", repository.savedPreferences?.brandingLogoBase64)

        controller.updatePdfExportStyle(PdfExportStyleUiState.COMPACT_LIST)
        advanceUntilIdle()

        assertEquals(PdfExportStyleUiState.COMPACT_LIST, controller.uiState.pdfExportStyle)
        assertEquals(PdfExportStyle.COMPACT_LIST, repository.savedPreferences?.pdfExportStyle)

        controller.updateExportUserFullName("  Anna   Bianchi ")
        advanceUntilIdle()

        assertEquals("Anna Bianchi", controller.uiState.exportUserFullName)
        assertEquals("Anna Bianchi", repository.savedPreferences?.exportUserFullName)

        controller.clearBrandingLogo()
        advanceUntilIdle()

        assertEquals(null, controller.uiState.brandingLogoBase64)
        assertEquals(null, repository.savedPreferences?.brandingLogoBase64)

        controller.dispose()
    }
}

private class FakeAccessibilityPreferencesRepository(
    private val initialPreferences: AccessibilityPreferences,
) : AccessibilityPreferencesRepository {
    var savedPreferences: AccessibilityPreferences? = null

    override suspend fun loadPreferences(): AccessibilityPreferences = initialPreferences

    override suspend fun savePreferences(preferences: AccessibilityPreferences) {
        savedPreferences = preferences
    }
}
