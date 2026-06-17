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
import kotlin.test.Test
import kotlin.test.assertEquals

class AccessibilityPreferencesMapperTest {
    @Test
    fun storeMapsToDomain() {
        val store = AccessibilityPreferencesStore(
            textSize = AccessibilityTextSizeEntity.EXTRA_LARGE,
            highContrast = true,
            comfortableSpacing = false,
            focusMode = true,
            themeMode = AppThemeModeEntity.DARK,
            language = AppLanguageEntity.GERMAN,
            standardWorkdayMinutes = 510,
            exportUserFullName = "Mario Rossi",
            brandingLogoBase64 = "AQID",
            pdfExportStyle = PdfExportStyleEntity.COMPACT_LIST,
        )

        val domain = store.toDomain()

        assertEquals(
            AccessibilityPreferences(
                textSize = AccessibilityTextSize.EXTRA_LARGE,
                highContrast = true,
                comfortableSpacing = false,
                focusMode = true,
                themeMode = AppThemeMode.DARK,
                language = AppLanguage.GERMAN,
                standardWorkdayMinutes = 510,
                exportUserFullName = "Mario Rossi",
                brandingLogoBase64 = "AQID",
                pdfExportStyle = PdfExportStyle.COMPACT_LIST,
            ),
            domain,
        )
    }

    @Test
    fun domainMapsToStore() {
        val preferences = AccessibilityPreferences(
            textSize = AccessibilityTextSize.LARGE,
            highContrast = false,
            comfortableSpacing = true,
            focusMode = true,
            themeMode = AppThemeMode.LIGHT,
            language = AppLanguage.FRENCH,
            standardWorkdayMinutes = 450,
            exportUserFullName = "Anna Bianchi",
            brandingLogoBase64 = "AQID",
            pdfExportStyle = PdfExportStyle.DETAIL_BLOCKS,
        )

        val store = preferences.toStore()

        assertEquals(
            AccessibilityPreferencesStore(
                textSize = AccessibilityTextSizeEntity.LARGE,
                highContrast = false,
                comfortableSpacing = true,
                focusMode = true,
                themeMode = AppThemeModeEntity.LIGHT,
                language = AppLanguageEntity.FRENCH,
                standardWorkdayMinutes = 450,
                exportUserFullName = "Anna Bianchi",
                brandingLogoBase64 = "AQID",
                pdfExportStyle = PdfExportStyleEntity.DETAIL_BLOCKS,
            ),
            store,
        )
    }
}
