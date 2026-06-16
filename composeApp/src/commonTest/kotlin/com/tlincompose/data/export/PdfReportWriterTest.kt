package com.tlincompose.data.export

import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.PdfExportStyle
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.datetime.Instant

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
                brandingLogoBase64 = Base64.Default.encode(minimalJpegBytes(width = 800, height = 350)),
            ),
            title = "Export",
        )

        val pdfText = pdfBytes.decodeToString()
        assertTrue(pdfText.contains("/DCTDecode"))
        assertTrue(pdfText.contains("/Im1 Do"))
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

        assertTrue(compactText.contains("Type: Project"))
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
                    activityTitle = "Apollo",
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
            brandingLogoBase64 = Base64.Default.encode(minimalJpegBytes(width = 800, height = 350)),
            pdfExportStyle = style,
        ),
        title = "Export",
    ).decodeToString()
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
