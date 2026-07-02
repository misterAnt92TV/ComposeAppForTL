package com.tlincompose.domain.usecase

import com.tlincompose.core.LocalDateComparator
import com.tlincompose.domain.model.DateRange
import kotlinx.datetime.LocalDate

class CreateDateRangeUseCase {
    operator fun invoke(firstDate: LocalDate, secondDate: LocalDate): DateRange =
        if (LocalDateComparator.compare(firstDate, secondDate) <= 0) {
            DateRange(firstDate, secondDate)
        } else {
            DateRange(secondDate, firstDate)
        }
}
