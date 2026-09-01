package com.matrixlauncher.ui.common

import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.DotDensity
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.LauncherSettings
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.WebSearchProvider
import org.json.JSONArray
import org.json.JSONObject

data class BackupData(
    val settings: LauncherSettings,
    val customLabels: Map<String, String>,
    val hiddenPackages: Set<String>
)

object ConfigBackupHelper {

    fun exportToJson(
        settings: LauncherSettings,
        customLabels: Map<String, String>,
        hiddenPackages: Set<String>
    ): String {
        val root = JSONObject()

        val sObj = JSONObject().apply {
            put("accentColor", settings.accentColor.name)
            put("customAccentHex", settings.customAccentHex)
            put("dotDensity", settings.dotDensity.name)
            put("dotShape", settings.dotShape.name)
            put("scrollerAlignment", settings.scrollerAlignment.name)
            put("swipeDownAction", settings.swipeDownAction)
            put("swipeUpAction", settings.swipeUpAction)
            put("swipeLeftAction", settings.swipeLeftAction)
            put("swipeRightAction", settings.swipeRightAction)
            put("doubleTapAction", settings.doubleTapAction)
            put("pinchInAction", settings.pinchInAction)
            put("pinchOutAction", settings.pinchOutAction)
            put("twoFingerSwipeDownAction", settings.twoFingerSwipeDownAction)
            put("twoFingerSwipeUpAction", settings.twoFingerSwipeUpAction)
            put("defaultSearchProvider", settings.defaultSearchProvider.name)
            put("is24HourClock", settings.is24HourClock)
            put("showScreenTimeGlance", settings.showScreenTimeGlance)
            put("showBatteryDotBar", settings.showBatteryDotBar)
            put("showScratchpad", settings.showScratchpad)
            put("scratchpadNote", settings.scratchpadNote)
            put("hapticsEnabled", settings.hapticsEnabled)
            put("agslShaderEnabled", settings.agslShaderEnabled)
            put("enableCrtScanlines", settings.enableCrtScanlines)
            put("autoFocusSearch", settings.autoFocusSearch)
            put("maxFavoritesCount", settings.maxFavoritesCount)
            put("mindfulPauseSeconds", settings.mindfulPauseSeconds)

            val favArray = JSONArray()
            settings.favoritePackageNames.forEach { favArray.put(it) }
            put("favorites", favArray)
        }
        root.put("settings", sObj)

        val labelsObj = JSONObject()
        customLabels.forEach { (pkg, label) ->
            labelsObj.put(pkg, label)
        }
        root.put("customLabels", labelsObj)

        val hiddenArray = JSONArray()
        hiddenPackages.forEach { hiddenArray.put(it) }
        root.put("hiddenPackages", hiddenArray)

        return root.toString(2)
    }

    fun importFromJson(jsonString: String): BackupData? {
        return try {
            val root = JSONObject(jsonString)
            val sObj = root.getJSONObject("settings")

            val favSet = mutableSetOf<String>()
            val favArray = sObj.optJSONArray("favorites")
            if (favArray != null) {
                for (i in 0 until favArray.length()) {
                    favSet.add(favArray.getString(i))
                }
            }

            val accent = try { AccentColor.valueOf(sObj.optString("accentColor")) } catch (e: Exception) { AccentColor.CRIMSON }
            val density = try { DotDensity.valueOf(sObj.optString("dotDensity")) } catch (e: Exception) { DotDensity.STANDARD }
            val shape = try { DotShape.valueOf(sObj.optString("dotShape")) } catch (e: Exception) { DotShape.CIRCLE }
            val scroller = try { ScrollerAlignment.valueOf(sObj.optString("scrollerAlignment")) } catch (e: Exception) { ScrollerAlignment.RIGHT }
            val provider = try { WebSearchProvider.valueOf(sObj.optString("defaultSearchProvider")) } catch (e: Exception) { WebSearchProvider.DUCK_DUCK_GO }

            val settings = LauncherSettings(
                accentColor = accent,
                customAccentHex = sObj.optString("customAccentHex", "#FF2E2E"),
                dotDensity = density,
                dotShape = shape,
                scrollerAlignment = scroller,
                swipeDownAction = sObj.optString("swipeDownAction", "EXPAND_NOTIFICATIONS"),
                swipeUpAction = sObj.optString("swipeUpAction", "OPEN_DRAWER"),
                swipeLeftAction = sObj.optString("swipeLeftAction", "NONE"),
                swipeRightAction = sObj.optString("swipeRightAction", "NONE"),
                doubleTapAction = sObj.optString("doubleTapAction", "TOGGLE_TORCH"),
                pinchInAction = sObj.optString("pinchInAction", "OPEN_SETTINGS"),
                pinchOutAction = sObj.optString("pinchOutAction", "OPEN_DRAWER"),
                twoFingerSwipeDownAction = sObj.optString("twoFingerSwipeDownAction", "OPEN_SEARCH"),
                twoFingerSwipeUpAction = sObj.optString("twoFingerSwipeUpAction", "OPEN_DRAWER"),
                defaultSearchProvider = provider,
                is24HourClock = sObj.optBoolean("is24HourClock", true),
                showScreenTimeGlance = sObj.optBoolean("showScreenTimeGlance", true),
                showBatteryDotBar = sObj.optBoolean("showBatteryDotBar", true),
                showScratchpad = sObj.optBoolean("showScratchpad", true),
                scratchpadNote = sObj.optString("scratchpadNote", "TAP TO WRITE SCRATCHPAD NOTE_"),
                hapticsEnabled = sObj.optBoolean("hapticsEnabled", true),
                agslShaderEnabled = sObj.optBoolean("agslShaderEnabled", true),
                enableCrtScanlines = sObj.optBoolean("enableCrtScanlines", false),
                autoFocusSearch = sObj.optBoolean("autoFocusSearch", false),
                maxFavoritesCount = sObj.optInt("maxFavoritesCount", 8),
                mindfulPauseSeconds = sObj.optInt("mindfulPauseSeconds", 0),
                favoritePackageNames = favSet
            )

            val customLabels = mutableMapOf<String, String>()
            val labelsObj = root.optJSONObject("customLabels")
            labelsObj?.keys()?.forEach { pkg ->
                customLabels[pkg] = labelsObj.getString(pkg)
            }

            val hiddenPackages = mutableSetOf<String>()
            val hiddenArray = root.optJSONArray("hiddenPackages")
            if (hiddenArray != null) {
                for (i in 0 until hiddenArray.length()) {
                    hiddenPackages.add(hiddenArray.getString(i))
                }
            }

            BackupData(settings, customLabels, hiddenPackages)
        } catch (e: Exception) {
            null
        }
    }
}
