package com.tlincompose.domain.model

enum class AccessibilityTextSize {
    STANDARD,
    LARGE,
    EXTRA_LARGE,
}

enum class AppLanguage {
    ENGLISH,
    ITALIAN,
    GERMAN,
    FRENCH,
    SPANISH,
}

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

enum class PdfExportStyle {
    RETRO,
    SIMPLE_TABLE,
    COMPACT_LIST,
    DETAIL_BLOCKS,
}

data class AccessibilityPreferences(
    val textSize: AccessibilityTextSize = AccessibilityTextSize.STANDARD,
    val highContrast: Boolean = false,
    val comfortableSpacing: Boolean = true,
    val focusMode: Boolean = false,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val standardWorkdayMinutes: Int = DefaultWorkdayMinutes,
    val brandingLogoBase64: String? = null,
    val pdfExportStyle: PdfExportStyle = PdfExportStyle.RETRO,
)
