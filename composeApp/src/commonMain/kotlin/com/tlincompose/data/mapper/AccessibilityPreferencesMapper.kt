package com.tlincompose.data.mapper

import com.tlincompose.data.local.AccessibilityPreferencesStore
import com.tlincompose.data.local.AccessibilityTextSizeEntity
import com.tlincompose.data.local.AppLanguageEntity
import com.tlincompose.data.local.AppThemeModeEntity
import com.tlincompose.data.local.PdfExportStyleEntity
import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.model.AccessibilityTextSize
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.AppThemeMode
import com.tlincompose.domain.model.PdfExportStyle

fun AccessibilityPreferencesStore.toDomain(): AccessibilityPreferences =
    AccessibilityPreferences(
        textSize = textSize.toDomain(),
        highContrast = highContrast,
        comfortableSpacing = comfortableSpacing,
        focusMode = focusMode,
        themeMode = themeMode.toDomain(),
        language = language.toDomain(),
        standardWorkdayMinutes = standardWorkdayMinutes,
        exportUserFullName = exportUserFullName,
        brandingLogoBase64 = brandingLogoBase64,
        pdfExportStyle = pdfExportStyle.toDomain(),
    )

fun AccessibilityPreferences.toStore(): AccessibilityPreferencesStore =
    AccessibilityPreferencesStore(
        textSize = textSize.toEntity(),
        highContrast = highContrast,
        comfortableSpacing = comfortableSpacing,
        focusMode = focusMode,
        themeMode = themeMode.toEntity(),
        language = language.toEntity(),
        standardWorkdayMinutes = standardWorkdayMinutes,
        exportUserFullName = exportUserFullName,
        brandingLogoBase64 = brandingLogoBase64,
        pdfExportStyle = pdfExportStyle.toEntity(),
    )

private fun AccessibilityTextSizeEntity.toDomain(): AccessibilityTextSize = when (this) {
    AccessibilityTextSizeEntity.STANDARD -> AccessibilityTextSize.STANDARD
    AccessibilityTextSizeEntity.LARGE -> AccessibilityTextSize.LARGE
    AccessibilityTextSizeEntity.EXTRA_LARGE -> AccessibilityTextSize.EXTRA_LARGE
}

private fun AccessibilityTextSize.toEntity(): AccessibilityTextSizeEntity = when (this) {
    AccessibilityTextSize.STANDARD -> AccessibilityTextSizeEntity.STANDARD
    AccessibilityTextSize.LARGE -> AccessibilityTextSizeEntity.LARGE
    AccessibilityTextSize.EXTRA_LARGE -> AccessibilityTextSizeEntity.EXTRA_LARGE
}

private fun AppThemeModeEntity.toDomain(): AppThemeMode = when (this) {
    AppThemeModeEntity.SYSTEM -> AppThemeMode.SYSTEM
    AppThemeModeEntity.LIGHT -> AppThemeMode.LIGHT
    AppThemeModeEntity.DARK -> AppThemeMode.DARK
}

private fun AppThemeMode.toEntity(): AppThemeModeEntity = when (this) {
    AppThemeMode.SYSTEM -> AppThemeModeEntity.SYSTEM
    AppThemeMode.LIGHT -> AppThemeModeEntity.LIGHT
    AppThemeMode.DARK -> AppThemeModeEntity.DARK
}

private fun AppLanguageEntity.toDomain(): AppLanguage = when (this) {
    AppLanguageEntity.ENGLISH -> AppLanguage.ENGLISH
    AppLanguageEntity.ITALIAN -> AppLanguage.ITALIAN
    AppLanguageEntity.GERMAN -> AppLanguage.GERMAN
    AppLanguageEntity.FRENCH -> AppLanguage.FRENCH
    AppLanguageEntity.SPANISH -> AppLanguage.SPANISH
}

private fun AppLanguage.toEntity(): AppLanguageEntity = when (this) {
    AppLanguage.ENGLISH -> AppLanguageEntity.ENGLISH
    AppLanguage.ITALIAN -> AppLanguageEntity.ITALIAN
    AppLanguage.GERMAN -> AppLanguageEntity.GERMAN
    AppLanguage.FRENCH -> AppLanguageEntity.FRENCH
    AppLanguage.SPANISH -> AppLanguageEntity.SPANISH
}

private fun PdfExportStyleEntity.toDomain(): PdfExportStyle = when (this) {
    PdfExportStyleEntity.RETRO -> PdfExportStyle.RETRO
    PdfExportStyleEntity.SIMPLE_TABLE -> PdfExportStyle.SIMPLE_TABLE
    PdfExportStyleEntity.COMPACT_LIST -> PdfExportStyle.COMPACT_LIST
    PdfExportStyleEntity.DETAIL_BLOCKS -> PdfExportStyle.DETAIL_BLOCKS
}

private fun PdfExportStyle.toEntity(): PdfExportStyleEntity = when (this) {
    PdfExportStyle.RETRO -> PdfExportStyleEntity.RETRO
    PdfExportStyle.SIMPLE_TABLE -> PdfExportStyleEntity.SIMPLE_TABLE
    PdfExportStyle.COMPACT_LIST -> PdfExportStyleEntity.COMPACT_LIST
    PdfExportStyle.DETAIL_BLOCKS -> PdfExportStyleEntity.DETAIL_BLOCKS
}
