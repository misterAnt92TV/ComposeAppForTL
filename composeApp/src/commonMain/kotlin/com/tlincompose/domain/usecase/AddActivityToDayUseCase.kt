package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.repository.TimesheetRepository
import kotlinx.datetime.LocalDate

class AddActivityToDayUseCase(
    private val repository: TimesheetRepository,
    private val saveDailyEntry: SaveDailyEntryUseCase,
) {
    suspend operator fun invoke(date: LocalDate, activity: Activity): DailyEntry {
        val existingEntry = repository.loadRange(DateRange(date, date))[date]
        val existingActivities = existingEntry?.activities.orEmpty()
        if (existingActivities.any { it == activity }) {
            return existingEntry ?: DailyEntry(date, listOf(activity))
        }

        val updatedEntry = DailyEntry(
            date = date,
            activities = existingActivities + activity,
        )
        saveDailyEntry(updatedEntry.date, updatedEntry.activities)
        return updatedEntry
    }
}
