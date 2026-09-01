package com.matrixlauncher.ui.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.AppShortcutModel
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.WebSearchProvider
import com.matrixlauncher.ui.common.SystemSettingShortcut
import com.matrixlauncher.ui.common.SystemSettingsShortcuts
import com.matrixlauncher.ui.theme.DarkSurface
import com.matrixlauncher.ui.theme.DividerColor
import com.matrixlauncher.ui.theme.DotInactiveColor
import com.matrixlauncher.ui.theme.DotMatrixTheme
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.OffWhite
import com.matrixlauncher.ui.theme.SurfaceCard
import com.matrixlauncher.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppDrawerScreen(
    modifier: Modifier = Modifier,
    apps: List<AppModel>,
    matchedShortcuts: List<SystemSettingShortcut> = emptyList(),
    calculatedResult: String? = null,
    searchQuery: String,
    autoFocusSearch: Boolean,
    scrollerAlignment: ScrollerAlignment = ScrollerAlignment.RIGHT,
    selectedAppForMenu: AppModel?,
    selectedAppShortcuts: List<AppShortcutModel> = emptyList(),
    selectedAppForRename: AppModel?,
    onSearchQueryChange: (String) -> Unit,
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit,
    onShortcutClick: (AppShortcutModel) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRenameApp: (packageName: String, newLabel: String?) -> Unit,
    onHideApp: (packageName: String, isHidden: Boolean) -> Unit,
    onAppInfo: (AppModel) -> Unit,
    onUninstall: (AppModel) -> Unit,
    onWebSearch: (query: String, provider: WebSearchProvider) -> Unit,
    onCloseContextMenu: () -> Unit,
    onCloseRenameDialog: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val accent = LocalMatrixAccentColor.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val isSearching = searchQuery.isNotBlank()

    val letterIndexMap = remember(apps, isSearching) {
        if (isSearching) emptyMap()
        else {
            val map = mutableMapOf<Char, Int>()
            apps.forEachIndexed { index, app ->
                val header = app.sectionHeader
                if (!map.containsKey(header)) {
                    map[header] = index
                }
            }
            map
        }
    }

    val availableLetters = remember(letterIndexMap) { letterIndexMap.keys }

    LaunchedEffect(autoFocusSearch) {
        if (autoFocusSearch) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar
            DrawerSearchBar(
                query = searchQuery,
                focusRequester = focusRequester,
                onQueryChange = onSearchQueryChange,
                onBackClick = onBackClick,
                onClearClick = { onSearchQueryChange("") }
            )

            // In-Line Calculator Result Card
            if (calculatedResult != null) {
                CalculatorResultCard(
                    expression = searchQuery,
                    result = calculatedResult,
                    accentColor = accent,
                    onCopyResult = {
                        clipboardManager.setText(AnnotatedString(calculatedResult))
                    }
                )
            }

            // Web Search Provider Chips (When searching)
            if (isSearching) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(WebSearchProvider.entries) { provider ->
                        WebSearchChip(
                            provider = provider,
                            onClick = { onWebSearch(searchQuery, provider) }
                        )
                    }
                }
            }

            // Direct System Settings Shortcuts
            if (matchedShortcuts.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Text(
                        text = "// SYSTEM SHORTCUTS",
                        color = accent.primaryColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    matchedShortcuts.forEach { shortcut ->
                        SystemShortcutItem(
                            shortcut = shortcut,
                            onClick = {
                                SystemSettingsShortcuts.launch(context, shortcut)
                            }
                        )
                    }
                }
            }

            // Content: Empty Search or App List
            if (apps.isEmpty() && matchedShortcuts.isEmpty() && calculatedResult == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = if (isSearching) "NO MATCHING APPLICATIONS" else "NO APPS INSTALLED",
                        color = DotInactiveColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        letterSpacing = 1.5.sp
                    )
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (!isSearching && apps.size > 10 && scrollerAlignment == ScrollerAlignment.LEFT) {
                        FastScroller(
                            availableLetters = availableLetters,
                            onLetterSelected = { letter ->
                                val targetIndex = letterIndexMap[letter]
                                if (targetIndex != null) {
                                    coroutineScope.launch { listState.scrollToItem(targetIndex) }
                                }
                            },
                            onDragStateChanged = { dragging ->
                                if (dragging) keyboardController?.hide()
                            },
                            modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 16.dp)
                        )
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp, start = 16.dp, end = 8.dp)
                    ) {
                        itemsIndexed(
                            items = apps,
                            key = { _, app -> app.uniqueKey }
                        ) { index, app ->
                            val showHeader = !isSearching && (index == 0 || app.sectionHeader != apps[index - 1].sectionHeader)

                            if (showHeader) {
                                DrawerSectionHeader(
                                    header = app.sectionHeader,
                                    accentColor = accent
                                )
                            }

                            DrawerAppItem(
                                app = app,
                                accentColor = accent,
                                onClick = { onAppClick(app) },
                                onLongClick = { onAppLongClick(app) }
                            )
                        }
                    }

                    if (!isSearching && apps.size > 10 && scrollerAlignment == ScrollerAlignment.RIGHT) {
                        FastScroller(
                            availableLetters = availableLetters,
                            onLetterSelected = { letter ->
                                val targetIndex = letterIndexMap[letter]
                                if (targetIndex != null) {
                                    coroutineScope.launch { listState.scrollToItem(targetIndex) }
                                }
                            },
                            onDragStateChanged = { dragging ->
                                if (dragging) keyboardController?.hide()
                            },
                            modifier = Modifier.padding(end = 4.dp, top = 8.dp, bottom = 16.dp)
                        )
                    }
                }
            }
        }

        // Context Menu Sheet with App Shortcuts
        if (selectedAppForMenu != null) {
            AppContextMenuSheet(
                app = selectedAppForMenu,
                shortcuts = selectedAppShortcuts,
                onDismiss = onCloseContextMenu,
                onShortcutClick = onShortcutClick,
                onToggleFavorite = { onToggleFavorite(selectedAppForMenu.packageName) },
                onRename = { /* Handled in parent */ },
                onHide = { onHideApp(selectedAppForMenu.packageName, true) },
                onAppInfo = { onAppInfo(selectedAppForMenu) },
                onUninstall = { onUninstall(selectedAppForMenu) }
            )
        }

        // Rename Dialog
        if (selectedAppForRename != null) {
            AppRenameDialog(
                app = selectedAppForRename,
                onDismiss = onCloseRenameDialog,
                onConfirm = { newName -> onRenameApp(selectedAppForRename.packageName, newName) }
            )
        }
    }
}

@Composable
private fun CalculatorResultCard(
    expression: String,
    result: String,
    accentColor: AccentColor,
    onCopyResult: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(SurfaceCard, RoundedCornerShape(4.dp))
            .border(1.dp, accentColor.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .clickable(onClick = onCopyResult)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "CALC // $expression",
                color = DotInactiveColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "= $result",
                color = accentColor.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy",
            tint = OffWhite,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun WebSearchChip(
    provider: WebSearchProvider,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(DarkSurface, RoundedCornerShape(4.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = DotInactiveColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = provider.label.uppercase(),
                color = White,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SystemShortcutItem(
    shortcut: SystemSettingShortcut,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            tint = DotInactiveColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = shortcut.title,
                color = White,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = shortcut.description,
                color = DotInactiveColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun DrawerSearchBar(
    query: String,
    focusRequester: FocusRequester,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit
) {
    val accent = LocalMatrixAccentColor.current

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

        Box(
            modifier = Modifier
                .weight(1f)
                .background(SurfaceCard, shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    color = White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                ),
                cursorBrush = SolidColor(accent.primaryColor),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "SEARCH OR COMPUTE_",
                            color = DotInactiveColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    innerTextField()
                }
            )
        }

        AnimatedVisibility(
            visible = query.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IconButton(onClick = onClearClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = OffWhite
                )
            }
        }
    }
}

@Composable
private fun DrawerSectionHeader(
    header: Char,
    accentColor: AccentColor
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 6.dp, start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "// $header",
            color = accentColor.primaryColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun DrawerAppItem(
    app: AppModel,
    accentColor: AccentColor,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(6.dp)) {
            drawCircle(
                color = if (app.isFavorite) accentColor.primaryColor else DotInactiveColor,
                radius = 2.dp.toPx()
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = app.displayLabel.uppercase(),
            color = White,
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 1.sp,
            modifier = Modifier.weight(1f)
        )

        if (app.isWorkProfile) {
            Text(
                text = "[WORK]",
                color = accentColor.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        if (app.isFavorite) {
            Text(
                text = "*",
                color = accentColor.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContextMenuSheet(
    app: AppModel,
    shortcuts: List<AppShortcutModel> = emptyList(),
    onDismiss: () -> Unit,
    onShortcutClick: (AppShortcutModel) -> Unit,
    onToggleFavorite: () -> Unit,
    onRename: () -> Unit,
    onHide: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        contentColor = White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(DotInactiveColor, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = app.displayLabel.uppercase(),
                color = White,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = app.packageName,
                color = DotInactiveColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )

            // Dynamic App Shortcuts Section
            if (shortcuts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "// APP SHORTCUTS",
                    color = accent.primaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                shortcuts.forEach { shortcut ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onShortcutClick(shortcut)
                                onDismiss()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = accent.primaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = shortcut.displayLabel.uppercase(),
                            color = White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = DividerColor)
            Spacer(modifier = Modifier.height(8.dp))

            ContextMenuAction(
                icon = if (app.isFavorite) Icons.Default.StarBorder else Icons.Default.Star,
                text = if (app.isFavorite) "UNPIN FROM HOME" else "PIN TO HOME",
                color = if (app.isFavorite) accent.primaryColor else White,
                onClick = {
                    onToggleFavorite()
                    onDismiss()
                }
            )

            ContextMenuAction(
                icon = Icons.Default.Edit,
                text = "RENAME APP",
                color = White,
                onClick = { onRename() }
            )

            ContextMenuAction(
                icon = Icons.Default.VisibilityOff,
                text = "HIDE FROM DRAWER",
                color = White,
                onClick = {
                    onHide()
                    onDismiss()
                }
            )

            ContextMenuAction(
                icon = Icons.Default.Info,
                text = "APP INFO",
                color = White,
                onClick = {
                    onAppInfo()
                    onDismiss()
                }
            )

            ContextMenuAction(
                icon = Icons.Default.Delete,
                text = "UNINSTALL",
                color = AccentColor.CRIMSON.primaryColor,
                onClick = {
                    onUninstall()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun ContextMenuAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = color,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun AppRenameDialog(
    app: AppModel,
    onDismiss: () -> Unit,
    onConfirm: (String?) -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    var text by remember { mutableStateOf(app.customLabel ?: app.label) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "RENAME APPLICATION",
                color = White,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "ORIGINAL: ${app.label}",
                    color = DotInactiveColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceCard, shape = RoundedCornerShape(4.dp))
                        .padding(12.dp),
                    textStyle = TextStyle(
                        color = White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp
                    ),
                    cursorBrush = SolidColor(accent.primaryColor),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(text.trim().takeIf { it.isNotBlank() && it != app.label })
                    onDismiss()
                }
            ) {
                Text(
                    text = "SAVE",
                    color = accent.primaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Row {
                if (app.customLabel != null) {
                    TextButton(
                        onClick = {
                            onConfirm(null)
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = "RESET",
                            color = OffWhite,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "CANCEL",
                        color = DotInactiveColor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    )
}
