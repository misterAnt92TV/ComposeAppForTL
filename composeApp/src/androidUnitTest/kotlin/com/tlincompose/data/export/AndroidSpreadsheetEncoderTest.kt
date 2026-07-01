package com.tlincompose.data.export

import com.tlincompose.domain.model.AppLanguage
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(kotlin.time.ExperimentalTime::class, ExperimentalEncodingApi::class)
class AndroidSpreadsheetEncoderTest {
    @Test
    fun xlsxIncludesBrandingEntriesWhenLogoIsPresent() {
        val bytes = buildXlsx(
            report = ExportReport(
                periodLabel = "Maggio 2026",
                exportedAt = Instant.parse("2026-05-20T14:35:00Z"),
                rows = emptyList(),
                summary = ExportSummary(0, 0, 0),
                language = AppLanguage.ITALIAN,
                brandingLogoBase64 = Base64.Default.encode(minimalJpegBytes(width = 800, height = 350)),
            ),
            title = "Export",
        )

        val entries = zipEntries(bytes)
        assertTrue("xl/media/brand-logo.jpg" in entries)
        assertTrue("xl/drawings/drawing1.xml" in entries)
        assertTrue("xl/worksheets/_rels/sheet1.xml.rels" in entries)
    }

    @Test
    fun xlsxDoesNotIncludeBrandingEntriesWhenLogoIsMissing() {
        val bytes = buildXlsx(
            report = ExportReport(
                periodLabel = "Maggio 2026",
                exportedAt = Instant.parse("2026-05-20T14:35:00Z"),
                rows = emptyList(),
                summary = ExportSummary(0, 0, 0),
                language = AppLanguage.ITALIAN,
            ),
            title = "Export",
        )

        val entries = zipEntries(bytes)
        assertFalse("xl/media/brand-logo.jpg" in entries)
        assertFalse("xl/drawings/drawing1.xml" in entries)
    }

    @Test
    fun xlsxAddsStylesAndMergedTitleForStyledLayouts() {
        val bytes = buildXlsx(
            report = ExportReport(
                periodLabel = "Maggio 2026",
                exportedAt = Instant.parse("2026-05-20T14:35:00Z"),
                rows = listOf(
                    ExportRow(
                        activityCode = "EXT-0001",
                        activityTitle = "Attività Demo",
                        typeLabel = "Project",
                        periods = listOf(ExportPeriod(LocalDate(2026, 5, 5), LocalDate(2026, 5, 6))),
                        hoursPerDayLabel = "8",
                        days = 2,
                        totalMinutes = 960,
                    ),
                ),
                summary = ExportSummary(2, 1, 960),
                language = AppLanguage.ITALIAN,
            ),
            title = "Export",
        )

        val entries = zipEntries(bytes)
        val sheetXml = zipEntryText(bytes, "xl/worksheets/sheet1.xml")
        assertTrue("xl/styles.xml" in entries)
        assertTrue(sheetXml.contains("mergeCell ref=\"A1:G1\""))
        assertTrue(sheetXml.contains("state=\"frozen\""))
    }

    @Test
    fun xlsxDetailStyleUsesBlockLayout() {
        val bytes = buildXlsx(
            report = ExportReport(
                periodLabel = "Maggio 2026",
                exportedAt = Instant.parse("2026-05-20T14:35:00Z"),
                rows = listOf(
                    ExportRow(
                        activityCode = "EXT-0001",
                        activityTitle = "Attività Demo",
                        typeLabel = "Project",
                        periods = listOf(ExportPeriod(LocalDate(2026, 5, 5), LocalDate(2026, 5, 6))),
                        hoursPerDayLabel = "8",
                        days = 2,
                        totalMinutes = 960,
                    ),
                ),
                summary = ExportSummary(2, 1, 960),
                language = AppLanguage.ITALIAN,
                pdfExportStyle = com.tlincompose.domain.model.PdfExportStyle.DETAIL_BLOCKS,
            ),
            title = "Export",
        )

        val sheetXml = zipEntryText(bytes, "xl/worksheets/sheet1.xml")
        assertTrue(sheetXml.contains("EXT-0001 - Attività Demo"))
        assertFalse(sheetXml.contains("Codice attività"))
    }

    private fun zipEntries(bytes: ByteArray): Set<String> = buildSet {
        ZipInputStream(ByteArrayInputStream(bytes)).use { input ->
            generateSequence { input.nextEntry }
                .forEach { add(it.name) }
        }
    }

    private fun zipEntryText(bytes: ByteArray, entryName: String): String =
        ZipInputStream(ByteArrayInputStream(bytes)).use { input ->
            generateSequence { input.nextEntry }
                .first { it.name == entryName }
            input.readBytes().decodeToString()
        }
}

private fun minimalJpegBytes(width: Int, height: Int): ByteArray = byteArrayOf(
    0xFF.toByte(), 0xD8.toByte(),
    0xFF.toByte(), 0xC0.toByte(),
    0x00, 0x11,
    0x08,
    ((height shr 8) and 0xFF).toByte(), (height and 0xFF).toByte(),
    ((width shr 8) and 0xFF).toByte(), (width and 0xFF).toByte(),
    0x03,
    0x01, 0x11, 0x00,
    0x02, 0x11, 0x00,
    0x03, 0x11, 0x00,
    0xFF.toByte(), 0xD9.toByte(),
)
