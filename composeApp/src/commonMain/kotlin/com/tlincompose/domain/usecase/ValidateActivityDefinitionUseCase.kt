package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.ActivityDefinitionDraftInput
import com.tlincompose.domain.model.ActivityDefinitionDraftValidationResult
import com.tlincompose.domain.model.ActivityDefinitionFieldError
import com.tlincompose.domain.model.ValidatedActivityDefinitionDraft
import kotlin.math.roundToInt

class ValidateActivityDefinitionUseCase {
    operator fun invoke(
        input: ActivityDefinitionDraftInput,
        existingExtCodes: Set<String> = emptySet(),
        originalExtCode: String? = null,
    ): ActivityDefinitionDraftValidationResult {
        val normalizedExtCode = input.extCode.trim().uppercase()
        val normalizedTitle = input.title.trim()
        val normalizedDuration = input.durationHoursText.trim().replace(',', '.')
        val normalizedUrl = input.projectUrl.trim()
        val normalizedIconPreset = input.projectIconPreset.takeIf { input.type == com.tlincompose.domain.model.EntryType.PROJECT }
        val normalizedCustomIconBase64 = input.projectCustomIconBase64
            ?.trim()
            ?.ifBlank { null }
            ?.takeIf { input.type == com.tlincompose.domain.model.EntryType.PROJECT && normalizedIconPreset == null }
        val normalizedOriginalExtCode = originalExtCode?.trim()?.uppercase()

        val extCodeError = when {
            normalizedExtCode.isBlank() -> ActivityDefinitionFieldError.BLANK_EXT_CODE
            normalizedExtCode != normalizedOriginalExtCode && normalizedExtCode in existingExtCodes.map(String::uppercase).toSet() ->
                ActivityDefinitionFieldError.DUPLICATE_EXT_CODE
            else -> null
        }

        val titleError = if (normalizedTitle.isBlank()) {
            ActivityDefinitionFieldError.BLANK_TITLE
        } else {
            null
        }

        val hours = normalizedDuration.toDoubleOrNull()
        val durationError = when {
            hours == null || hours <= 0.0 -> ActivityDefinitionFieldError.NON_POSITIVE_DURATION
            else -> null
        }

        val projectUrlError = if (normalizedUrl.isNotEmpty() &&
            !normalizedUrl.startsWith("http://") &&
            !normalizedUrl.startsWith("https://")
        ) {
            ActivityDefinitionFieldError.INVALID_PROJECT_URL
        } else {
            null
        }

        if (extCodeError != null || titleError != null || durationError != null || projectUrlError != null) {
            return ActivityDefinitionDraftValidationResult(
                extCodeError = extCodeError,
                titleError = titleError,
                durationError = durationError,
                projectUrlError = projectUrlError,
            )
        }

        return ActivityDefinitionDraftValidationResult(
            validatedDraft = ValidatedActivityDefinitionDraft(
                extCode = normalizedExtCode,
                type = input.type,
                title = normalizedTitle,
                description = input.description.trim(),
                defaultMinutes = (hours!! * 60.0).roundToInt(),
                projectUrl = normalizedUrl.ifBlank { null },
                projectIconPreset = normalizedIconPreset,
                projectCustomIconBase64 = normalizedCustomIconBase64,
            ),
        )
    }
}
