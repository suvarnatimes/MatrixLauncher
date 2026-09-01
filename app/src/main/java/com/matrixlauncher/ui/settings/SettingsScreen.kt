package com.matrixlauncher.ui.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.DotDensity
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.DoubleTapAction
import com.matrixlauncher.domain.model.LauncherSettings
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.ScreenTimeStats
import com.matrixlauncher.domain.model.SwipeGestureAction
import com.matrixlauncher.domain.model.WebSearchProvider
import com.matrixlauncher.ui.common.ConfigBackupHelper
import com.matrixlauncher.ui.theme.Black
import com.matrixlauncher.ui.theme.DarkSurface
import com.matrixlauncher.ui.theme.DividerColor
import com.matrixlauncher.ui.theme.DotInactiveColor
import com.matrixlauncher.ui.theme.DotMatrixTheme
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.OffWhite
import com.matrixlauncher.ui.theme.SurfaceCard
import com.matrixlauncher.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settings: LauncherSettings,
    screenTimeStats: ScreenTimeStats,
    hiddenApps: List<AppModel>,
    onAccentColorChange: (AccentColor) -> Unit,
    onCustomHexChange: (String) -> Unit,
    onDotDensityChange: (DotDensity) -> Unit,
    onDotShapeChange: (DotShape) -> Unit,
    onScrollerAlignmentChange: (ScrollerAlignment) -> Unit,
    onDoubleTapActionChange: (DoubleTapAction) -> Unit,
    onSwipeLeftActionChange: (SwipeGestureAction) -> Unit,
    onSwipeRightActionChange: (SwipeGestureAction) -> Unit,
    onSearchProviderChange: (WebSearchProvider) -> Unit,
    onToggle24Hour: (Boolean) -> Unit,
    onToggleScreenTime: (Boolean) -> Unit,
    onToggleBatteryBar: (Boolean) -> Unit,
    onToggleScratchpad: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onToggleShader: (Boolean) -> Unit,
    onToggleCrtScanlines: (Boolean) -> Unit,
    onToggleAutoFocusSearch: (Boolean) -> Unit,
    onMaxFavoritesChange: (Int) -> Unit,
    onMindfulPauseChange: (Int) -> Unit,
    onUnhideApp: (String) -> Unit,
    onImportConfig: (String) -> Unit,
    onSetDefaultLauncher: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val accent = LocalMatrixAccentColor.current
    var showHiddenAppsSheet by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SETTINGS // MATRIX",
                    color = White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Divider(color = DividerColor)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section 1: Visual Identity & Retro CRT
                item {
                    SettingsSectionTitle(title = "VISUAL IDENTITY & DISPLAY")
                }

                item {
                    Text(
                        text = "LED ACCENT PALETTE",
                        color = OffWhite,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(AccentColor.entries) { colorOption ->
                            val isSelected = settings.accentColor == colorOption
                            AccentColorChip(
                                color = colorOption,
                                isSelected = isSelected,
                                onClick = { onAccentColorChange(colorOption) }
                            )
                        }
                    }

                    if (settings.accentColor == AccentColor.CUSTOM) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceCard, RoundedCornerShape(4.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "HEX: ",
                                color = DotInactiveColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                            BasicTextField(
                                value = settings.customAccentHex,
                                onValueChange = onCustomHexChange,
                                textStyle = TextStyle(
                                    color = White,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                cursorBrush = SolidColor(accent.primaryColor),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "DOT GRID DENSITY",
                        color = OffWhite,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DotDensity.entries.forEach { densityOption ->
                            val isSelected = settings.dotDensity == densityOption
                            OptionChipButton(
                                label = densityOption.label.split(" ").first(),
                                isSelected = isSelected,
                                accentColor = accent,
                                onClick = { onDotDensityChange(densityOption) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "DOT SHAPE GEOMETRY",
                        color = OffWhite,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DotShape.entries.forEach { shapeOption ->
                            val isSelected = settings.dotShape == shapeOption
                            OptionChipButton(
                                label = shapeOption.label.split(" ").first(),
                                isSelected = isSelected,
                                accentColor = accent,
                                onClick = { onDotShapeChange(shapeOption) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    SettingsToggleRow(
                        title = "RETRO CRT SCANLINES",
                        subtitle = "Horizontal TV raster scanlines (API 33+)",
                        checked = settings.enableCrtScanlines,
                        onCheckedChange = onToggleCrtScanlines
                    )
                }

                // Section 2: Gestures & Fast Scroller
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsSectionTitle(title = "POWER GESTURES & CONTROLS")
                }

                item {
                    Text(
                        text = "FAST SCROLLER RAIL POSITION",
                        color = OffWhite,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScrollerAlignment.entries.forEach { align ->
                            val isSelected = settings.scrollerAlignment == align
                            OptionChipButton(
                                label = if (align == ScrollerAlignment.RIGHT) "RIGHT (DEFAULT)" else "LEFT-HANDED",
                                isSelected = isSelected,
                                accentColor = accent,
                                onClick = { onScrollerAlignmentChange(align) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    DropdownSettingSelector(
                        title = "HOME DOUBLE-TAP ACTION",
                        currentValue = settings.doubleTapAction.label,
                        options = DoubleTapAction.entries.map { it.label },
                        onSelect = { label ->
                            val action = DoubleTapAction.entries.first { it.label == label }
                            onDoubleTapActionChange(action)
                        }
                    )
                }

                item {
                    DropdownSettingSelector(
                        title = "HOME SWIPE LEFT ACTION",
                        currentValue = settings.swipeLeftAction.label,
                        options = SwipeGestureAction.entries.map { it.label },
                        onSelect = { label ->
                            val action = SwipeGestureAction.entries.first { it.label == label }
                            onSwipeLeftActionChange(action)
                        }
                    )
                }

                // Section 3: Universal Search & Productivity
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsSectionTitle(title = "SEARCH & PRODUCTIVITY")
                }

                item {
                    DropdownSettingSelector(
                        title = "DEFAULT WEB SEARCH ENGINE",
                        currentValue = settings.defaultSearchProvider.label,
                        options = WebSearchProvider.entries.map { it.label },
                        onSelect = { label ->
                            val provider = WebSearchProvider.entries.first { it.label == label }
                            onSearchProviderChange(provider)
                        }
                    )
                }

                item {
                    SettingsToggleRow(
                        title = "HOME SCRATCHPAD NOTE",
                        subtitle = "1-line editable note under the clock",
                        checked = settings.showScratchpad,
                        onCheckedChange = onToggleScratchpad
                    )
                }

                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "MAX PINNED FAVORITES",
                                color = White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${settings.maxFavoritesCount} APPS",
                                color = accent.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Slider(
                            value = settings.maxFavoritesCount.toFloat(),
                            onValueChange = { onMaxFavoritesChange(it.toInt()) },
                            valueRange = 1f..15f,
                            steps = 13,
                            colors = SliderDefaults.colors(
                                thumbColor = accent.primaryColor,
                                activeTrackColor = accent.primaryColor,
                                inactiveTrackColor = SurfaceCard
                            )
                        )
                    }
                }

                // Section 4: Digital Wellbeing & Clock
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsSectionTitle(title = "WELLBEING & SYSTEM")
                }

                item {
                    DropdownSettingSelector(
                        title = "MINDFUL LAUNCH PAUSE",
                        currentValue = if (settings.mindfulPauseSeconds == 0) "Disabled" else "${settings.mindfulPauseSeconds} Seconds",
                        options = listOf("Disabled", "3 Seconds", "5 Seconds", "10 Seconds"),
                        onSelect = { option ->
                            val sec = when (option) {
                                "3 Seconds" -> 3
                                "5 Seconds" -> 5
                                "10 Seconds" -> 10
                                else -> 0
                            }
                            onMindfulPauseChange(sec)
                        }
                    )
                }

                item {
                    SettingsToggleRow(
                        title = "24-HOUR CLOCK",
                        subtitle = if (settings.is24HourClock) "23:59 format" else "11:59 PM format",
                        checked = settings.is24HourClock,
                        onCheckedChange = onToggle24Hour
                    )
                }

                item {
                    SettingsToggleRow(
                        title = "BATTERY DOT BAR",
                        subtitle = "10-segment LED battery indicator",
                        checked = settings.showBatteryDotBar,
                        onCheckedChange = onToggleBatteryBar
                    )
                }

                item {
                    SettingsToggleRow(
                        title = "SCREEN TIME GLANCE",
                        subtitle = if (screenTimeStats.hasPermission) "Daily usage: ${screenTimeStats.formattedDuration}" else "Requires Usage Access permission",
                        checked = settings.showScreenTimeGlance,
                        onCheckedChange = onToggleScreenTime
                    )
                    if (settings.showScreenTimeGlance && !screenTimeStats.hasPermission) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accent.primaryColor,
                                contentColor = Black
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "GRANT USAGE PERMISSION",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                item {
                    SettingsToggleRow(
                        title = "HAPTIC FEEDBACK",
                        subtitle = "Tactile clicks on app launch and scroller",
                        checked = settings.hapticsEnabled,
                        onCheckedChange = onToggleHaptics
                    )
                }

                item {
                    SettingsToggleRow(
                        title = "AGSL MATRIX SHADER",
                        subtitle = "Hardware-accelerated breathing grid (API 33+)",
                        checked = settings.agslShaderEnabled,
                        onCheckedChange = onToggleShader
                    )
                }

                item {
                    SettingsToggleRow(
                        title = "AUTO-FOCUS SEARCH KEYBOARD",
                        subtitle = "Immediately open keyboard when opening drawer",
                        checked = settings.autoFocusSearch,
                        onCheckedChange = onToggleAutoFocusSearch
                    )
                }

                // Section 5: Hidden Apps, Backup & Restore
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsSectionTitle(title = "BACKUP & CONFIGURATION")
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showHiddenAppsSheet = true }
                            .background(SurfaceCard, RoundedCornerShape(4.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "HIDDEN APPLICATIONS",
                                color = White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${hiddenApps.size} apps hidden from drawer",
                                color = DotInactiveColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "VIEW",
                            color = accent.primaryColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val json = ConfigBackupHelper.exportToJson(
                                    settings = settings,
                                    customLabels = emptyMap(),
                                    hiddenPackages = hiddenApps.map { it.packageName }.toSet()
                                )
                                exportedJsonText = json
                                showExportDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard, contentColor = White),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "EXPORT", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showImportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard, contentColor = White),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "IMPORT", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                    }
                }

                item {
                    Button(
                        onClick = onSetDefaultLauncher,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurface,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, accent.primaryColor, RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = "SET AS DEFAULT LAUNCHER",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = accent.primaryColor
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "MATRIX LAUNCHER v1.1.0\nBATTERY EFFICIENT // RETRO OLED",
                        color = DotInactiveColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Hidden Apps Sheet
        if (showHiddenAppsSheet) {
            HiddenAppsModalSheet(
                hiddenApps = hiddenApps,
                onDismiss = { showHiddenAppsSheet = false },
                onUnhideApp = onUnhideApp
            )
        }

        // Export Dialog
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                containerColor = DarkSurface,
                title = {
                    Text(text = "EXPORT CONFIGURATION", color = White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(text = "Copy the JSON configuration below to transfer your setup:", color = DotInactiveColor, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().background(SurfaceCard, RoundedCornerShape(4.dp)).padding(8.dp)) {
                            Text(text = exportedJsonText, color = OffWhite, fontFamily = FontFamily.Monospace, fontSize = 10.sp, maxLines = 8)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        clipboardManager.setText(AnnotatedString(exportedJsonText))
                        showExportDialog = false
                    }) {
                        Text(text = "COPY TO CLIPBOARD", color = accent.primaryColor, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text(text = "CLOSE", color = DotInactiveColor, fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }

        // Import Dialog
        if (showImportDialog) {
            var importInput by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                containerColor = DarkSurface,
                title = {
                    Text(text = "IMPORT CONFIGURATION", color = White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(text = "Paste your JSON configuration string below:", color = DotInactiveColor, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = importInput,
                            onValueChange = { importInput = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp).background(SurfaceCard, RoundedCornerShape(4.dp)).padding(8.dp),
                            textStyle = TextStyle(color = White, fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                            cursorBrush = SolidColor(accent.primaryColor)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onImportConfig(importInput.trim())
                        showImportDialog = false
                    }) {
                        Text(text = "RESTORE", color = accent.primaryColor, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) {
                        Text(text = "CANCEL", color = DotInactiveColor, fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = "// $title",
        color = DotInactiveColor,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp
    )
}

@Composable
private fun AccentColorChip(
    color: AccentColor,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = color.primaryColor, shape = CircleShape)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) White else DividerColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Black, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = color.name.take(4),
            color = if (isSelected) White else DotInactiveColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun OptionChipButton(
    label: String,
    isSelected: Boolean,
    accentColor: AccentColor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) accentColor.primaryColor.copy(alpha = 0.15f) else SurfaceCard,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) accentColor.primaryColor else DividerColor,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) accentColor.primaryColor else OffWhite,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSettingSelector(
    title: String,
    currentValue: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = OffWhite,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .background(SurfaceCard, RoundedCornerShape(4.dp))
                    .border(1.dp, DividerColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = currentValue.uppercase(),
                    color = White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(DarkSurface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.uppercase(),
                                color = White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val accent = LocalMatrixAccentColor.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = White,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = DotInactiveColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Black,
                checkedTrackColor = accent.primaryColor,
                uncheckedThumbColor = DotInactiveColor,
                uncheckedTrackColor = SurfaceCard
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HiddenAppsModalSheet(
    hiddenApps: List<AppModel>,
    onDismiss: () -> Unit,
    onUnhideApp: (String) -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        contentColor = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "HIDDEN APPLICATIONS",
                color = White,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Tap UNHIDE to restore an application to your app drawer.",
                color = DotInactiveColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = DividerColor)
            Spacer(modifier = Modifier.height(8.dp))

            if (hiddenApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO HIDDEN APPS",
                        color = DotInactiveColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(hiddenApps.size) { index ->
                        val app = hiddenApps[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.displayLabel.uppercase(),
                                    color = White,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = app.packageName,
                                    color = DotInactiveColor,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            }

                            Button(
                                onClick = { onUnhideApp(app.packageName) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accent.primaryColor,
                                    contentColor = Black
                                ),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "UNHIDE",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
