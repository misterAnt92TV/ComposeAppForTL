@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.tlincompose.data.export

import co.touchlab.kermit.Logger
import com.tlincompose.TestDispatcherProvider
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ExportFormat
import com.tlincompose.domain.model.MonthRange
import com.tlincompose.domain.model.PdfExportStyle
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultMonthExporterTest {
    @Test
    fun activitiesWithSameCodeAreGroupedIntoSingleExportRow() {
        val rows = groupActivitiesForExport(
            entries = listOf(
                DailyEntry(
                    LocalDate(2026, 5, 5),
                    listOf(Activity(type = EntryType.PROJECT, extCode = "EXT-0001", title = "Apollo", minutes = 480)),
                ),
                DailyEntry(
                    LocalDate(2026, 5, 6),
                    listOf(Activity(type = EntryType.PROJECT, extCode = "EXT-0001", title = "Apollo", minutes = 480)),
                ),
                DailyEntry(
                    LocalDate(2026, 5, 8),
                    listOf(Activity(type = EntryType.PROJECT, extCode = "EXT-0001", title = "Apollo Platform", minutes = 240)),
                ),
                DailyEntry(
                    LocalDate(2026, 5, 9),
                    listOf(Activity(type = EntryType.VACATION, extCode = "EXT-0002", title = "Ferie", minutes = 240)),
                ),
            ),
            language = AppLanguage.ENGLISH,
        )

        assertEquals(2, rows.size)
        assertEquals("EXT-0001", rows[0].activityCode)
        assertEquals("Apollo / Apollo Platform", rows[0].activityTitle)
        assertEquals("05/05/2026 - 06/05/2026, 08/05/2026", rows[0].periodsLabel)
        assertEquals("4 / 8", rows[0].hoursPerDayLabel)
        assertEquals(3, rows[0].days)
        assertEquals(1200, rows[0].totalMinutes)
    }

    @Test
    fun exportProducesExpectedNamesAndFormats() = runTest {
        val exportedAt = Instant.parse("2026-05-20T14:35:00Z")
        val exporter = DefaultMonthExporter(
            dispatcherProvider = TestDispatcherProvider(StandardTestDispatcher(testScheduler)),
            logger = Logger.withTag("DefaultMonthExporterTest"),
            xlsxEncoder = { _, _ -> "xlsx".encodeToByteArray() },
            pdfEncoder = { _, _ -> "%PDF-1.4".encodeToByteArray() },
            nowProvider = { exportedAt },
        )
        val entries = listOf(
            DailyEntry(
                LocalDate(2026, 5, 1),
                listOf(Activity(type = EntryType.VACATION, extCode = "EXT-0002", title = "Ferie", minutes = 480)),
            ),
        )

        val csv = exporter.exportMonth(CalendarMonth(2026, 5), entries, ExportFormat.CSV, AppLanguage.ENGLISH)
        val xlsx = exporter.exportMonth(CalendarMonth(2026, 5), entries, ExportFormat.XLSX, AppLanguage.ENGLISH)
        val pdf = exporter.exportMonth(CalendarMonth(2026, 5), entries, ExportFormat.PDF, AppLanguage.ENGLISH)

        assertEquals("TLInCompose_2026-05.csv", csv.fileName)
        assertTrue(csv.bytes.decodeToString().contains("Period"))
        assertTrue(csv.bytes.decodeToString().contains("Exported at"))
        assertTrue(csv.bytes.decodeToString().contains("20/05/2026 16:35"))
        assertTrue(csv.bytes.decodeToString().contains("Recorded days"))
        assertTrue(csv.bytes.decodeToString().contains("\"Activity code\";\"Activity\";\"Type\";\"Periods\";\"Hours/day\";\"Days\";\"Total hours\""))
        assertTrue(csv.bytes.decodeToString().contains("\"EXT-0002\";\"Ferie\";\"Vacation\";\"01/05/2026\";\"8\";\"1\";\"8\""))
        assertEquals("TLInCompose_2026-05.xlsx", xlsx.fileName)
        assertContentEquals("xlsx".encodeToByteArray(), xlsx.bytes)
        assertEquals("TLInCompose_2026-05.pdf", pdf.fileName)
        assertTrue(pdf.bytes.decodeToString().startsWith("%PDF-1.4"))
    }

    @Test
    fun exportDateRangeProducesExpectedFileName() = runTest {
        val exporter = DefaultMonthExporter(
            dispatcherProvider = TestDispatcherProvider(StandardTestDispatcher(testScheduler)),
            logger = Logger.withTag("DefaultMonthExporterTest"),
            xlsxEncoder = { _, _ -> "xlsx".encodeToByteArray() },
            pdfEncoder = { _, _ -> "%PDF-1.4".encodeToByteArray() },
        )
        val entries = listOf(
            DailyEntry(
                LocalDate(2026, 5, 10),
                listOf(Activity(type = EntryType.PROJECT, extCode = "EXT-0001", title = "Apollo", minutes = 480)),
            ),
            DailyEntry(
                LocalDate(2026, 5, 12),
                listOf(Activity(type = EntryType.PERMIT, extCode = "EXT-0003", title = "Permesso", minutes = 120)),
            ),
        )

        val pdf = exporter.exportDateRange(
            range = DateRange(
                startDate = LocalDate(2026, 5, 10),
                endDate = LocalDate(2026, 5, 12),
            ),
            entries = entries,
            format = ExportFormat.PDF,
            language = AppLanguage.ENGLISH,
        )

        assertEquals("TLInCompose_2026-05-10_2026-05-12.pdf", pdf.fileName)
        assertTrue(pdf.bytes.decodeToString().startsWith("%PDF-1.4"))
    }

    @Test
    fun exportMonthRangeProducesExpectedFileName() = runTest {
        val exporter = DefaultMonthExporter(
            dispatcherProvider = TestDispatcherProvider(StandardTestDispatcher(testScheduler)),
            logger = Logger.withTag("DefaultMonthExporterTest"),
            xlsxEncoder = { _, _ -> "xlsx".encodeToByteArray() },
            pdfEncoder = { _, _ -> "%PDF-1.4".encodeToByteArray() },
        )
        val entries = listOf(
            DailyEntry(
                LocalDate(2026, 5, 10),
                listOf(Activity(type = EntryType.PROJECT, extCode = "EXT-0001", title = "Apollo", minutes = 480)),
            ),
        )

        val pdf = exporter.exportMonthRange(
            range = MonthRange(
                startMonth = CalendarMonth(2026, 5),
                endMonth = CalendarMonth(2026, 7),
            ),
            entries = entries,
            format = ExportFormat.PDF,
            language = AppLanguage.ENGLISH,
        )

        assertEquals("TLInCompose_2026-05_2026-07.pdf", pdf.fileName)
        assertTrue(pdf.bytes.decodeToString().startsWith("%PDF-1.4"))
    }

    @Test
    fun exportPropagatesBrandingLogoAndLeavesCsvUnchanged() = runTest {
        var xlsxReport: ExportReport? = null
        var pdfReport: ExportReport? = null
        val exporter = DefaultMonthExporter(
            dispatcherProvider = TestDispatcherProvider(StandardTestDispatcher(testScheduler)),
            logger = Logger.withTag("DefaultMonthExporterTest"),
            xlsxEncoder = { report, _ ->
                xlsxReport = report
                "xlsx".encodeToByteArray()
            },
            pdfEncoder = { report, _ ->
                pdfReport = report
                "%PDF-1.4".encodeToByteArray()
            },
        )
        val entries = listOf(
            DailyEntry(
                LocalDate(2026, 5, 3),
                listOf(Activity(type = EntryType.PROJECT, extCode = "EXT-0001", title = "Apollo", minutes = 480)),
            ),
        )

        val csvWithoutLogo = exporter.exportMonth(
            month = CalendarMonth(2026, 5),
            entries = entries,
            format = ExportFormat.CSV,
            language = AppLanguage.ITALIAN,
        )
        val csvWithLogo = exporter.exportMonth(
            month = CalendarMonth(2026, 5),
            entries = entries,
            format = ExportFormat.CSV,
            language = AppLanguage.ITALIAN,
            brandingLogoBase64 = "AQID",
        )
        exporter.exportMonth(
            month = CalendarMonth(2026, 5),
            entries = entries,
            format = ExportFormat.XLSX,
            language = AppLanguage.ITALIAN,
            brandingLogoBase64 = "AQID",
        )
        exporter.exportMonth(
            month = CalendarMonth(2026, 5),
            entries = entries,
            format = ExportFormat.PDF,
            language = AppLanguage.ITALIAN,
            brandingLogoBase64 = "AQID",
            pdfExportStyle = PdfExportStyle.DETAIL_BLOCKS,
        )

        assertContentEquals(csvWithoutLogo.bytes, csvWithLogo.bytes)
        assertEquals("AQID", xlsxReport?.brandingLogoBase64)
        assertEquals(PdfExportStyle.DETAIL_BLOCKS, pdfReport?.pdfExportStyle)
    }
}
