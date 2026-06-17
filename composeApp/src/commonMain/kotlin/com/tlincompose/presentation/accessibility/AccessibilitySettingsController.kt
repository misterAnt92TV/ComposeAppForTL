package com.tlincompose.presentation.accessibility

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.core.MaxBrandingLogoBytes
import com.tlincompose.core.normalizeUserFacingName
import com.tlincompose.domain.usecase.LoadAccessibilityPreferencesUseCase
import com.tlincompose.domain.usecase.SaveAccessibilityPreferencesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AccessibilitySettingsController(
    private val loadAccessibilityPreferences: LoadAccessibilityPreferencesUseCase,
    private val saveAccessibilityPreferences: SaveAccessibilityPreferencesUseCase,
    dispatcherProvider: DispatcherProvider,
    logger: Logger,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
    private val log = logger.withTag("AccessibilitySettingsController")
    private var hasLocalChanges = false

    var uiState by mutableStateOf(AccessibilitySettingsUiState())
        private set

    init {
        scope.launch {
            runCatching {
                loadAccessibilityPreferences()
            }.onSuccess { preferences ->
                if (hasLocalChanges) return@onSuccess
                uiState = preferences.toUiState()
                log.d { "Impostazioni accessibilita caricate correttamente." }
            }.onFailure {
                log.w(it) { "Impossibile caricare le impostazioni accessibilita. Uso i valori di default." }
            }
        }
    }

    fun dispose() {
        scope.cancel()
    }

    fun updateTextScale(textScale: AccessibilityTextScaleUiState) {
        updatePreferences { copy(textScale = textScale) }
    }

    fun updateHighContrast(enabled: Boolean) {
        updatePreferences { copy(highContrast = enabled) }
    }

    fun updateComfortableSpacing(enabled: Boolean) {
        updatePreferences { copy(comfortableSpacing = enabled) }
    }

    fun updateFocusMode(enabled: Boolean) {
        updatePreferences { copy(focusMode = enabled) }
    }

    fun updateThemeMode(themeMode: ThemeModeUiState) {
        updatePreferences { copy(themeMode = themeMode) }
    }

    fun updateLanguage(language: com.tlincompose.domain.model.AppLanguage) {
        updatePreferences { copy(language = language) }
    }

    fun updateStandardWorkdayMinutes(minutes: Int) {
        updatePreferences { copy(standardWorkdayMinutes = minutes) }
    }

    fun updateExportUserFullName(fullName: String) {
        updatePreferences { copy(exportUserFullName = normalizeUserFacingName(fullName)) }
    }

    fun updatePdfExportStyle(style: PdfExportStyleUiState) {
        updatePreferences { copy(pdfExportStyle = style) }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun updateBrandingLogo(
        imageBytes: ByteArray,
        onLogoTooLarge: () -> Unit,
        onInvalidLogo: () -> Unit,
    ) {
        if (imageBytes.isEmpty()) {
            onInvalidLogo()
            return
        }
        if (imageBytes.size > MaxBrandingLogoBytes) {
            onLogoTooLarge()
            return
        }
        updatePreferences {
            copy(brandingLogoBase64 = Base64.Default.encode(imageBytes))
        }
    }

    fun clearBrandingLogo() {
        updatePreferences { copy(brandingLogoBase64 = null) }
    }

    private fun updatePreferences(transform: AccessibilitySettingsUiState.() -> AccessibilitySettingsUiState) {
        val updatedState = transform(uiState)
        hasLocalChanges = true
        uiState = updatedState

        scope.launch {
            runCatching {
                saveAccessibilityPreferences(updatedState.toDomain())
            }.onFailure {
                log.e(it) { "Impossibile salvare le impostazioni accessibilita." }
            }
        }
    }
}
