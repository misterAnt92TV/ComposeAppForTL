package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.MonthRange

class CreateMonthRangeUseCase {
    operator fun invoke(firstMonth: CalendarMonth, secondMonth: CalendarMonth): MonthRange =
        if (firstMonth <= secondMonth) {
            MonthRange(firstMonth, secondMonth)
        } else {
            MonthRange(secondMonth, firstMonth)
        }
}
