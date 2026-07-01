@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.tlincompose.data.export

import com.tlincompose.core.DateMath
import com.tlincompose.core.LocalDateComparator
import com.tlincompose.core.AppStrings
import com.tlincompose.core.appStrings
import com.tlincompose.core.displayName
import com.tlincompose.core.exportActivitiesLabel
import com.tlincompose.core.exportActivityCodeLabel
import com.tlincompose.core.exportActivityLabel
import com.tlincompose.core.exportEmployeeIdMetadataLabel
import com.tlincompose.core.exportDaysLabel
import com.tlincompose.core.exportGeneratedAtLabel
import com.tlincompose.core.exportGeneratedAtValue
import com.tlincompose.core.exportHoursPerDayLabel
import com.tlincompose.core.exportNoActivitiesForSelectedPeriod
import com.tlincompose.core.exportOfficeLabel
import com.tlincompose.core.exportPeriodLabel
import com.tlincompose.core.exportPersonIdMetadataLabel
import com.tlincompose.core.exportPeriodsLabel
import com.tlincompose.core.exportRecordedDaysLabel
import com.tlincompose.core.exportReportLabel
import com.tlincompose.core.exportTotalHoursLabel
import com.tlincompose.core.exportTypeLabel
import com.tlincompose.core.exportUserLabel
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
    val exportUserFullName: String? = null,
    val exportOfficeName: String? = null,
    val exportEmployeeId: String? = null,
    val exportPersonId: String? = null,
    val brandingLogoBase64: String? = null,
    val pdfExportStyle: PdfExportStyle = PdfExportStyle.RETRO,
)

sealed interface SpreadsheetCell {
    val style: SpreadsheetCellStyle
    val mergeAcross: Int

    data class Text(
        val value: String,
        override val style: SpreadsheetCellStyle = SpreadsheetCellStyle.BODY_TEXT,
        override val mergeAcross: Int = 0,
    ) : SpreadsheetCell

    data class Number(
        val value: String,
        override val style: SpreadsheetCellStyle = SpreadsheetCellStyle.BODY_NUMBER,
        override val mergeAcross: Int = 0,
    ) : SpreadsheetCell
}

enum class SpreadsheetCellStyle {
    TITLE,
    SECTION_HEADER,
    METADATA_LABEL,
    METADATA_VALUE,
    TABLE_HEADER,
    BODY_TEXT,
    BODY_NUMBER,
    BLOCK_HEADER,
}

data class SpreadsheetPositionedCell(
    val columnIndex: Int,
    val cell: SpreadsheetCell,
)

data class SpreadsheetRowLayout(
    val cells: List<SpreadsheetPositionedCell> = emptyList(),
)

data class SpreadsheetSheetLayout(
    val columnWidths: List<Double>,
    val rows: List<SpreadsheetRowLayout>,
    val freezePaneCell: String? = null,
)

data class ExportMetadataRow(
    val label: String,
    val value: String,
    val spreadsheetCell: SpreadsheetCell = SpreadsheetCell.Text(
        value = value,
        style = SpreadsheetCellStyle.METADATA_VALUE,
    ),
)

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
    val rows = buildList<List<String>> {
        metadataRows(strings).forEach { row ->
            add(listOf(row.label, row.value))
        }
        add(emptyList())
        when (pdfExportStyle) {
            PdfExportStyle.RETRO,
            PdfExportStyle.SIMPLE_TABLE -> addAll(tabularCsvRows(strings))
            PdfExportStyle.COMPACT_LIST -> addAll(compactCsvRows(strings))
            PdfExportStyle.DETAIL_BLOCKS -> addAll(detailCsvRows(strings))
        }
    }

    return rows.joinToString(separator = "\n") { line ->
        line.joinToString(separator = ";") { cell ->
            "\"${cell.replace("\"", "\"\"")}\""
        }
    }
}

fun ExportReport.toSpreadsheetSheet(title: String): SpreadsheetSheetLayout {
    val strings = appStrings(language)
    return when (pdfExportStyle) {
        PdfExportStyle.RETRO -> buildTabularSpreadsheetSheet(
            title = title,
            strings = strings,
            columns = retroTableColumns(strings),
        )
        PdfExportStyle.SIMPLE_TABLE -> buildTabularSpreadsheetSheet(
            title = title,
            strings = strings,
            columns = simpleTableColumns(strings),
        )
        PdfExportStyle.COMPACT_LIST -> buildCompactSpreadsheetSheet(title, strings)
        PdfExportStyle.DETAIL_BLOCKS -> buildDetailSpreadsheetSheet(title, strings)
    }
}

fun ExportReport.toSpreadsheetRows(title: String): List<List<SpreadsheetCell>> =
    toSpreadsheetSheet(title).rows.map { row ->
        buildList {
            row.cells.sortedBy(SpreadsheetPositionedCell::columnIndex).forEach { positionedCell ->
                add(positionedCell.cell)
            }
        }
    }

private fun ExportReport.tabularCsvRows(strings: AppStrings): List<List<String>> = buildList {
    val columns = when (pdfExportStyle) {
        PdfExportStyle.SIMPLE_TABLE -> simpleTableColumns(strings)
        else -> retroTableColumns(strings)
    }
    add(listOf(strings.exportActivitiesLabel))
    add(columns.map(ExportTableColumn::header))
    rows.forEach { row ->
        add(columns.map { column -> row.valueFor(column.key) })
    }
    if (rows.isEmpty()) {
        add(listOf(strings.exportNoActivitiesForSelectedPeriod))
    }
}

private fun ExportReport.compactCsvRows(strings: AppStrings): List<List<String>> = buildList {
    add(listOf(strings.exportActivitiesLabel))
    if (rows.isEmpty()) {
        add(listOf(strings.exportNoActivitiesForSelectedPeriod))
        return@buildList
    }
    rows.forEach { row ->
        add(
            listOf(
                strings.exportActivityLabel,
                row.activityTitle,
                strings.exportActivityCodeLabel,
                row.activityCode,
            ),
        )
        add(
            listOf(
                strings.exportPeriodsLabel,
                row.periodsLabel,
                strings.exportTypeLabel,
                row.typeLabel,
            ),
        )
        add(
            listOf(
                strings.exportHoursPerDayLabel,
                row.hoursPerDayLabel,
                strings.exportDaysLabel,
                row.days.toString(),
                strings.exportTotalHoursLabel,
                formatHours(row.totalMinutes),
            ),
        )
        add(emptyList())
    }
}

private fun ExportReport.detailCsvRows(strings: AppStrings): List<List<String>> = buildList {
    add(listOf(strings.exportActivitiesLabel))
    if (rows.isEmpty()) {
        add(listOf(strings.exportNoActivitiesForSelectedPeriod))
        return@buildList
    }
    rows.forEach { row ->
        add(listOf(strings.exportActivityLabel, row.heading()))
        add(listOf(strings.exportTypeLabel, row.typeLabel))
        add(listOf(strings.exportPeriodsLabel, row.periodsLabel))
        add(listOf(strings.exportHoursPerDayLabel, row.hoursPerDayLabel))
        add(listOf(strings.exportDaysLabel, row.days.toString()))
        add(listOf(strings.exportTotalHoursLabel, formatHours(row.totalMinutes)))
        add(emptyList())
    }
}

private fun ExportReport.buildTabularSpreadsheetSheet(
    title: String,
    strings: AppStrings,
    columns: List<ExportTableColumn>,
): SpreadsheetSheetLayout {
    val sheetRows = buildBaseSpreadsheetRows(title, strings).toMutableList()
    sheetRows += sectionHeaderRow(strings.exportActivitiesLabel)
    sheetRows += SpreadsheetRowLayout(
        cells = columns.mapIndexed { index, column ->
            SpreadsheetPositionedCell(
                columnIndex = index,
                cell = SpreadsheetCell.Text(
                    value = column.header,
                    style = SpreadsheetCellStyle.TABLE_HEADER,
                ),
            )
        },
    )
    rows.forEach { row ->
        sheetRows += SpreadsheetRowLayout(
            cells = columns.mapIndexed { index, column ->
                SpreadsheetPositionedCell(
                    columnIndex = index,
                    cell = row.spreadsheetCellFor(column.key),
                )
            },
        )
    }
    if (rows.isEmpty()) {
        sheetRows += SpreadsheetRowLayout(
            cells = listOf(
                SpreadsheetPositionedCell(
                    columnIndex = 0,
                    cell = SpreadsheetCell.Text(
                        value = strings.exportNoActivitiesForSelectedPeriod,
                        style = SpreadsheetCellStyle.BODY_TEXT,
                        mergeAcross = columns.lastIndex,
                    ),
                ),
            ),
        )
    }
    return SpreadsheetSheetLayout(
        columnWidths = columns.map(ExportTableColumn::spreadsheetWidth),
        rows = sheetRows,
        freezePaneCell = "A${sheetRows.indexOfLast { row ->
            row.cells.firstOrNull()?.cell?.style == SpreadsheetCellStyle.TABLE_HEADER
        } + 2}",
    )
}

private fun ExportReport.buildCompactSpreadsheetSheet(
    title: String,
    strings: AppStrings,
): SpreadsheetSheetLayout {
    val sheetRows = buildBaseSpreadsheetRows(title, strings).toMutableList()
    sheetRows += sectionHeaderRow(strings.exportActivitiesLabel)
    if (rows.isEmpty()) {
        sheetRows += SpreadsheetRowLayout(
            cells = listOf(
                SpreadsheetPositionedCell(
                    columnIndex = 0,
                    cell = SpreadsheetCell.Text(
                        value = strings.exportNoActivitiesForSelectedPeriod,
                        style = SpreadsheetCellStyle.BODY_TEXT,
                        mergeAcross = 6,
                    ),
                ),
            ),
        )
    } else {
        rows.forEach { row ->
            sheetRows += SpreadsheetRowLayout(
                cells = listOf(
                    SpreadsheetPositionedCell(
                        columnIndex = 0,
                        cell = SpreadsheetCell.Text(
                            value = row.activityTitle,
                            style = SpreadsheetCellStyle.BLOCK_HEADER,
                            mergeAcross = 3,
                        ),
                    ),
                    SpreadsheetPositionedCell(
                        columnIndex = 4,
                        cell = SpreadsheetCell.Text(
                            value = strings.exportActivityCodeLabel,
                            style = SpreadsheetCellStyle.METADATA_LABEL,
                        ),
                    ),
                    SpreadsheetPositionedCell(
                        columnIndex = 5,
                        cell = SpreadsheetCell.Text(
                            value = row.activityCode,
                            style = SpreadsheetCellStyle.METADATA_VALUE,
                            mergeAcross = 1,
                        ),
                    ),
                ),
            )
            sheetRows += labeledSpreadsheetRow(
                label = strings.exportPeriodsLabel,
                value = SpreadsheetCell.Text(
                    value = row.periodsLabel,
                    style = SpreadsheetCellStyle.METADATA_VALUE,
                    mergeAcross = 5,
                ),
            )
            sheetRows += SpreadsheetRowLayout(
                cells = listOf(
                    SpreadsheetPositionedCell(0, SpreadsheetCell.Text(strings.exportTypeLabel, SpreadsheetCellStyle.METADATA_LABEL)),
                    SpreadsheetPositionedCell(1, SpreadsheetCell.Text(row.typeLabel, SpreadsheetCellStyle.METADATA_VALUE)),
                    SpreadsheetPositionedCell(3, SpreadsheetCell.Text(strings.exportHoursPerDayLabel, SpreadsheetCellStyle.METADATA_LABEL)),
                    SpreadsheetPositionedCell(4, SpreadsheetCell.Text(row.hoursPerDayLabel, SpreadsheetCellStyle.METADATA_VALUE)),
                    SpreadsheetPositionedCell(5, SpreadsheetCell.Text(strings.exportDaysLabel, SpreadsheetCellStyle.METADATA_LABEL)),
                    SpreadsheetPositionedCell(6, SpreadsheetCell.Number(row.days.toString())),
                ),
            )
            sheetRows += labeledSpreadsheetRow(
                label = strings.exportTotalHoursLabel,
                value = SpreadsheetCell.Number(formatHours(row.totalMinutes), mergeAcross = 5),
            )
            sheetRows += SpreadsheetRowLayout()
        }
    }
    return SpreadsheetSheetLayout(
        columnWidths = listOf(18.0, 22.0, 12.0, 16.0, 14.0, 12.0, 10.0),
        rows = sheetRows,
    )
}

private fun ExportReport.buildDetailSpreadsheetSheet(
    title: String,
    strings: AppStrings,
): SpreadsheetSheetLayout {
    val sheetRows = buildBaseSpreadsheetRows(title, strings).toMutableList()
    sheetRows += sectionHeaderRow(strings.exportActivitiesLabel)
    if (rows.isEmpty()) {
        sheetRows += SpreadsheetRowLayout(
            cells = listOf(
                SpreadsheetPositionedCell(
                    columnIndex = 0,
                    cell = SpreadsheetCell.Text(
                        value = strings.exportNoActivitiesForSelectedPeriod,
                        style = SpreadsheetCellStyle.BODY_TEXT,
                        mergeAcross = 6,
                    ),
                ),
            ),
        )
    } else {
        rows.forEach { row ->
            sheetRows += SpreadsheetRowLayout(
                cells = listOf(
                    SpreadsheetPositionedCell(
                        columnIndex = 0,
                        cell = SpreadsheetCell.Text(
                            value = row.heading(),
                            style = SpreadsheetCellStyle.BLOCK_HEADER,
                            mergeAcross = 6,
                        ),
                    ),
                ),
            )
            sheetRows += labeledSpreadsheetRow(strings.exportTypeLabel, SpreadsheetCell.Text(row.typeLabel, SpreadsheetCellStyle.METADATA_VALUE, mergeAcross = 5))
            sheetRows += labeledSpreadsheetRow(strings.exportPeriodsLabel, SpreadsheetCell.Text(row.periodsLabel, SpreadsheetCellStyle.METADATA_VALUE, mergeAcross = 5))
            sheetRows += labeledSpreadsheetRow(strings.exportHoursPerDayLabel, SpreadsheetCell.Text(row.hoursPerDayLabel, SpreadsheetCellStyle.METADATA_VALUE, mergeAcross = 5))
            sheetRows += labeledSpreadsheetRow(strings.exportDaysLabel, SpreadsheetCell.Number(row.days.toString(), mergeAcross = 5))
            sheetRows += labeledSpreadsheetRow(strings.exportTotalHoursLabel, SpreadsheetCell.Number(formatHours(row.totalMinutes), mergeAcross = 5))
            sheetRows += SpreadsheetRowLayout()
        }
    }
    return SpreadsheetSheetLayout(
        columnWidths = listOf(18.0, 24.0, 12.0, 12.0, 12.0, 12.0, 12.0),
        rows = sheetRows,
    )
}

private fun ExportReport.buildBaseSpreadsheetRows(
    title: String,
    strings: AppStrings,
): List<SpreadsheetRowLayout> = buildList {
    add(
        SpreadsheetRowLayout(
            cells = listOf(
                SpreadsheetPositionedCell(
                    columnIndex = 0,
                    cell = SpreadsheetCell.Text(
                        value = title,
                        style = SpreadsheetCellStyle.TITLE,
                        mergeAcross = 6,
                    ),
                ),
            ),
        ),
    )
    metadataRows(strings).forEach { row ->
        add(labeledSpreadsheetRow(row.label, row.spreadsheetCell.withMergeAcross(5)))
    }
    add(SpreadsheetRowLayout())
}

private fun sectionHeaderRow(label: String): SpreadsheetRowLayout = SpreadsheetRowLayout(
    cells = listOf(
        SpreadsheetPositionedCell(
            columnIndex = 0,
            cell = SpreadsheetCell.Text(
                value = label,
                style = SpreadsheetCellStyle.SECTION_HEADER,
                mergeAcross = 6,
            ),
        ),
    ),
)

private fun labeledSpreadsheetRow(
    label: String,
    value: SpreadsheetCell,
): SpreadsheetRowLayout = SpreadsheetRowLayout(
    cells = listOf(
        SpreadsheetPositionedCell(
            columnIndex = 0,
            cell = SpreadsheetCell.Text(
                value = label,
                style = SpreadsheetCellStyle.METADATA_LABEL,
            ),
        ),
        SpreadsheetPositionedCell(columnIndex = 1, cell = value),
    ),
)

private fun SpreadsheetCell.withMergeAcross(mergeAcross: Int): SpreadsheetCell = when (this) {
    is SpreadsheetCell.Text -> copy(mergeAcross = mergeAcross)
    is SpreadsheetCell.Number -> copy(mergeAcross = mergeAcross)
}

private enum class ExportTableColumnKey {
    ACTIVITY_CODE,
    ACTIVITY_TITLE,
    TYPE,
    PERIODS,
    HOURS_PER_DAY,
    DAYS,
    TOTAL_HOURS,
}

private data class ExportTableColumn(
    val key: ExportTableColumnKey,
    val header: String,
    val spreadsheetWidth: Double,
)

private fun retroTableColumns(strings: AppStrings): List<ExportTableColumn> = listOf(
    ExportTableColumn(ExportTableColumnKey.ACTIVITY_CODE, strings.exportActivityCodeLabel, 14.0),
    ExportTableColumn(ExportTableColumnKey.ACTIVITY_TITLE, strings.exportActivityLabel, 30.0),
    ExportTableColumn(ExportTableColumnKey.TYPE, strings.exportTypeLabel, 14.0),
    ExportTableColumn(ExportTableColumnKey.PERIODS, strings.exportPeriodsLabel, 28.0),
    ExportTableColumn(ExportTableColumnKey.HOURS_PER_DAY, strings.exportHoursPerDayLabel, 12.0),
    ExportTableColumn(ExportTableColumnKey.DAYS, strings.exportDaysLabel, 8.0),
    ExportTableColumn(ExportTableColumnKey.TOTAL_HOURS, strings.exportTotalHoursLabel, 12.0),
)

private fun simpleTableColumns(strings: AppStrings): List<ExportTableColumn> = listOf(
    ExportTableColumn(ExportTableColumnKey.ACTIVITY_TITLE, strings.exportActivityLabel, 30.0),
    ExportTableColumn(ExportTableColumnKey.ACTIVITY_CODE, strings.exportActivityCodeLabel, 14.0),
    ExportTableColumn(ExportTableColumnKey.PERIODS, strings.exportPeriodsLabel, 28.0),
    ExportTableColumn(ExportTableColumnKey.TYPE, strings.exportTypeLabel, 14.0),
    ExportTableColumn(ExportTableColumnKey.HOURS_PER_DAY, strings.exportHoursPerDayLabel, 12.0),
    ExportTableColumn(ExportTableColumnKey.DAYS, strings.exportDaysLabel, 8.0),
    ExportTableColumn(ExportTableColumnKey.TOTAL_HOURS, strings.exportTotalHoursLabel, 12.0),
)

private fun ExportRow.valueFor(key: ExportTableColumnKey): String = when (key) {
    ExportTableColumnKey.ACTIVITY_CODE -> activityCode
    ExportTableColumnKey.ACTIVITY_TITLE -> activityTitle
    ExportTableColumnKey.TYPE -> typeLabel
    ExportTableColumnKey.PERIODS -> periodsLabel
    ExportTableColumnKey.HOURS_PER_DAY -> hoursPerDayLabel
    ExportTableColumnKey.DAYS -> days.toString()
    ExportTableColumnKey.TOTAL_HOURS -> formatHours(totalMinutes)
}

private fun ExportRow.spreadsheetCellFor(key: ExportTableColumnKey): SpreadsheetCell = when (key) {
    ExportTableColumnKey.DAYS -> SpreadsheetCell.Number(days.toString())
    ExportTableColumnKey.TOTAL_HOURS -> SpreadsheetCell.Number(formatHours(totalMinutes))
    else -> SpreadsheetCell.Text(valueFor(key))
}

private fun ExportRow.heading(): String =
    if (activityCode == "-") {
        activityTitle
    } else {
        "$activityCode - $activityTitle"
    }

fun ExportReport.metadataRows(strings: AppStrings): List<ExportMetadataRow> = buildList {
    add(ExportMetadataRow(strings.exportReportLabel, strings.appName))
    add(ExportMetadataRow(strings.exportPeriodLabel, periodLabel))
    exportUserFullName?.takeIf(String::isNotBlank)?.let { fullName ->
        add(ExportMetadataRow(strings.exportUserLabel, fullName))
    }
    exportOfficeName?.takeIf(String::isNotBlank)?.let { officeName ->
        add(ExportMetadataRow(strings.exportOfficeLabel, officeName))
    }
    exportEmployeeId?.takeIf(String::isNotBlank)?.let { employeeId ->
        add(ExportMetadataRow(strings.exportEmployeeIdMetadataLabel, employeeId))
    }
    exportPersonId?.takeIf(String::isNotBlank)?.let { personId ->
        add(ExportMetadataRow(strings.exportPersonIdMetadataLabel, personId))
    }
    add(ExportMetadataRow(strings.exportGeneratedAtLabel, strings.exportGeneratedAtValue(exportedAt)))
    add(
        ExportMetadataRow(
            label = strings.exportRecordedDaysLabel,
            value = summary.recordedDays.toString(),
            spreadsheetCell = SpreadsheetCell.Number(summary.recordedDays.toString()),
        ),
    )
    add(
        ExportMetadataRow(
            label = strings.exportActivitiesLabel,
            value = summary.activityCount.toString(),
            spreadsheetCell = SpreadsheetCell.Number(summary.activityCount.toString()),
        ),
    )
    add(
        ExportMetadataRow(
            label = strings.exportTotalHoursLabel,
            value = formatHours(summary.totalLoggedMinutes),
            spreadsheetCell = SpreadsheetCell.Number(formatHours(summary.totalLoggedMinutes)),
        ),
    )
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
