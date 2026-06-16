
@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.tlincompose.core

import com.tlincompose.domain.model.AppLanguage

const val APP_VERSION = "v.1.12"

/**
 * Sealed class defining keys for all localizable strings.
 * Each key represents a unique text element with optional parameters.
 */
sealed class StringKey {
    object AppName : StringKey()
    object ActivityCatalogTitle : StringKey()
    data class ActivityCount(val count: Int) : StringKey()
    data class BrandingLogoDescription(val appName: String) : StringKey()
    data class ExportPeriodTitle(val period: String) : StringKey()
    object ExportSelectedMonthsDescription : StringKey()
    data class SelectedMonthsReadyMessage(val period: String) : StringKey()
    data class StandardWorkday(val hours: String) : StringKey()
    object TodayLabel : StringKey()
    object WeekStartsMonday : StringKey()

    // Android save/IO system messages
    object SaveCancelledMessage : StringKey()
    object InvalidDestinationMessage : StringKey()
    object UnableToOpenDestinationFile : StringKey()
    object SaveErrorMessage : StringKey()
    object UnableToReadSelectedImage : StringKey()
    data class FileSavedMessage(val fileName: String) : StringKey()
}

/**
 * Localized strings entrypoint backed by StringKey plus compatibility extensions.
 */
class AppStrings(val language: AppLanguage) {
    val appName: String
        get() = "TLInCompose"

    operator fun get(key: StringKey): String = when (key) {
        StringKey.AppName -> appName
        StringKey.ActivityCatalogTitle -> activityCatalogTitle
        is StringKey.ActivityCount -> activityCountPhrase(key.count)
        is StringKey.BrandingLogoDescription -> brandingLogoContentDescription(key.appName)
        is StringKey.ExportPeriodTitle -> exportPeriodTitle(key.period)
        StringKey.ExportSelectedMonthsDescription -> exportSelectedMonthsDescription
        is StringKey.SelectedMonthsReadyMessage -> selectedMonthsReadyMessage(key.period)
        is StringKey.StandardWorkday -> standardWorkday(key.hours)
        StringKey.TodayLabel -> todayLabel
        StringKey.WeekStartsMonday -> weekStartsMonday
        StringKey.SaveCancelledMessage -> saveCancelledMessage
        StringKey.InvalidDestinationMessage -> invalidDestinationMessage
        StringKey.UnableToOpenDestinationFile -> unableToOpenDestinationFile
        StringKey.SaveErrorMessage -> saveErrorMessage
        StringKey.UnableToReadSelectedImage -> unableToReadSelectedImage
        is StringKey.FileSavedMessage -> fileSavedMessage(key.fileName)
    }
}

/**
 * Factory function to create AppStrings for a given language
 */
fun appStrings(language: AppLanguage): AppStrings = AppStrings(language)
