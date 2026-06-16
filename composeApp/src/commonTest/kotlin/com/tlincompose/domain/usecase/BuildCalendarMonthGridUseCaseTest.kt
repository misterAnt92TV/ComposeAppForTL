package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.HolidayKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.datetime.LocalDate

class BuildCalendarMonthGridUseCaseTest {
    private val useCase = BuildCalendarMonthGridUseCase()

    @Test
    fun monthGridAlwaysContainsSixWeeks() {
        val grid = useCase(CalendarMonth(2026, 5))

        assertEquals(42, grid.size)
        assertEquals(LocalDate(2026, 4, 27), grid.first().date)
        assertEquals(LocalDate(2026, 6, 7), grid.last().date)
    }

    @Test
    fun italianHolidaysAreIncludedInGrid() {
        val grid = useCase(CalendarMonth(2026, 4))
        val pasquetta = grid.firstOrNull { it.date == LocalDate(2026, 4, 6) }

        assertNotNull(pasquetta)
        assertEquals(HolidayKey.EASTER_MONDAY, pasquetta.holiday?.key)
    }
}
