package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.EntryType
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateMonthWorkSummaryUseCaseTest {
    @Test
    fun countsMonthHoursAndIgnoresWeekendAndHolidayForCompletion() {
        val useCase = CalculateMonthWorkSummaryUseCase()
        val summary = useCase(
            month = CalendarMonth(2026, 5),
            entries = listOf(
                dailyEntry(date = LocalDate(2026, 5, 1), minutes = 480),
                dailyEntry(date = LocalDate(2026, 5, 4), minutes = 480),
                dailyEntry(date = LocalDate(2026, 5, 5), minutes = 240),
                dailyEntry(date = LocalDate(2026, 5, 10), minutes = 480),
            ),
        )

        assertEquals(1680, summary.totalLoggedMinutes)
        assertEquals(9600, summary.targetCompletionMinutes)
        assertEquals(720, summary.completedCompletionMinutes)
        assertEquals(8880, summary.remainingCompletionMinutes)
    }

    @Test
    fun capsDailyCompletionAtStandardWorkdayEvenWithOvertime() {
        val useCase = CalculateMonthWorkSummaryUseCase()
        val summary = useCase(
            month = CalendarMonth(2026, 5),
            entries = listOf(
                dailyEntry(date = LocalDate(2026, 5, 4), minutes = 600),
            ),
        )

        assertEquals(600, summary.totalLoggedMinutes)
        assertEquals(9600, summary.targetCompletionMinutes)
        assertEquals(480, summary.completedCompletionMinutes)
        assertEquals(9120, summary.remainingCompletionMinutes)
    }

    @Test
    fun usesCustomStandardWorkdayWhenProvided() {
        val useCase = CalculateMonthWorkSummaryUseCase()
        val summary = useCase(
            month = CalendarMonth(2026, 5),
            entries = listOf(
                dailyEntry(date = LocalDate(2026, 5, 4), minutes = 420),
            ),
            standardWorkdayMinutes = 420,
        )

        assertEquals(420, summary.completedCompletionMinutes)
        assertEquals(8400, summary.targetCompletionMinutes)
        assertEquals(7980, summary.remainingCompletionMinutes)
    }

    private fun dailyEntry(date: LocalDate, minutes: Int): DailyEntry =
        DailyEntry(
            date = date,
            activities = listOf(
                Activity(
                    type = EntryType.PROJECT,
                    title = "Attività",
                    minutes = minutes,
                ),
            ),
        )
}
