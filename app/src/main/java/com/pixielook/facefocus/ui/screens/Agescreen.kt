package com.pixielook.facefocus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixielook.facefocus.ui.theme.PixieLookTheme

// ─── Screen / Card colors ─────────────────────────────────────────────────────
private val ScreenBg   = Color(0xFF080808)
private val OuterCardBg = Color(0xFF0D0D0D)

// ─── Per-card fills (dark tinted backgrounds) ─────────────────────────────────
private val Fill18Below  = Color(0xFF200C04)   // dark reddish-brown
private val Fill18to25   = Color(0xFF1A1600)   // dark olive/gold
private val Fill26to35   = Color(0xFF051218)   // dark teal-navy
private val Fill36to45   = Color(0xFF210520)   // dark magenta/purple
private val Fill46Above  = Color(0xFF051218)   // dark teal-navy (same as 26-35)

// ─── Per-card border + glow colors ───────────────────────────────────────────
// Each card: border is bottom-heavy — bright at bottom, fades toward top
private val Color18Below  = Color(0xFFFF6020)   // orange-red
private val Color18to25   = Color(0xFFE8B800)   // amber-gold
private val Color26to35   = Color(0xFF00D8FF)   // cyan
private val Color36to45   = Color(0xFFFF20C0)   // hot pink / magenta
private val Color46Above  = Color(0xFF00CCFF)   // cyan (slightly softer)

// ─── Next button: solid blue fill ────────────────────────────────────────────
private val NextBtnFill = Brush.radialGradient(
    listOf(Color(0xFF2A80FF), Color(0xFF0A4FCC))
)
private val NextBtnGlowColor = Color(0xFF1A6FFF)

@Composable
fun AgeScreen(
    onNext: () -> Unit = {}
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val isTablet    = screenWidth > 900.dp

    var selected by remember { mutableStateOf("18 below") }

    // Full screen: near-black bg
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .padding(
                horizontal = if (isTablet) 52.dp else 20.dp,
                vertical   = if (isTablet) 36.dp else 16.dp
            )
    ) {

        // ── OUTER CARD ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .background(OuterCardBg)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(
                    horizontal = if (isTablet) 36.dp else 20.dp,
                    vertical   = if (isTablet) 28.dp else 18.dp
                )
        ) {

            Column(modifier = Modifier.fillMaxSize()) {

                // ── PROGRESS PILLS — centred ──────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Active pill — amber/orange
                    Box(
                        modifier = Modifier
                            .width(72.dp).height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFFAA00), Color(0xFFFFCC44))
                                )
                            )
                    )
                    Spacer(Modifier.width(6.dp))
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .width(14.dp).height(5.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.20f))
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                }

                Spacer(Modifier.height(if (isTablet) 28.dp else 18.dp))

                // ── HEADER ROW: title + subtitle on left, next btn on right ──
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left: title + subtitle
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = "Your Age",
                            color      = Color.White,
                            fontSize   = if (isTablet) 38.sp else 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 44.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text       = "We'd like to know our users better to serve them better.",
                            color      = Color.White.copy(alpha = 0.58f),
                            fontSize   = if (isTablet) 15.sp else 13.sp,
                            lineHeight = if (isTablet) 24.sp else 20.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    // Right: Next button — solid blue, no rainbow
                    Box {
                        // Blue glow
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .blur(22.dp)
                                .graphicsLayer { alpha = 0.80f }
                                .background(NextBtnGlowColor, CircleShape)
                        )
                        // Button
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(NextBtnFill)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication        = null,
                                    onClick           = onNext
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Rounded.KeyboardArrowRight,
                                contentDescription = "Next",
                                tint               = Color.White,
                                modifier           = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(if (isTablet) 36.dp else 24.dp))

                // ── ROW 1: 3 equal cards ──────────────────────────────────────
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .height(if (isTablet) 130.dp else 110.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AgeOptionCard(
                        label       = "18 below",
                        fill        = Fill18Below,
                        glowColor   = Color18Below,
                        textColor   = Color.White,
                        selected    = selected == "18 below",
                        modifier    = Modifier.weight(1f).fillMaxHeight()
                    ) { selected = "18 below" }

                    AgeOptionCard(
                        label       = "18 to 25",
                        fill        = Fill18to25,
                        glowColor   = Color18to25,
                        textColor   = Color(0xFFFFE066),
                        selected    = selected == "18 to 25",
                        modifier    = Modifier.weight(1f).fillMaxHeight()
                    ) { selected = "18 to 25" }

                    AgeOptionCard(
                        label       = "26 to 35",
                        fill        = Fill26to35,
                        glowColor   = Color26to35,
                        textColor   = Color(0xFF88EEFF),
                        selected    = selected == "26 to 35",
                        modifier    = Modifier.weight(1f).fillMaxHeight()
                    ) { selected = "26 to 35" }
                }

                Spacer(Modifier.height(14.dp))

                // ── ROW 2: 2 cards (left ~44%, right ~56%) ────────────────────
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .height(if (isTablet) 130.dp else 110.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AgeOptionCard(
                        label       = "36 to 45",
                        fill        = Fill36to45,
                        glowColor   = Color36to45,
                        textColor   = Color(0xFFFF88E0),
                        selected    = selected == "36 to 45",
                        modifier    = Modifier.weight(0.44f).fillMaxHeight()
                    ) { selected = "36 to 45" }

                    AgeOptionCard(
                        label       = "46 and above",
                        fill        = Fill46Above,
                        glowColor   = Color46Above,
                        textColor   = Color(0xFF88EEFF),
                        selected    = selected == "46 and above",
                        modifier    = Modifier.weight(0.56f).fillMaxHeight()
                    ) { selected = "46 and above" }
                }
            }
        }
    }
}

// ─── Age Option Card ──────────────────────────────────────────────────────────
// Key visual: bottom-heavy neon glow — border brightens at bottom edge
@Composable
fun AgeOptionCard(
    label     : String,
    fill      : Color,
    glowColor : Color,
    textColor : Color,
    selected  : Boolean,
    modifier  : Modifier = Modifier,
    onClick   : () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Bottom-heavy border brush: transparent top → glowColor bottom
    val borderBrush = Brush.verticalGradient(
        listOf(
            glowColor.copy(alpha = if (selected) 0.30f else 0.20f),  // top: faint
            glowColor.copy(alpha = if (selected) 0.55f else 0.38f),  // mid
            glowColor.copy(alpha = if (selected) 1.00f else 0.75f)   // bottom: bright
        )
    )

    // Inner fill: base dark color + subtle vertical gradient (lighter top, darker bottom)
    val fillBrush = Brush.verticalGradient(
        listOf(
            fill.copy(alpha = 1f),
            fill.copy(alpha = 0.85f)
        )
    )

    Box(modifier = modifier) {

        // ── Bottom glow blob (spreads below card) ─────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.80f)
                .height(28.dp)
                .blur(if (selected) 22.dp else 14.dp)
                .graphicsLayer {
                    alpha       = if (selected) 1f else 0.65f
                    translationY = 8f
                }
                .background(
                    glowColor.copy(alpha = 0.85f),
                    RoundedCornerShape(50)
                )
        )

        // ── Card body ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(18.dp))
                .background(fillBrush)
                .clickable(
                    interactionSource = interactionSource,
                    indication        = null,
                    onClick           = onClick
                )
        ) {

            // Border — drawn as inner layer on top of fill
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = if (selected) 2.dp else 1.5.dp,
                        brush = borderBrush,
                        shape = RoundedCornerShape(18.dp)
                    )
            )

            // Top sheen highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.06f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Label centred
            Text(
                text       = label,
                color      = if (selected) textColor else textColor.copy(alpha = 0.80f),
                fontSize   = if (label.length > 8) 22.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center,
                modifier   = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 12.dp)
            )
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(
    showBackground = true,
    widthDp        = 1280,
    heightDp       = 800,
    name           = "Age Screen – Tablet Landscape"
)
@Composable
fun AgeScreenPreview() {
    PixieLookTheme {
        AgeScreen(onNext = {})
    }
}