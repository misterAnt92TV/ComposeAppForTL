package com.tlincompose.domain.usecase

import com.tlincompose.core.DateMath
import com.tlincompose.domain.model.CalendarDay
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.Holiday
import com.tlincompose.domain.model.HolidayKey
import kotlinx.datetime.LocalDate

interface HolidayProvider {
    fun holidayFor(date: LocalDate): Holiday?
}

object ItalianHolidayProvider : HolidayProvider {
    private val fixedHolidays = mapOf(
        1 to mapOf(1 to HolidayKey.NEW_YEAR, 6 to HolidayKey.EPIPHANY),
        4 to mapOf(25 to HolidayKey.LIBERATION_DAY),
        5 to mapOf(1 to HolidayKey.LABOUR_DAY),
        6 to mapOf(2 to HolidayKey.REPUBLIC_DAY),
        8 to mapOf(15 to HolidayKey.FERRAGOSTO),
        11 to mapOf(1 to HolidayKey.ALL_SAINTS),
        12 to mapOf(
            8 to HolidayKey.IMMACULATE_CONCEPTION,
            25 to HolidayKey.CHRISTMAS,
            26 to HolidayKey.SAINT_STEPHENS_DAY,
        ),
    )

    override fun holidayFor(date: LocalDate): Holiday? {
        fixedHolidays[date.month.ordinal + 1]?.get(date.day)?.let { return Holiday(it) }

        val easterSunday = easterSunday(date.year)
        if (date == easterSunday) return Holiday(HolidayKey.EASTER)
        if (date == DateMath.nextDate(easterSunday)) return Holiday(HolidayKey.EASTER_MONDAY)

        return null
    }

    private fun easterSunday(year: Int): LocalDate {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31
        val day = ((h + l - 7 * m + 114) % 31) + 1
        return LocalDate(year, month, day)
    }
}

class BuildCalendarMonthGridUseCase(
    private val holidayProvider: HolidayProvider = ItalianHolidayProvider,
) {
    operator fun invoke(month: CalendarMonth): List<CalendarDay> {
        val leadingDays = DateMath.weekdayMondayFirst(month.firstDate) - 1
        val gridStart = DateMath.shiftDate(month.firstDate, -leadingDays)
        return List(42) { index ->
            val date = DateMath.shiftDate(gridStart, index)
            CalendarDay(
                date = date,
                inCurrentMonth = month.contains(date),
                isWeekend = DateMath.isWeekend(date),
                holiday = holidayProvider.holidayFor(date),
            )
        }
    }
}
