package com.tlincompose.domain.model

import kotlinx.datetime.LocalDate

const val DefaultWorkdayMinutes: Int = 8 * 60

enum class ProjectIconPreset {
    WORK,
    CODE,
    PALETTE,
    BUILD,
    BUG_REPORT,
    FOLDER,
    SETTINGS,
    SCHOOL,
}

data class ActivityDefinition(
    val extCode: String,
    val type: EntryType,
    val title: String,
    val description: String,
    val defaultMinutes: Int = DefaultWorkdayMinutes,
    val createdDate: LocalDate,
    val updatedDate: LocalDate,
    val projectUrl: String? = null,
    val projectIconPreset: ProjectIconPreset? = null,
    val projectCustomIconBase64: String? = null,
)

data class ActivityDefinitionDraftInput(
    val extCode: String,
    val type: EntryType,
    val title: String,
    val description: String,
    val durationHoursText: String,
    val projectUrl: String,
    val projectIconPreset: ProjectIconPreset? = null,
    val projectCustomIconBase64: String? = null,
)

data class ValidatedActivityDefinitionDraft(
    val extCode: String,
    val type: EntryType,
    val title: String,
    val description: String,
    val defaultMinutes: Int,
    val projectUrl: String?,
    val projectIconPreset: ProjectIconPreset? = null,
    val projectCustomIconBase64: String? = null,
)

enum class ActivityDefinitionFieldError {
    BLANK_EXT_CODE,
    DUPLICATE_EXT_CODE,
    BLANK_TITLE,
    NON_POSITIVE_DURATION,
    INVALID_PROJECT_URL,
}

data class ActivityDefinitionDraftValidationResult(
    val validatedDraft: ValidatedActivityDefinitionDraft? = null,
    val extCodeError: ActivityDefinitionFieldError? = null,
    val titleError: ActivityDefinitionFieldError? = null,
    val durationError: ActivityDefinitionFieldError? = null,
    val projectUrlError: ActivityDefinitionFieldError? = null,
) {
    val hasErrors: Boolean
        get() = extCodeError != null || titleError != null || durationError != null || projectUrlError != null
}
