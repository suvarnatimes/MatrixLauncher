package com.matrixlauncher.ui.settings

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.DotDensity
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.HomeWidgetType
import com.matrixlauncher.domain.model.IconStyle
import com.matrixlauncher.domain.model.LauncherSettings
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.ScreenTimeStats
import com.matrixlauncher.domain.model.WebSearchProvider
import com.matrixlauncher.ui.common.ConfigBackupHelper
import com.matrixlauncher.ui.graphics.DotMatrixAppIcon
import com.matrixlauncher.ui.theme.Black
import com.matrixlauncher.ui.theme.DarkSurface
import com.matrixlauncher.ui.theme.DividerColor
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.OffWhite
import com.matrixlauncher.ui.theme.SurfaceCard
import com.matrixlauncher.ui.theme.TextMuted
import com.matrixlauncher.ui.theme.TextPrimary
import com.matrixlauncher.ui.theme.TextSecondary
import com.matrixlauncher.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settings: LauncherSettings,
    allApps: List<AppModel> = emptyList(),
    screenTimeStats: ScreenTimeStats,
    hiddenApps: List<AppModel>,
    isDefaultLauncher: Boolean,
    onAccentColorChange: (AccentColor) -> Unit,
    onCustomHexChange: (String) -> Unit,
    onDotDensityChange: (DotDensity) -> Unit,
    onDotShapeChange: (DotShape) -> Unit,
    onIconStyleChange: (IconStyle) -> Unit,
    onEnabledWidgetsChange: (List<HomeWidgetType>) -> Unit,
    onOpenIconStudio: () -> Unit,
    onScrollerAlignmentChange: (ScrollerAlignment) -> Unit,
    onUpdateGestureAction: (gestureKey: String, actionString: String) -> Unit,
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
    val clipboardManager = LocalClipboardManager.current
    val accent = LocalMatrixAccentColor.current
    var showHiddenAppsSheet by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }

    // App Picker Dialog for Gestures
    var gestureKeyForAppPick by remember { mutableStateOf<String?>(null) }

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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "MATRIX SETTINGS",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section 0: DEFAULT LAUNCHER STATUS & TOGGLE
                item {
                    SettingsSectionHeader(title = "DEFAULT HOME LAUNCHER")
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurface, RoundedCornerShape(6.dp))
                            .border(
                                1.dp,
                                if (isDefaultLauncher) accent.primaryColor else AccentColor.CRIMSON.primaryColor,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable(onClick = onSetDefaultLauncher)
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    tint = if (isDefaultLauncher) accent.primaryColor else AccentColor.CRIMSON.primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isDefaultLauncher) "MATRIX LAUNCHER IS ACTIVE" else "NOT SET AS DEFAULT",
                                        color = TextPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isDefaultLauncher) "Tap to change default home app" else "Tap to enable as default home launcher",
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Text(
                                text = if (isDefaultLauncher) "[ON]" else "[SET]",
                                color = if (isDefaultLauncher) accent.primaryColor else AccentColor.CRIMSON.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Section 1: ICON STUDIO & ICON STYLES
                item {
                    SettingsSectionHeader(title = "APP ICONS & ICON STUDIO")
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceCard, RoundedCornerShape(6.dp))
                            .border(1.dp, accent.primaryColor, RoundedCornerShape(6.dp))
                            .clickable(onClick = onOpenIconStudio)
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Brush,
                                    contentDescription = null,
                                    tint = accent.primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "CUSTOMIZE APP ICONS (STUDIO)",
                                        color = TextPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Upload custom PNG/JPEG, pixelate to LED dots & colors",
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Text(
                                text = "OPEN >",
                                color = accent.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "GLOBAL ICON THEME",
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(IconStyle.entries) { style ->
                            val isSelected = settings.iconStyle == style
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) accent.primaryColor.copy(alpha = 0.25f) else DarkSurface,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) accent.primaryColor else DividerColor,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { onIconStyleChange(style) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = style.label,
                                        color = if (isSelected) accent.primaryColor else TextPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = style.description,
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 2: GESTURES & MULTI-TOUCH CONTROLS (With App Binding)
                item {
                    SettingsSectionHeader(title = "GESTURES & MULTI-TOUCH CONTROLS")
                    Spacer(modifier = Modifier.height(8.dp))

                    GestureRowItem(
                        gestureLabel = "SWIPE UP (1 FINGER)",
                        currentAction = settings.swipeUpAction,
                        allApps = allApps,
                        onActionChange = { onUpdateGestureAction("SWIPE_UP", it) },
                        onPickApp = { gestureKeyForAppPick = "SWIPE_UP" }
                    )

                    GestureRowItem(
                        gestureLabel = "SWIPE DOWN (1 FINGER)",
                        currentAction = settings.swipeDownAction,
                        allApps = allApps,
                        onActionChange = { onUpdateGestureAction("SWIPE_DOWN", it) },
                        onPickApp = { gestureKeyForAppPick = "SWIPE_DOWN" }
                    )

                    GestureRowItem(
                        gestureLabel = "SWIPE LEFT (1 FINGER)",
                        currentAction = settings.swipeLeftAction,
                        allApps = allApps,
                        onActionChange = { onUpdateGestureAction("SWIPE_LEFT", it) },
                        onPickApp = { gestureKeyForAppPick = "SWIPE_LEFT" }
                    )

                    GestureRowItem(
                        gestureLabel = "SWIPE RIGHT (1 FINGER)",
                        currentAction = settings.swipeRightAction,
                        allApps = allApps,
                        onActionChange = { onUpdateGestureAction("SWIPE_RIGHT", it) },
                        onPickApp = { gestureKeyForAppPick = "SWIPE_RIGHT" }
                    )

                    GestureRowItem(
                        gestureLabel = "DOUBLE-TAP HOME SCREEN",
                        currentAction = settings.doubleTapAction,
                        allApps = allApps,
                        onActionChange = { onUpdateGestureAction("DOUBLE_TAP", it) },
                        onPickApp = { gestureKeyForAppPick = "DOUBLE_TAP" }
                    )

                    GestureRowItem(
                        gestureLabel = "TWO-FINGER SWIPE UP",
                        currentAction = settings.twoFingerSwipeUpAction,
                        allApps = allApps,
                        onActionChange = { onUpdateGestureAction("TWO_FINGER_SWIPE_UP", it) },
                        onPickApp = { gestureKeyForAppPick = "TWO_FINGER_SWIPE_UP" }
                    )

                    GestureRowItem(
                        gestureLabel = "TWO-FINGER SWIPE DOWN",
                        currentAction = settings.twoFingerSwipeDownAction,
                        allApps = allApps,
                        onActionChange = { onUpdateGestureAction("TWO_FINGER_SWIPE_DOWN", it) },
                        onPickApp = { gestureKeyForAppPick = "TWO_FINGER_SWIPE_DOWN" }
                    )

                    GestureRowItem(
                        gestureLabel = "PINCH IN (ZOOM IN - 2 FINGERS)",
                        currentAction = settings.pinchInAction,
                        allApps = allApps,
                        onActionChange = { onUpdateGestureAction("PINCH_IN", it) },
                        onPickApp = { gestureKeyForAppPick = "PINCH_IN" }
                    )

                    GestureRowItem(
                        gestureLabel = "PINCH OUT (ZOOM OUT - 2 FINGERS)",
                        currentAction = settings.pinchOutAction,
                        allApps = allApps,
                        onActionChange = { onUpdateGestureAction("PINCH_OUT", it) },
                        onPickApp = { gestureKeyForAppPick = "PINCH_OUT" }
                    )

                    SettingsToggleRow(
                        title = "TACTILE HAPTIC FEEDBACK",
                        subtitle = "Subtle vibration ticks when navigating and tapping",
                        checked = settings.hapticsEnabled,
                        onCheckedChange = onToggleHaptics
                    )
                }

                // Section 3: TRENDY DOT-MATRIX WIDGETS MANAGER
                item {
                    SettingsSectionHeader(title = "TRENDY DOT-MATRIX WIDGETS")
                    Spacer(modifier = Modifier.height(8.dp))

                    HomeWidgetType.entries.forEach { widget ->
                        val isEnabled = settings.enabledWidgets.contains(widget)
                        SettingsToggleRow(
                            title = widget.title,
                            subtitle = widget.description,
                            checked = isEnabled,
                            onCheckedChange = { checked ->
                                val current = settings.enabledWidgets.toMutableList()
                                if (checked) {
                                    if (!current.contains(widget)) current.add(widget)
                                } else {
                                    current.remove(widget)
                                }
                                onEnabledWidgetsChange(current)
                            }
                        )
                    }
                }

                // Section 4: THEME & COLOR PALETTE
                item {
                    SettingsSectionHeader(title = "LED ACCENT COLOR")
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(AccentColor.entries) { colorOption ->
                            val isSelected = settings.accentColor == colorOption
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) accent.primaryColor.copy(alpha = 0.25f) else DarkSurface,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) accent.primaryColor else DividerColor,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { onAccentColorChange(colorOption) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(colorOption.primaryColor, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = colorOption.displayName,
                                        color = if (isSelected) accent.primaryColor else TextPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 5: DOT MATRIX GRID & SHAPES
                item {
                    SettingsSectionHeader(title = "DOT MATRIX GRID")
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "DOT SHAPE", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DotShape.entries.forEach { shape ->
                            val isSelected = settings.dotShape == shape
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) accent.primaryColor.copy(alpha = 0.2f) else DarkSurface,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) accent.primaryColor else DividerColor,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { onDotShapeChange(shape) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = shape.label,
                                    color = if (isSelected) accent.primaryColor else TextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    SettingsToggleRow(
                        title = "AGSL HARDWARE SHADER",
                        subtitle = "Hardware-accelerated CRT scanlines & breathing wave",
                        checked = settings.agslShaderEnabled,
                        onCheckedChange = onToggleShader
                    )

                    SettingsToggleRow(
                        title = "RETRO CRT SCANLINES",
                        subtitle = "Subtle cathode-ray tube horizontal scanlines",
                        checked = settings.enableCrtScanlines,
                        onCheckedChange = onToggleCrtScanlines
                    )
                }

                // Section 6: DIGITAL WELLBEING & PRIVACY
                item {
                    SettingsSectionHeader(title = "WELLBEING & PRIVACY")
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "MAX PINNED FAVORITES: ${settings.maxFavoritesCount}",
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = settings.maxFavoritesCount.toFloat(),
                        onValueChange = { onMaxFavoritesChange(it.toInt()) },
                        valueRange = 1f..15f,
                        steps = 13,
                        colors = SliderDefaults.colors(
                            thumbColor = accent.primaryColor,
                            activeTrackColor = accent.primaryColor,
                            inactiveTrackColor = DividerColor
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hidden Apps Sheet Trigger
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurface, RoundedCornerShape(4.dp))
                            .border(1.dp, DividerColor, RoundedCornerShape(4.dp))
                            .clickable { showHiddenAppsSheet = true }
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "HIDDEN APPS (${hiddenApps.size})",
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "MANAGE >",
                                color = accent.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Section 7: CONFIG BACKUP & RESTORE
                item {
                    SettingsSectionHeader(title = "BACKUP & RESTORE")
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                exportedJsonText = ConfigBackupHelper.exportToJson(
                                    settings = settings,
                                    customLabels = emptyMap(),
                                    hiddenPackages = hiddenApps.map { it.packageName }.toSet()
                                )
                                showExportDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard, contentColor = TextPrimary),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "EXPORT", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showImportDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard, contentColor = TextPrimary),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "IMPORT", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }

        // App Picker Bottom Sheet for Gestures
        if (gestureKeyForAppPick != null) {
            val targetKey = gestureKeyForAppPick!!
            AppPickerBottomSheet(
                allApps = allApps,
                onDismiss = { gestureKeyForAppPick = null },
                onSelectApp = { app ->
                    onUpdateGestureAction(targetKey, "APP:${app.packageName}")
                    gestureKeyForAppPick = null
                }
            )
        }

        // Hidden Apps Modal Bottom Sheet
        if (showHiddenAppsSheet) {
            HiddenAppsSheet(
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
                title = { Text(text = "EXPORT CONFIGURATION", color = TextPrimary, fontFamily = FontFamily.Monospace) },
                text = {
                    Column {
                        Text(text = "JSON configuration string:", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = exportedJsonText, color = OffWhite, fontFamily = FontFamily.Monospace, fontSize = 10.sp, maxLines = 8)
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(exportedJsonText))
                            showExportDialog = false
                        }
                    ) {
                        Text(text = "COPY TO CLIPBOARD", color = accent.primaryColor, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text(text = "CLOSE", color = TextSecondary, fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }

        // Import Dialog
        if (showImportDialog) {
            var importInputText by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                containerColor = DarkSurface,
                title = { Text(text = "IMPORT CONFIGURATION", color = TextPrimary, fontFamily = FontFamily.Monospace) },
                text = {
                    Column {
                        Text(text = "Paste JSON configuration string below:", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = importInputText,
                            onValueChange = { importInputText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(SurfaceCard, RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            textStyle = TextStyle(color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                            cursorBrush = SolidColor(accent.primaryColor)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onImportConfig(importInputText)
                            showImportDialog = false
                        }
                    ) {
                        Text(text = "RESTORE", color = accent.primaryColor, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) {
                        Text(text = "CANCEL", color = TextSecondary, fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GestureRowItem(
    gestureLabel: String,
    currentAction: String,
    allApps: List<AppModel>,
    onActionChange: (String) -> Unit,
    onPickApp: () -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    var expanded by remember { mutableStateOf(false) }

    val presetOptions = listOf(
        "OPEN_DRAWER" to "OPEN ALL APPLICATIONS",
        "OPEN_APP" to "LAUNCH SPECIFIC APP...",
        "EXPAND_NOTIFICATIONS" to "EXPAND NOTIFICATIONS",
        "OPEN_SEARCH" to "OPEN SEARCH",
        "OPEN_SETTINGS" to "OPEN SETTINGS",
        "TOGGLE_TORCH" to "TOGGLE FLASHLIGHT",
        "OPEN_CAMERA" to "OPEN CAMERA",
        "LOCK_SCREEN" to "LOCK SCREEN",
        "NONE" to "DO NOTHING"
    )

    val currentDisplay = when {
        currentAction.startsWith("APP:") -> {
            val pkg = currentAction.removePrefix("APP:")
            val app = allApps.firstOrNull { it.packageName == pkg }
            "APP: ${app?.displayLabel?.uppercase() ?: pkg}"
        }
        else -> presetOptions.firstOrNull { it.first == currentAction }?.second ?: currentAction
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(text = gestureLabel, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .background(DarkSurface, RoundedCornerShape(4.dp))
                    .border(1.dp, DividerColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = currentDisplay, color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(DarkSurface)
            ) {
                presetOptions.forEach { (actionCode, label) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = label,
                                color = if (actionCode == "OPEN_APP") accent.primaryColor else TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = if (actionCode == "OPEN_APP") FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            expanded = false
                            if (actionCode == "OPEN_APP") {
                                onPickApp()
                            } else {
                                onActionChange(actionCode)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppPickerBottomSheet(
    allApps: List<AppModel>,
    onDismiss: () -> Unit,
    onSelectApp: (AppModel) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var search by remember { mutableStateOf("") }

    val filtered = remember(allApps, search) {
        if (search.isBlank()) allApps
        else allApps.filter { it.displayLabel.contains(search, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(text = "SELECT APP FOR GESTURE", color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCard, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                BasicTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                    decorationBox = { inner ->
                        if (search.isEmpty()) Text(text = "Search apps...", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                        inner()
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                items(filtered) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectApp(app) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DotMatrixAppIcon(app = app, sizeDp = 22.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = app.displayLabel.uppercase(), color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    val accent = LocalMatrixAccentColor.current
    Text(
        text = "// $title",
        color = accent.primaryColor,
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp
    )
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
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Black,
                checkedTrackColor = accent.primaryColor,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = DarkSurface
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HiddenAppsSheet(
    hiddenApps: List<AppModel>,
    onDismiss: () -> Unit,
    onUnhideApp: (String) -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(text = "HIDDEN APPLICATIONS", color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))

            if (hiddenApps.isEmpty()) {
                Text(text = "NO HIDDEN APPS", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(hiddenApps) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = app.displayLabel.uppercase(), color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                            TextButton(onClick = { onUnhideApp(app.packageName) }) {
                                Text(text = "UNHIDE", color = accent.primaryColor, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
