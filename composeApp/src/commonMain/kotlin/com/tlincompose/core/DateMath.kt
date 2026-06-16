package com.tlincompose.core

import kotlinx.datetime.LocalDate

val LocalDateComparator: Comparator<LocalDate> = Comparator { left, right ->
    when {
        left.year != right.year -> left.year.compareTo(right.year)
        left.stableMonthNumber() != right.stableMonthNumber() -> left.stableMonthNumber().compareTo(right.stableMonthNumber())
        else -> left.day.compareTo(right.day)
    }
}

object DateMath {
    private val monthLengths = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    fun isLeapYear(year: Int): Boolean =
        (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

    fun daysInMonth(year: Int, monthNumber: Int): Int =
        if (monthNumber == 2 && isLeapYear(year)) 29 else monthLengths[monthNumber - 1]

    fun weekdayMondayFirst(date: LocalDate): Int =
        weekdayMondayFirst(date.year, date.stableMonthNumber(), date.day)

    fun weekdayMondayFirst(year: Int, monthNumber: Int, dayOfMonth: Int): Int {
        var adjustedYear = year
        val offsets = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
        if (monthNumber < 3) adjustedYear -= 1
        val sundayFirst = (
            adjustedYear +
                adjustedYear / 4 -
                adjustedYear / 100 +
                adjustedYear / 400 +
                offsets[monthNumber - 1] +
                dayOfMonth
            ) % 7
        return if (sundayFirst == 0) 7 else sundayFirst
    }

    fun isWeekend(date: LocalDate): Boolean = weekdayMondayFirst(date) >= 6

    fun nextDate(date: LocalDate): LocalDate {
        val monthLength = daysInMonth(date.year, date.stableMonthNumber())
        return when {
            date.day < monthLength -> LocalDate(date.year, date.stableMonthNumber(), date.day + 1)
            date.stableMonthNumber() < 12 -> LocalDate(date.year, date.stableMonthNumber() + 1, 1)
            else -> LocalDate(date.year + 1, 1, 1)
        }
    }

    fun previousDate(date: LocalDate): LocalDate = when {
        date.day > 1 -> LocalDate(date.year, date.stableMonthNumber(), date.day - 1)
        date.stableMonthNumber() > 1 -> {
            val previousMonth = date.stableMonthNumber() - 1
            LocalDate(date.year, previousMonth, daysInMonth(date.year, previousMonth))
        }

        else -> LocalDate(date.year - 1, 12, 31)
    }

    fun shiftDate(date: LocalDate, dayOffset: Int): LocalDate {
        var current = date
        repeat(kotlin.math.abs(dayOffset)) {
            current = if (dayOffset >= 0) nextDate(current) else previousDate(current)
        }
        return current
    }

    fun countInclusiveDays(startDate: LocalDate, endDate: LocalDate): Int {
        var days = 1
        var cursor = startDate
        while (LocalDateComparator.compare(cursor, endDate) < 0) {
            cursor = nextDate(cursor)
            days += 1
        }
        return days
    }
}

private fun LocalDate.stableMonthNumber(): Int = month.ordinal + 1
