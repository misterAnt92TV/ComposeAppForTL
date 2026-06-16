package com.tlincompose.data.export

import com.tlincompose.core.BrandingLogoSpreadsheetMaxHeightPx
import com.tlincompose.core.BrandingLogoSpreadsheetMaxWidthPx
import com.tlincompose.core.computeBrandingLogoRenderSize
import com.tlincompose.core.decodeBrandingLogoImage
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

actual fun buildXlsx(report: ExportReport, title: String): ByteArray {
    val rows = report.toSpreadsheetRows(title)
    val brandingLogo = decodeBrandingLogoImage(report.brandingLogoBase64)
    val sheetXml = buildSheetXml(rows, includeBrandingLogo = brandingLogo != null)

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
                </Relationships>
                """.trimIndent().encodeToByteArray(),
            )
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
    rows: List<List<SpreadsheetCell>>,
    includeBrandingLogo: Boolean,
): String = buildString {
    append("""<?xml version="1.0" encoding="UTF-8"?>""")
    append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"")
    if (includeBrandingLogo) {
        append(" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"")
    }
    append("><sheetData>")
    rows.forEachIndexed { rowIndex, row ->
        append("""<row r="${rowIndex + 1}">""")
        row.forEachIndexed { columnIndex, cell ->
            val reference = cellReference(columnIndex, rowIndex)
            when (cell) {
                is SpreadsheetCell.Text -> {
                    append("""<c r="$reference" t="inlineStr"><is><t>${escapeXml(cell.value)}</t></is></c>""")
                }

                is SpreadsheetCell.Number -> {
                    append("""<c r="$reference"><v>${escapeXml(cell.value)}</v></c>""")
                }
            }
        }
        append("</row>")
    }
    append("</sheetData>")
    if (includeBrandingLogo) {
        append("""<drawing r:id="rId1"/>""")
    }
    append("</worksheet>")
}

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
