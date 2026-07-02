package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.CalendarMonth
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateMonthRangeUseCaseTest {
    private val useCase = CreateMonthRangeUseCase()

    @Test
    fun normalizesMonthOrder() {
        val range = useCase(
            firstMonth = CalendarMonth(2026, 7),
            secondMonth = CalendarMonth(2026, 5),
        )

        assertEquals(CalendarMonth(2026, 5), range.startMonth)
        assertEquals(CalendarMonth(2026, 7), range.endMonth)
    }
}
