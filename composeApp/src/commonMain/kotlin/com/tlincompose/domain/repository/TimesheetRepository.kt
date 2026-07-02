package com.tlincompose.domain.repository

import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import kotlinx.datetime.LocalDate

interface TimesheetRepository {
    suspend fun loadMonth(month: CalendarMonth): Map<LocalDate, DailyEntry>
    suspend fun loadRange(range: DateRange): Map<LocalDate, DailyEntry>
    suspend fun loadAll(): List<DailyEntry>
    suspend fun saveEntry(entry: DailyEntry)
    suspend fun deleteEntry(date: LocalDate)
    suspend fun replaceAll(entries: List<DailyEntry>)
    suspend fun syncActivitiesWithDefinition(previousExtCode: String, definition: ActivityDefinition): Int
}
