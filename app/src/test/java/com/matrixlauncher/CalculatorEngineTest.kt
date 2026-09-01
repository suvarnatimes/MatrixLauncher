package com.matrixlauncher

import com.matrixlauncher.ui.common.CalculatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorEngineTest {

    @Test
    fun `isMathExpression correctly identifies arithmetic queries`() {
        assertTrue(CalculatorEngine.isMathExpression("12 + 45"))
        assertTrue(CalculatorEngine.isMathExpression("100 * 3.5"))
        assertTrue(CalculatorEngine.isMathExpression("500 / 2"))
        assertTrue(CalculatorEngine.isMathExpression("(12 + 8) * 4"))
        assertTrue(CalculatorEngine.isMathExpression("25% * 800"))

        assertFalse(CalculatorEngine.isMathExpression("Chrome"))
        assertFalse(CalculatorEngine.isMathExpression("12345"))
        assertFalse(CalculatorEngine.isMathExpression(""))
        assertFalse(CalculatorEngine.isMathExpression("hello world + test"))
    }

    @Test
    fun `evaluate computes accurate decimal arithmetic`() {
        assertEquals("57", CalculatorEngine.evaluate("12 + 45"))
        assertEquals("350", CalculatorEngine.evaluate("100 * 3.5"))
        assertEquals("250", CalculatorEngine.evaluate("500 / 2"))
        assertEquals("80", CalculatorEngine.evaluate("(12 + 8) * 4"))
        assertEquals("200", CalculatorEngine.evaluate("25% * 800"))
        assertEquals("1024", CalculatorEngine.evaluate("2 ^ 10"))
    }

    @Test
    fun `division by zero returns null safely without crash`() {
        assertNull(CalculatorEngine.evaluate("10 / 0"))
    }
}
