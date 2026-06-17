package com.tlincompose.data.export

import kotlin.test.Test
import kotlin.test.assertContentEquals

class PdfFontSupportTest {
    @Test
    fun windows1252EncodingPreservesAccentedWesternCharacters() {
        val bytes = encodeWindows1252("Attività • José García")

        assertContentEquals(
            byteArrayOf(
                'A'.code.toByte(),
                't'.code.toByte(),
                't'.code.toByte(),
                'i'.code.toByte(),
                'v'.code.toByte(),
                'i'.code.toByte(),
                't'.code.toByte(),
                0xE0.toByte(),
                ' '.code.toByte(),
                0x95.toByte(),
                ' '.code.toByte(),
                'J'.code.toByte(),
                'o'.code.toByte(),
                's'.code.toByte(),
                0xE9.toByte(),
                ' '.code.toByte(),
                'G'.code.toByte(),
                'a'.code.toByte(),
                'r'.code.toByte(),
                'c'.code.toByte(),
                0xED.toByte(),
                'a'.code.toByte(),
            ),
            bytes,
        )
    }
}
