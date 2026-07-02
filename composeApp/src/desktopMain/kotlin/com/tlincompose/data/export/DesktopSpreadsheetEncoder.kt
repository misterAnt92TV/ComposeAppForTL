package com.tlincompose.data.export

import com.tlincompose.core.BrandingLogoSpreadsheetMaxHeightPx
import com.tlincompose.core.BrandingLogoSpreadsheetMaxWidthPx
import com.tlincompose.core.computeBrandingLogoRenderSize
import com.tlincompose.core.decodeBrandingLogoImage
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

actual fun buildXlsx(report: ExportReport, title: String): ByteArray {
    val sheet = report.toSpreadsheetSheet(title)
    val brandingLogo = decodeBrandingLogoImage(report.brandingLogoBase64)
    val sheetXml = buildSheetXml(sheet, includeBrandingLogo = brandingLogo != null)

    return ByteArrayOutputStream().use { output ->
        ZipOutputStream(output).use { zip ->
            zip.putNextEntry(ZipEntry("[Content_Types].xml"))
            zip.write(
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                    <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                    <Default Extension="jpg" ContentType="image/jpeg"/>
                    <Default Extension="xml" ContentType="application/xml"/>
                    <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
                    <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
                    ${if (brandingLogo != null) "<Override PartName=\"/xl/drawings/drawing1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.drawing+xml\"/>" else ""}
                    <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
                </Types>
                """.trimIndent().encodeToByteArray(),
            )
            zip.closeEntry()

            zip.putNextEntry(ZipEntry("_rels/.rels"))
            zip.write(
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                    <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
                </Relationships>
                """.trimIndent().encodeToByteArray(),
            )
            zip.closeEntry()

            zip.putNextEntry(ZipEntry("xl/workbook.xml"))
            zip.write(
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
                    xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                    <sheets>
                        <sheet name="Timesheet" sheetId="1" r:id="rId1"/>
                    </sheets>
                </workbook>
                """.trimIndent().encodeToByteArray(),
            )
            zip.closeEntry()

            zip.putNextEntry(ZipEntry("xl/_rels/workbook.xml.rels"))
            zip.write(
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                    <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
                    <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
                </Relationships>
                """.trimIndent().encodeToByteArray(),
            )
            zip.closeEntry()

            zip.putNextEntry(ZipEntry("xl/styles.xml"))
            zip.write(buildStylesXml().encodeToByteArray())
            zip.closeEntry()

            zip.putNextEntry(ZipEntry("xl/worksheets/sheet1.xml"))
            zip.write(sheetXml.encodeToByteArray())
            zip.closeEntry()

            if (brandingLogo != null) {
                zip.putNextEntry(ZipEntry("xl/worksheets/_rels/sheet1.xml.rels"))
                zip.write(
                    """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                        <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/drawing" Target="../drawings/drawing1.xml"/>
                    </Relationships>
                    """.trimIndent().encodeToByteArray(),
                )
                zip.closeEntry()

                zip.putNextEntry(ZipEntry("xl/drawings/drawing1.xml"))
                zip.write(
                    buildDrawingXml(
                        widthPx = brandingLogo.size.width,
                        heightPx = brandingLogo.size.height,
                    ).encodeToByteArray(),
                )
                zip.closeEntry()

                zip.putNextEntry(ZipEntry("xl/drawings/_rels/drawing1.xml.rels"))
                zip.write(
                    """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                        <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/image" Target="../media/brand-logo.jpg"/>
                    </Relationships>
                    """.trimIndent().encodeToByteArray(),
                )
                zip.closeEntry()

                zip.putNextEntry(ZipEntry("xl/media/brand-logo.jpg"))
                zip.write(brandingLogo.bytes)
                zip.closeEntry()
            }
        }
        output.toByteArray()
    }
}

private fun buildSheetXml(
    sheet: SpreadsheetSheetLayout,
    includeBrandingLogo: Boolean,
): String = buildString {
    append("""<?xml version="1.0" encoding="UTF-8"?>""")
    append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"")
    if (includeBrandingLogo) {
        append(" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"")
    }
    append(">")
    append("""<sheetViews><sheetView tabSelected="1" workbookViewId="0">""")
    sheet.freezePaneCell?.let { freezePaneCell ->
        val freezeRow = freezePaneCell.dropWhile { !it.isDigit() }.toIntOrNull()
        if (freezeRow != null && freezeRow > 1) {
            append(
                """<pane ySplit="${freezeRow - 1}" topLeftCell="$freezePaneCell" activePane="bottomLeft" state="frozen"/>""",
            )
        }
    }
    append("</sheetView></sheetViews>")
    append("""<sheetFormatPr defaultRowHeight="18"/>""")
    append("<cols>")
    sheet.columnWidths.forEachIndexed { index, width ->
        append("""<col min="${index + 1}" max="${index + 1}" width="$width" customWidth="1"/>""")
    }
    append("</cols>")
    append("<sheetData>")
    sheet.rows.forEachIndexed { rowIndex, row ->
        append("""<row r="${rowIndex + 1}"${rowAttributes(row)} >""")
        row.cells.sortedBy(SpreadsheetPositionedCell::columnIndex).forEach { positionedCell ->
            val reference = cellReference(positionedCell.columnIndex, rowIndex)
            val styleIndex = styleIndex(positionedCell.cell.style)
            when (val cell = positionedCell.cell) {
                is SpreadsheetCell.Text -> {
                    append(
                        """<c r="$reference" s="$styleIndex" t="inlineStr"><is><t xml:space="preserve">${escapeXml(cell.value)}</t></is></c>""",
                    )
                }

                is SpreadsheetCell.Number -> {
                    append("""<c r="$reference" s="$styleIndex"><v>${escapeXml(cell.value)}</v></c>""")
                }
            }
        }
        append("</row>")
    }
    append("</sheetData>")
    val mergeRefs = buildMergeRefs(sheet.rows)
    if (mergeRefs.isNotEmpty()) {
        append("""<mergeCells count="${mergeRefs.size}">""")
        mergeRefs.forEach { mergeRef ->
            append("""<mergeCell ref="$mergeRef"/>""")
        }
        append("</mergeCells>")
    }
    if (includeBrandingLogo) {
        append("""<drawing r:id="rId1"/>""")
    }
    append("</worksheet>")
}

private fun rowAttributes(row: SpreadsheetRowLayout): String {
    val firstStyle = row.cells.firstOrNull()?.cell?.style ?: return ""
    return when (firstStyle) {
        SpreadsheetCellStyle.TITLE -> " ht=\"26\" customHeight=\"1\""
        SpreadsheetCellStyle.SECTION_HEADER,
        SpreadsheetCellStyle.BLOCK_HEADER,
        -> " ht=\"21\" customHeight=\"1\""
        else -> ""
    }
}

private fun buildMergeRefs(rows: List<SpreadsheetRowLayout>): List<String> = buildList {
    rows.forEachIndexed { rowIndex, row ->
        row.cells.forEach { positionedCell ->
            if (positionedCell.cell.mergeAcross > 0) {
                add(
                    "${cellReference(positionedCell.columnIndex, rowIndex)}:" +
                        cellReference(positionedCell.columnIndex + positionedCell.cell.mergeAcross, rowIndex),
                )
            }
        }
    }
}

private fun styleIndex(style: SpreadsheetCellStyle): Int = when (style) {
    SpreadsheetCellStyle.TITLE -> 1
    SpreadsheetCellStyle.SECTION_HEADER -> 2
    SpreadsheetCellStyle.METADATA_LABEL -> 3
    SpreadsheetCellStyle.METADATA_VALUE -> 4
    SpreadsheetCellStyle.TABLE_HEADER -> 5
    SpreadsheetCellStyle.BODY_TEXT -> 6
    SpreadsheetCellStyle.BODY_NUMBER -> 7
    SpreadsheetCellStyle.BLOCK_HEADER -> 8
}

private fun buildStylesXml(): String = """
    <?xml version="1.0" encoding="UTF-8"?>
    <styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
        <fonts count="4">
            <font><sz val="11"/><color theme="1"/><name val="Aptos"/><family val="2"/></font>
            <font><b/><sz val="15"/><color rgb="FF1F2937"/><name val="Aptos"/><family val="2"/></font>
            <font><b/><sz val="11"/><color rgb="FF1F2937"/><name val="Aptos"/><family val="2"/></font>
            <font><b/><sz val="11"/><color rgb="FFFFFFFF"/><name val="Aptos"/><family val="2"/></font>
        </fonts>
        <fills count="5">
            <fill><patternFill patternType="none"/></fill>
            <fill><patternFill patternType="gray125"/></fill>
            <fill><patternFill patternType="solid"><fgColor rgb="FFF8FAFC"/><bgColor indexed="64"/></patternFill></fill>
            <fill><patternFill patternType="solid"><fgColor rgb="FF1F4E78"/><bgColor indexed="64"/></patternFill></fill>
            <fill><patternFill patternType="solid"><fgColor rgb="FFDCE6F1"/><bgColor indexed="64"/></patternFill></fill>
        </fills>
        <borders count="2">
            <border><left/><right/><top/><bottom/><diagonal/></border>
            <border>
                <left style="thin"/><right style="thin"/><top style="thin"/><bottom style="thin"/><diagonal/>
            </border>
        </borders>
        <cellStyleXfs count="1">
            <xf numFmtId="0" fontId="0" fillId="0" borderId="0"/>
        </cellStyleXfs>
        <cellXfs count="9">
            <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
            <xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1" applyAlignment="1">
                <alignment vertical="center" wrapText="1"/>
            </xf>
            <xf numFmtId="0" fontId="3" fillId="3" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                <alignment vertical="center"/>
            </xf>
            <xf numFmtId="0" fontId="2" fillId="2" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                <alignment vertical="center"/>
            </xf>
            <xf numFmtId="0" fontId="0" fillId="0" borderId="1" xfId="0" applyBorder="1" applyAlignment="1">
                <alignment vertical="top" wrapText="1"/>
            </xf>
            <xf numFmtId="0" fontId="3" fillId="3" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                <alignment horizontal="center" vertical="center" wrapText="1"/>
            </xf>
            <xf numFmtId="0" fontId="0" fillId="0" borderId="1" xfId="0" applyBorder="1" applyAlignment="1">
                <alignment vertical="top" wrapText="1"/>
            </xf>
            <xf numFmtId="0" fontId="0" fillId="0" borderId="1" xfId="0" applyBorder="1" applyAlignment="1">
                <alignment horizontal="right" vertical="center"/>
            </xf>
            <xf numFmtId="0" fontId="2" fillId="4" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                <alignment vertical="center" wrapText="1"/>
            </xf>
        </cellXfs>
        <cellStyles count="1">
            <cellStyle name="Normal" xfId="0" builtinId="0"/>
        </cellStyles>
    </styleSheet>
""".trimIndent()

private fun buildDrawingXml(widthPx: Int, heightPx: Int): String {
    val renderSize = computeBrandingLogoRenderSize(
        width = widthPx,
        height = heightPx,
        maxWidth = BrandingLogoSpreadsheetMaxWidthPx.toFloat(),
        maxHeight = BrandingLogoSpreadsheetMaxHeightPx.toFloat(),
    )
    val widthEmu = renderSize.width * 9_525
    val heightEmu = renderSize.height * 9_525
    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <xdr:wsDr xmlns:xdr="http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing"
            xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main"
            xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
            <xdr:oneCellAnchor>
                <xdr:from>
                    <xdr:col>4</xdr:col>
                    <xdr:colOff>0</xdr:colOff>
                    <xdr:row>0</xdr:row>
                    <xdr:rowOff>0</xdr:rowOff>
                </xdr:from>
                <xdr:ext cx="$widthEmu" cy="$heightEmu"/>
                <xdr:pic>
                    <xdr:nvPicPr>
                        <xdr:cNvPr id="1" name="BrandLogo"/>
                        <xdr:cNvPicPr/>
                    </xdr:nvPicPr>
                    <xdr:blipFill>
                        <a:blip r:embed="rId1"/>
                        <a:stretch><a:fillRect/></a:stretch>
                    </xdr:blipFill>
                    <xdr:spPr>
                        <a:xfrm>
                            <a:off x="0" y="0"/>
                            <a:ext cx="$widthEmu" cy="$heightEmu"/>
                        </a:xfrm>
                        <a:prstGeom prst="rect">
                            <a:avLst/>
                        </a:prstGeom>
                    </xdr:spPr>
                </xdr:pic>
                <xdr:clientData/>
            </xdr:oneCellAnchor>
        </xdr:wsDr>
    """.trimIndent()
}

private fun cellReference(columnIndex: Int, rowIndex: Int): String {
    var index = columnIndex
    var label = ""
    do {
        label = ('A'.code + (index % 26)).toChar() + label
        index = (index / 26) - 1
    } while (index >= 0)
    return "$label${rowIndex + 1}"
}

private fun escapeXml(value: String): String = buildString {
    value.forEach { char ->
        when (char) {
            '&' -> append("&amp;")
            '<' -> append("&lt;")
            '>' -> append("&gt;")
            '"' -> append("&quot;")
            '\'' -> append("&apos;")
            else -> append(char)
        }
    }
}
