package com.tlincompose.domain.usecase

import com.tlincompose.core.DateMath
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.BuiltInActivityDefinitions
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DefaultExtWorkMode
import com.tlincompose.domain.model.DefaultWorkdayMinutes
import com.tlincompose.domain.repository.TimesheetRepository

class ApplyDefaultExtActivityToMonthUseCase(
    private val repository: TimesheetRepository,
    private val saveDailyEntry: SaveDailyEntryUseCase,
    private val holidayProvider: HolidayProvider = ItalianHolidayProvider,
) {
    suspend operator fun invoke(
        month: CalendarMonth,
        workMode: DefaultExtWorkMode,
        definition: ActivityDefinition?,
    ): Int {
        val currentEntries = repository.loadMonth(month)
        val baseDefinition = definition ?: fallbackDefinition()
        val defaultActivity = Activity(
            type = baseDefinition.type,
            extCode = baseDefinition.extCode,
            title = defaultTitle(baseDefinition.title, workMode),
            description = baseDefinition.description,
            projectUrl = baseDefinition.projectUrl,
            minutes = DefaultWorkdayMinutes,
        )

        var appliedDays = 0
        for (dayOfMonth in 1..month.daysInMonth) {
            val date = kotlinx.datetime.LocalDate(month.year, month.monthNumber, dayOfMonth)
            if (DateMath.isWeekend(date) || holidayProvider.holidayFor(date) != null) {
                continue
            }
            val existingEntry = currentEntries[date]
            if (existingEntry != null && existingEntry.activities.isNotEmpty()) {
                continue
            }

            saveDailyEntry(date, listOf(defaultActivity))
            appliedDays += 1
        }

        return appliedDays
    }

    private fun defaultTitle(
        baseTitle: String,
        workMode: DefaultExtWorkMode,
    ): String = when (workMode) {
        DefaultExtWorkMode.SMART_WORKING -> "$baseTitle (Smart working)"
        DefaultExtWorkMode.OFFICE -> "$baseTitle (Ufficio)"
    }

    private fun fallbackDefinition(): ActivityDefinition = ActivityDefinition(
        extCode = BuiltInActivityDefinitions.DefaultExtCode,
        type = com.tlincompose.domain.model.EntryType.PROJECT,
        title = "Attività EXT di default",
        description = "Attività applicata automaticamente sui giorni lavorativi senza attività.",
        defaultMinutes = DefaultWorkdayMinutes,
        createdDate = kotlinx.datetime.LocalDate(2026, 1, 1),
        updatedDate = kotlinx.datetime.LocalDate(2026, 1, 1),
        projectIconPreset = com.tlincompose.domain.model.ProjectIconPreset.WORK,
    )
}
