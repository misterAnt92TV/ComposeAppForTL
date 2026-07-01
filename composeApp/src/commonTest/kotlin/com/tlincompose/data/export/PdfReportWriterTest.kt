package com.tlincompose.data.export

import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.PdfExportStyle
import kotlinx.datetime.Instant
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(kotlin.time.ExperimentalTime::class, ExperimentalEncodingApi::class)
class PdfReportWriterTest {
    @Test
    fun pdfIncludesBrandingImageBlockWhenLogoIsPresent() {
        val pdfBytes = PdfReportWriter.build(
            report = ExportReport(
                periodLabel = "Maggio 2026",
                exportedAt = Instant.parse("2026-05-20T14:35:00Z"),
                rows = emptyList(),
                summary = ExportSummary(
                    recordedDays = 0,
                    activityCount = 0,
                    totalLoggedMinutes = 0,
                ),
                language = AppLanguage.ITALIAN,
                exportUserFullName = "Mario Rossi",
                exportOfficeName = "Sede Milano",
                exportEmployeeId = "EMP-123",
                exportPersonId = "P-456",
                brandingLogoBase64 = Base64.Default.encode(minimalJpegBytes(width = 800, height = 350)),
            ),
            title = "Export",
            font = testPdfFont(),
        )

        val pdfText = pdfBytes.decodeToString()
        assertTrue(pdfText.contains("/DCTDecode"))
        assertTrue(pdfText.contains("/Im1 Do"))
        assertTrue(pdfText.contains("/FontFile2"))
    }

    @Test
    fun retroStyleKeepsPipeBasedTable() {
        val pdfText = buildPdfText(PdfExportStyle.RETRO)

        assertTrue(pdfText.contains("| Activity"))
        assertTrue(pdfText.contains("+--------------"))
    }

    @Test
    fun simpleTableStyleRemovesPipes() {
        val pdfText = buildPdfText(PdfExportStyle.SIMPLE_TABLE)

        assertTrue(pdfText.contains("Activity"))
        assertFalse(pdfText.contains("+--------------"))
    }

    @Test
    fun compactAndDetailStylesRenderAlternativeLayouts() {
        val compactText = buildPdfText(PdfExportStyle.COMPACT_LIST)
        val detailText = buildPdfText(PdfExportStyle.DETAIL_BLOCKS)

        assertTrue(compactText.contains("User: Mario Rossi"))
        assertTrue(compactText.contains("Office: Milan HQ"))
        assertTrue(compactText.contains("Employee ID: EMP-123"))
        assertTrue(compactText.contains("Person ID: P-456"))
        assertTrue(compactText.contains("Attivit\\340"))
        assertTrue(detailText.contains("============================================================================================"))
    }
}

@OptIn(ExperimentalEncodingApi::class, kotlin.time.ExperimentalTime::class)
private fun buildPdfText(style: PdfExportStyle): String {
    return PdfReportWriter.build(
        report = ExportReport(
            periodLabel = "Maggio 2026",
            exportedAt = Instant.parse("2026-05-20T14:35:00Z"),
            rows = listOf(
                ExportRow(
                    activityCode = "EXT-0001",
                    activityTitle = "Attività Demo",
                    typeLabel = "Project",
                    periods = listOf(ExportPeriod(kotlinx.datetime.LocalDate(2026, 5, 5), kotlinx.datetime.LocalDate(2026, 5, 6))),
                    hoursPerDayLabel = "8",
                    days = 2,
                    totalMinutes = 960,
                ),
            ),
            summary = ExportSummary(
                recordedDays = 2,
                activityCount = 1,
                totalLoggedMinutes = 960,
            ),
            language = AppLanguage.ENGLISH,
            exportUserFullName = "Mario Rossi",
            exportOfficeName = "Milan HQ",
            exportEmployeeId = "EMP-123",
            exportPersonId = "P-456",
            brandingLogoBase64 = Base64.Default.encode(minimalJpegBytes(width = 800, height = 350)),
            pdfExportStyle = style,
        ),
        title = "Export",
        font = testPdfFont(),
    ).decodeToString()
}

private fun testPdfFont(): PdfEmbeddedFont = PdfEmbeddedFont(
    postScriptName = "TestFont",
    fontBytes = byteArrayOf(0x00, 0x01, 0x02),
    firstChar = 32,
    lastChar = 255,
    widths = List(224) { 500 },
    ascent = 800,
    descent = -200,
    capHeight = 700,
    bbox = intArrayOf(-200, -200, 1000, 900),
    italicAngle = 0,
    missingWidth = 500,
)

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
