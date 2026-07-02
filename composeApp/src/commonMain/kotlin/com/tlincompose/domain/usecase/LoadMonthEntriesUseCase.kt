package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.repository.TimesheetRepository
import kotlinx.datetime.LocalDate

class LoadMonthEntriesUseCase(
    private val repository: TimesheetRepository,
) {
    suspend operator fun invoke(month: CalendarMonth): Map<LocalDate, DailyEntry> =
        repository.loadMonth(month)
}
