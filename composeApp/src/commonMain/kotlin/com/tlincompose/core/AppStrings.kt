
@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.tlincompose.core

import com.tlincompose.domain.model.AppLanguage

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

    // --- Sezione "Librerie di terze parti" ---
    object ThirdPartyLibrariesTitle : StringKey()
    object ThirdPartyLibrariesDescription : StringKey()
    data class ThirdPartyLibraryVersion(val version: String) : StringKey()
    object ThirdPartyLibraryOpenSite : StringKey()
    data class ThirdPartyLibraryContentDescription(val name: String) : StringKey()
    // --- Fine sezione ---

    object DeveloperSectionTitle : StringKey()
    object DeveloperSectionDescription : StringKey()
    object DeveloperGithubLabel : StringKey()
    object DeveloperEmailLabel : StringKey()
    object DeveloperAvailabilityMessage : StringKey()
    object DeveloperOpenLabel : StringKey()
    object DeveloperCopyLabel : StringKey()

    // Android save/IO system messages
    object SaveCancelledMessage : StringKey()
    object InvalidDestinationMessage : StringKey()
    object UnableToOpenDestinationFile : StringKey()
    object SaveErrorMessage : StringKey()
    object UnableToReadSelectedImage : StringKey()
    object UnableToReadSelectedFile : StringKey()
    data class FileSavedMessage(val fileName: String) : StringKey()
    object BackupJsonTitle : StringKey()
    object BackupJsonDescription : StringKey()
    object BackupJsonWarning : StringKey()
    object BackupJsonExportLabel : StringKey()
    object BackupJsonImportLabel : StringKey()
    object BackupJsonExportingLabel : StringKey()
    object BackupImportingLabel : StringKey()
    object BackupImportConfirmTitle : StringKey()
    data class BackupImportConfirmBody(val fileName: String) : StringKey()
    object BackupImportReplaceLabel : StringKey()
    object BackupImportSuccessMessage : StringKey()
    object BackupExportErrorMessage : StringKey()
    object BackupImportInvalidJsonMessage : StringKey()
    object BackupImportUnsupportedVersionMessage : StringKey()
    object BackupImportEmptyContentMessage : StringKey()
    object BackupImportPersistenceErrorMessage : StringKey()
    object ChooseBackupFileDialogTitle : StringKey()
    object JsonFilesLabel : StringKey()
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
        StringKey.UnableToReadSelectedFile -> unableToReadSelectedFile
        is StringKey.FileSavedMessage -> fileSavedMessage(key.fileName)
        StringKey.BackupJsonTitle -> backupJsonTitle
        StringKey.BackupJsonDescription -> backupJsonDescription
        StringKey.BackupJsonWarning -> backupJsonWarning
        StringKey.BackupJsonExportLabel -> backupJsonExportLabel
        StringKey.BackupJsonImportLabel -> backupJsonImportLabel
        StringKey.BackupJsonExportingLabel -> backupJsonExportingLabel
        StringKey.BackupImportingLabel -> backupImportingLabel
        StringKey.BackupImportConfirmTitle -> backupImportConfirmTitle
        is StringKey.BackupImportConfirmBody -> backupImportConfirmBody(key.fileName)
        StringKey.BackupImportReplaceLabel -> backupImportReplaceLabel
        StringKey.BackupImportSuccessMessage -> backupImportSuccessMessage
        StringKey.BackupExportErrorMessage -> backupExportErrorMessage
        StringKey.BackupImportInvalidJsonMessage -> backupImportInvalidJsonMessage
        StringKey.BackupImportUnsupportedVersionMessage -> backupImportUnsupportedVersionMessage
        StringKey.BackupImportEmptyContentMessage -> backupImportEmptyContentMessage
        StringKey.BackupImportPersistenceErrorMessage -> backupImportPersistenceErrorMessage
        StringKey.ChooseBackupFileDialogTitle -> chooseBackupFileDialogTitle
        StringKey.JsonFilesLabel -> jsonFilesLabel

        // --- Sezione "Librerie di terze parti" ---
        StringKey.ThirdPartyLibrariesTitle -> "Librerie di terze parti utilizzate"
        StringKey.ThirdPartyLibrariesDescription -> "Di seguito sono elencate le principali librerie di terze parti utilizzate da TLInCompose."
        is StringKey.ThirdPartyLibraryVersion -> "Versione: ${key.version}"
        StringKey.ThirdPartyLibraryOpenSite -> "Apri sito"
        is StringKey.ThirdPartyLibraryContentDescription -> "Pagina ufficiale di ${key.name}"
        // --- Fine sezione ---
        StringKey.DeveloperSectionTitle -> "Sviluppatore"
        StringKey.DeveloperSectionDescription -> "Informazioni di contatto e disponibilita per migliorare l'app."
        StringKey.DeveloperGithubLabel -> "GitHub"
        StringKey.DeveloperEmailLabel -> "Email"
        StringKey.DeveloperAvailabilityMessage -> "Sono aperto a suggerimenti e a nuovi sviluppi per migliorare l'app."
        StringKey.DeveloperOpenLabel -> "Apri"
        StringKey.DeveloperCopyLabel -> "Copia"
    }
}

/**
 * Factory function to create AppStrings for a given language
 */
fun appStrings(language: AppLanguage): AppStrings = AppStrings(language)
