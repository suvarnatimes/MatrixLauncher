package com.matrixlauncher

import com.matrixlauncher.ui.graphics.DotMatrixCanvas
import com.matrixlauncher.ui.graphics.DotMatrixFont
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DotMatrixFontTest {

    @Test
    fun `all alphanumeric characters have 7 rows in glyph table`() {
        val testChars = ('0'..'9') + ('A'..'Z') + listOf(':', '.', '-', '_', '/', '%', '>', '<', '#', '+', '!', '?', '(', ')', ' ')
        for (char in testChars) {
            val glyph = DotMatrixFont.getGlyph(char)
            assertNotNull("Glyph for $char must not be null", glyph)
            assertEquals("Glyph for $char must have 7 rows", 7, glyph.size)
        }
    }

    @Test
    fun `lowercase letters resolve to same glyph as uppercase`() {
        for (char in 'a'..'z') {
            val upperGlyph = DotMatrixFont.getGlyph(char.uppercaseChar())
            val lowerGlyph = DotMatrixFont.getGlyph(char)
            assertTrue("Glyph for '$char' should match uppercase", upperGlyph.contentEquals(lowerGlyph))
        }
    }

    @Test
    fun `isDotActive returns valid bounds and correct bits`() {
        // Character '1': col 2 should be active on row 0
        assertTrue(DotMatrixFont.isDotActive('1', 2, 0))
        // Outside bounds should return false
        assertFalse(DotMatrixFont.isDotActive('1', -1, 0))
        assertFalse(DotMatrixFont.isDotActive('1', 5, 0))
        assertFalse(DotMatrixFont.isDotActive('1', 0, 7))
    }

    @Test
    fun `width and height calculations are strictly positive for non-empty text`() {
        val width = DotMatrixCanvas.calculateDotMatrixTextWidth(
            textLength = 5,
            dotRadius = 2f,
            dotSpacing = 6f,
            charSpacing = 8f
        )
        val height = DotMatrixCanvas.calculateDotMatrixTextHeight(
            dotRadius = 2f,
            dotSpacing = 6f
        )

        assertTrue(width > 0f)
        assertTrue(height > 0f)
        assertEquals(0f, DotMatrixCanvas.calculateDotMatrixTextWidth(0, 2f, 6f), 0.001f)
    }

    @Test
    fun `unsupported character falls back gracefully`() {
        val unknownGlyph = DotMatrixFont.getGlyph('§')
        assertEquals(7, unknownGlyph.size)
    }
}
