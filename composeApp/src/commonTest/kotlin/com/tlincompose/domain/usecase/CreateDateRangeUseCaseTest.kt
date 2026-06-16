package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.DateRange
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

class CreateDateRangeUseCaseTest {
    private val useCase = CreateDateRangeUseCase()

    @Test
    fun keepsAscendingDatesAsTheyAre() {
        val range = useCase(
            firstDate = LocalDate(2026, 5, 10),
            secondDate = LocalDate(2026, 5, 14),
        )

        assertEquals(
            DateRange(
                startDate = LocalDate(2026, 5, 10),
                endDate = LocalDate(2026, 5, 14),
            ),
            range,
        )
    }

    @Test
    fun normalizesDescendingDates() {
        val range = useCase(
            firstDate = LocalDate(2026, 5, 14),
            secondDate = LocalDate(2026, 5, 10),
        )

        assertEquals(
            DateRange(
                startDate = LocalDate(2026, 5, 10),
                endDate = LocalDate(2026, 5, 14),
            ),
            range,
        )
    }
}
