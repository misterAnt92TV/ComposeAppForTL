package com.tlincompose.domain.usecase

import com.tlincompose.core.DateMath
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DefaultWorkdayMinutes
import com.tlincompose.domain.model.MonthWorkSummary
import kotlinx.datetime.LocalDate

class CalculateMonthWorkSummaryUseCase(
    private val holidayProvider: HolidayProvider = ItalianHolidayProvider,
) {
    operator fun invoke(
        month: CalendarMonth,
        entries: Collection<DailyEntry>,
        standardWorkdayMinutes: Int = DefaultWorkdayMinutes,
    ): MonthWorkSummary {
        val entriesByDate = entries.associateBy(DailyEntry::date)
        var totalLoggedMinutes = 0
        var targetCompletionMinutes = 0
        var completedCompletionMinutes = 0

        for (dayOfMonth in 1..month.daysInMonth) {
            val date = LocalDate(month.year, month.monthNumber, dayOfMonth)
            val loggedMinutes = entriesByDate[date]?.activities?.sumOf { it.minutes } ?: 0
            totalLoggedMinutes += loggedMinutes

            if (DateMath.isWeekend(date) || holidayProvider.holidayFor(date) != null) {
                continue
            }

            targetCompletionMinutes += standardWorkdayMinutes
            completedCompletionMinutes += loggedMinutes.coerceAtMost(standardWorkdayMinutes)
        }

        return MonthWorkSummary(
            totalLoggedMinutes = totalLoggedMinutes,
            targetCompletionMinutes = targetCompletionMinutes,
            completedCompletionMinutes = completedCompletionMinutes,
            remainingCompletionMinutes = (targetCompletionMinutes - completedCompletionMinutes).coerceAtLeast(0),
        )
    }
}
