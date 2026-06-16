@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.tlincompose.data.export

import co.touchlab.kermit.Logger
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.core.LocalDateComparator
import com.tlincompose.core.appStrings
import com.tlincompose.core.displayLabel
import com.tlincompose.core.exportDocumentTitle
import com.tlincompose.core.formatDateRange
import com.tlincompose.core.formatFileDate
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.model.ExportFormat
import com.tlincompose.domain.model.MonthRange
import com.tlincompose.domain.model.PdfExportStyle
import com.tlincompose.domain.repository.TimesheetExporter
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlin.time.Clock

expect fun buildXlsx(report: ExportReport, title: String): ByteArray

class DefaultMonthExporter(
    private val dispatcherProvider: DispatcherProvider,
    logger: Logger,
    private val xlsxEncoder: (ExportReport, String) -> ByteArray = ::buildXlsx,
    private val pdfEncoder: (ExportReport, String) -> ByteArray = PdfReportWriter::build,
    private val nowProvider: () -> Instant = { Clock.System.now() },
) : TimesheetExporter {
    private val log = logger.withTag("DefaultMonthExporter")

    override suspend fun exportMonth(
        month: CalendarMonth,
        entries: List<DailyEntry>,
        format: ExportFormat,
        language: AppLanguage,
        brandingLogoBase64: String?,
        pdfExportStyle: PdfExportStyle,
    ): ExportDocument = withContext(dispatcherProvider.io) {
        log.i { "Avvio export ${format.name} per il mese ${month.fileStamp}." }
        val monthLabel = month.displayLabel(language)
        val strings = appStrings(language)
        buildDocument(
            title = strings.exportDocumentTitle(monthLabel),
            periodLabel = monthLabel,
            fileNameBase = "TLInCompose_${month.fileStamp}",
            entries = entries,
            format = format,
            language = language,
            brandingLogoBase64 = brandingLogoBase64,
            pdfExportStyle = pdfExportStyle,
        )
    }

    override suspend fun exportDateRange(
        range: DateRange,
        entries: List<DailyEntry>,
        format: ExportFormat,
        language: AppLanguage,
        brandingLogoBase64: String?,
        pdfExportStyle: PdfExportStyle,
    ): ExportDocument = withContext(dispatcherProvider.io) {
        log.i { "Avvio export ${format.name} per l'intervallo ${range.startDate} - ${range.endDate}." }
        val strings = appStrings(language)
        val periodLabel = formatDateRange(range.startDate, range.endDate)
        buildDocument(
            title = strings.exportDocumentTitle(periodLabel),
            periodLabel = periodLabel,
            fileNameBase = "TLInCompose_${formatFileDate(range.startDate)}_${formatFileDate(range.endDate)}",
            entries = entries,
            format = format,
            language = language,
            brandingLogoBase64 = brandingLogoBase64,
            pdfExportStyle = pdfExportStyle,
        )
    }

    override suspend fun exportMonthRange(
        range: MonthRange,
        entries: List<DailyEntry>,
        format: ExportFormat,
        language: AppLanguage,
        brandingLogoBase64: String?,
        pdfExportStyle: PdfExportStyle,
    ): ExportDocument = withContext(dispatcherProvider.io) {
        log.i { "Avvio export ${format.name} per i mesi ${range.startMonth.fileStamp} - ${range.endMonth.fileStamp}." }
        val strings = appStrings(language)
        val periodLabel = range.displayLabel(language)
        buildDocument(
            title = strings.exportDocumentTitle(periodLabel),
            periodLabel = periodLabel,
            fileNameBase = if (range.startMonth == range.endMonth) {
                "TLInCompose_${range.startMonth.fileStamp}"
            } else {
                "TLInCompose_${range.startMonth.fileStamp}_${range.endMonth.fileStamp}"
            },
            entries = entries,
            format = format,
            language = language,
            brandingLogoBase64 = brandingLogoBase64,
            pdfExportStyle = pdfExportStyle,
        )
    }

    private fun buildDocument(
        title: String,
        periodLabel: String,
        fileNameBase: String,
        entries: List<DailyEntry>,
        format: ExportFormat,
        language: AppLanguage,
        brandingLogoBase64: String?,
        pdfExportStyle: PdfExportStyle,
    ): ExportDocument {
        val orderedEntries = entries.sortedWith(compareBy(LocalDateComparator) { it.date })
        val report = ExportReport(
            periodLabel = periodLabel,
            exportedAt = nowProvider(),
            rows = groupActivitiesForExport(orderedEntries, language),
            summary = ExportSummary(
                recordedDays = orderedEntries.size,
                activityCount = orderedEntries.sumOf { it.activities.size },
                totalLoggedMinutes = orderedEntries.sumOf { entry -> entry.activities.sumOf { it.minutes } },
            ),
            language = language,
            brandingLogoBase64 = brandingLogoBase64,
            pdfExportStyle = pdfExportStyle,
        )

        val bytes = when (format) {
            ExportFormat.CSV -> report.toCsv().encodeToByteArray()
            ExportFormat.XLSX -> xlsxEncoder(report, title)
            ExportFormat.PDF -> pdfEncoder(report, title)
        }

        val document = ExportDocument(
            fileName = "$fileNameBase.${format.extension}",
            mimeType = format.mimeType,
            bytes = bytes,
        )
        log.d { "Creato file ${document.fileName} con ${report.rows.size} righe esportate." }
        return document
    }
}
