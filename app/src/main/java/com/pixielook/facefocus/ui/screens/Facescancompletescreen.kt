package com.pixielook.facefocus.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixielook.facefocus.ui.theme.PixieLookTheme

// ─── Colors ───────────────────────────────────────────────────────────────────
private val ScreenBg    = Color(0xFF080808)
private val CardBg      = Color(0xFF0C0C0C)

// The outer card border in the screenshot is a sweep gradient:
// Top-left:  bright orange  #FF8800
// Top-right: vivid purple   #CC44FF
// Right:     blue-cyan      #2288FF
// Bottom:    cyan-blue      #0099FF
// Bottom-left: pink-magenta #FF22AA
// Back to orange
// This creates the exact warm-top / cool-sides / pink-bottom look visible in the image.
private val CardBorderColors = listOf(
    Color(0xFFFF8800),   // orange         — top-left start
    Color(0xFFFFAA00),   // amber          — top centre
    Color(0xFFDD44FF),   // purple         — top-right
    Color(0xFF6622FF),   // blue-purple    — right top
    Color(0xFF2266FF),   // blue           — right mid
    Color(0xFF0099FF),   // cyan-blue      — right bottom / bottom-right
    Color(0xFF0088DD),   // cyan           — bottom mid
    Color(0xFF2255FF),   // blue           — bottom left area
    Color(0xFFAA22FF),   // purple         — bottom-left
    Color(0xFFFF22AA),   // hot pink       — left bottom
    Color(0xFFFF5500),   // orange-red     — left mid
    Color(0xFFFF8800),   // back to orange — left top / close loop
)

// Finish button
private val FinishGradient = Brush.horizontalGradient(
    listOf(
        Color(0xFFFF6600),   // deep orange left
        Color(0xFFFFAA00),   // bright amber centre
        Color(0xFFFF7700),   // orange right
    )
)
private val FinishGlowColor  = Color(0xFFFF8800)
private val FinishTextColor  = Color.White

// Bottom nav — amber/orange tint matching screenshot
private val NavIconColor = Color(0xFFCC5500)

@Composable
fun FaceScanCompleteScreen(
    onFinish : () -> Unit = {},
    onBack   : () -> Unit = {},
    onHome   : () -> Unit = {}
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val isTablet    = screenWidth > 900.dp

    // Border glow pulse
    val infiniteTransition = rememberInfiniteTransition(label = "borderGlow")
    val borderGlowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.70f,
        targetValue   = 1.00f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderGlow"
    )

    // Button glow pulse
    val btnGlow by infiniteTransition.animateFloat(
        initialValue  = 0.55f,
        targetValue   = 1.00f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btnGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {

        // ── MAIN CONTENT — card centred, bottom nav outside ───────────────────
        Column(
            modifier            = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(Modifier.weight(0.12f))

            // ── OUTER CARD ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (isTablet) 0.76f else 0.92f)
                    .fillMaxHeight(if (isTablet) 0.72f else 0.78f)
            ) {
                // Sweep-gradient border glow (blurred clone behind card)
                NeonCardGlow(
                    modifier   = Modifier.matchParentSize(),
                    colors     = CardBorderColors,
                    glowAlpha  = borderGlowAlpha,
                    cornerDp   = 28.dp,
                    blurRadius = 18.dp,
                    isTablet   = isTablet
                )

                // Card body
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(28.dp))
                        .background(CardBg)
                        // Sweep gradient border — drawn as a Canvas stroke
                        .drawBehind {
                            val strokePx = 2.dp.toPx()
                            val inset    = strokePx / 2f
                            val path = Path().apply {
                                addRoundRect(
                                    RoundRect(
                                        left         = inset,
                                        top          = inset,
                                        right        = size.width  - inset,
                                        bottom       = size.height - inset,
                                        cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx())
                                    )
                                )
                            }
                            drawPath(
                                path  = path,
                                brush = Brush.sweepGradient(
                                    colors = CardBorderColors.map { it.copy(alpha = borderGlowAlpha) },
                                    center = Offset(size.width / 2f, size.height / 2f)
                                ),
                                style = Stroke(width = strokePx)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        modifier            = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isTablet) 64.dp else 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // ── TITLE ─────────────────────────────────────────────
                        Text(
                            text       = "Face Scan Complete",
                            color      = Color.White,
                            fontSize   = if (isTablet) 34.sp else 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign  = TextAlign.Center
                        )

                        Spacer(Modifier.height(20.dp))

                        // ── BODY ──────────────────────────────────────────────
                        Text(
                            text       = "You can now enjoy Pixie Look's Augmented Reality features! Browse hairstyles or products now to test out looks and styles in real time.",
                            color      = Color.White.copy(alpha = 0.62f),
                            fontSize   = if (isTablet) 16.sp else 14.sp,
                            lineHeight = if (isTablet) 26.sp else 22.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign  = TextAlign.Center
                        )

                        Spacer(Modifier.height(if (isTablet) 52.dp else 40.dp))

                        // ── FINISH BUTTON ─────────────────────────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (isTablet) 0.64f else 1f)
                                .height(if (isTablet) 62.dp else 54.dp)
                        ) {
                            // Outer orange glow
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .blur(16.dp)
                                    .graphicsLayer { alpha = btnGlow * 0.60f }
                                    .background(
                                        FinishGlowColor,
                                        RoundedCornerShape(12.dp)
                                    )
                            )
                            // Button body
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(FinishGradient)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null,
                                        onClick           = onFinish
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                // Inner top highlight — bright centre shimmer
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.50f)
                                        .fillMaxHeight(0.55f)
                                        .align(Alignment.TopCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color.White.copy(alpha = 0.28f),
                                                    Color.Transparent
                                                )
                                            ),
                                            RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                        )
                                )
                                Text(
                                    text       = "Finish",
                                    color      = FinishTextColor,
                                    fontSize   = if (isTablet) 20.sp else 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign  = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(0.06f))

            // ── BOTTOM NAV ────────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isTablet) 20.dp else 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector        = Icons.Rounded.Menu,
                    contentDescription = "Menu",
                    tint               = NavIconColor,
                    modifier           = Modifier
                        .size(28.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = {}
                        )
                )
                Spacer(Modifier.width(72.dp))
                Icon(
                    imageVector        = Icons.Rounded.Home,
                    contentDescription = "Home",
                    tint               = NavIconColor,
                    modifier           = Modifier
                        .size(30.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = onHome
                        )
                )
                Spacer(Modifier.width(72.dp))
                Icon(
                    imageVector        = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Back",
                    tint               = NavIconColor,
                    modifier           = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = onBack
                        )
                )
            }
        }
    }
}

// ─── Neon Card Glow — blurred duplicate of the border sweep for the outer halo ─
@Composable
private fun NeonCardGlow(
    modifier   : Modifier,
    colors     : List<Color>,
    glowAlpha  : Float,
    cornerDp   : Dp,
    blurRadius : Dp,
    isTablet   : Boolean
) {
    Box(
        modifier = modifier
            .blur(blurRadius)
            .graphicsLayer { alpha = glowAlpha * 0.55f }
            .drawBehind {
                val strokePx = 8.dp.toPx()
                val inset    = strokePx / 2f
                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left         = inset,
                            top          = inset,
                            right        = size.width  - inset,
                            bottom       = size.height - inset,
                            cornerRadius = CornerRadius(cornerDp.toPx(), cornerDp.toPx())
                        )
                    )
                }
                drawPath(
                    path  = path,
                    brush = Brush.sweepGradient(
                        colors = colors,
                        center = Offset(size.width / 2f, size.height / 2f)
                    ),
                    style = Stroke(width = strokePx)
                )
            }
    )
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(
    showBackground = true,
    widthDp        = 1456,
    heightDp       = 816,
    name           = "Face Scan Complete – Tablet Landscape"
)
@Composable
fun FaceScanCompleteScreenPreview() {
    PixieLookTheme {
        FaceScanCompleteScreen(
            onFinish = {},
            onBack   = {},
            onHome   = {}
        )
    }
}