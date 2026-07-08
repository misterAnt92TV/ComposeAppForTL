package com.tlincompose.domain.usecase

import com.tlincompose.core.DateMath
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import kotlinx.datetime.LocalDate

class CoverIncompleteMonthWithActivityUseCase(
    private val saveDailyEntry: SaveDailyEntryUseCase,
    private val holidayProvider: HolidayProvider = ItalianHolidayProvider,
) {
    suspend operator fun invoke(
        month: CalendarMonth,
        existingEntries: Map<LocalDate, DailyEntry>,
        activityTemplate: Activity,
        standardWorkdayMinutes: Int,
    ): List<DailyEntry> {
        val updatedEntries = mutableListOf<DailyEntry>()

        for (dayOfMonth in 1..month.daysInMonth) {
            val date = LocalDate(month.year, month.monthNumber, dayOfMonth)
            if (DateMath.isWeekend(date) || holidayProvider.holidayFor(date) != null) {
                continue
            }

            val existingActivities = existingEntries[date]?.activities.orEmpty()
            val loggedMinutes = existingActivities.sumOf(Activity::minutes)
            val remainingMinutes = standardWorkdayMinutes - loggedMinutes
            if (remainingMinutes <= 0) {
                continue
            }

            val updatedActivity = activityTemplate.copy(minutes = remainingMinutes)
            val mergedActivities = mergeOrAppendActivity(existingActivities, updatedActivity)
            saveDailyEntry(date, mergedActivities)
            updatedEntries += DailyEntry(date, mergedActivities)
        }

        return updatedEntries
    }

    private fun mergeOrAppendActivity(
        existingActivities: List<Activity>,
        activityToAdd: Activity,
    ): List<Activity> {
        val existingIndex = existingActivities.indexOfFirst { it.matchesTemplate(activityToAdd) }
        if (existingIndex < 0) {
            return existingActivities + activityToAdd
        }

        return existingActivities.toMutableList().apply {
            val existingActivity = this[existingIndex]
            this[existingIndex] = existingActivity.copy(
                minutes = existingActivity.minutes + activityToAdd.minutes,
            )
        }
    }

    private fun Activity.matchesTemplate(other: Activity): Boolean =
        type == other.type &&
            extCode == other.extCode &&
            title == other.title &&
            description == other.description &&
            projectUrl == other.projectUrl &&
            workLocation == other.workLocation
}
