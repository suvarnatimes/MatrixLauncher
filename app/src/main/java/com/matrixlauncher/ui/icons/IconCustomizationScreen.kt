package com.matrixlauncher.ui.icons

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matrixlauncher.domain.model.AccentColor
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.IconStyle
import com.matrixlauncher.ui.graphics.DotMatrixAppIcon
import com.matrixlauncher.ui.graphics.DotMatrixStockIcons
import com.matrixlauncher.ui.theme.DarkSurface
import com.matrixlauncher.ui.theme.DividerColor
import com.matrixlauncher.ui.theme.DotInactiveColor
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.OffWhite
import com.matrixlauncher.ui.theme.SurfaceCard
import com.matrixlauncher.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconCustomizationScreen(
    apps: List<AppModel>,
    iconStyle: IconStyle,
    dotShape: DotShape,
    onUpdateAppIcon: (packageName: String, iconUri: String?, colorHex: String?, glyphName: String?, shape: String?) -> Unit,
    onUploadImageForApp: (packageName: String, uri: Uri) -> Unit,
    onResetAppIcon: (packageName: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalMatrixAccentColor.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedAppForEdit by remember { mutableStateOf<AppModel?>(null) }

    val filteredApps = remember(apps, searchQuery) {
        if (searchQuery.isBlank()) apps
        else apps.filter {
            it.displayLabel.contains(searchQuery, ignoreCase = true) ||
            it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = White
                    )
                }

                Text(
                    text = "ICON CUSTOMIZATION STUDIO",
                    color = White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(SurfaceCard, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = DotInactiveColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            color = White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(accent.primaryColor),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "FILTER APPS TO EDIT ICON...",
                                    color = DotInactiveColor,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // App Icon List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(
                    items = filteredApps,
                    key = { it.uniqueKey }
                ) { app ->
                    AppIconRowItem(
                        app = app,
                        iconStyle = iconStyle,
                        dotShape = dotShape,
                        onClick = { selectedAppForEdit = app }
                    )
                }
            }
        }

        // Icon Editor Dialog / Modal
        if (selectedAppForEdit != null) {
            val app = selectedAppForEdit!!
            IconEditorDialog(
                app = app,
                iconStyle = iconStyle,
                dotShape = dotShape,
                onDismiss = { selectedAppForEdit = null },
                onSave = { glyph, colorHex, shape ->
                    onUpdateAppIcon(app.packageName, app.customIconUri, colorHex, glyph, shape)
                    selectedAppForEdit = null
                },
                onUploadImage = { uri ->
                    onUploadImageForApp(app.packageName, uri)
                    selectedAppForEdit = null
                },
                onReset = {
                    onResetAppIcon(app.packageName)
                    selectedAppForEdit = null
                }
            )
        }
    }
}

@Composable
private fun AppIconRowItem(
    app: AppModel,
    iconStyle: IconStyle,
    dotShape: DotShape,
    onClick: () -> Unit
) {
    val accent = LocalMatrixAccentColor.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DotMatrixAppIcon(
            app = app,
            iconStyle = iconStyle,
            dotShape = dotShape,
            sizeDp = 32.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.displayLabel.uppercase(),
                color = White,
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
            Text(
                text = if (app.customIconUri != null) "CUSTOM PNG ICON" else if (app.customGlyphName != null) "GLYPH: ${app.customGlyphName}" else "STOCK DOT-MATRIX",
                color = if (app.customIconUri != null || app.customGlyphName != null) accent.primaryColor else DotInactiveColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
        }

        Text(
            text = "EDIT",
            color = accent.primaryColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun IconEditorDialog(
    app: AppModel,
    iconStyle: IconStyle,
    dotShape: DotShape,
    onDismiss: () -> Unit,
    onSave: (glyphName: String?, colorHex: String?, shape: String?) -> Unit,
    onUploadImage: (Uri) -> Unit,
    onReset: () -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    var selectedGlyph by remember { mutableStateOf(app.customGlyphName) }
    var selectedColorHex by remember { mutableStateOf(app.customIconColorHex ?: "") }
    var selectedShape by remember { mutableStateOf(app.customIconShape ?: dotShape.name) }

    // Image Picker for PNG / JPEG upload
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onUploadImage(uri)
        }
    }

    val previewApp = remember(app, selectedGlyph, selectedColorHex, selectedShape) {
        app.copy(
            customGlyphName = selectedGlyph,
            customIconColorHex = selectedColorHex.ifBlank { null },
            customIconShape = selectedShape
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "EDIT ICON: ${app.displayLabel.uppercase()}",
                color = White,
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Live Real-Time Dot Matrix Preview
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(SurfaceCard, RoundedCornerShape(8.dp))
                        .border(1.dp, accent.primaryColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    DotMatrixAppIcon(
                        app = previewApp,
                        iconStyle = iconStyle,
                        dotShape = try { DotShape.valueOf(selectedShape) } catch (e: Exception) { dotShape },
                        sizeDp = 44.dp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Upload Image Button
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard, contentColor = White),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        tint = accent.primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "UPLOAD CUSTOM PNG / IMAGE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = DividerColor)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "SELECT DOT-MATRIX GLYPH",
                    color = DotInactiveColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Preset Glyphs Selector
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(DotMatrixStockIcons.ALL_GLYPHS.keys.toList()) { glyphName ->
                        val isSelected = selectedGlyph.equals(glyphName, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) accent.primaryColor.copy(alpha = 0.3f) else SurfaceCard,
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) accent.primaryColor else DividerColor,
                                    RoundedCornerShape(4.dp)
                                )
                                .clickable { selectedGlyph = glyphName }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = glyphName,
                                color = if (isSelected) accent.primaryColor else OffWhite,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Preset Color Selector
                Text(
                    text = "SELECT LED GLOW COLOR",
                    color = DotInactiveColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AccentColor.entries.filter { it != AccentColor.CUSTOM }.forEach { colorPreset ->
                        val hex = when (colorPreset) {
                            AccentColor.CRIMSON -> "#FF2E2E"
                            AccentColor.AMBER -> "#FFB300"
                            AccentColor.EMERALD -> "#00E676"
                            AccentColor.CYAN -> "#00E5FF"
                            AccentColor.PURPLE -> "#D500F9"
                            AccentColor.WHITE -> "#FFFFFF"
                            else -> "#FF2E2E"
                        }
                        val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(colorPreset.primaryColor, CircleShape)
                                .border(if (isSelected) 2.dp else 0.dp, White, CircleShape)
                                .clickable { selectedColorHex = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedGlyph, selectedColorHex.ifBlank { null }, selectedShape) },
                colors = ButtonDefaults.buttonColors(containerColor = accent.primaryColor, contentColor = Color.Black),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "SAVE ICON",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onReset) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = DotInactiveColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "RESET",
                        color = DotInactiveColor,
                        fontFamily = FontFamily.Monospace
                    )
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
