package com.matrixlauncher

import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.DotDensity
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.DoubleTapAction
import com.matrixlauncher.domain.model.LauncherSettings
import com.matrixlauncher.ui.common.ConfigBackupHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfigBackupHelperTest {

    @Test
    fun `export and import round trip maintains data integrity`() {
        val originalSettings = LauncherSettings(
            accentColor = AccentColor.EMERALD,
            customAccentHex = "#00FF88",
            dotDensity = DotDensity.DENSE,
            dotShape = DotShape.SQUARE,
            doubleTapAction = DoubleTapAction.TOGGLE_TORCH,
            maxFavoritesCount = 12,
            scratchpadNote = "BUY GROCERIES",
            favoritePackageNames = setOf("com.chrome", "com.spotify")
        )

        val customLabels = mapOf("com.chrome" to "Web", "com.spotify" to "Tunes")
        val hiddenPackages = setOf("com.secret.app")

        val json = ConfigBackupHelper.exportToJson(originalSettings, customLabels, hiddenPackages)
        assertNotNull(json)
        assertTrue(json.contains("BUY GROCERIES"))

        val restored = ConfigBackupHelper.importFromJson(json)
        assertNotNull(restored)
        assertEquals(AccentColor.EMERALD, restored!!.settings.accentColor)
        assertEquals(DotShape.SQUARE, restored.settings.dotShape)
        assertEquals(12, restored.settings.maxFavoritesCount)
        assertEquals("BUY GROCERIES", restored.settings.scratchpadNote)
        assertEquals("Web", restored.customLabels["com.chrome"])
        assertTrue(restored.hiddenPackages.contains("com.secret.app"))
    }
}
