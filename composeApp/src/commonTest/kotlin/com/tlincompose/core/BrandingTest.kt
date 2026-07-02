package com.tlincompose.core

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalEncodingApi::class)
class BrandingTest {
    @Test
    fun targetSizeUsesSquareBoundsForSquareLogos() {
        val target = brandingLogoTargetSizeFor(width = 512, height = 512)

        assertEquals(BrandingSquareLogoMaxWidthPx, target.width)
        assertEquals(BrandingSquareLogoMaxHeightPx, target.height)
    }

    @Test
    fun targetSizeUsesRectangularBoundsForWideLogos() {
        val target = brandingLogoTargetSizeFor(width = 1200, height = 400)

        assertEquals(BrandingRectangleLogoMaxWidthPx, target.width)
        assertEquals(BrandingRectangleLogoMaxHeightPx, target.height)
    }

    @Test
    fun renderSizeScalesSmallImagesUpToMaxBounds() {
        val renderSize = computeBrandingLogoRenderSize(
            width = 100,
            height = 100,
            maxWidth = BrandingSquareLogoMaxWidthPx.toFloat(),
            maxHeight = BrandingSquareLogoMaxHeightPx.toFloat(),
        )

        assertEquals(BrandingSquareLogoMaxWidthPx, renderSize.width)
        assertEquals(BrandingSquareLogoMaxHeightPx, renderSize.height)
    }

    @Test
    fun svgDetectionRecognizesSvgPayload() {
        assertTrue(isSvgImageBytes("""<svg viewBox="0 0 24 24"></svg>""".encodeToByteArray()))
    }

    @Test
    fun jpegParserExtractsDimensions() {
        val image = decodeBrandingLogoImage(Base64.Default.encode(minimalJpegBytes(width = 450, height = 450)))

        assertNotNull(image)
        assertEquals(450, image.size.width)
        assertEquals(450, image.size.height)
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
