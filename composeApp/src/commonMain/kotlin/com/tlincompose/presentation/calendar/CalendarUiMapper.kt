package com.tlincompose.presentation.calendar

import com.tlincompose.core.activitySummaryLabel
import com.tlincompose.core.appStrings
import com.tlincompose.core.displayLabel
import com.tlincompose.core.formatHours
import com.tlincompose.core.label
import com.tlincompose.domain.model.ActivityDraftInput
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarDay
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.MonthWorkSummary
import kotlinx.datetime.LocalDate
import kotlin.math.roundToInt

fun List<CalendarDay>.toUiModels(
    entriesByDate: Map<LocalDate, DailyEntry>,
    today: LocalDate,
    language: AppLanguage,
    selectedRange: DateRange? = null,
    pendingRange: DateRange? = null,
): List<MonthCellUiModel> = map { day ->
    val strings = appStrings(language)
    val entry = entriesByDate[day.date]
    val rangeStart = selectedRange?.startDate ?: pendingRange?.startDate
    val activeRange = selectedRange ?: pendingRange
    MonthCellUiModel(
        date = day.date,
        inCurrentMonth = day.inCurrentMonth,
        isWeekend = day.isWeekend,
        isToday = day.date == today,
        holidayLabel = day.holiday?.label(language),
        activityItems = entry?.activities?.mapIndexed { index, activity ->
            CalendarActivityUiModel(
                activityIndex = index,
                entryType = activity.type,
                summary = strings.activitySummaryLabel(
                    valueLabel = activity.displayLabel(language),
                    hoursText = formatHours(activity.minutes),
                ),
            )
        }.orEmpty(),
        activityCount = entry?.activities?.size ?: 0,
        totalMinutes = entry?.activities?.sumOf { it.minutes } ?: 0,
        isRangeStart = day.date == rangeStart,
        isRangeEnd = day.date == selectedRange?.endDate,
        isInSelectedRange = activeRange?.contains(day.date) == true,
        isActivityDragSource = false,
        isActivityDropTarget = false,
    )
}

fun DailyEntry?.toDayEditorUiState(
    target: DayEditTargetUiState,
): DayEditorUiState {
    val rows = this?.activities?.map {
        ActivityDraftUiState(
            type = it.type,
            extCode = it.extCode,
            title = it.title,
            description = it.description,
            projectUrl = it.projectUrl.orEmpty(),
            hoursText = formatHours(it.minutes),
        )
    } ?: listOf(ActivityDraftUiState())

    return DayEditorUiState(
        target = target,
        rows = rows,
        errors = List(rows.size) { null },
    )
}

fun DailyEntry?.toDayEditorUiState(date: LocalDate): DayEditorUiState =
    toDayEditorUiState(
        DayEditTargetUiState(sourceDate = date),
    )

fun ActivityDraftUiState.toDomainInput(): ActivityDraftInput =
    ActivityDraftInput(
        type = type,
        extCode = extCode,
        title = title,
        description = description,
        projectUrl = projectUrl.ifBlank { null },
        hoursText = hoursText,
    )

fun MonthWorkSummary.toUiState(): MonthSummaryUiState =
    MonthSummaryUiState(
        totalLoggedMinutes = totalLoggedMinutes,
        targetCompletionMinutes = targetCompletionMinutes,
        completedCompletionMinutes = completedCompletionMinutes,
        remainingCompletionMinutes = remainingCompletionMinutes,
        completionFraction = completionFraction,
        completionPercentage = (completionFraction * 100).roundToInt(),
    )
