package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.repository.TimesheetRepository
import kotlinx.datetime.LocalDate

class SaveDailyEntryUseCase(
    private val repository: TimesheetRepository,
) {
    suspend operator fun invoke(date: LocalDate, activities: List<Activity>) {
        if (activities.isEmpty()) {
            repository.deleteEntry(date)
            return
        }

        repository.saveEntry(
            DailyEntry(
                date = date,
                activities = activities,
            ),
        )
    }
}
