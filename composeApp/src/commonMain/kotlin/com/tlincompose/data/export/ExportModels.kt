@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.tlincompose.data.export

import com.tlincompose.core.DateMath
import com.tlincompose.core.LocalDateComparator
import com.tlincompose.core.appStrings
import com.tlincompose.core.displayName
import com.tlincompose.core.exportActivitiesLabel
import com.tlincompose.core.exportActivityCodeLabel
import com.tlincompose.core.exportActivityLabel
import com.tlincompose.core.exportDaysLabel
import com.tlincompose.core.exportGeneratedAtLabel
import com.tlincompose.core.exportGeneratedAtValue
import com.tlincompose.core.exportHoursPerDayLabel
import com.tlincompose.core.exportPeriodLabel
import com.tlincompose.core.exportPeriodsLabel
import com.tlincompose.core.exportRecordedDaysLabel
import com.tlincompose.core.exportReportLabel
import com.tlincompose.core.exportTotalHoursLabel
import com.tlincompose.core.exportTypeLabel
import com.tlincompose.core.formatDate
import com.tlincompose.core.formatDateRange
import com.tlincompose.core.formatHours
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.PdfExportStyle
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class ExportPeriod(
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    val label: String
        get() = if (startDate == endDate) formatDate(startDate) else formatDateRange(startDate, endDate)
}

data class ExportRow(
    val activityCode: String,
    val activityTitle: String,
    val typeLabel: String,
    val periods: List<ExportPeriod>,
    val hoursPerDayLabel: String,
    val days: Int,
    val totalMinutes: Int,
) {

    val periodsLabel: String
        get() = periods.joinToString(separator = ", ") { it.label }
}

data class ExportSummary(
    val recordedDays: Int,
    val activityCount: Int,
    val totalLoggedMinutes: Int,
)

data class ExportReport(
    val periodLabel: String,
    val exportedAt: Instant,
    val rows: List<ExportRow>,
    val summary: ExportSummary,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val brandingLogoBase64: String? = null,
    val pdfExportStyle: PdfExportStyle = PdfExportStyle.RETRO,
)

sealed interface SpreadsheetCell {
    data class Text(val value: String) : SpreadsheetCell
    data class Number(val value: String) : SpreadsheetCell
}

fun groupActivitiesForExport(
    entries: List<DailyEntry>,
    language: AppLanguage,
): List<ExportRow> {
    val flattened = entries
        .sortedWith(compareBy(LocalDateComparator) { it.date })
        .flatMap { entry ->
            entry.activities
                .groupBy { activity -> entry.date.toDailyActivityKey(activity, language) }
                .map { (key, sameDayActivities) ->
                    FlatActivity(
                        date = key.date,
                        type = key.type,
                        activityKey = key.activityKey,
                        activityCode = key.activityCode,
                        activityTitle = key.activityTitle,
                        minutes = sameDayActivities.sumOf(Activity::minutes),
                    )
                }
        }
        .sortedWith(
            compareBy<FlatActivity>(
                { it.activityCode },
                { it.activityTitle.lowercase() },
                { it.type.ordinal },
                { it.date.year },
                { it.date.month.ordinal + 1 },
                { it.date.day },
            ),
        )

    if (flattened.isEmpty()) return emptyList()

    return flattened
        .groupBy { ActivityGroupKey(type = it.type, activityKey = it.activityKey) }
        .map { (key, activities) ->
            val sortedActivities = activities.sortedWith(compareBy(LocalDateComparator) { it.date })
            val distinctTitles = sortedActivities.map(FlatActivity::activityTitle).distinct()
            ExportRow(
                activityCode = sortedActivities.first().activityCode,
                activityTitle = distinctTitles.joinToString(separator = " / "),
                typeLabel = key.type.displayName(language),
                periods = buildExportPeriods(sortedActivities.map(FlatActivity::date)),
                hoursPerDayLabel = sortedActivities
                    .map(FlatActivity::minutes)
                    .distinct()
                    .sorted()
                    .joinToString(separator = " / ") { formatHours(it) },
                days = sortedActivities.size,
                totalMinutes = sortedActivities.sumOf(FlatActivity::minutes),
            )
        }
        .sortedWith(
            compareBy<ExportRow>(
                { it.activityCode },
                { it.activityTitle.lowercase() },
                { it.typeLabel },
            ),
        )
}

fun ExportReport.toCsv(): String {
    val strings = appStrings(language)
    val rows = buildList {
        add(listOf(strings.exportReportLabel, strings.appName))
        add(listOf(strings.exportPeriodLabel, periodLabel))
        add(listOf(strings.exportGeneratedAtLabel, strings.exportGeneratedAtValue(exportedAt)))
        add(listOf(strings.exportRecordedDaysLabel, summary.recordedDays.toString()))
        add(listOf(strings.exportActivitiesLabel, summary.activityCount.toString()))
        add(listOf(strings.exportTotalHoursLabel, formatHours(summary.totalLoggedMinutes)))
        add(emptyList())
        add(
            listOf(
                strings.exportActivityCodeLabel,
                strings.exportActivityLabel,
                strings.exportTypeLabel,
                strings.exportPeriodsLabel,
                strings.exportHoursPerDayLabel,
                strings.exportDaysLabel,
                strings.exportTotalHoursLabel,
            ),
        )
        this@toCsv.rows.forEach { row ->
            add(
                listOf(
                    row.activityCode,
                    row.activityTitle,
                    row.typeLabel,
                    row.periodsLabel,
                    row.hoursPerDayLabel,
                    row.days.toString(),
                    formatHours(row.totalMinutes),
                ),
            )
        }
    }

    return rows.joinToString(separator = "\n") { line ->
        line.joinToString(separator = ";") { cell ->
            "\"${cell.replace("\"", "\"\"")}\""
        }
    }
}

fun ExportReport.toSpreadsheetRows(title: String): List<List<SpreadsheetCell>> {
    val strings = appStrings(language)
    return buildList {
        add(listOf(SpreadsheetCell.Text(title)))
        add(listOf(SpreadsheetCell.Text(strings.exportPeriodLabel), SpreadsheetCell.Text(periodLabel)))
        add(
            listOf(
                SpreadsheetCell.Text(strings.exportGeneratedAtLabel),
                SpreadsheetCell.Text(strings.exportGeneratedAtValue(exportedAt)),
            ),
        )
        add(
            listOf(
                SpreadsheetCell.Text(strings.exportRecordedDaysLabel),
                SpreadsheetCell.Number(summary.recordedDays.toString()),
            ),
        )
        add(
            listOf(
                SpreadsheetCell.Text(strings.exportActivitiesLabel),
                SpreadsheetCell.Number(summary.activityCount.toString()),
            ),
        )
        add(
            listOf(
                SpreadsheetCell.Text(strings.exportTotalHoursLabel),
                SpreadsheetCell.Number(formatHours(summary.totalLoggedMinutes)),
            ),
        )
        add(emptyList())
        add(
            listOf(
                SpreadsheetCell.Text(strings.exportActivityCodeLabel),
                SpreadsheetCell.Text(strings.exportActivityLabel),
                SpreadsheetCell.Text(strings.exportTypeLabel),
                SpreadsheetCell.Text(strings.exportPeriodsLabel),
                SpreadsheetCell.Text(strings.exportHoursPerDayLabel),
                SpreadsheetCell.Text(strings.exportDaysLabel),
                SpreadsheetCell.Text(strings.exportTotalHoursLabel),
            ),
        )
        rows.forEach { row ->
            add(
                listOf(
                    SpreadsheetCell.Text(row.activityCode),
                    SpreadsheetCell.Text(row.activityTitle),
                    SpreadsheetCell.Text(row.typeLabel),
                    SpreadsheetCell.Text(row.periodsLabel),
                    SpreadsheetCell.Text(row.hoursPerDayLabel),
                    SpreadsheetCell.Number(row.days.toString()),
                    SpreadsheetCell.Number(formatHours(row.totalMinutes)),
                ),
            )
        }
    }
}

private fun LocalDate.toDailyActivityKey(
    activity: Activity,
    language: AppLanguage,
): DailyActivityKey {
    val normalizedCode = activity.extCode
        ?.trim()
        ?.uppercase()
        ?.takeIf(String::isNotBlank)
    val activityTitle = activity.title
        .trim()
        .ifBlank { activity.type.displayName(language) }
    return DailyActivityKey(
        date = this,
        type = activity.type,
        activityKey = normalizedCode ?: "TITLE:${activityTitle.lowercase()}",
        activityCode = normalizedCode ?: "-",
        activityTitle = activityTitle,
    )
}

private fun buildExportPeriods(dates: List<LocalDate>): List<ExportPeriod> {
    if (dates.isEmpty()) return emptyList()
    val sortedDates = dates.sortedWith(LocalDateComparator)
    val periods = mutableListOf<ExportPeriod>()
    var startDate = sortedDates.first()
    var endDate = sortedDates.first()

    sortedDates.drop(1).forEach { nextDate ->
        if (DateMath.nextDate(endDate) == nextDate) {
            endDate = nextDate
        } else {
            periods += ExportPeriod(startDate = startDate, endDate = endDate)
            startDate = nextDate
            endDate = nextDate
        }
    }
    periods += ExportPeriod(startDate = startDate, endDate = endDate)
    return periods
}

private data class DailyActivityKey(
    val date: LocalDate,
    val type: EntryType,
    val activityKey: String,
    val activityCode: String,
    val activityTitle: String,
)

private data class ActivityGroupKey(
    val type: EntryType,
    val activityKey: String,
)

private data class FlatActivity(
    val date: LocalDate,
    val type: EntryType,
    val activityKey: String,
    val activityCode: String,
    val activityTitle: String,
    val minutes: Int,
)
