package com.tlincompose.domain.usecase

import com.tlincompose.core.DateMath
import com.tlincompose.core.LocalDateComparator
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.DateRange

class SaveDateRangeEntriesUseCase(
    private val saveDailyEntry: SaveDailyEntryUseCase,
) {
    suspend operator fun invoke(range: DateRange, activities: List<Activity>) {
        var currentDate = range.startDate
        while (LocalDateComparator.compare(currentDate, range.endDate) <= 0) {
            saveDailyEntry(currentDate, activities)
            currentDate = DateMath.nextDate(currentDate)
        }
    }
}
