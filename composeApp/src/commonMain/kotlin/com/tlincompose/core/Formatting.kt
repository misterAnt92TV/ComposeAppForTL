@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.tlincompose.core

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

fun formatDate(date: LocalDate): String =
    "${date.day.toTwoDigits()}/${(date.month.ordinal + 1).toTwoDigits()}/${date.year}"

fun formatDateRange(startDate: LocalDate, endDate: LocalDate): String =
    "${formatDate(startDate)} - ${formatDate(endDate)}"

fun formatFileDate(date: LocalDate): String =
    "${date.year}-${(date.month.ordinal + 1).toTwoDigits()}-${date.day.toTwoDigits()}"

fun formatDateTime(dateTime: LocalDateTime): String =
    "${formatDate(dateTime.date)} ${dateTime.hour.toTwoDigits()}:${dateTime.minute.toTwoDigits()}"

fun formatInstant(
    instant: Instant,
    timeZone: TimeZone = AppTimeZone,
): String = formatDateTime(instant.toLocalDateTime(timeZone))

fun formatHours(minutes: Int): String {
    val hundredths = (minutes * 100.0 / 60.0).roundToInt()
    val whole = hundredths / 100
    val remainder = hundredths % 100
    return when {
        remainder == 0 -> whole.toString()
        remainder % 10 == 0 -> "$whole.${remainder / 10}"
        else -> "$whole.${remainder.toString().padStart(2, '0')}"
    }
}

fun normalizeUserFacingName(value: String): String = value
    .trim()
    .split(Regex("\\s+"))
    .filter(String::isNotBlank)
    .joinToString(separator = " ")

fun normalizeExportMetadataIdentifier(value: String): String = value.trim()

fun exportFileNameUserSegment(value: String): String =
    normalizeUserFacingName(value).replace(' ', '_')

internal fun Int.toTwoDigits(): String = toString().padStart(2, '0')
