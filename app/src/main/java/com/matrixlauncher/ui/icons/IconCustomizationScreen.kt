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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import com.matrixlauncher.ui.theme.OffWhite
import com.matrixlauncher.ui.theme.SurfaceCard
import com.matrixlauncher.ui.theme.TextMuted
import com.matrixlauncher.ui.theme.TextPrimary
import com.matrixlauncher.ui.theme.TextSecondary
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "ICON CUSTOMIZATION STUDIO",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .background(SurfaceCard, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(accent.primaryColor),
                        decorationBox = { inner ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search app to customize icon...",
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            }
                            inner()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Apps Grid/List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    AppIconRowItem(
                        app = app,
                        iconStyle = iconStyle,
                        dotShape = dotShape,
                        onClick = { selectedAppForEdit = app }
                    )
                    HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                }
            }
        }

        // Edit Modal Dialog for Selected App
        if (selectedAppForEdit != null) {
            EditAppIconDialog(
                app = selectedAppForEdit!!,
                iconStyle = iconStyle,
                dotShape = dotShape,
                onDismiss = { selectedAppForEdit = null },
                onSaveGlyph = { glyph, colorHex ->
                    onUpdateAppIcon(selectedAppForEdit!!.packageName, null, colorHex, glyph, null)
                    selectedAppForEdit = null
                },
                onUploadImage = { uri ->
                    onUploadImageForApp(selectedAppForEdit!!.packageName, uri)
                    selectedAppForEdit = null
                },
                onReset = {
                    onResetAppIcon(selectedAppForEdit!!.packageName)
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            DotMatrixAppIcon(
                app = app,
                iconStyle = iconStyle,
                dotShape = dotShape,
                sizeDp = 28.dp
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = app.displayLabel.uppercase(),
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (app.customIconUri != null) "CUSTOM UPLOADED PNG"
                           else if (app.customGlyphName != null) "GLYPH: ${app.customGlyphName}"
                           else "DEFAULT ICON",
                    color = if (app.customIconUri != null || app.customGlyphName != null) accent.primaryColor else TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }

        Text(
            text = "EDIT >",
            color = accent.primaryColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EditAppIconDialog(
    app: AppModel,
    iconStyle: IconStyle,
    dotShape: DotShape,
    onDismiss: () -> Unit,
    onSaveGlyph: (glyph: String, colorHex: String?) -> Unit,
    onUploadImage: (Uri) -> Unit,
    onReset: () -> Unit
) {
    val accent = LocalMatrixAccentColor.current
    var selectedGlyph by remember { mutableStateOf(app.customGlyphName ?: "CAMERA") }
    var selectedColor by remember { mutableStateOf(app.customIconColorHex ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onUploadImage(uri)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Column {
                Text(
                    text = "EDIT ICON // ${app.displayLabel.uppercase()}",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pick stock glyph, LED color, or upload image",
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Upload Custom PNG/JPEG Button
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard, contentColor = TextPrimary),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        tint = accent.primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "UPLOAD PNG/JPEG IMAGE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "PRESET DOT-MATRIX GLYPHS",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(DotMatrixStockIcons.ALL_GLYPHS.keys.toList()) { glyphKey ->
                        val isSelected = selectedGlyph.equals(glyphKey, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) accent.primaryColor.copy(alpha = 0.25f) else SurfaceCard,
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) accent.primaryColor else DividerColor,
                                    RoundedCornerShape(4.dp)
                                )
                                .clickable { selectedGlyph = glyphKey }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = glyphKey,
                                color = if (isSelected) accent.primaryColor else TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "LED GLOW COLOR",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(AccentColor.entries) { colorItem ->
                        val isSelected = selectedColor == colorItem.primaryColor.toArgbHex()
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(colorItem.primaryColor, CircleShape)
                                .border(
                                    2.dp,
                                    if (isSelected) White else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { selectedColor = colorItem.primaryColor.toArgbHex() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Reset to Default Button
                TextButton(
                    onClick = onReset,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "RESET TO DEFAULT", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSaveGlyph(selectedGlyph, selectedColor.ifBlank { null }) }) {
                Text(
                    text = "SAVE GLYPH",
                    color = accent.primaryColor,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CANCEL", color = TextSecondary, fontFamily = FontFamily.Monospace)
            }
        }
    )
}

private fun Color.toArgbHex(): String {
    val a = (alpha * 255).toInt()
    val r = (red * 255).toInt()
    val g = (green * 255).toInt()
    val b = (blue * 255).toInt()
    return String.format("#%02X%02X%02X%02X", a, r, g, b)
}
