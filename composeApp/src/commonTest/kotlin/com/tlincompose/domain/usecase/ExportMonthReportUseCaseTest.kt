package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ExportActivityTypeFilter
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.model.ExportFormat
import com.tlincompose.domain.model.MonthRange
import com.tlincompose.domain.model.PdfExportStyle
import com.tlincompose.domain.repository.TimesheetExporter
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportMonthReportUseCaseTest {
    @Test
    fun filtersEntriesBeforeDelegatingToExporter() = runTest {
        val exporter = RecordingExporter()
        val entries = listOf(
            DailyEntry(
                LocalDate(2026, 5, 10),
                listOf(
                    Activity(type = EntryType.PROJECT, extCode = "EXT-0001", title = "Apollo", minutes = 480),
                    Activity(type = EntryType.PERMIT, extCode = "EXT-0002", title = "Permesso", minutes = 120),
                ),
            ),
        )

        val result = ExportMonthReportUseCase(
            exporter = exporter,
            filterExportEntries = FilterExportEntriesUseCase(),
        )(
            month = CalendarMonth(2026, 5),
            entries = entries,
            format = ExportFormat.CSV,
            language = AppLanguage.ITALIAN,
            filter = ExportActivityTypeFilter(setOf(EntryType.PERMIT)),
        )

        assertEquals(listOf(EntryType.PERMIT), exporter.exportedEntries.single().activities.map(Activity::type))
        assertEquals("month.csv", result.fileName)
    }

    private class RecordingExporter : TimesheetExporter {
        var exportedEntries: List<DailyEntry> = emptyList()
        var brandingLogoBase64: String? = null
        var pdfExportStyle: PdfExportStyle? = null

        override suspend fun exportMonth(
            month: CalendarMonth,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument {
            exportedEntries = entries
            this.brandingLogoBase64 = brandingLogoBase64
            this.pdfExportStyle = pdfExportStyle
            return ExportDocument("month.csv", format.mimeType, byteArrayOf())
        }

        override suspend fun exportDateRange(
            range: DateRange,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument = error("exportDateRange should not be called in this test")

        override suspend fun exportMonthRange(
            range: MonthRange,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument = error("exportMonthRange should not be called in this test")
    }
}
