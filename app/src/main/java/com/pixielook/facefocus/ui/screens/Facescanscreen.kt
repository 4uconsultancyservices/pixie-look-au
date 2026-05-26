package com.pixielook.facefocus.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixielook.facefocus.ui.theme.PixieLookTheme
import kotlin.math.min

// ─── Colors ───────────────────────────────────────────────────────────────────
private val ScreenBg    = Color(0xFF080808)
private val OuterCardBg = Color(0xFF0D0D0D)

// Outer card rainbow sweep border — same as HairStylingTimeScreen
private val OuterBorderBrush = Brush.sweepGradient(
    listOf(
        Color(0xFF8B20FF),   // purple  (left)
        Color(0xFFFF20C0),   // hot pink (top-left)
        Color(0xFFFF4488),   // pink     (top)
        Color(0xFF00AAFF),   // sky blue (top-right)
        Color(0xFF00D8FF),   // cyan     (right)
        Color(0xFFFF3366),   // pink-red (bottom-right) — matches screenshot bottom
        Color(0xFFFF20C0),   // hot pink (bottom)
        Color(0xFF8B20FF),   // back to purple
    )
)

// Rainbow ring colors — top: orange/red, sweeping through yellow, green, cyan, blue, purple, pink
// Matches screenshot: warm colors on top-right, cool colors on bottom-left
private val RingColors = listOf(
    Color(0xFFFF4400),   // red-orange    (0°)
    Color(0xFFFF8800),   // amber         (45°)
    Color(0xFFFFDD00),   // yellow        (90°)
    Color(0xFF44FF88),   // mint green    (120°)
    Color(0xFF00DDFF),   // cyan          (165°)
    Color(0xFF4488FF),   // blue          (210°)
    Color(0xFF8833FF),   // purple        (255°)
    Color(0xFFFF22CC),   // hot pink      (300°)
    Color(0xFFFF4455),   // red-pink      (330°)
    Color(0xFFFF4400),   // back to start (360°)
)

// Get Started button — orange left → coral/pink right
private val BtnGradient = Brush.horizontalGradient(
    listOf(Color(0xFFFF9900), Color(0xFFFF3366))
)

// Progress pills
private val PillActive   = Brush.horizontalGradient(listOf(Color(0xFFFFAA00), Color(0xFFFFCC44)))
private val PillInactive = Color.White.copy(alpha = 0.22f)

@Composable
fun FaceScanScreen(
    onGetStarted : () -> Unit = {}
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val isTablet    = screenWidth > 900.dp
    val density     = LocalDensity.current

    // ── Rotating ring animation ───────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "faceScan")

    val ringRotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation  = tween(3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRotation"
    )

    // Ring glow pulse — outer blur glow breathes
    val ringGlowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.55f,
        targetValue   = 1.00f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringGlow"
    )

    // Squircle icon scale — gentle pulse
    val iconScale by infiniteTransition.animateFloat(
        initialValue  = 0.96f,
        targetValue   = 1.02f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )

    val ringSize  : Dp = if (isTablet) 240.dp else 200.dp
    val ringStroke: Dp = if (isTablet) 22.dp  else 18.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .padding(
                horizontal = if (isTablet) 52.dp else 20.dp,
                vertical   = if (isTablet) 36.dp else 20.dp
            )
    ) {

        // ── OUTER CARD ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(28.dp))
                .background(OuterCardBg)
                .border(
                    width = 2.dp,
                    brush = OuterBorderBrush,
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(
                    horizontal = if (isTablet) 40.dp else 24.dp,
                    vertical   = if (isTablet) 28.dp else 20.dp
                )
        ) {

            Column(
                modifier            = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // ── PROGRESS PILLS ────────────────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(72.dp).height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(PillActive)
                    )
                    Spacer(Modifier.width(6.dp))
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(PillInactive)
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                }

                Spacer(Modifier.height(if (isTablet) 8.dp else 4.dp))

                // ── RAINBOW RING + SQUIRCLE ICON ──────────────────────────────
                Box(
                    modifier         = Modifier.size(ringSize + ringStroke * 2 + 20.dp),
                    contentAlignment = Alignment.Center
                ) {

                    // Glow blur ring behind the main ring
                    Box(
                        modifier = Modifier
                            .size(ringSize + 24.dp)
                            .rotate(ringRotation)
                            .blur(if (isTablet) 28.dp else 22.dp)
                            .graphicsLayer { alpha = ringGlowAlpha }
                            .drawBehind {
                                drawRainbowArc(
                                    strokeWidth = (ringStroke + 10.dp).toPx(),
                                    colors      = RingColors,
                                    gapDegrees  = 0f
                                )
                            }
                    )

                    // Main crisp rainbow ring — rotates continuously
                    Box(
                        modifier = Modifier
                            .size(ringSize)
                            .rotate(ringRotation)
                            .drawBehind {
                                drawRainbowArc(
                                    strokeWidth = ringStroke.toPx(),
                                    colors      = RingColors,
                                    gapDegrees  = 0f
                                )
                            }
                    )

                    // Squircle face-outline icon — centred inside ring
                    Box(
                        modifier         = Modifier
                            .size(ringSize * 0.56f)
                            .graphicsLayer {
                                scaleX = iconScale
                                scaleY = iconScale
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Draw squircle outline — white stroke, rounded-square shape
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawBehind {
                                    drawSquircleOutline(
                                        color       = Color.White,
                                        strokeWidth = 6.dp.toPx(),
                                        cornerRadius = size.minDimension * 0.30f
                                    )
                                }
                        )
                    }
                }

                Spacer(Modifier.height(if (isTablet) 8.dp else 4.dp))

                // ── TEXT BLOCK ────────────────────────────────────────────────
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text       = "Face Scan",
                        color      = Color.White,
                        fontSize   = if (isTablet) 36.sp else 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text       = "Our Face Scan feature is a simple, quick, and non-invasive process that " +
                                "uses your device's camera to capture a comprehensive map of your face, " +
                                "allowing our AR technology to accurately overlay hairstyles and beauty products.",
                        color      = Color.White.copy(alpha = 0.62f),
                        fontSize   = if (isTablet) 16.sp else 14.sp,
                        lineHeight = if (isTablet) 26.sp else 22.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier.fillMaxWidth(if (isTablet) 0.72f else 1f)
                    )
                }

                Spacer(Modifier.height(if (isTablet) 16.dp else 8.dp))

                // ── GET STARTED BUTTON ────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (isTablet) 0.50f else 0.82f)
                        .height(if (isTablet) 66.dp else 58.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BtnGradient)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = onGetStarted
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "Get Started",
                        color      = Color.White,
                        fontSize   = if (isTablet) 22.sp else 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(if (isTablet) 4.dp else 2.dp))
            }
        }
    }
}

// ─── Draw a full 360° rainbow arc (sweep gradient via segments) ───────────────
private fun DrawScope.drawRainbowArc(
    strokeWidth : Float,
    colors      : List<Color>,
    gapDegrees  : Float = 0f
) {
    val diameter = min(size.width, size.height)
    val radius   = diameter / 2f
    val topLeft  = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
    val arcSize  = Size(diameter, diameter)

    val segCount   = colors.size - 1
    val totalArc   = 360f - gapDegrees
    val segSweep   = totalArc / segCount
    val startAngle = -90f  // start at top

    for (i in 0 until segCount) {
        val segStart = startAngle + i * segSweep
        // Slightly overlap segments to avoid hairline gaps
        val segEnd   = segSweep + 1f

        val brush = Brush.sweepGradient(
            colors   = listOf(colors[i], colors[i + 1]),
            center   = Offset(size.width / 2f, size.height / 2f)
        )

        drawArc(
            brush      = Brush.linearGradient(
                colors      = listOf(colors[i], colors[i + 1]),
                start       = Offset(size.width / 2f + radius * kotlin.math.cos(Math.toRadians((segStart).toDouble())).toFloat(),
                    size.height / 2f + radius * kotlin.math.sin(Math.toRadians((segStart).toDouble())).toFloat()),
                end         = Offset(size.width / 2f + radius * kotlin.math.cos(Math.toRadians((segStart + segSweep).toDouble())).toFloat(),
                    size.height / 2f + radius * kotlin.math.sin(Math.toRadians((segStart + segSweep).toDouble())).toFloat())
            ),
            startAngle = segStart,
            sweepAngle = segEnd,
            useCenter  = false,
            topLeft    = topLeft,
            size       = arcSize,
            style      = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
    }
}

// ─── Draw squircle (rounded-square) outline ───────────────────────────────────
private fun DrawScope.drawSquircleOutline(
    color        : Color,
    strokeWidth  : Float,
    cornerRadius : Float
) {
    val inset = strokeWidth / 2f
    drawRoundRect(
        color        = color,
        topLeft      = Offset(inset, inset),
        size         = Size(size.width - strokeWidth, size.height - strokeWidth),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
        style        = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(
    showBackground = true,
    widthDp        = 1280,
    heightDp       = 800,
    name           = "Face Scan Screen – Tablet Landscape"
)
@Composable
fun FaceScanScreenPreview() {
    PixieLookTheme {
        FaceScanScreen(onGetStarted = {})
    }
}