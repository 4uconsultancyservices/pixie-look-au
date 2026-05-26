package com.pixielook.facefocus.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────────────
//  StyleCard
//
//  Layout (top → bottom inside a clipped rounded frame):
//
//  ┌──────────────────────────────┐  ← outer glowing colored border (always on)
//  │                              │
//  │        face photo            │  ← fills all space above the label box
//  │        (Crop)                │
//  │                              │
//  ├──────────────────────────────┤  ← 1 dp divider in glow color
//  │         Feminine             │  ← dark label box, full card width
//  └──────────────────────────────┘
//
//  Glow: always visible at resting intensity, brightens on selection.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun StyleCard(
    label      : String,
    @DrawableRes imageRes: Int,
    glowColor  : Color,
    isSelected : Boolean,
    onClick    : () -> Unit,
    modifier   : Modifier = Modifier,
    cardRadius : Dp = 20.dp,
    labelBoxH  : Dp = 52.dp,
) {
    val shape = RoundedCornerShape(cardRadius)

    // ── Animated values ───────────────────────────────────────────────────
    // Glow spreads wider & brighter on selection, but always visible at rest
    val glowSpread by animateDpAsState(
        targetValue   = if (isSelected) 32.dp else 12.dp,
        animationSpec = tween(350),
        label         = "glowSpread"
    )
    val glowAlpha by animateFloatAsState(
        targetValue   = if (isSelected) 0.85f else 0.40f,
        animationSpec = tween(350),
        label         = "glowAlpha"
    )
    // Border stroke: always on, bolder when selected
    val borderWidth by animateDpAsState(
        targetValue   = if (isSelected) 2.5.dp else 1.5.dp,
        animationSpec = tween(300),
        label         = "borderWidth"
    )
    val borderAlpha by animateFloatAsState(
        targetValue   = if (isSelected) 1f else 0.55f,
        animationSpec = tween(300),
        label         = "borderAlpha"
    )
    // Subtle pop-in scale on selection
    val cardScale by animateFloatAsState(
        targetValue   = if (isSelected) 1.025f else 1f,
        animationSpec = tween(250),
        label         = "cardScale"
    )

    // ── Root: draws the outer neon glow behind the clipped card ──────────
    Column(
        modifier = modifier
            .fillMaxHeight()
            .scale(cardScale)
            // 1. Paint the neon halo behind everything (not clipped)
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        asFrameworkPaint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.TRANSPARENT
                            setShadowLayer(
                                glowSpread.toPx(),
                                0f, 0f,
                                glowColor.copy(alpha = glowAlpha).toArgb()
                            )
                        }
                    }
                    canvas.drawRoundRect(
                        left    = 0f,
                        top     = 0f,
                        right   = size.width,
                        bottom  = size.height,
                        radiusX = cardRadius.toPx(),
                        radiusY = cardRadius.toPx(),
                        paint   = paint
                    )
                }
            }
            // 2. Clip everything inside to the rounded shape
            .clip(shape)
            // 3. Colored border stroke (on top of the clip)
            .border(
                width = borderWidth,
                color = glowColor.copy(alpha = borderAlpha),
                shape = shape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
    ) {

        // ── Face photo — fills all height above the label box ─────────────
        Image(
            painter            = painterResource(id = imageRes),
            contentDescription = label,
            modifier           = Modifier
                .fillMaxWidth()
                .weight(1f),               // takes all remaining space
            contentScale       = ContentScale.Crop
        )

        // ── Label box — full card width, dark bg, top divider in glow color ─
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(labelBoxH)
                // Very dark semi-transparent background
                .background(Color(0xFF0A0A14))
                // Top divider line in the card's glow colour
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        val linePaint = Paint().apply {
                            asFrameworkPaint().apply {
                                isAntiAlias = true
                                color = glowColor.copy(alpha = glowAlpha).toArgb()
                                setShadowLayer(
                                    8.dp.toPx(),
                                    0f, 0f,
                                    glowColor.copy(alpha = glowAlpha * 0.8f).toArgb()
                                )
                                style = android.graphics.Paint.Style.STROKE
                                strokeWidth = borderWidth.toPx()
                            }
                        }
                        canvas.drawLine(
                            p1    = androidx.compose.ui.geometry.Offset(0f, 0f),
                            p2    = androidx.compose.ui.geometry.Offset(size.width, 0f),
                            paint = linePaint
                        )
                    }
                }
        ) {
            Text(
                text          = label,
                color         = Color.White,
                fontSize      = 17.sp,
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                fontFamily    = FontFamily.SansSerif
            )
        }
    }
}