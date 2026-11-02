package com.tlincompose.data.local

import kotlinx.serialization.Serializable

@Serializable
enum class AccessibilityTextSizeEntity {
    STANDARD,
    LARGE,
    EXTRA_LARGE,
}

@Serializable
enum class AppThemeModeEntity {
    SYSTEM,
    LIGHT,
    DARK,
}

@Serializable
enum class AppLanguageEntity {
    ENGLISH,
    ITALIAN,
    GERMAN,
    FRENCH,
    SPANISH,
}

@Serializable
enum class PdfExportStyleEntity {
    RETRO,
    SIMPLE_TABLE,
    COMPACT_LIST,
    DETAIL_BLOCKS,
}

@Serializable
data class AccessibilityPreferencesStore(
    val textSize: AccessibilityTextSizeEntity = AccessibilityTextSizeEntity.STANDARD,
    val highContrast: Boolean = false,
    val comfortableSpacing: Boolean = true,
    val focusMode: Boolean = false,
    val reduceMotion: Boolean = false,
    val themeMode: AppThemeModeEntity = AppThemeModeEntity.SYSTEM,
    val language: AppLanguageEntity = AppLanguageEntity.ENGLISH,
    val standardWorkdayMinutes: Int = com.tlincompose.domain.model.DefaultWorkdayMinutes,
    val exportUserFullName: String = "",
    val exportOfficeName: String = "",
    val exportEmployeeId: String = "",
    val exportPersonId: String = "",
    val brandingLogoBase64: String? = null,
    val pdfExportStyle: PdfExportStyleEntity = PdfExportStyleEntity.RETRO,
)
