package com.matrixlauncher.ui.graphics

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.matrixlauncher.domain.model.AppModel
import com.matrixlauncher.domain.model.DotShape
import com.matrixlauncher.domain.model.IconStyle
import com.matrixlauncher.ui.theme.DotInactiveColor
import com.matrixlauncher.ui.theme.LocalMatrixAccentColor
import java.io.InputStream

object DotMatrixStockIcons {

    // 12x12 Dot Matrix Glyphs for Stock Apps
    // 0 = inactive dot, 1 = active illuminated LED dot

    val CAMERA = arrayOf(
        "001111000000",
        "011111100110",
        "111111111111",
        "110011001111",
        "101111101111",
        "101100101111",
        "101100101111",
        "101111101111",
        "110011001111",
        "111111111111",
        "111111111111",
        "000000000000"
    )

    val CALCULATOR = arrayOf(
        "111111111111",
        "100000000001",
        "101111111101",
        "100000000001",
        "111111111111",
        "101100110001",
        "101100110001",
        "100000000001",
        "101100111101",
        "101100111101",
        "100000000001",
        "111111111111"
    )

    val CLOCK = arrayOf(
        "000011110000",
        "001111111100",
        "011110011110",
        "111110011111",
        "111110011111",
        "111110011111",
        "111110000011",
        "111111111111",
        "011111111110",
        "001111111100",
        "000011110000",
        "000000000000"
    )

    val PHONE = arrayOf(
        "000011110000",
        "001111111100",
        "011100001110",
        "111000000111",
        "111000000111",
        "111100001111",
        "011100001110",
        "001100001100",
        "001100001100",
        "001110011100",
        "000111111000",
        "000011110000"
    )

    val MESSAGES = arrayOf(
        "011111111110",
        "111111111111",
        "110000000011",
        "110111111011",
        "110000000011",
        "110111100011",
        "110000000011",
        "111111111111",
        "011111111110",
        "001100000000",
        "000110000000",
        "000010000000"
    )

    val SETTINGS = arrayOf(
        "000011110000",
        "010111111010",
        "001100001100",
        "011011110110",
        "110111111011",
        "110110011011",
        "110110011011",
        "110111111011",
        "011011110110",
        "001100001100",
        "010111111010",
        "000011110000"
    )

    val GALLERY = arrayOf(
        "111111111111",
        "100000000001",
        "101100000001",
        "101100000001",
        "100000011001",
        "100000111101",
        "100110111101",
        "101111111101",
        "111111111111",
        "111111111111",
        "100000000001",
        "111111111111"
    )

    val BROWSER = arrayOf(
        "000011110000",
        "001100111100",
        "010011000010",
        "110111100011",
        "100111100001",
        "101111110001",
        "100011111101",
        "100001111001",
        "110001111011",
        "010000110010",
        "001111001100",
        "000011110000"
    )

    val MUSIC = arrayOf(
        "000001111110",
        "000001111110",
        "000001000010",
        "000001000010",
        "000001000010",
        "000001000010",
        "001101001101",
        "011111011111",
        "011111011111",
        "001110001110",
        "000000000000",
        "000000000000"
    )

    val CALENDAR = arrayOf(
        "011000000110",
        "111111111111",
        "100000000001",
        "111111111111",
        "101001001001",
        "101001001001",
        "100000000001",
        "101001001001",
        "101001001001",
        "100000000001",
        "101001001001",
        "111111111111"
    )

    val CONTACTS = arrayOf(
        "000011110000",
        "001111111100",
        "001111111100",
        "000011110000",
        "000000000000",
        "000111111000",
        "001111111100",
        "011111111110",
        "111111111111",
        "111111111111",
        "111111111111",
        "000000000000"
    )

    val FILES = arrayOf(
        "001111000000",
        "011111100000",
        "111111111110",
        "100000000001",
        "101111111101",
        "101111111101",
        "101111111101",
        "101111111101",
        "101111111101",
        "100000000001",
        "111111111111",
        "000000000000"
    )

    val MAPS = arrayOf(
        "000011110000",
        "001111111100",
        "011111111110",
        "111100001111",
        "111100001111",
        "011111111110",
        "001111111100",
        "000111111000",
        "000011110000",
        "000001100000",
        "000001100000",
        "000000000000"
    )

    val MAIL = arrayOf(
        "111111111111",
        "110000000011",
        "101000000101",
        "100100001001",
        "100010010001",
        "100001100001",
        "100010010001",
        "100100001001",
        "101000000101",
        "110000000011",
        "111111111111",
        "000000000000"
    )

    val NOTES = arrayOf(
        "011111111100",
        "010000000100",
        "010111110100",
        "010000000100",
        "010111110100",
        "010000000100",
        "010111000100",
        "010000000100",
        "011111111100",
        "000000000011",
        "000000000111",
        "000000001110"
    )

    val RECORDER = arrayOf(
        "000011110000",
        "000111111000",
        "000111111000",
        "000111111000",
        "000111111000",
        "110111111011",
        "110011110011",
        "011000000110",
        "001111111100",
        "000001100000",
        "000001100000",
        "000111111000"
    )

    val STORE = arrayOf(
        "000011110000",
        "000110011000",
        "001100001100",
        "111111111111",
        "100000000001",
        "100110000001",
        "100111100001",
        "100111111001",
        "100111100001",
        "100110000001",
        "100000000001",
        "111111111111"
    )

    val ALL_GLYPHS = mapOf(
        "CAMERA" to CAMERA,
        "CALCULATOR" to CALCULATOR,
        "CLOCK" to CLOCK,
        "PHONE" to PHONE,
        "MESSAGES" to MESSAGES,
        "SETTINGS" to SETTINGS,
        "GALLERY" to GALLERY,
        "BROWSER" to BROWSER,
        "MUSIC" to MUSIC,
        "CALENDAR" to CALENDAR,
        "CONTACTS" to CONTACTS,
        "FILES" to FILES,
        "MAPS" to MAPS,
        "MAIL" to MAIL,
        "NOTES" to NOTES,
        "RECORDER" to RECORDER,
        "STORE" to STORE
    )

    fun detectStockGlyph(app: AppModel): Array<String>? {
        if (!app.customGlyphName.isNullOrBlank()) {
            return ALL_GLYPHS[app.customGlyphName.uppercase()]
        }

        val pkg = app.packageName.lowercase()
        val label = app.label.lowercase()

        return when {
            pkg.contains("camera") || label.contains("camera") -> CAMERA
            pkg.contains("calculator") || label.contains("calculator") || label.contains("calc") -> CALCULATOR
            pkg.contains("clock") || pkg.contains("alarm") || label.contains("clock") || label.contains("alarm") -> CLOCK
            pkg.contains("dialer") || pkg.contains("phone") || label.contains("phone") || label.contains("call") -> PHONE
            pkg.contains("messaging") || pkg.contains("mms") || pkg.contains("sms") || label.contains("message") -> MESSAGES
            pkg.contains("settings") || label.contains("settings") || label.contains("config") -> SETTINGS
            pkg.contains("gallery") || pkg.contains("photos") || label.contains("gallery") || label.contains("photos") -> GALLERY
            pkg.contains("chrome") || pkg.contains("browser") || pkg.contains("firefox") || label.contains("browser") || label.contains("internet") -> BROWSER
            pkg.contains("music") || pkg.contains("audio") || pkg.contains("spotify") || label.contains("music") || label.contains("song") -> MUSIC
            pkg.contains("calendar") || label.contains("calendar") || label.contains("schedule") -> CALENDAR
            pkg.contains("contacts") || pkg.contains("people") || label.contains("contact") -> CONTACTS
            pkg.contains("files") || pkg.contains("filemanager") || pkg.contains("documents") || label.contains("file") -> FILES
            pkg.contains("maps") || pkg.contains("navigation") || label.contains("map") -> MAPS
            pkg.contains("gm") || pkg.contains("mail") || pkg.contains("email") || label.contains("mail") -> MAIL
            pkg.contains("notes") || pkg.contains("keep") || label.contains("note") -> NOTES
            pkg.contains("recorder") || pkg.contains("soundrecorder") || label.contains("recorder") -> RECORDER
            pkg.contains("vending") || pkg.contains("store") || label.contains("store") || label.contains("play store") -> STORE
            else -> null
        }
    }
}

@Composable
fun DotMatrixAppIcon(
    app: AppModel,
    modifier: Modifier = Modifier,
    iconStyle: IconStyle = IconStyle.DOT_MATRIX_STOCK,
    dotShape: DotShape = DotShape.CIRCLE,
    sizeDp: Dp = 26.dp,
    activeColor: Color = LocalMatrixAccentColor.current.primaryColor,
    inactiveColor: Color = DotInactiveColor.copy(alpha = 0.25f)
) {
    val context = LocalContext.current

    // Parse custom color override if specified
    val resolvedColor = remember(app.customIconColorHex, activeColor) {
        if (!app.customIconColorHex.isNullOrBlank()) {
            try {
                Color(android.graphics.Color.parseColor(app.customIconColorHex))
            } catch (e: Exception) {
                activeColor
            }
        } else {
            activeColor
        }
    }

    // Custom uploaded image bitmap
    val customBitmap = remember(app.customIconUri) {
        if (!app.customIconUri.isNullOrBlank()) {
            try {
                val uri = Uri.parse(app.customIconUri)
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                inputStream?.use {
                    BitmapFactory.decodeStream(it)
                }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    if (customBitmap != null) {
        // Draw Custom Uploaded PNG/JPEG Image in canvas frame
        val imageBitmap = remember(customBitmap) { customBitmap.asImageBitmap() }
        Canvas(modifier = modifier.size(sizeDp)) {
            drawImage(image = imageBitmap, dstSize = androidx.compose.ui.unit.IntSize(size.width.toInt(), size.height.toInt()))
        }
        return
    }

    val stockGlyph = remember(app, iconStyle) {
        if (iconStyle == IconStyle.DOT_MATRIX_STOCK) {
            DotMatrixStockIcons.detectStockGlyph(app)
        } else {
            null
        }
    }

    if (stockGlyph != null) {
        // Render 12x12 Dot Matrix Stock Icon
        Canvas(modifier = modifier.size(sizeDp)) {
            val gridRows = stockGlyph.size
            val gridCols = stockGlyph[0].length

            val dotW = size.width / gridCols
            val dotH = size.height / gridRows
            val radius = (dotW.coerceAtMost(dotH) * 0.38f)

            for (row in 0 until gridRows) {
                val line = stockGlyph[row]
                for (col in 0 until gridCols) {
                    val isActive = col < line.length && line[col] == '1'
                    val cx = col * dotW + dotW / 2f
                    val cy = row * dotH + dotH / 2f
                    val color = if (isActive) resolvedColor else inactiveColor

                    when (dotShape) {
                        DotShape.CIRCLE -> {
                            drawCircle(color = color, radius = radius, center = Offset(cx, cy))
                        }
                        DotShape.SQUARE -> {
                            val s = radius * 1.8f
                            drawRect(color = color, topLeft = Offset(cx - s / 2, cy - s / 2), size = Size(s, s))
                        }
                        DotShape.ROUNDED_CRT -> {
                            val s = radius * 1.8f
                            drawRoundRect(
                                color = color,
                                topLeft = Offset(cx - s / 2, cy - s / 2),
                                size = Size(s, s),
                                cornerRadius = CornerRadius(radius * 0.5f, radius * 0.5f)
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Render 2-Character Dot-Matrix Monogram Badge
        val monogram = remember(app.displayLabel) {
            val words = app.displayLabel.trim().split(" ").filter { it.isNotBlank() }
            if (words.size >= 2) {
                "${words[0].first().uppercaseChar()}${words[1].first().uppercaseChar()}"
            } else if (app.displayLabel.length >= 2) {
                app.displayLabel.take(2).uppercase()
            } else {
                app.displayLabel.take(1).uppercase()
            }
        }

        Canvas(modifier = modifier.size(sizeDp)) {
            // Draw square frame with active monogram dots
            val frameSize = size.width
            val glyph1 = DotMatrixFont.GLYPHS[monogram.firstOrNull()] ?: DotMatrixFont.GLYPHS['#']!!
            val glyph2 = if (monogram.length > 1) DotMatrixFont.GLYPHS[monogram[1]] else null

            val totalCols = if (glyph2 != null) 12 else 7
            val totalRows = 7

            val dotSpacing = (frameSize / (totalCols + 1))
            val radius = (dotSpacing * 0.35f)

            // Draw Character 1
            for (r in 0 until 7) {
                val rowBits1 = glyph1[r]
                for (c in 0 until 5) {
                    val isActive = ((rowBits1 shr (4 - c)) and 1) == 1
                    val cx = (c + 1) * dotSpacing
                    val cy = (r + 1) * dotSpacing + (frameSize - 7 * dotSpacing) / 2f
                    val color = if (isActive) resolvedColor else inactiveColor
                    drawCircle(color = color, radius = radius, center = Offset(cx, cy))
                }
            }

            // Draw Character 2 if present
            if (glyph2 != null) {
                for (r in 0 until 7) {
                    val rowBits2 = glyph2[r]
                    for (c in 0 until 5) {
                        val isActive = ((rowBits2 shr (4 - c)) and 1) == 1
                        val cx = (c + 7) * dotSpacing
                        val cy = (r + 1) * dotSpacing + (frameSize - 7 * dotSpacing) / 2f
                        val color = if (isActive) resolvedColor else inactiveColor
                        drawCircle(color = color, radius = radius, center = Offset(cx, cy))
                    }
                }
            }
        }
    }
}
