package com.tlincompose.presentation.calendar

import com.tlincompose.core.DateMath
import com.tlincompose.domain.model.ActivityWorkLocation
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.MonthRange
import kotlinx.datetime.LocalDate

data class MonthCellUiModel(
    val date: LocalDate,
    val inCurrentMonth: Boolean,
    val isWeekend: Boolean,
    val isToday: Boolean,
    val holidayLabel: String?,
    val activityItems: List<CalendarActivityUiModel>,
    val activityCount: Int,
    val totalMinutes: Int,
    val dailyLimitMinutes: Int,
    val isOverDailyLimit: Boolean,
    val isRangeStart: Boolean,
    val isRangeEnd: Boolean,
    val isInSelectedRange: Boolean,
    val isActivityDragSource: Boolean,
    val isActivityDropTarget: Boolean,
)

data class CalendarActivityUiModel(
    val activityIndex: Int,
    val entryType: EntryType,
    val summary: String,
)

data class ActivityDraftUiState(
    val type: EntryType = EntryType.PROJECT,
    val extCode: String? = null,
    val title: String = "",
    val description: String = "",
    val projectUrl: String = "",
    val hoursText: String = "",
    val workLocation: ActivityWorkLocation = ActivityWorkLocation.SMART_WORKING,
)

data class DayEditTargetUiState(
    val sourceDate: LocalDate,
    val startDate: LocalDate = sourceDate,
    val endDate: LocalDate = sourceDate,
    val includesWeekend: Boolean = DateMath.isWeekend(sourceDate),
) {
    val isRange: Boolean
        get() = startDate != endDate

    val affectedDayCount: Int
        get() = DateMath.countInclusiveDays(startDate, endDate)

    val selectedRange: DateRange
        get() = DateRange(startDate, endDate)
}

data class DayEditorUiState(
    val target: DayEditTargetUiState,
    val rows: List<ActivityDraftUiState>,
    val errors: List<String?>,
    val canCoverIncompleteMonth: Boolean = false,
)

data class DayDragSelectionUiState(
    val anchorDate: LocalDate? = null,
    val selectedRange: DateRange? = null,
) {
    val isDragging: Boolean
        get() = anchorDate != null && selectedRange != null
}

data class ActivityDragUiState(
    val sourceDate: LocalDate? = null,
    val sourceActivityIndex: Int? = null,
    val targetDate: LocalDate? = null,
) {
    val isDragging: Boolean
        get() = sourceDate != null && sourceActivityIndex != null
}

data class RangeSelectionUiState(
    val isSelecting: Boolean = false,
    val startMonth: CalendarMonth? = null,
    val selectedRange: MonthRange? = null,
)

data class MonthSummaryUiState(
    val totalLoggedMinutes: Int = 0,
    val targetCompletionMinutes: Int = 0,
    val completedCompletionMinutes: Int = 0,
    val remainingCompletionMinutes: Int = 0,
    val completionFraction: Float = 0f,
    val completionPercentage: Int = 0,
)
