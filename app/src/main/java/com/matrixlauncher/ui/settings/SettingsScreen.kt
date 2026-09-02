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
import androidx.compose.material.icons.filled.BatterySaver
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
import com.matrixlauncher.ui.theme.SurfaceCard
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
    onCustomUserNameChange: (String) -> Unit = {},
    onNameStyleChange: (Int) -> Unit = {},
    onCrossStyleChange: (Int) -> Unit = {},
    onClockStyleChange: (Int) -> Unit = {},
    onCrossSizeScaleChange: (Float) -> Unit = {},
    onToggleBatterySaver: (Boolean) -> Unit = {},
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
    var showImportDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }
    var gestureKeyForAppPick by remember { mutableStateOf<String?>(null) }
    var nameInputState by remember(settings.customUserName) { mutableStateOf(settings.customUserName) }

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
                    .padding(horizontal = 8.dp, vertical = 8.dp),
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
                    text = "SETTINGS // MATRIX OS",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 0: Default Home App Action Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceCard, RoundedCornerShape(6.dp))
                            .border(1.dp, if (isDefaultLauncher) accent.primaryColor else AccentColor.CRIMSON.primaryColor, RoundedCornerShape(6.dp))
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

                // Section 1: HERO BANNER, CUSTOM NAME & WIDGET STYLES
                item {
                    SettingsSectionHeader(title = "HERO BANNER & WIDGET STYLES")
                    Spacer(modifier = Modifier.height(8.dp))

                    // Custom Name Input
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceCard, RoundedCornerShape(6.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "CUSTOM USER NAME (DOUBLE-LINED)",
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Displays large in 2-dot thick bold double-lined letters in the Hero Date+Name+Time widget",
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BasicTextField(
                                value = nameInputState,
                                onValueChange = { nameInputState = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .background(DarkSurface, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                textStyle = TextStyle(
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                cursorBrush = SolidColor(accent.primaryColor),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onCustomUserNameChange(nameInputState.trim()) },
                                colors = ButtonDefaults.buttonColors(containerColor = accent.primaryColor, contentColor = Black),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "SAVE",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Jesus Cross Style Selector (3 styles)
                    SettingsStyleSelector(
                        title = "JESUS CROSS DESIGN",
                        description = "Pick your preferred dot-matrix cross art",
                        options = listOf("DOUBLE-BORDER OUTLINE", "TRIPLE GOLGOTHA", "RADIANT CELTIC"),
                        selectedIndex = settings.crossStyleIndex % 3,
                        onSelect = onCrossStyleChange
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Cross Size Adjuster Slider
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceCard, RoundedCornerShape(6.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "CROSS SIZE SCALE",
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(settings.crossSizeScale * 100).toInt()}%",
                                color = accent.primaryColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Adjust cross size from compact to grand imposing size",
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                        Slider(
                            value = settings.crossSizeScale,
                            onValueChange = onCrossSizeScaleChange,
                            valueRange = 0.8f..2.2f,
                            steps = 14,
                            colors = SliderDefaults.colors(
                                thumbColor = accent.primaryColor,
                                activeTrackColor = accent.primaryColor,
                                inactiveTrackColor = DarkSurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Standalone Name Design (3 styles)
                    SettingsStyleSelector(
                        title = "STANDALONE NAME DESIGN",
                        description = "Style for standalone big name widget",
                        options = listOf("DOUBLE-LINED BOLD", "FRAMED BADGE", "CYBER FLANKED"),
                        selectedIndex = settings.nameStyleIndex % 3,
                        onSelect = onNameStyleChange
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Standalone Clock Design (3 styles)
                    SettingsStyleSelector(
                        title = "CLOCK WIDGET DESIGN",
                        description = "Layout for standalone clock widget",
                        options = listOf("CLASSIC DIGITAL", "STACKED 2-LINE", "COMPACT RETRO"),
                        selectedIndex = settings.clockStyleIndex % 3,
                        onSelect = onClockStyleChange
                    )
                }

                // Section 2: POWER & BATTERY SAVER MODE
                item {
                    SettingsSectionHeader(title = "POWER & BATTERY OPTIMIZATION")
                    Spacer(modifier = Modifier.height(8.dp))

                    SettingsToggleRow(
                        title = "ULTRA BATTERY SAVER MODE",
                        subtitle = "Disables shaders/CRT scanlines, runs on pure pitch-black OLED background to maximize battery life",
                        checked = settings.batterySaverEnabled,
                        onCheckedChange = onToggleBatterySaver
                    )
                }

                // Section 3: ICON STUDIO & ICON STYLES
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

                // Section 4: GESTURES & MULTI-TOUCH CONTROLS
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

                // Section 5: THEME & COLOR PALETTE
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
                                            .size(14.dp)
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

                // Section 6: DOT GRID MATRIX CALIBRATION
                item {
                    SettingsSectionHeader(title = "BACKGROUND DOT GRID MATRIX")
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "DOT DENSITY",
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
                        items(DotDensity.entries) { density ->
                            val isSelected = settings.dotDensity == density
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) accent.primaryColor.copy(alpha = 0.25f) else DarkSurface,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .border(1.dp, if (isSelected) accent.primaryColor else DividerColor, RoundedCornerShape(4.dp))
                                    .clickable { onDotDensityChange(density) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = density.label,
                                    color = if (isSelected) accent.primaryColor else TextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "DOT SHAPE",
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
                        items(DotShape.entries) { shape ->
                            val isSelected = settings.dotShape == shape
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) accent.primaryColor.copy(alpha = 0.25f) else DarkSurface,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .border(1.dp, if (isSelected) accent.primaryColor else DividerColor, RoundedCornerShape(4.dp))
                                    .clickable { onDotShapeChange(shape) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = shape.label,
                                    color = if (isSelected) accent.primaryColor else TextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingsToggleRow(
                        title = "RETRO CRT SCANLINES",
                        subtitle = "Subtle cathode ray tube scanlines on OLED grid",
                        checked = settings.enableCrtScanlines,
                        onCheckedChange = onToggleCrtScanlines
                    )

                    SettingsToggleRow(
                        title = "HARDWARE AGSL SHADER",
                        subtitle = "Hardware-accelerated ambient pixel rendering",
                        checked = settings.agslShaderEnabled,
                        onCheckedChange = onToggleShader
                    )
                }

                // Section 7: GENERAL PREFERENCES
                item {
                    SettingsSectionHeader(title = "GENERAL PREFERENCES")
                    Spacer(modifier = Modifier.height(8.dp))

                    SettingsToggleRow(
                        title = "24-HOUR TIME FORMAT",
                        subtitle = if (settings.is24HourClock) "24-hour military clock" else "12-hour AM/PM clock",
                        checked = settings.is24HourClock,
                        onCheckedChange = onToggle24Hour
                    )

                    SettingsToggleRow(
                        title = "BATTERY DOT BAR",
                        subtitle = "Segmented horizontal LED battery gauge",
                        checked = settings.showBatteryDotBar,
                        onCheckedChange = onToggleBatteryBar
                    )

                    SettingsToggleRow(
                        title = "AUTO-FOCUS SEARCH",
                        subtitle = "Open keyboard automatically when entering drawer",
                        checked = settings.autoFocusSearch,
                        onCheckedChange = onToggleAutoFocusSearch
                    )
                }

                // Section 8: BACKUP & RESTORE
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
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard, contentColor = TextPrimary),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, tint = accent.primaryColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "EXPORT CONFIG", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showImportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard, contentColor = TextPrimary),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, tint = accent.primaryColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "IMPORT CONFIG", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // App Picker Dialog for Gestures
        if (gestureKeyForAppPick != null) {
            AppPickerBottomSheet(
                allApps = allApps,
                onDismiss = { gestureKeyForAppPick = null },
                onSelectApp = { app ->
                    onUpdateGestureAction(gestureKeyForAppPick!!, "APP:${app.packageName}")
                    gestureKeyForAppPick = null
                }
            )
        }

        // Export Dialog
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                containerColor = DarkSurface,
                title = { Text(text = "BACKUP CONFIGURATION", color = TextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(text = "Copy this JSON config to restore your layout & settings anytime:", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = exportedJsonText,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().background(SurfaceCard, RoundedCornerShape(4.dp)).padding(10.dp),
                            textStyle = TextStyle(color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        )
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
                        Text(text = "CLOSE", color = TextSecondary, fontFamily = FontFamily.Monospace)
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
                title = { Text(text = "RESTORE CONFIGURATION", color = TextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(text = "Paste your exported JSON config below:", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = importInput,
                            onValueChange = { importInput = it },
                            modifier = Modifier.fillMaxWidth().background(SurfaceCard, RoundedCornerShape(4.dp)).padding(10.dp),
                            textStyle = TextStyle(color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                            cursorBrush = SolidColor(accent.primaryColor)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (importInput.isNotBlank()) {
                            onImportConfig(importInput.trim())
                            showImportDialog = false
                        }
                    }) {
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

@Composable
fun SettingsStyleSelector(
    title: String,
    description: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val accent = LocalMatrixAccentColor.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard, RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            color = TextSecondary,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options.indices.toList()) { index ->
                val isSelected = selectedIndex == index
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
                        .clickable { onSelect(index) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = options[index],
                        color = if (isSelected) accent.primaryColor else TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = "// $title",
        color = LocalMatrixAccentColor.current.primaryColor,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(top = 8.dp)
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
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Black,
                checkedTrackColor = accent.primaryColor,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = DarkSurface
            )
        )
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

    val builtInActions = listOf(
        "OPEN_DRAWER" to "OPEN ALL APPLICATIONS",
        "EXPAND_NOTIFICATIONS" to "EXPAND NOTIFICATIONS",
        "OPEN_SEARCH" to "OPEN QUICK SEARCH",
        "OPEN_SETTINGS" to "OPEN LAUNCHER SETTINGS",
        "TOGGLE_TORCH" to "TOGGLE FLASHLIGHT",
        "TOGGLE_BATTERY_SAVER" to "TOGGLE BATTERY SAVER",
        "OPEN_CAMERA" to "OPEN CAMERA",
        "LOCK_SCREEN" to "LOCK SCREEN",
        "NONE" to "DO NOTHING"
    )

    val currentDisplayLabel = when {
        currentAction.startsWith("APP:") -> {
            val pkg = currentAction.removePrefix("APP:")
            val app = allApps.firstOrNull { it.packageName == pkg }
            "OPEN APP: ${app?.displayLabel?.uppercase() ?: pkg}"
        }
        else -> builtInActions.firstOrNull { it.first == currentAction }?.second ?: currentAction
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = gestureLabel,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = currentDisplayLabel,
                color = accent.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
        }

        Row {
            // Pick App Button
            Box(
                modifier = Modifier
                    .background(DarkSurface, RoundedCornerShape(4.dp))
                    .border(1.dp, DividerColor, RoundedCornerShape(4.dp))
                    .clickable(onClick = onPickApp)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "SELECT APP",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Built-in Actions Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                Box(
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .background(SurfaceCard, RoundedCornerShape(4.dp))
                        .border(1.dp, DividerColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "ACTIONS ▼",
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    builtInActions.forEach { (actionKey, label) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = label,
                                    color = if (currentAction == actionKey) accent.primaryColor else TextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            },
                            onClick = {
                                onActionChange(actionKey)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPickerBottomSheet(
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "ASSIGN APP TO GESTURE",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            BasicTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCard, RoundedCornerShape(4.dp))
                    .padding(10.dp),
                textStyle = TextStyle(color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                cursorBrush = SolidColor(LocalMatrixAccentColor.current.primaryColor),
                decorationBox = { inner ->
                    if (search.isEmpty()) Text("Search app...", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    inner()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(350.dp)
            ) {
                items(filtered, key = { it.packageName }) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectApp(app) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DotMatrixAppIcon(app = app, sizeDp = 24.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = app.displayLabel.uppercase(),
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
