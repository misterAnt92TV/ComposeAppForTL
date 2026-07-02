package com.tlincompose.core

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.abs
import kotlin.math.min

const val BrandingSquareLogoMaxWidthPx: Int = 450
const val BrandingSquareLogoMaxHeightPx: Int = 450
const val BrandingRectangleLogoMaxWidthPx: Int = 800
const val BrandingRectangleLogoMaxHeightPx: Int = 350
const val BrandingLogoPdfMaxWidthPt: Float = 110f
const val BrandingLogoPdfMaxHeightPt: Float = 52f
const val BrandingLogoSpreadsheetMaxWidthPx: Int = 180
const val BrandingLogoSpreadsheetMaxHeightPx: Int = 80
const val BrandingLogoHeaderMaxWidthDp: Int = 108
const val BrandingLogoHeaderMaxHeightDp: Int = 48
const val MaxBrandingLogoBytes: Int = 512 * 1024

private const val BrandingLogoSquareAspectTolerance: Float = 0.2f

data class BrandingLogoSize(
    val width: Int,
    val height: Int,
)

data class BrandingLogoImage(
    val bytes: ByteArray,
    val size: BrandingLogoSize,
)

fun brandingLogoTargetSizeFor(width: Int, height: Int): BrandingLogoSize {
    require(width > 0) { "width must be positive" }
    require(height > 0) { "height must be positive" }
    return if (isSquareOrRoundLogo(width, height)) {
        BrandingLogoSize(
            width = BrandingSquareLogoMaxWidthPx,
            height = BrandingSquareLogoMaxHeightPx,
        )
    } else {
        BrandingLogoSize(
            width = BrandingRectangleLogoMaxWidthPx,
            height = BrandingRectangleLogoMaxHeightPx,
        )
    }
}

fun computeBrandingLogoRenderSize(
    width: Int,
    height: Int,
    maxWidth: Float,
    maxHeight: Float,
): BrandingLogoSize {
    require(width > 0) { "width must be positive" }
    require(height > 0) { "height must be positive" }
    val scale = min(maxWidth / width.toFloat(), maxHeight / height.toFloat())
    return BrandingLogoSize(
        width = (width * scale).toInt().coerceAtLeast(1),
        height = (height * scale).toInt().coerceAtLeast(1),
    )
}

fun isSvgImageBytes(bytes: ByteArray): Boolean {
    val content = bytes.decodeToString()
        .trimStart('\uFEFF', ' ', '\n', '\r', '\t')
    return content.contains("<svg", ignoreCase = true)
}

fun parseSvgViewportSize(bytes: ByteArray): BrandingLogoSize? {
    val svgTag = Regex("<svg\\b[^>]*>", RegexOption.IGNORE_CASE).find(bytes.decodeToString())?.value ?: return null
    val width = svgTag.findSvgAttributeNumber("width")
    val height = svgTag.findSvgAttributeNumber("height")
    if (width != null && height != null && width > 0 && height > 0) {
        return BrandingLogoSize(width = width, height = height)
    }
    val viewBox = Regex("""viewBox\s*=\s*['"]([^'"]+)['"]""", RegexOption.IGNORE_CASE)
        .find(svgTag)
        ?.groupValues
        ?.getOrNull(1)
        ?.trim()
        ?: return null
    val values = viewBox.split(Regex("""[\s,]+"""))
    if (values.size != 4) return null
    val viewBoxWidth = values[2].toFloatOrNull()?.toInt() ?: return null
    val viewBoxHeight = values[3].toFloatOrNull()?.toInt() ?: return null
    return if (viewBoxWidth > 0 && viewBoxHeight > 0) {
        BrandingLogoSize(width = viewBoxWidth, height = viewBoxHeight)
    } else {
        null
    }
}

@OptIn(ExperimentalEncodingApi::class)
fun decodeBrandingLogoImage(base64: String?): BrandingLogoImage? {
    if (base64.isNullOrBlank()) return null
    val bytes = runCatching { Base64.Default.decode(base64) }.getOrNull() ?: return null
    val size = parseJpegSize(bytes) ?: return null
    return BrandingLogoImage(bytes = bytes, size = size)
}

fun parseJpegSize(bytes: ByteArray): BrandingLogoSize? {
    if (bytes.size < 4) return null
    if ((bytes[0].toInt() and 0xFF) != 0xFF || (bytes[1].toInt() and 0xFF) != 0xD8) return null

    var index = 2
    while (index + 8 < bytes.size) {
        if ((bytes[index].toInt() and 0xFF) != 0xFF) {
            index++
            continue
        }
        var markerIndex = index + 1
        while (markerIndex < bytes.size && (bytes[markerIndex].toInt() and 0xFF) == 0xFF) {
            markerIndex++
        }
        if (markerIndex >= bytes.size) return null
        val marker = bytes[markerIndex].toInt() and 0xFF
        if (marker == 0xD9 || marker == 0xDA) return null
        if (markerIndex + 2 >= bytes.size) return null
        val segmentLength = readBigEndianUInt16(bytes, markerIndex + 1)
        if (segmentLength < 2) return null
        if (marker in setOf(0xC0, 0xC1, 0xC2, 0xC3, 0xC5, 0xC6, 0xC7, 0xC9, 0xCA, 0xCB, 0xCD, 0xCE, 0xCF)) {
            if (markerIndex + 7 >= bytes.size) return null
            val height = readBigEndianUInt16(bytes, markerIndex + 4)
            val width = readBigEndianUInt16(bytes, markerIndex + 6)
            return if (width > 0 && height > 0) {
                BrandingLogoSize(width = width, height = height)
            } else {
                null
            }
        }
        index = markerIndex + 1 + segmentLength
    }
    return null
}

private fun isSquareOrRoundLogo(width: Int, height: Int): Boolean {
    val aspectRatio = width.toFloat() / height.toFloat()
    return abs(aspectRatio - 1f) <= BrandingLogoSquareAspectTolerance
}

private fun String.findSvgAttributeNumber(name: String): Int? {
    val value = Regex("""$name\s*=\s*['"]([^'"]+)['"]""", RegexOption.IGNORE_CASE)
        .find(this)
        ?.groupValues
        ?.getOrNull(1)
        ?: return null
    val numericPart = Regex("""[-+]?\d*\.?\d+""").find(value)?.value ?: return null
    return numericPart.toFloatOrNull()?.toInt()
}

private fun readBigEndianUInt16(bytes: ByteArray, startIndex: Int): Int {
    return ((bytes[startIndex].toInt() and 0xFF) shl 8) or (bytes[startIndex + 1].toInt() and 0xFF)
}
