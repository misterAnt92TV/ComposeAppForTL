package com.tlincompose.data.export

import com.tlincompose.core.AppStrings
import com.tlincompose.core.BrandingLogoPdfMaxHeightPt
import com.tlincompose.core.BrandingLogoPdfMaxWidthPt
import com.tlincompose.core.appStrings
import com.tlincompose.core.computeBrandingLogoRenderSize
import com.tlincompose.core.decodeBrandingLogoImage
import com.tlincompose.core.exportActivitiesLabel
import com.tlincompose.core.exportActivityCodeLabel
import com.tlincompose.core.exportActivityLabel
import com.tlincompose.core.exportDaysLabel
import com.tlincompose.core.exportGeneratedAtLabel
import com.tlincompose.core.exportGeneratedAtValue
import com.tlincompose.core.exportHoursPerDayCompactLabel
import com.tlincompose.core.exportHoursPerDayLabel
import com.tlincompose.core.exportNoActivitiesForSelectedPeriod
import com.tlincompose.core.exportPeriodsLabel
import com.tlincompose.core.exportTotalHoursCompactLabel
import com.tlincompose.core.exportTotalHoursLabel
import com.tlincompose.core.exportTypeLabel
import com.tlincompose.core.formatHours
import com.tlincompose.core.labeledValue
import com.tlincompose.domain.model.PdfExportStyle

object PdfReportWriter {
    fun build(report: ExportReport, title: String, font: PdfEmbeddedFont): ByteArray {
        val lines = buildLines(report, title)
        val brandingLogo = decodeBrandingLogoImage(report.brandingLogoBase64)
        val pages = lines.chunked(60).ifEmpty { listOf(emptyList()) }
        val pageObjectIds = mutableListOf<Int>()
        val contentObjectIds = mutableListOf<Int>()
        var nextObjectId = 3

        repeat(pages.size) {
            pageObjectIds += nextObjectId++
            contentObjectIds += nextObjectId++
        }
        val brandingLogoObjectId = brandingLogo?.let { nextObjectId++ }
        val fontFileObjectId = nextObjectId++
        val fontDescriptorObjectId = nextObjectId++
        val toUnicodeObjectId = nextObjectId++
        val fontObjectId = nextObjectId

        val objects = mutableListOf<String>()
        objects += "<< /Type /Catalog /Pages 2 0 R >>"
        objects += "<< /Type /Pages /Count ${pages.size} /Kids [${pageObjectIds.joinToString(" ") { "$it 0 R" }}] >>"

        pages.forEachIndexed { index, pageLines ->
            val logoPlacement = brandingLogo
                ?.takeIf { index == 0 && brandingLogoObjectId != null }
                ?.let { buildPdfLogoPlacement(it.size.width, it.size.height) }
            val content = buildPageContent(
                lines = pageLines,
                font = font,
                logoPlacement = logoPlacement,
            )
            val xObjectSection = if (index == 0 && brandingLogoObjectId != null) {
                " /XObject << /Im1 $brandingLogoObjectId 0 R >>"
            } else {
                ""
            }
            objects += "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 $fontObjectId 0 R >>$xObjectSection >> /Contents ${contentObjectIds[index]} 0 R >>"
            objects += "<< /Length ${content.length} >>\nstream\n$content\nendstream"
        }

        brandingLogo?.let { logo ->
            val logoHex = logo.bytes.joinToString(separator = "") { byte -> "%02X".format(byte.toInt() and 0xFF) }
            objects += buildImageObject(
                brandingLogoHex = logoHex,
                widthPx = logo.size.width,
                heightPx = logo.size.height,
            )
        }
        objects += buildFontFileObject(font)
        objects += buildFontDescriptorObject(font, fontFileObjectId)
        objects += buildToUnicodeObject(toUnicodeObjectId)
        objects += buildFontObject(font, fontDescriptorObjectId, toUnicodeObjectId)

        return buildPdf(objects)
    }

    private fun buildLines(report: ExportReport, title: String): List<PdfLine> {
        val language = report.language
        val strings = appStrings(language)
        val lines = buildCommonHeaderLines(report, title, strings).toMutableList()
        lines += PdfLine("")
        lines += when (report.pdfExportStyle) {
            PdfExportStyle.RETRO -> buildRetroTableLines(report.rows, strings)
            PdfExportStyle.SIMPLE_TABLE -> buildSimpleTableLines(report.rows, strings)
            PdfExportStyle.COMPACT_LIST -> buildCompactListLines(report.rows, strings)
            PdfExportStyle.DETAIL_BLOCKS -> buildDetailBlockLines(report.rows, strings)
        }
        return lines
    }

    private fun structuredPdfColumns(strings: AppStrings): List<PdfColumn> = listOf(
        PdfColumn(strings.exportActivityLabel, 50),
        PdfColumn(strings.exportTypeLabel, 12),
        PdfColumn(strings.exportHoursPerDayCompactLabel, 8, alignment = PdfColumnAlignment.END),
        PdfColumn(strings.exportDaysLabel, 5, alignment = PdfColumnAlignment.END),
        PdfColumn(strings.exportTotalHoursCompactLabel, 9, alignment = PdfColumnAlignment.END),
    )

    private fun formatSimpleTableSeparator(
        columns: List<PdfColumn>,
        separatorChar: Char = '-',
    ): String = buildString {
        columns.forEachIndexed { index, column ->
            if (index > 0) append("  ")
            append(separatorChar.toString().repeat(column.width))
        }
    }

    private fun formatSimpleTableRow(
        columns: List<PdfColumn>,
        values: List<String>,
    ): List<String> {
        return formatAlignedRow(
            columns = columns,
            values = values,
            prefix = "",
            separator = "  ",
            suffix = "",
        )
    }

    private fun formatAlignedRow(
        columns: List<PdfColumn>,
        values: List<String>,
        prefix: String,
        separator: String,
        suffix: String,
    ): List<String> {
        val wrappedCells = columns.zip(values).map { (column, value) ->
            wrapText(value, column.width).ifEmpty { listOf("") }
        }
        val lines = mutableListOf<String>()
        val rowHeight = wrappedCells.maxOfOrNull(List<String>::size) ?: 1
        repeat(rowHeight) { lineIndex ->
            lines += buildString {
                append(prefix)
                columns.forEachIndexed { columnIndex, column ->
                    append(
                        padOrTrim(
                            text = wrappedCells[columnIndex].getOrElse(lineIndex) { "" },
                            width = column.width,
                            alignment = column.alignment,
                        ),
                    )
                    if (columnIndex != columns.lastIndex) {
                        append(separator)
                    }
                }
                append(suffix)
            }
        }
        return lines
    }

    private fun buildCommonHeaderLines(
        report: ExportReport,
        title: String,
        strings: AppStrings,
    ): List<PdfLine> = buildList {
        add(PdfLine(title))
        report.metadataRows(strings).forEach { row ->
            add(PdfLine(strings.labeledValue(row.label, row.value)))
        }
    }

    private fun buildRetroTableLines(rows: List<ExportRow>, strings: AppStrings): List<PdfLine> =
        buildStructuredTableLines(
            rows = rows,
            strings = strings,
            headerSeparatorChar = '=',
            rowSeparatorChar = '-',
        )

    private fun buildSimpleTableLines(rows: List<ExportRow>, strings: AppStrings): List<PdfLine> =
        buildStructuredTableLines(
            rows = rows,
            strings = strings,
            headerSeparatorChar = '-',
            rowSeparatorChar = null,
        )

    private fun buildStructuredTableLines(
        rows: List<ExportRow>,
        strings: AppStrings,
        headerSeparatorChar: Char,
        rowSeparatorChar: Char?,
    ): List<PdfLine> {
        if (rows.isEmpty()) {
            return listOf(PdfLine(strings.exportNoActivitiesForSelectedPeriod))
        }

        val columns = structuredPdfColumns(strings)
        val headerSeparator = formatSimpleTableSeparator(columns, separatorChar = headerSeparatorChar)
        val rowSeparator = rowSeparatorChar?.toString()?.repeat(PDF_CONTENT_WIDTH)

        return buildList {
            addAll(formatSimpleTableRow(columns, columns.map(PdfColumn::header)).map(PdfLine.Companion::table))
            add(PdfLine(headerSeparator, PdfLineRole.TABLE))
            rows.forEachIndexed { index, row ->
                addAll(formatSimpleTableRow(columns, row.toStructuredPdfRowValues()).map(PdfLine.Companion::table))
                addAll(
                    formatSecondaryTableLines(
                        label = strings.exportPeriodsLabel,
                        value = row.periodsLabel(strings),
                    ).map(PdfLine.Companion::table),
                )
                if (index != rows.lastIndex) {
                    if (rowSeparator != null) {
                        add(PdfLine(rowSeparator, PdfLineRole.TABLE))
                    } else {
                        add(PdfLine(""))
                    }
                }
            }
        }
    }

    private fun buildCompactListLines(rows: List<ExportRow>, strings: AppStrings): List<PdfLine> = buildList {
        if (rows.isEmpty()) {
            add(PdfLine(strings.exportNoActivitiesForSelectedPeriod))
            return@buildList
        }
        rows.forEachIndexed { index, row ->
            add(PdfLine(row.heading()))
            addAll(wrapLabeledValue(strings.exportTypeLabel, row.typeLabel).map(::PdfLine))
            addAll(wrapLabeledValue(strings.exportPeriodsLabel, row.periodsLabel(strings)).map(::PdfLine))
            addAll(
                wrapText(
                    "${strings.exportHoursPerDayLabel}: ${row.hoursPerDayLabel}   " +
                        "${strings.exportDaysLabel}: ${row.days}   " +
                        "${strings.exportTotalHoursLabel}: ${formatHours(row.totalMinutes)}",
                ).map(::PdfLine),
            )
            if (index != rows.lastIndex) {
                add(PdfLine(""))
            }
        }
    }

    private fun buildDetailBlockLines(rows: List<ExportRow>, strings: AppStrings): List<PdfLine> = buildList {
        if (rows.isEmpty()) {
            add(PdfLine(strings.exportNoActivitiesForSelectedPeriod))
            return@buildList
        }
        val divider = "=".repeat(92)
        rows.forEach { row ->
            add(PdfLine(divider))
            addAll(wrapText(row.heading()).map(::PdfLine))
            addAll(wrapLabeledValue(strings.exportTypeLabel, row.typeLabel).map(::PdfLine))
            addAll(wrapLabeledValue(strings.exportPeriodsLabel, row.periodsLabel(strings)).map(::PdfLine))
            addAll(wrapLabeledValue(strings.exportHoursPerDayLabel, row.hoursPerDayLabel).map(::PdfLine))
            addAll(wrapLabeledValue(strings.exportDaysLabel, row.days.toString()).map(::PdfLine))
            addAll(wrapLabeledValue(strings.exportTotalHoursLabel, formatHours(row.totalMinutes)).map(::PdfLine))
        }
        add(PdfLine(divider))
    }

    private fun wrapLabeledValue(label: String, value: String): List<String> =
        wrapText("$label: $value")

    private fun formatSecondaryTableLines(label: String, value: String): List<String> {
        val prefix = "  $label: "
        val continuationPrefix = " ".repeat(prefix.length)
        return wrapText(
            text = value,
            width = PDF_CONTENT_WIDTH - prefix.length,
        ).mapIndexed { index, line ->
            if (index == 0) {
                prefix + line
            } else {
                continuationPrefix + line
            }
        }
    }

    private fun ExportRow.toStructuredPdfRowValues(): List<String> = listOf(
        tableActivityLabel(),
        typeLabel,
        hoursPerDayLabel,
        days.toString(),
        formatHours(totalMinutes),
    )

    private fun ExportRow.tableActivityLabel(): String =
        if (activityCode == "-") {
            activitySummaryLabel()
        } else {
            "${activitySummaryLabel()} ($activityCode)"
        }

    private fun ExportRow.heading(): String =
        if (activityCode == "-") {
            activitySummaryLabel()
        } else {
            val summary = activitySummaryLabel()
            if (summary.contains("($activityCode)")) {
                summary
            } else {
                "$activityCode - $summary"
            }
        }

    private fun padOrTrim(
        text: String,
        width: Int,
        alignment: PdfColumnAlignment,
    ): String {
        val sanitized = text
        val padded = if (sanitized.length >= width) {
            sanitized.take(width - 1) + " "
        } else {
            when (alignment) {
                PdfColumnAlignment.START -> sanitized.padEnd(width, ' ')
                PdfColumnAlignment.END -> sanitized.padStart(width, ' ')
            }
        }
        return padded
    }

    private fun wrapText(text: String, width: Int = PDF_CONTENT_WIDTH): List<String> {
        val sanitized = text
        if (sanitized.length <= width) return listOf(sanitized)

        val lines = mutableListOf<String>()
        var remaining = sanitized
        while (remaining.length > width) {
            val splitIndex = remaining.take(width + 1).lastIndexOf(' ').takeIf { it > 0 } ?: width
            lines += remaining.take(splitIndex).trimEnd()
            remaining = remaining.drop(splitIndex).trimStart()
        }
        lines += remaining
        return lines
    }

    private fun buildPageContent(
        lines: List<PdfLine>,
        font: PdfEmbeddedFont,
        logoPlacement: PdfLogoPlacement?,
    ): String = buildString {
        if (logoPlacement != null) {
            append("q\n")
            append("${logoPlacement.width} 0 0 ${logoPlacement.height} ${logoPlacement.x} ${logoPlacement.y} cm\n")
            append("/Im1 Do\n")
            append("Q\n")
        }
        append("BT\n")
        append("/F1 8 Tf\n")
        append("28 ${logoPlacement?.textStartY ?: 812} Td\n")
        append("10 TL\n")
        lines.forEachIndexed { index, line ->
            append("(")
            append(
                escapePdfText(
                    text = line.text,
                    font = font,
                ),
            )
            append(") Tj\n")
            if (index != lines.lastIndex) {
                append("T*\n")
            }
        }
        append("ET")
    }

    private fun escapePdfText(text: String, font: PdfEmbeddedFont): String = buildString {
        encodeWindows1252(text).forEach { byte ->
            val value = byte.toInt() and 0xFF
            when (value) {
                '\\'.code -> append("\\\\")
                '('.code -> append("\\(")
                ')'.code -> append("\\)")
                in 32..126 -> append(value.toChar())
                else -> {
                    // Keep the stream ASCII-safe while preserving the original single-byte glyph code.
                    append("\\")
                    append(value.toString(8).padStart(3, '0'))
                }
            }
        }
    }

    private fun buildFontObject(
        font: PdfEmbeddedFont,
        fontDescriptorObjectId: Int,
        toUnicodeObjectId: Int,
    ): String = """
        << /Type /Font /Subtype /TrueType /BaseFont /${font.postScriptName}
        /FirstChar ${font.firstChar}
        /LastChar ${font.lastChar}
        /Widths [${font.widths.joinToString(separator = " ")}]
        /Encoding /WinAnsiEncoding
        /FontDescriptor $fontDescriptorObjectId 0 R
        /ToUnicode $toUnicodeObjectId 0 R
        >>
    """.trimIndent()

    private fun buildFontDescriptorObject(
        font: PdfEmbeddedFont,
        fontFileObjectId: Int,
    ): String = """
        << /Type /FontDescriptor /FontName /${font.postScriptName}
        /Flags 32
        /FontBBox [${font.bbox.joinToString(separator = " ")}]
        /ItalicAngle ${font.italicAngle}
        /Ascent ${font.ascent}
        /Descent ${font.descent}
        /CapHeight ${font.capHeight}
        /StemV 80
        /MissingWidth ${font.missingWidth}
        /FontFile2 $fontFileObjectId 0 R
        >>
    """.trimIndent()

    private fun buildFontFileObject(font: PdfEmbeddedFont): String {
        val fontHex = font.fontBytes.joinToString(separator = "") { byte -> "%02X".format(byte.toInt() and 0xFF) }
        val stream = "$fontHex>"
        return """
            << /Length ${stream.length} /Length1 ${font.fontBytes.size} /Filter /ASCIIHexDecode >>
            stream
            $stream
            endstream
        """.trimIndent()
    }

    private fun buildToUnicodeObject(objectId: Int): String {
        val mappings = (32..255).joinToString(separator = "\n") { byteValue ->
            "<${byteValue.toString(16).padStart(2, '0').uppercase()}> <${windows1252Decode(byteValue).toString(16).padStart(4, '0').uppercase()}>"
        }
        val cmap = """
            /CIDInit /ProcSet findresource begin
            12 dict begin
            begincmap
            /CIDSystemInfo << /Registry (Adobe) /Ordering (UCS) /Supplement 0 >> def
            /CMapName /F1Unicode def
            /CMapType 2 def
            1 begincodespacerange
            <00> <FF>
            endcodespacerange
            224 beginbfchar
            $mappings
            endbfchar
            endcmap
            CMapName currentdict /CMap defineresource pop
            end
            end
        """.trimIndent()
        return """
            << /Length ${cmap.length} >>
            stream
            $cmap
            endstream
        """.trimIndent()
    }

    private fun buildImageObject(
        brandingLogoHex: String,
        widthPx: Int,
        heightPx: Int,
    ): String {
        val stream = "$brandingLogoHex>"
        return """
            << /Type /XObject /Subtype /Image /Width $widthPx /Height $heightPx /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter [/ASCIIHexDecode /DCTDecode] /Length ${stream.length} >>
            stream
            $stream
            endstream
        """.trimIndent()
    }

    private fun buildPdfLogoPlacement(widthPx: Int, heightPx: Int): PdfLogoPlacement {
        val renderSize = computeBrandingLogoRenderSize(
            width = widthPx,
            height = heightPx,
            maxWidth = BrandingLogoPdfMaxWidthPt,
            maxHeight = BrandingLogoPdfMaxHeightPt,
        )
        val drawWidth = renderSize.width.toFloat()
        val drawHeight = renderSize.height.toFloat()
        val drawX = 595f - 28f - drawWidth
        val drawY = 842f - 24f - drawHeight
        return PdfLogoPlacement(
            width = drawWidth,
            height = drawHeight,
            x = drawX,
            y = drawY,
            textStartY = drawY - 12f,
        )
    }

    private fun buildPdf(objects: List<String>): ByteArray {
        val builder = StringBuilder()
        val offsets = mutableListOf<Int>()

        builder.append("%PDF-1.4\n")
        objects.forEachIndexed { index, body ->
            offsets += builder.length
            builder.append("${index + 1} 0 obj\n")
            builder.append(body)
            builder.append("\nendobj\n")
        }

        val xrefOffset = builder.length
        builder.append("xref\n")
        builder.append("0 ${objects.size + 1}\n")
        builder.append("0000000000 65535 f \n")
        offsets.forEach { offset ->
            builder.append(offset.toString().padStart(10, '0'))
            builder.append(" 00000 n \n")
        }
        builder.append("trailer\n")
        builder.append("<< /Size ${objects.size + 1} /Root 1 0 R >>\n")
        builder.append("startxref\n")
        builder.append(xrefOffset)
        builder.append("\n%%EOF")

        return builder.toString().encodeToByteArray()
    }
}

private data class PdfLogoPlacement(
    val width: Float,
    val height: Float,
    val x: Float,
    val y: Float,
    val textStartY: Float,
)

private enum class PdfLineRole {
    REGULAR,
    TABLE,
}

private data class PdfLine(
    val text: String,
    val role: PdfLineRole = PdfLineRole.REGULAR,
) {
    companion object {
        fun table(text: String): PdfLine = PdfLine(text = text, role = PdfLineRole.TABLE)
    }
}

private data class PdfColumn(
    val header: String,
    val width: Int,
    val alignment: PdfColumnAlignment = PdfColumnAlignment.START,
)

private enum class PdfColumnAlignment {
    START,
    END,
}

private const val PDF_CONTENT_WIDTH = 92
