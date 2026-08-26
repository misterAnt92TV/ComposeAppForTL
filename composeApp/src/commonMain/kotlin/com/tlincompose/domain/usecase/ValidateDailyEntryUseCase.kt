package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDraftInput
import com.tlincompose.domain.model.DailyEntryValidationError
import com.tlincompose.domain.model.DraftValidationResult
import kotlin.math.roundToInt

class ValidateDailyEntryUseCase {
    operator fun invoke(rows: List<ActivityDraftInput>): DraftValidationResult {
        val errors = MutableList<DailyEntryValidationError?>(rows.size) { null }
        val activities = mutableListOf<Activity>()

        rows.forEachIndexed { index, row ->
            if (isBlankRow(row)) return@forEachIndexed

            if (row.extCode.isNullOrBlank() || row.title.isBlank()) {
                errors[index] = DailyEntryValidationError.INVALID_EXT_SELECTION
                return@forEachIndexed
            }

            val normalizedHours = row.hoursText.trim().replace(',', '.')
            val hours = normalizedHours.toDoubleOrNull()
            if (hours == null || hours <= 0.0) {
                errors[index] = DailyEntryValidationError.NON_POSITIVE_HOURS
                return@forEachIndexed
            }

            activities += Activity(
                type = row.type,
                extCode = row.extCode.trim(),
                title = row.title.trim(),
                description = row.description.trim(),
                projectUrl = row.projectUrl?.trim()?.ifBlank { null },
                minutes = (hours * 60.0).roundToInt(),
                workLocation = row.workLocation,
            )
        }

        return DraftValidationResult(
            activities = activities,
            errors = errors,
        )
    }

    private fun isBlankRow(row: ActivityDraftInput): Boolean =
        row.extCode.isNullOrBlank() && row.title.isBlank() && row.hoursText.isBlank()
}
