package com.tlincompose.domain.model

import com.tlincompose.core.AppTimeZone
import com.tlincompose.core.DateMath
import com.tlincompose.core.toTwoDigits
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

enum class EntryType {
    PROJECT,
    COURSE,
    VACATION,
    PERMIT,
    ;
}

data class Activity(
    val type: EntryType,
    val extCode: String? = null,
    val title: String,
    val description: String = "",
    val projectUrl: String? = null,
    val minutes: Int,
) {
    val displayLabel: String
        get() = buildString {
            extCode?.takeIf(String::isNotBlank)?.let {
                append(it)
                append(" • ")
            }
            append(title)
        }
}

data class DailyEntry(
    val date: LocalDate,
    val activities: List<Activity>,
)

data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    init {
        require(startDate <= endDate) { "startDate must be before or equal to endDate" }
    }

    fun contains(date: LocalDate): Boolean = date in startDate..endDate
}

enum class HolidayKey {
    NEW_YEAR,
    EPIPHANY,
    LIBERATION_DAY,
    LABOUR_DAY,
    REPUBLIC_DAY,
    FERRAGOSTO,
    ALL_SAINTS,
    IMMACULATE_CONCEPTION,
    CHRISTMAS,
    SAINT_STEPHENS_DAY,
    EASTER,
    EASTER_MONDAY,
}

data class CalendarMonth(
    val year: Int,
    val monthNumber: Int,
) : Comparable<CalendarMonth> {
    init {
        require(monthNumber in 1..12) { "monthNumber must be between 1 and 12" }
    }

    val fileStamp: String
        get() = "$year-${monthNumber.toTwoDigits()}"

    val firstDate: LocalDate
        get() = LocalDate(year, monthNumber, 1)

    val daysInMonth: Int
        get() = DateMath.daysInMonth(year, monthNumber)

    val lastDate: LocalDate
        get() = LocalDate(year, monthNumber, daysInMonth)

    fun contains(date: LocalDate): Boolean = date.year == year && date.month.ordinal + 1 == monthNumber

    override fun compareTo(other: CalendarMonth): Int = when {
        year != other.year -> year.compareTo(other.year)
        else -> monthNumber.compareTo(other.monthNumber)
    }

    fun plusMonths(offset: Int): CalendarMonth {
        val zeroBased = (year * 12) + (monthNumber - 1) + offset
        val normalizedYear = floorDiv(zeroBased, 12)
        val normalizedMonth = mod(zeroBased, 12) + 1
        return CalendarMonth(normalizedYear, normalizedMonth)
    }

    companion object {
        @OptIn(ExperimentalTime::class)
        fun current(timeZone: TimeZone = AppTimeZone): CalendarMonth {
            val today = Clock.System.todayIn(timeZone)
            return CalendarMonth(today.year, today.month.ordinal + 1)
        }

        private fun floorDiv(value: Int, divisor: Int): Int {
            var quotient = value / divisor
            if (value % divisor != 0 && value xor divisor < 0) {
                quotient -= 1
            }
            return quotient
        }

        private fun mod(value: Int, divisor: Int): Int {
            val remainder = value % divisor
            return if (remainder < 0) remainder + divisor else remainder
        }
    }
}

data class MonthRange(
    val startMonth: CalendarMonth,
    val endMonth: CalendarMonth,
) {
    init {
        require(startMonth <= endMonth) { "startMonth must be before or equal to endMonth" }
    }

    val startDate: LocalDate
        get() = startMonth.firstDate

    val endDate: LocalDate
        get() = endMonth.lastDate

    fun contains(date: LocalDate): Boolean = date in startDate..endDate
}

data class Holiday(
    val key: HolidayKey,
)

data class CalendarDay(
    val date: LocalDate,
    val inCurrentMonth: Boolean,
    val isWeekend: Boolean,
    val holiday: Holiday?,
)

enum class ExportFormat(
    val extension: String,
    val mimeType: String,
) {
    CSV("csv", "text/csv"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PDF("pdf", "application/pdf"),
}

data class ExportActivityTypeFilter(
    val includedTypes: Set<EntryType> = EntryType.entries.toSet(),
) {
    init {
        require(includedTypes.isNotEmpty()) { "includedTypes must not be empty" }
    }

    val isDefault: Boolean
        get() = includedTypes.size == EntryType.entries.size

    fun includes(type: EntryType): Boolean = type in includedTypes
}

data class ExportDocument(
    val fileName: String,
    val mimeType: String,
    val bytes: ByteArray,
)

data class ActivityDraftInput(
    val type: EntryType,
    val extCode: String?,
    val title: String,
    val description: String,
    val projectUrl: String?,
    val hoursText: String,
)

data class DraftValidationResult(
    val activities: List<Activity>,
    val errors: List<DailyEntryValidationError?>,
) {
    val hasErrors: Boolean
        get() = errors.any { it != null }
}

enum class DailyEntryValidationError {
    INVALID_EXT_SELECTION,
    NON_POSITIVE_HOURS,
}
