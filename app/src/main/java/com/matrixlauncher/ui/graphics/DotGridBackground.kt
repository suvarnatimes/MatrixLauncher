package com.matrixlauncher.ui.graphics

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.matrixlauncher.domain.model.DotDensity
import com.matrixlauncher.domain.model.DotShape
import org.intellij.lang.annotations.Language

/**
 * AGSL Shader for Android 13+ (API 33+) featuring procedural dot grid, breathing wave, and optional CRT scanlines.
 */
@Language("AGSL")
private const val DOT_MATRIX_AGSL = """
    uniform float2 uResolution;
    uniform float uTime;
    uniform float uSpacing;
    uniform float uRadius;
    uniform float uScanlines;
    uniform float4 uDotColor;
    uniform float4 uBgColor;

    vec4 main(vec2 fragCoord) {
        vec2 grid = mod(fragCoord, uSpacing) - (uSpacing * 0.5);
        float dist = length(grid);

        // Subtle pulsing wave across grid
        float wave = sin((fragCoord.y * 0.005) - (uTime * 0.8)) * 0.15;
        float effectiveRadius = uRadius * (1.0 + wave);

        // Smooth antialiased dot edge
        float alpha = 1.0 - smoothstep(effectiveRadius - 0.75, effectiveRadius + 0.75, dist);
        vec4 color = mix(uBgColor, uDotColor, alpha * uDotColor.a);

        // Optional CRT horizontal scanlines
        if (uScanlines > 0.5) {
            float scanline = sin(fragCoord.y * 1.5) * 0.08;
            color.rgb -= vec3(scanline);
        }

        return color;
    }
"""

@Composable
fun DotGridBackground(
    modifier: Modifier = Modifier,
    dotDensity: DotDensity = DotDensity.STANDARD,
    dotShape: DotShape = DotShape.CIRCLE,
    dotColor: Color = Color(0xFF181818),
    backgroundColor: Color = Color(0xFF000000),
    enableShader: Boolean = true,
    enableCrtScanlines: Boolean = false
) {
    val density = LocalDensity.current
    val spacingPx = with(density) { dotDensity.spacingDp.dp.toPx() }
    val radiusPx = with(density) { dotDensity.dotRadiusDp.dp.toPx() }

    val infiniteTransition = rememberInfiniteTransition(label = "MatrixBackgroundTransition")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "MatrixTime"
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && enableShader) {
        AgslDotGridBackground(
            modifier = modifier,
            spacingPx = spacingPx,
            radiusPx = radiusPx,
            dotColor = dotColor,
            backgroundColor = backgroundColor,
            time = time,
            scanlines = if (enableCrtScanlines) 1.0f else 0.0f
        )
    } else {
        CanvasDotGridBackground(
            modifier = modifier,
            spacingPx = spacingPx,
            radiusPx = radiusPx,
            dotShape = dotShape,
            dotColor = dotColor,
            backgroundColor = backgroundColor
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun AgslDotGridBackground(
    modifier: Modifier,
    spacingPx: Float,
    radiusPx: Float,
    dotColor: Color,
    backgroundColor: Color,
    time: Float,
    scanlines: Float
) {
    val shader = remember { RuntimeShader(DOT_MATRIX_AGSL) }

    Canvas(modifier = modifier.fillMaxSize()) {
        shader.setFloatUniform("uResolution", size.width, size.height)
        shader.setFloatUniform("uTime", time)
        shader.setFloatUniform("uSpacing", spacingPx)
        shader.setFloatUniform("uRadius", radiusPx)
        shader.setFloatUniform("uScanlines", scanlines)
        shader.setColorUniform(
            "uDotColor",
            android.graphics.Color.argb(
                (dotColor.alpha * 255).toInt(),
                (dotColor.red * 255).toInt(),
                (dotColor.green * 255).toInt(),
                (dotColor.blue * 255).toInt()
            )
        )
        shader.setColorUniform(
            "uBgColor",
            android.graphics.Color.argb(
                (backgroundColor.alpha * 255).toInt(),
                (backgroundColor.red * 255).toInt(),
                (backgroundColor.green * 255).toInt(),
                (backgroundColor.blue * 255).toInt()
            )
        )

        drawRect(brush = ShaderBrush(shader))
    }
}

@Composable
private fun CanvasDotGridBackground(
    modifier: Modifier,
    spacingPx: Float,
    radiusPx: Float,
    dotShape: DotShape,
    dotColor: Color,
    backgroundColor: Color
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(color = backgroundColor)
        drawGridDots(spacingPx, radiusPx, dotShape, dotColor)
    }
}

private fun DrawScope.drawGridDots(
    spacingPx: Float,
    radiusPx: Float,
    dotShape: DotShape,
    dotColor: Color
) {
    if (spacingPx <= 0f) return
    val cols = (size.width / spacingPx).toInt() + 1
    val rows = (size.height / spacingPx).toInt() + 1

    val startX = (size.width - ((cols - 1) * spacingPx)) / 2f
    val startY = (size.height - ((rows - 1) * spacingPx)) / 2f

    val squareSide = radiusPx * 2f

    for (c in 0 until cols) {
        val cx = startX + c * spacingPx
        for (r in 0 until rows) {
            val cy = startY + r * spacingPx
            when (dotShape) {
                DotShape.CIRCLE -> {
                    drawCircle(
                        color = dotColor,
                        radius = radiusPx,
                        center = Offset(cx, cy)
                    )
                }
                DotShape.SQUARE -> {
                    drawRect(
                        color = dotColor,
                        topLeft = Offset(cx - radiusPx, cy - radiusPx),
                        size = Size(squareSide, squareSide)
                    )
                }
                DotShape.ROUNDED_CRT -> {
                    drawRoundRect(
                        color = dotColor,
                        topLeft = Offset(cx - radiusPx, cy - radiusPx),
                        size = Size(squareSide, squareSide),
                        cornerRadius = CornerRadius(radiusPx * 0.4f, radiusPx * 0.4f)
                    )
                }
            }
        }
    }
}
