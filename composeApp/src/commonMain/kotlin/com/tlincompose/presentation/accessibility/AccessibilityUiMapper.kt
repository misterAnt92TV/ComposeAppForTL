package com.tlincompose.presentation.accessibility

import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.model.AccessibilityTextSize
import com.tlincompose.domain.model.AppThemeMode
import com.tlincompose.domain.model.PdfExportStyle

fun AccessibilityPreferences.toUiState(): AccessibilitySettingsUiState =
    AccessibilitySettingsUiState(
        textScale = textSize.toUiState(),
        highContrast = highContrast,
        comfortableSpacing = comfortableSpacing,
        focusMode = focusMode,
        themeMode = themeMode.toUiState(),
        language = language,
        standardWorkdayMinutes = standardWorkdayMinutes,
        exportUserFullName = exportUserFullName,
        brandingLogoBase64 = brandingLogoBase64,
        pdfExportStyle = pdfExportStyle.toUiState(),
    )

fun AccessibilitySettingsUiState.toDomain(): AccessibilityPreferences =
    AccessibilityPreferences(
        textSize = textScale.toDomain(),
        highContrast = highContrast,
        comfortableSpacing = comfortableSpacing,
        focusMode = focusMode,
        themeMode = themeMode.toDomain(),
        language = language,
        standardWorkdayMinutes = standardWorkdayMinutes,
        exportUserFullName = exportUserFullName,
        brandingLogoBase64 = brandingLogoBase64,
        pdfExportStyle = pdfExportStyle.toDomain(),
    )

private fun AccessibilityTextSize.toUiState(): AccessibilityTextScaleUiState = when (this) {
    AccessibilityTextSize.STANDARD -> AccessibilityTextScaleUiState.STANDARD
    AccessibilityTextSize.LARGE -> AccessibilityTextScaleUiState.LARGE
    AccessibilityTextSize.EXTRA_LARGE -> AccessibilityTextScaleUiState.EXTRA_LARGE
}

private fun AccessibilityTextScaleUiState.toDomain(): AccessibilityTextSize = when (this) {
    AccessibilityTextScaleUiState.STANDARD -> AccessibilityTextSize.STANDARD
    AccessibilityTextScaleUiState.LARGE -> AccessibilityTextSize.LARGE
    AccessibilityTextScaleUiState.EXTRA_LARGE -> AccessibilityTextSize.EXTRA_LARGE
}

private fun AppThemeMode.toUiState(): ThemeModeUiState = when (this) {
    AppThemeMode.SYSTEM -> ThemeModeUiState.SYSTEM
    AppThemeMode.LIGHT -> ThemeModeUiState.LIGHT
    AppThemeMode.DARK -> ThemeModeUiState.DARK
}

private fun ThemeModeUiState.toDomain(): AppThemeMode = when (this) {
    ThemeModeUiState.SYSTEM -> AppThemeMode.SYSTEM
    ThemeModeUiState.LIGHT -> AppThemeMode.LIGHT
    ThemeModeUiState.DARK -> AppThemeMode.DARK
}

private fun PdfExportStyle.toUiState(): PdfExportStyleUiState = when (this) {
    PdfExportStyle.RETRO -> PdfExportStyleUiState.RETRO
    PdfExportStyle.SIMPLE_TABLE -> PdfExportStyleUiState.SIMPLE_TABLE
    PdfExportStyle.COMPACT_LIST -> PdfExportStyleUiState.COMPACT_LIST
    PdfExportStyle.DETAIL_BLOCKS -> PdfExportStyleUiState.DETAIL_BLOCKS
}

private fun PdfExportStyleUiState.toDomain(): PdfExportStyle = when (this) {
    PdfExportStyleUiState.RETRO -> PdfExportStyle.RETRO
    PdfExportStyleUiState.SIMPLE_TABLE -> PdfExportStyle.SIMPLE_TABLE
    PdfExportStyleUiState.COMPACT_LIST -> PdfExportStyle.COMPACT_LIST
    PdfExportStyleUiState.DETAIL_BLOCKS -> PdfExportStyle.DETAIL_BLOCKS
}
