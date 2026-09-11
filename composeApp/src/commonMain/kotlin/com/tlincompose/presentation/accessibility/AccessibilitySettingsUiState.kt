package com.tlincompose.presentation.accessibility

enum class AccessibilityTextScaleUiState(
    val fontScale: Float,
) {
    STANDARD(fontScale = 1f),
    LARGE(fontScale = 1.12f),
    EXTRA_LARGE(fontScale = 1.24f),
}

enum class ThemeModeUiState {
    SYSTEM,
    LIGHT,
    DARK,
}

enum class PdfExportStyleUiState {
    RETRO,
    SIMPLE_TABLE,
    COMPACT_LIST,
    DETAIL_BLOCKS,
}

data class AccessibilitySettingsUiState(
    val textScale: AccessibilityTextScaleUiState = AccessibilityTextScaleUiState.STANDARD,
    val highContrast: Boolean = false,
    val comfortableSpacing: Boolean = true,
    val focusMode: Boolean = false,
    val reduceMotion: Boolean = false,
    val themeMode: ThemeModeUiState = ThemeModeUiState.SYSTEM,
    val language: com.tlincompose.domain.model.AppLanguage = com.tlincompose.domain.model.AppLanguage.ENGLISH,
    val standardWorkdayMinutes: Int = com.tlincompose.domain.model.DefaultWorkdayMinutes,
    val exportUserFullName: String = "",
    val exportOfficeName: String = "",
    val exportEmployeeId: String = "",
    val exportPersonId: String = "",
    val brandingLogoBase64: String? = null,
    val pdfExportStyle: PdfExportStyleUiState = PdfExportStyleUiState.RETRO,
) {
    val hasBrandingLogo: Boolean
        get() = !brandingLogoBase64.isNullOrBlank()
}
