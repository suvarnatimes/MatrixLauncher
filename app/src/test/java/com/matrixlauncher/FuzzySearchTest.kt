package com.matrixlauncher

import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.ui.common.FuzzySearch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FuzzySearchTest {

    @Test
    fun `empty query returns all apps unchanged`() {
        val apps = listOf(
            AppModel("pkg1", "Act1", "Calculator"),
            AppModel("pkg2", "Act2", "Chrome")
        )
        val filtered = FuzzySearch.filterApps(apps, "")
        assertEquals(2, filtered.size)
        assertEquals(apps, filtered)
    }

    @Test
    fun `exact match scores highest`() {
        val apps = listOf(
            AppModel("pkg1", "Act1", "Google Chrome"),
            AppModel("pkg2", "Act2", "Chrome"),
            AppModel("pkg3", "Act3", "Chromium")
        )
        val filtered = FuzzySearch.filterApps(apps, "chrome")
        assertEquals("Chrome", filtered.first().displayLabel)
    }

    @Test
    fun `prefix match outranks generic subsequence match`() {
        val apps = listOf(
            AppModel("pkg1", "Act1", "Microphone"),
            AppModel("pkg2", "Act2", "Phone"),
            AppModel("pkg3", "Act3", "Photo Editor")
        )
        val filtered = FuzzySearch.filterApps(apps, "ph")
        assertTrue(filtered.any { it.displayLabel == "Phone" })
        assertTrue(filtered.any { it.displayLabel == "Photo Editor" })
        assertEquals("Phone", filtered.first().displayLabel)
    }

    @Test
    fun `acronym matching works for multi-word labels`() {
        val result = FuzzySearch.match("gc", "Google Chrome")
        assertTrue(result.isMatch)
        assertTrue(result.score > 0)
    }

    @Test
    fun `case insensitive matching works`() {
        val apps = listOf(
            AppModel("com.google.android.youtube", "Main", "YouTube")
        )
        val filtered = FuzzySearch.filterApps(apps, "youtube")
        assertEquals(1, filtered.size)
        assertEquals("YouTube", filtered.first().displayLabel)
    }

    @Test
    fun `non matching query returns empty list`() {
        val apps = listOf(
            AppModel("pkg1", "Act1", "Camera"),
            AppModel("pkg2", "Act2", "Settings")
        )
        val filtered = FuzzySearch.filterApps(apps, "xyz123")
        assertTrue(filtered.isEmpty())
    }

    @Test
    fun `custom renamed label is prioritized in search`() {
        val app = AppModel("pkg1", "Act1", "OriginalName", customLabel = "SuperBrowser")
        val result = FuzzySearch.match("super", app.displayLabel)
        assertTrue(result.isMatch)
    }
}
