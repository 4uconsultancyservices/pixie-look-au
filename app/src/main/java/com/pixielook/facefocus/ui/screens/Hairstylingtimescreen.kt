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
private val ScreenBg    = Color(0xFF080808)
private val OuterCardBg = Color(0xFF0D0D0D)

// ─── Outer card rainbow border ────────────────────────────────────────────────
// Matches the screenshot: left side cool-purple → top pink-violet → right blue-cyan → bottom amber-orange
private val OuterBorderBrush = Brush.sweepGradient(
    listOf(
        Color(0xFF8B20FF),   // purple  (left)
        Color(0xFFFF20C0),   // hot pink (top-left)
        Color(0xFFFF4488),   // pink     (top)
        Color(0xFF00AAFF),   // sky blue (top-right)
        Color(0xFF00D8FF),   // cyan     (right)
        Color(0xFFFFAA00),   // amber    (bottom-right)
        Color(0xFFFF6600),   // orange   (bottom)
        Color(0xFFFF20C0),   // hot pink (bottom-left)
        Color(0xFF8B20FF),   // back to purple
    )
)

// ─── Per-card fills (dark tinted backgrounds) ─────────────────────────────────
private val FillBelow15   = Color(0xFF0E0A14)   // dark purple/magenta tint
private val Fill15to30    = Color(0xFF051218)   // dark teal-navy
private val Fill30to1hr   = Color(0xFF180E04)   // dark amber/brown
private val Fill1to2hr    = Color(0xFF051218)   // dark teal-navy
private val FillAbove2hr1 = Color(0xFF18080C)   // dark amber-red tint
private val FillAbove2hr2 = Color(0xFF0E0A14)   // dark purple tint

// ─── Per-card border + glow colors ───────────────────────────────────────────
private val ColorBelow15   = Color(0xFFCC44FF)   // purple/violet
private val Color15to30    = Color(0xFF2299FF)   // blue
private val Color30to1hr   = Color(0xFFFFAA00)   // amber/orange
private val Color1to2hr    = Color(0xFF22AAFF)   // blue (slightly different)
private val ColorAbove2hr1 = Color(0xFFFF8800)   // orange
private val ColorAbove2hr2 = Color(0xFF8844FF)   // purple

// ─── Next button ─────────────────────────────────────────────────────────────
private val NextBtnBorderColor = Color(0xFF44AAFF)
private val NextBtnGlowColor   = Color(0xFF1166FF)

@Composable
fun HairStylingTimeScreen(
    onNext: () -> Unit = {}
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val isTablet    = screenWidth > 900.dp

    var selected by remember { mutableStateOf("Below 15 mins") }

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

        // ── OUTER CARD with rainbow border ────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .background(OuterCardBg)
                .border(
                    width = 2.dp,
                    brush = OuterBorderBrush,
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
                    // Active pill — amber/orange gradient
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
                    // Inactive pills
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
                            text       = "Your Time for Hair Styling",
                            color      = Color.White,
                            fontSize   = if (isTablet) 38.sp else 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 44.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text       = "Within your self-care time, this is the amount you allot to only hair styling.",
                            color      = Color.White.copy(alpha = 0.55f),
                            fontSize   = if (isTablet) 15.sp else 13.sp,
                            lineHeight = if (isTablet) 24.sp else 20.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    // Right: Next button — outlined circle with cyan border + glow
                    Box(contentAlignment = Alignment.Center) {
                        // Cyan glow behind button
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .blur(24.dp)
                                .graphicsLayer { alpha = 0.60f }
                                .background(NextBtnGlowColor, CircleShape)
                        )
                        // Outlined circle button
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                                .border(
                                    width = 2.dp,
                                    color = NextBtnBorderColor,
                                    shape = CircleShape
                                )
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

                Spacer(Modifier.height(if (isTablet) 32.dp else 22.dp))

                // ── ROW 1: 3 equal cards ──────────────────────────────────────
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .height(if (isTablet) 140.dp else 118.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    HairTimeOptionCard(
                        label     = "Below 15 mins",
                        fill      = FillBelow15,
                        glowColor = ColorBelow15,
                        textColor = Color.White,
                        selected  = selected == "Below 15 mins",
                        modifier  = Modifier.weight(1f).fillMaxHeight()
                    ) { selected = "Below 15 mins" }

                    HairTimeOptionCard(
                        label     = "15 - 30 mins",
                        fill      = Fill15to30,
                        glowColor = Color15to30,
                        textColor = Color.White,
                        selected  = selected == "15 - 30 mins",
                        modifier  = Modifier.weight(1f).fillMaxHeight()
                    ) { selected = "15 - 30 mins" }

                    HairTimeOptionCard(
                        label     = "30 mins - 1 hour",
                        fill      = Fill30to1hr,
                        glowColor = Color30to1hr,
                        textColor = Color.White,
                        selected  = selected == "30 mins - 1 hour",
                        modifier  = Modifier.weight(1f).fillMaxHeight()
                    ) { selected = "30 mins - 1 hour" }
                }

                Spacer(Modifier.height(14.dp))

                // ── ROW 2: 3 equal cards ──────────────────────────────────────
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .height(if (isTablet) 140.dp else 118.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    HairTimeOptionCard(
                        label     = "1 - 2 hours",
                        fill      = Fill1to2hr,
                        glowColor = Color1to2hr,
                        textColor = Color.White,
                        selected  = selected == "1 - 2 hours",
                        modifier  = Modifier.weight(1f).fillMaxHeight()
                    ) { selected = "1 - 2 hours" }

                    HairTimeOptionCard(
                        label     = "Above 2 hours",
                        fill      = FillAbove2hr1,
                        glowColor = ColorAbove2hr1,
                        textColor = Color.White,
                        selected  = selected == "Above 2 hours (1)",
                        modifier  = Modifier.weight(1f).fillMaxHeight()
                    ) { selected = "Above 2 hours (1)" }

                    HairTimeOptionCard(
                        label     = "Above 2 hours",
                        fill      = FillAbove2hr2,
                        glowColor = ColorAbove2hr2,
                        textColor = Color.White,
                        selected  = selected == "Above 2 hours (2)",
                        modifier  = Modifier.weight(1f).fillMaxHeight()
                    ) { selected = "Above 2 hours (2)" }
                }
            }
        }
    }
}

// ─── Hair Time Option Card ────────────────────────────────────────────────────
// Matches screenshot: bottom-heavy neon glow, dark fill, bright neon border
@Composable
fun HairTimeOptionCard(
    label     : String,
    fill      : Color,
    glowColor : Color,
    textColor : Color,
    selected  : Boolean,
    modifier  : Modifier = Modifier,
    onClick   : () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Bottom-heavy border brush: transparent/faint top → glowColor bottom
    val borderBrush = Brush.verticalGradient(
        listOf(
            glowColor.copy(alpha = if (selected) 0.35f else 0.22f),   // top: faint
            glowColor.copy(alpha = if (selected) 0.60f else 0.42f),   // mid
            glowColor.copy(alpha = if (selected) 1.00f else 0.80f)    // bottom: bright
        )
    )

    // Inner fill brush
    val fillBrush = Brush.verticalGradient(
        listOf(
            fill.copy(alpha = 1f),
            fill.copy(alpha = 0.88f)
        )
    )

    Box(modifier = modifier) {

        // ── Bottom glow blob spreading below card ────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.75f)
                .height(28.dp)
                .blur(if (selected) 24.dp else 16.dp)
                .graphicsLayer {
                    alpha        = if (selected) 1f else 0.60f
                    translationY = 10f
                }
                .background(
                    glowColor.copy(alpha = 0.90f),
                    RoundedCornerShape(50)
                )
        )

        // ── Card body ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(16.dp))
                .background(fillBrush)
                .clickable(
                    interactionSource = interactionSource,
                    indication        = null,
                    onClick           = onClick
                )
        ) {

            // Neon border on top of fill
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = if (selected) 2.dp else 1.5.dp,
                        brush = borderBrush,
                        shape = RoundedCornerShape(16.dp)
                    )
            )

            // Subtle top sheen highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.42f)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Label centred
            Text(
                text       = label,
                color      = if (selected) textColor else textColor.copy(alpha = 0.85f),
                fontSize   = when {
                    label.length > 14 -> 18.sp
                    label.length > 10 -> 20.sp
                    else              -> 22.sp
                },
                fontWeight = FontWeight.SemiBold,
                textAlign  = TextAlign.Center,
                modifier   = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(
    showBackground = true,
    widthDp        = 1280,
    heightDp       = 800,
    name           = "Hair Styling Time Screen – Tablet Landscape"
)
@Composable
fun HairStylingTimeScreenPreview() {
    PixieLookTheme {
        HairStylingTimeScreen(onNext = {})
    }
}