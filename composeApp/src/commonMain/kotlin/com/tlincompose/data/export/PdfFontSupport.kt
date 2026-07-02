package com.tlincompose.data.export

import kotlin.math.roundToInt

data class PdfFontResource(
    val postScriptName: String,
    val fontBytes: ByteArray,
)

fun interface PdfFontProvider {
    fun loadRegularFont(): PdfFontResource
}

data class PdfEmbeddedFont(
    val postScriptName: String,
    val fontBytes: ByteArray,
    val firstChar: Int,
    val lastChar: Int,
    val widths: List<Int>,
    val ascent: Int,
    val descent: Int,
    val capHeight: Int,
    val bbox: IntArray,
    val italicAngle: Int,
    val missingWidth: Int,
)

internal fun parsePdfEmbeddedFont(resource: PdfFontResource): PdfEmbeddedFont {
    val parser = TrueTypeFontParser(resource.fontBytes)
    return parser.toPdfEmbeddedFont(resource.postScriptName)
}

internal fun windows1252Decode(byteValue: Int): Int = when (byteValue and 0xFF) {
    in 0x00..0x7F -> byteValue and 0xFF
    in 0xA0..0xFF -> byteValue and 0xFF
    0x80 -> 0x20AC
    0x82 -> 0x201A
    0x83 -> 0x0192
    0x84 -> 0x201E
    0x85 -> 0x2026
    0x86 -> 0x2020
    0x87 -> 0x2021
    0x88 -> 0x02C6
    0x89 -> 0x2030
    0x8A -> 0x0160
    0x8B -> 0x2039
    0x8C -> 0x0152
    0x8E -> 0x017D
    0x91 -> 0x2018
    0x92 -> 0x2019
    0x93 -> 0x201C
    0x94 -> 0x201D
    0x95 -> 0x2022
    0x96 -> 0x2013
    0x97 -> 0x2014
    0x98 -> 0x02DC
    0x99 -> 0x2122
    0x9A -> 0x0161
    0x9B -> 0x203A
    0x9C -> 0x0153
    0x9E -> 0x017E
    0x9F -> 0x0178
    else -> '?'.code
}

internal fun encodeWindows1252(text: String): ByteArray = ByteArray(text.length) { index ->
    encodeWindows1252Char(text[index]).toByte()
}

private fun encodeWindows1252Char(char: Char): Int {
    val code = char.code
    return when {
        code in 0x00..0x7F -> code
        code in 0x00A0..0x00FF -> code
        else -> when (code) {
            0x20AC -> 0x80
            0x201A -> 0x82
            0x0192 -> 0x83
            0x201E -> 0x84
            0x2026 -> 0x85
            0x2020 -> 0x86
            0x2021 -> 0x87
            0x02C6 -> 0x88
            0x2030 -> 0x89
            0x0160 -> 0x8A
            0x2039 -> 0x8B
            0x0152 -> 0x8C
            0x017D -> 0x8E
            0x2018 -> 0x91
            0x2019 -> 0x92
            0x201C -> 0x93
            0x201D -> 0x94
            0x2022 -> 0x95
            0x2013 -> 0x96
            0x2014 -> 0x97
            0x02DC -> 0x98
            0x2122 -> 0x99
            0x0161 -> 0x9A
            0x203A -> 0x9B
            0x0153 -> 0x9C
            0x017E -> 0x9E
            0x0178 -> 0x9F
            else -> '?'.code
        }
    }
}

private class TrueTypeFontParser(
    private val bytes: ByteArray,
) {
    private val tables: Map<String, TrueTypeTable> = buildMap {
        val numTables = readUInt16(4)
        var offset = 12
        repeat(numTables) {
            val tag = bytes.decodeToString(offset, offset + 4)
            put(
                tag,
                TrueTypeTable(
                    offset = readUInt32(offset + 8),
                    length = readUInt32(offset + 12),
                ),
            )
            offset += 16
        }
    }

    fun toPdfEmbeddedFont(fallbackPostScriptName: String): PdfEmbeddedFont {
        val head = requireTable("head")
        val hhea = requireTable("hhea")
        val maxp = requireTable("maxp")
        val hmtx = requireTable("hmtx")
        val cmap = requireTable("cmap")

        val unitsPerEm = readUInt16(head.offset + 18)
        val bbox = intArrayOf(
            readInt16(head.offset + 36),
            readInt16(head.offset + 38),
            readInt16(head.offset + 40),
            readInt16(head.offset + 42),
        )
        val ascent = readInt16(hhea.offset + 4)
        val descent = readInt16(hhea.offset + 6)
        val numberOfHMetrics = readUInt16(hhea.offset + 34)
        val numGlyphs = readUInt16(maxp.offset + 4)
        val advanceWidths = readAdvanceWidths(hmtx.offset, numGlyphs, numberOfHMetrics)
        val glyphIndexForCodePoint = parseCmap(cmap)
        val capHeight = readCapHeightOrNull()?.takeIf { it > 0 } ?: ascent
        val italicAngle = readItalicAngleOrNull() ?: 0
        val postScriptName = readPostScriptNameOrNull()?.ifBlank { null } ?: fallbackPostScriptName

        val widths = buildList {
            for (code in 32..255) {
                val codePoint = windows1252Decode(code)
                val glyphIndex = glyphIndexForCodePoint[codePoint]
                    ?: glyphIndexForCodePoint['?'.code]
                    ?: 0
                val advanceWidth = advanceWidths.getOrElse(glyphIndex) { advanceWidths.lastOrNull() ?: unitsPerEm }
                add((advanceWidth * 1000f / unitsPerEm).roundToInt())
            }
        }

        val missingGlyphIndex = glyphIndexForCodePoint['?'.code] ?: 0
        val missingWidth = advanceWidths.getOrElse(missingGlyphIndex) { unitsPerEm }
            .let { (it * 1000f / unitsPerEm).roundToInt() }

        return PdfEmbeddedFont(
            postScriptName = sanitizePdfName(postScriptName),
            fontBytes = bytes,
            firstChar = 32,
            lastChar = 255,
            widths = widths,
            ascent = (ascent * 1000f / unitsPerEm).roundToInt(),
            descent = (descent * 1000f / unitsPerEm).roundToInt(),
            capHeight = (capHeight * 1000f / unitsPerEm).roundToInt(),
            bbox = bbox.map { (it * 1000f / unitsPerEm).roundToInt() }.toIntArray(),
            italicAngle = italicAngle,
            missingWidth = missingWidth,
        )
    }

    private fun readAdvanceWidths(offset: Int, numGlyphs: Int, numberOfHMetrics: Int): IntArray {
        val widths = IntArray(numGlyphs)
        var cursor = offset
        var lastAdvanceWidth = 0
        repeat(numberOfHMetrics.coerceAtMost(numGlyphs)) { index ->
            lastAdvanceWidth = readUInt16(cursor)
            widths[index] = lastAdvanceWidth
            cursor += 4
        }
        repeat((numGlyphs - numberOfHMetrics).coerceAtLeast(0)) { index ->
            widths[numberOfHMetrics + index] = lastAdvanceWidth
            cursor += 2
        }
        return widths
    }

    private fun parseCmap(table: TrueTypeTable): Map<Int, Int> {
        val numTables = readUInt16(table.offset + 2)
        var selectedOffset: Int? = null
        var selectedFormat = -1
        repeat(numTables) { index ->
            val recordOffset = table.offset + 4 + (index * 8)
            val platformId = readUInt16(recordOffset)
            val encodingId = readUInt16(recordOffset + 2)
            val subtableOffset = table.offset + readUInt32(recordOffset + 4)
            val format = readUInt16(subtableOffset)
            val isMicrosoftUnicode = platformId == 3 && (encodingId == 1 || encodingId == 10)
            if (!isMicrosoftUnicode) return@repeat
            if (format == 4 && selectedFormat != 4) {
                selectedOffset = subtableOffset
                selectedFormat = 4
            } else if (format == 12 && selectedOffset == null) {
                selectedOffset = subtableOffset
                selectedFormat = 12
            }
        }
        val offset = selectedOffset ?: error("Missing cmap format 4/12 for PDF font.")
        return when (selectedFormat) {
            4 -> parseFormat4(offset)
            12 -> parseFormat12(offset)
            else -> error("Unsupported cmap format $selectedFormat.")
        }
    }

    private fun parseFormat4(offset: Int): Map<Int, Int> {
        val segCount = readUInt16(offset + 6) / 2
        val endCodesOffset = offset + 14
        val startCodesOffset = endCodesOffset + (segCount * 2) + 2
        val idDeltaOffset = startCodesOffset + (segCount * 2)
        val idRangeOffsetOffset = idDeltaOffset + (segCount * 2)
        val mapping = mutableMapOf<Int, Int>()

        for (segmentIndex in 0 until segCount) {
            val endCode = readUInt16(endCodesOffset + (segmentIndex * 2))
            val startCode = readUInt16(startCodesOffset + (segmentIndex * 2))
            val idDelta = readInt16(idDeltaOffset + (segmentIndex * 2))
            val idRangeOffset = readUInt16(idRangeOffsetOffset + (segmentIndex * 2))
            if (startCode == 0xFFFF && endCode == 0xFFFF) continue

            for (codePoint in startCode..endCode) {
                val glyphIndex = if (idRangeOffset == 0) {
                    (codePoint + idDelta) and 0xFFFF
                } else {
                    val glyphIndexAddress = idRangeOffsetOffset +
                        (segmentIndex * 2) +
                        idRangeOffset +
                        ((codePoint - startCode) * 2)
                    if (glyphIndexAddress + 1 >= bytes.size) {
                        0
                    } else {
                        val glyphId = readUInt16(glyphIndexAddress)
                        if (glyphId == 0) 0 else (glyphId + idDelta) and 0xFFFF
                    }
                }
                if (glyphIndex != 0) {
                    mapping[codePoint] = glyphIndex
                }
            }
        }

        return mapping
    }

    private fun parseFormat12(offset: Int): Map<Int, Int> {
        val numGroups = readUInt32(offset + 12)
        val mapping = mutableMapOf<Int, Int>()
        repeat(numGroups) { index ->
            val groupOffset = offset + 16 + (index * 12)
            val startCharCode = readUInt32(groupOffset)
            val endCharCode = readUInt32(groupOffset + 4)
            val startGlyphId = readUInt32(groupOffset + 8)
            for (codePoint in startCharCode..endCharCode) {
                mapping[codePoint] = startGlyphId + (codePoint - startCharCode)
            }
        }
        return mapping
    }

    private fun readPostScriptNameOrNull(): String? {
        val table = tables["name"] ?: return null
        val count = readUInt16(table.offset + 2)
        val stringOffset = table.offset + readUInt16(table.offset + 4)
        repeat(count) { index ->
            val recordOffset = table.offset + 6 + (index * 12)
            val platformId = readUInt16(recordOffset)
            val encodingId = readUInt16(recordOffset + 2)
            val languageId = readUInt16(recordOffset + 4)
            val nameId = readUInt16(recordOffset + 6)
            val length = readUInt16(recordOffset + 8)
            val offsetFromStorage = readUInt16(recordOffset + 10)
            if (nameId != 6 || platformId != 3 || encodingId !in setOf(1, 10) || languageId != 0x0409) return@repeat
            val start = stringOffset + offsetFromStorage
            return bytes.copyOfRange(start, start + length).decodeUtf16Be()
        }
        return null
    }

    private fun readCapHeightOrNull(): Int? {
        val table = tables["OS/2"] ?: return null
        val version = readUInt16(table.offset)
        if (version < 2 || table.length < 90) return null
        return readInt16(table.offset + 88)
    }

    private fun readItalicAngleOrNull(): Int? {
        val table = tables["post"] ?: return null
        val raw = readInt32(table.offset + 4)
        return raw / 65536
    }

    private fun requireTable(tag: String): TrueTypeTable =
        tables[tag] ?: error("Missing TrueType table $tag.")

    private fun readUInt16(offset: Int): Int =
        ((bytes[offset].toInt() and 0xFF) shl 8) or (bytes[offset + 1].toInt() and 0xFF)

    private fun readInt16(offset: Int): Int {
        val value = readUInt16(offset)
        return if (value and 0x8000 != 0) value - 0x10000 else value
    }

    private fun readUInt32(offset: Int): Int =
        ((bytes[offset].toInt() and 0xFF) shl 24) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
            ((bytes[offset + 2].toInt() and 0xFF) shl 8) or
            (bytes[offset + 3].toInt() and 0xFF)

    private fun readInt32(offset: Int): Int = readUInt32(offset)
}

private data class TrueTypeTable(
    val offset: Int,
    val length: Int,
)

private fun ByteArray.decodeUtf16Be(): String = buildString {
    var index = 0
    while (index + 1 < size) {
        append((((this@decodeUtf16Be[index].toInt() and 0xFF) shl 8) or (this@decodeUtf16Be[index + 1].toInt() and 0xFF)).toChar())
        index += 2
    }
}

private fun sanitizePdfName(value: String): String = value.filter { it.isLetterOrDigit() || it == '-' || it == '_' }

