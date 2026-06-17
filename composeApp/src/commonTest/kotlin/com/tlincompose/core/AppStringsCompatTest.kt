package com.tlincompose.core

import com.tlincompose.domain.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

class AppStringsCompatTest {
    @Test
    fun italianAndExportStringsUseUnicodeCharacters() {
        val strings = appStrings(AppLanguage.ITALIAN)

        assertEquals("Settimana da lunedì", strings.weekStartsMonday)
        assertEquals("Attività", strings.exportActivityLabel)
        assertEquals("Codice attività", strings.exportActivityCodeLabel)
    }

    @Test
    fun multiLanguageSamplesKeepAccentsAndDiacritics() {
        assertEquals("Février", appStrings(AppLanguage.FRENCH).monthNames[1])
        assertEquals("Mié", appStrings(AppLanguage.SPANISH).weekdayShortLabelsMondayFirst[2])
        assertEquals("März", appStrings(AppLanguage.GERMAN).monthNames[2])
    }
}
