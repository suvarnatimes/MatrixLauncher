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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.AppShortcutModel
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.IconStyle
import com.matrixlauncher.domain.model.ScrollerAlignment
import com.matrixlauncher.domain.model.WebSearchProvider
import com.matrixlauncher.ui.common.SystemSettingShortcut
import com.matrixlauncher.ui.common.SystemSettingsShortcuts
import com.matrixlauncher.ui.graphics.DotMatrixAppIcon
import com.matrixlauncher.ui.theme.DarkSurface
import com.matrixlauncher.ui.theme.DividerColor
import com.matrixlauncher.ui.theme.DotInactiveColor
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.OffWhite
import com.matrixlauncher.ui.theme.SurfaceCard
import com.matrixlauncher.ui.theme.TextMuted
import com.matrixlauncher.ui.theme.TextPrimary
import com.matrixlauncher.ui.theme.TextSecondary
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
    iconStyle: IconStyle = IconStyle.DOT_MATRIX_STOCK,
    dotShape: DotShape = DotShape.CIRCLE,
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
    onOpenIconStudio: () -> Unit = {},
    onAppInfo: (AppModel) -> Unit,
    onUninstall: (AppModel) -> Unit,
    onWebSearch: (query: String, provider: WebSearchProvider) -> Unit,
    onCloseContextMenu: () -> Unit,
    onCloseRenameDialog: () -> Unit,
    onBackClick: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val accent = LocalMatrixAccentColor.current

    LaunchedEffect(Unit) {
        if (autoFocusSearch) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    val availableLetters = remember(apps) {
        apps.mapNotNull { it.displayLabel.firstOrNull()?.uppercaseChar() }
            .distinct()
            .sorted()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Input Header
            DrawerSearchBar(
                query = searchQuery,
                focusRequester = focusRequester,
                onQueryChange = onSearchQueryChange,
                onBackClick = onBackClick,
                onClearClick = { onSearchQueryChange("") }
            )

            // Dynamic Search Result Extensions (Calculator / Web / Shortcuts)
            if (searchQuery.isNotBlank()) {
                DrawerSearchExtensions(
                    searchQuery = searchQuery,
                    calculatedResult = calculatedResult,
                    matchedShortcuts = matchedShortcuts,
                    onWebSearch = onWebSearch
                )
            }

            // Main App List with Alphabet Fast Scroller
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (scrollerAlignment == ScrollerAlignment.LEFT && searchQuery.isBlank() && availableLetters.isNotEmpty()) {
                    AlphabetFastScroller(
                        letters = availableLetters,
                        onLetterSelected = { letter ->
                            val index = apps.indexOfFirst {
                                it.displayLabel.startsWith(letter, ignoreCase = true)
                            }
                            if (index != -1) {
                                coroutineScope.launch {
                                    listState.scrollToItem(index)
                                }
                            }
                        }
                    )
                }

                // App List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (apps.isEmpty() && searchQuery.isNotBlank()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "NO MATCHING APPLICATIONS",
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    } else {
                        itemsIndexed(
                            items = apps,
                            key = { _, app -> app.uniqueKey }
                        ) { index, app ->
                            val showSectionHeader = searchQuery.isBlank() && (
                                index == 0 ||
                                apps[index - 1].displayLabel.first().uppercaseChar() != app.displayLabel.first().uppercaseChar()
                            )

                            if (showSectionHeader) {
                                DrawerSectionHeader(
                                    letter = app.displayLabel.first().uppercaseChar(),
                                    accentColor = accent
                                )
                            }

                            DrawerAppItem(
                                app = app,
                                iconStyle = iconStyle,
                                dotShape = dotShape,
                                accentColor = accent,
                                onClick = { onAppClick(app) },
                                onLongClick = { onAppLongClick(app) }
                            )
                        }
                    }
                }

                if (scrollerAlignment == ScrollerAlignment.RIGHT && searchQuery.isBlank() && availableLetters.isNotEmpty()) {
                    AlphabetFastScroller(
                        letters = availableLetters,
                        onLetterSelected = { letter ->
                            val index = apps.indexOfFirst {
                                it.displayLabel.startsWith(letter, ignoreCase = true)
                            }
                            if (index != -1) {
                                coroutineScope.launch {
                                    listState.scrollToItem(index)
                                }
                            }
                        }
                    )
                }
            }
        }

        // Context Menu Bottom Sheet
        if (selectedAppForMenu != null) {
            AppContextBottomSheet(
                app = selectedAppForMenu,
                shortcuts = selectedAppShortcuts,
                onShortcutClick = onShortcutClick,
                onToggleFavorite = { onToggleFavorite(selectedAppForMenu.packageName) },
                onRename = { onRenameApp(selectedAppForMenu.packageName, null) },
                onHide = { onHideApp(selectedAppForMenu.packageName, true) },
                onOpenIconStudio = onOpenIconStudio,
                onAppInfo = { onAppInfo(selectedAppForMenu) },
                onUninstall = { onUninstall(selectedAppForMenu) },
                onDismiss = onCloseContextMenu
            )
        }

        // Rename Dialog
        if (selectedAppForRename != null) {
            AppRenameDialog(
                app = selectedAppForRename,
                onDismiss = onCloseRenameDialog,
                onConfirm = { newLabel ->
                    onRenameApp(selectedAppForRename.packageName, newLabel)
                }
            )
        }
    }
}

@Composable
private fun DrawerSectionHeader(
    letter: Char,
    accentColor: AccentColor
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "[$letter]",
                color = accentColor.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(
                color = DividerColor,
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrawerAppItem(
    app: AppModel,
    iconStyle: IconStyle,
    dotShape: DotShape,
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
            .padding(vertical = 10.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconStyle != IconStyle.TEXT_ONLY) {
            DotMatrixAppIcon(
                app = app,
                iconStyle = iconStyle,
                dotShape = dotShape,
                sizeDp = 24.dp
            )
            Spacer(modifier = Modifier.width(14.dp))
        }

        Text(
            text = app.displayLabel.uppercase(),
            color = TextPrimary,
            fontFamily = FontFamily.Monospace,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp,
            modifier = Modifier.weight(1f)
        )

        if (app.isFavorite) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Pinned",
                tint = accentColor.primaryColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        if (app.isWorkProfile) {
            Text(
                text = "[W]",
                color = accentColor.primaryColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AlphabetFastScroller(
    letters: List<Char>,
    onLetterSelected: (Char) -> Unit
) {
    val accent = LocalMatrixAccentColor.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 6.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        letters.forEach { letter ->
            Text(
                text = "$letter",
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onLetterSelected(letter) }
                    .padding(vertical = 2.dp, horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun DrawerSearchExtensions(
    searchQuery: String,
    calculatedResult: String?,
    matchedShortcuts: List<SystemSettingShortcut>,
    onWebSearch: (query: String, provider: WebSearchProvider) -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(SurfaceCard, RoundedCornerShape(4.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(4.dp))
            .padding(10.dp)
    ) {
        // Calculator evaluation result
        if (calculatedResult != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        clipboardManager.setText(AnnotatedString(calculatedResult))
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "= ",
                        color = accent.primaryColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = calculatedResult,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Result",
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
            HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 6.dp))
        }

        // Web Search Quick Fallbacks
        Text(
            text = "SEARCH WEB FOR \"${searchQuery.uppercase()}\"",
            color = TextSecondary,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(WebSearchProvider.entries) { provider ->
                WebSearchChip(
                    provider = provider,
                    onClick = { onWebSearch(searchQuery, provider) }
                )
            }
        }

        // System Settings Shortcuts
        if (matchedShortcuts.isNotEmpty()) {
            HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "SYSTEM SETTINGS",
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
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
}

@Composable
private fun WebSearchChip(
    provider: WebSearchProvider,
    onClick: () -> Unit
) {
    val accent = LocalMatrixAccentColor.current
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
                tint = accent.primaryColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = provider.label.uppercase(),
                color = TextPrimary,
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
            tint = TextSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = shortcut.title,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = shortcut.description,
                color = TextSecondary,
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary
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
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                ),
                cursorBrush = SolidColor(accent.primaryColor),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (query.isEmpty()) {
                                Text(
                                    text = "SEARCH / CALCULATE...",
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                            innerTextField()
                        }

                        if (query.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Query",
                                tint = TextSecondary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable(onClick = onClearClick)
                            )
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContextBottomSheet(
    app: AppModel,
    shortcuts: List<AppShortcutModel>,
    onShortcutClick: (AppShortcutModel) -> Unit,
    onToggleFavorite: () -> Unit,
    onRename: () -> Unit,
    onHide: () -> Unit,
    onOpenIconStudio: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
    onDismiss: () -> Unit
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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DotMatrixAppIcon(app = app, sizeDp = 26.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = app.displayLabel.uppercase(),
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = app.packageName,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }

            // App Dynamic Shortcuts
            if (shortcuts.isNotEmpty()) {
                Text(
                    text = "SHORTCUTS",
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                shortcuts.forEach { shortcut ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onShortcutClick(shortcut) }
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
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    }
                }
                HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            }

            // Quick Actions List
            ContextActionRow(
                icon = if (app.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                title = if (app.isFavorite) "UNPIN FROM HOME" else "PIN TO FAVORITES",
                onClick = onToggleFavorite
            )

            ContextActionRow(
                icon = Icons.Default.Brush,
                title = "CUSTOMIZE ICON (STUDIO)",
                onClick = onOpenIconStudio
            )

            ContextActionRow(
                icon = Icons.Default.Edit,
                title = "RENAME APPLICATION",
                onClick = onRename
            )

            ContextActionRow(
                icon = Icons.Default.VisibilityOff,
                title = "HIDE APPLICATION",
                onClick = onHide
            )

            ContextActionRow(
                icon = Icons.Default.Info,
                title = "APPLICATION DETAILS",
                onClick = onAppInfo
            )

            ContextActionRow(
                icon = Icons.Default.Delete,
                title = "UNINSTALL APPLICATION",
                onClick = onUninstall,
                isDestructive = true
            )
        }
    }
}

@Composable
private fun ContextActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val accent = LocalMatrixAccentColor.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) AccentColor.CRIMSON.primaryColor else accent.primaryColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            color = if (isDestructive) AccentColor.CRIMSON.primaryColor else TextPrimary,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
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
    var labelInput by remember { mutableStateOf(app.customLabel ?: app.label) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "RENAME APPLICATION",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Original: ${app.label}",
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = labelInput,
                    onValueChange = { labelInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceCard, RoundedCornerShape(4.dp))
                        .padding(10.dp),
                    textStyle = TextStyle(
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    ),
                    cursorBrush = SolidColor(accent.primaryColor),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalLabel = labelInput.trim()
                    onConfirm(if (finalLabel == app.label || finalLabel.isEmpty()) null else finalLabel)
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
            TextButton(onClick = onDismiss) {
                Text(
                    text = "CANCEL",
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    )
}
