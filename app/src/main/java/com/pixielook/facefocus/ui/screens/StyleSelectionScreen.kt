package com.pixielook.facefocus.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixielook.facefocus.R
import com.pixielook.facefocus.ui.components.StyleCard
import com.pixielook.facefocus.ui.theme.PixieLookTheme

// ─────────────────────────────────────────────────────────────────────────────
//  Colour palette
// ─────────────────────────────────────────────────────────────────────────────
private val BgDark        = Color(0xFF080812)
private val OrangeActive  = Color(0xFFF97316)
private val BlueNext      = Color(0xFF2563EB)
private val GlowFeminine  = Color(0xFFF43F5E)
private val GlowMasculine = Color(0xFF3B82F6)
private val GlowNonBinary = Color(0xFFA855F7)

// ─────────────────────────────────────────────────────────────────────────────
//  Device-class tokens (landscape)
// ─────────────────────────────────────────────────────────────────────────────
private enum class Device { PHONE, SMALL_TAB, TABLET, LARGE_TAB }

private fun classifyDevice(w: Int, h: Int): Device {
    val s = minOf(w, h)
    return when {
        s >= 720 -> Device.LARGE_TAB
        s >= 600 -> Device.TABLET
        s >= 480 -> Device.SMALL_TAB
        else     -> Device.PHONE
    }
}

private data class Tokens(
    val hPad         : Dp,
    val vPad         : Dp,
    val titleSp      : TextUnit,
    val subtitleSp   : TextUnit,
    val nextBtnSize  : Dp,
    val nextIconSize : Dp,
    val progActiveW  : Dp,
    val progInactiveW: Dp,
    val progH        : Dp,
    val cardGap      : Dp,
    val cardRadius   : Dp,
    val labelSp      : TextUnit,
)

private fun tokensFor(d: Device) = when (d) {
    Device.PHONE -> Tokens(
        hPad = 28.dp, vPad = 24.dp,
        titleSp = 28.sp, subtitleSp = 13.sp,
        nextBtnSize = 52.dp, nextIconSize = 34.dp,
        progActiveW = 56.dp, progInactiveW = 18.dp, progH = 4.dp,
        cardGap = 16.dp, cardRadius = 14.dp, labelSp = 15.sp,
    )
    Device.SMALL_TAB -> Tokens(
        hPad = 40.dp, vPad = 28.dp,
        titleSp = 34.sp, subtitleSp = 15.sp,
        nextBtnSize = 60.dp, nextIconSize = 40.dp,
        progActiveW = 68.dp, progInactiveW = 20.dp, progH = 4.dp,
        cardGap = 22.dp, cardRadius = 16.dp, labelSp = 17.sp,
    )
    Device.TABLET -> Tokens(                            // ← primary 10″ 1280×800
        hPad = 60.dp, vPad = 36.dp,
        titleSp = 42.sp, subtitleSp = 18.sp,
        nextBtnSize = 72.dp, nextIconSize = 48.dp,
        progActiveW = 80.dp, progInactiveW = 24.dp, progH = 4.dp,
        cardGap = 28.dp, cardRadius = 20.dp, labelSp = 20.sp,
    )
    Device.LARGE_TAB -> Tokens(
        hPad = 80.dp, vPad = 44.dp,
        titleSp = 52.sp, subtitleSp = 22.sp,
        nextBtnSize = 88.dp, nextIconSize = 56.dp,
        progActiveW = 96.dp, progInactiveW = 28.dp, progH = 5.dp,
        cardGap = 36.dp, cardRadius = 24.dp, labelSp = 24.sp,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  StyleSelectionScreen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun StyleSelectionScreen(onNext: () -> Unit) {

    var selectedStyle by remember { mutableStateOf<String?>(null) }

    val cfg    = LocalConfiguration.current
    val device = classifyDevice(cfg.screenWidthDp, cfg.screenHeightDp)
    val t      = tokensFor(device)

    // Entrance fade
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .alpha(alpha.value)
            .padding(horizontal = t.hPad, vertical = t.vPad)
    ) {

        // ── Progress indicator (top-centre) ──────────────────────────────
        ProgressBar(
            tokens  = t,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // ── Header row: title+subtitle | next button ──────────────────────
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(top = t.progH + 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top
        ) {
            // Left: title + subtitle
            Column(modifier = Modifier.weight(1f).padding(end = 24.dp)) {
                Text(
                    text          = "Your Style",
                    color         = Color.White,
                    fontSize      = t.titleSp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    fontFamily    = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text       = "For a personalized experience and results,\ntell us the hair style you want",
                    color      = Color.White.copy(alpha = 0.65f),
                    fontSize   = t.subtitleSp,
                    lineHeight = (t.subtitleSp.value * 1.5f).sp,
                    fontFamily = FontFamily.SansSerif
                )
            }

            // Right: Next button
            NextButton(
                enabled  = selectedStyle != null,
                size     = t.nextBtnSize,
                iconSize = t.nextIconSize,
                onClick  = onNext
            )
        }

        // ── Style cards row (fills remaining height) ──────────────────────
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                // Top bound: header area (~title + subtitle + padding ≈ 38% height)
                .fillMaxHeight(0.72f),
            horizontalArrangement = Arrangement.spacedBy(t.cardGap)
        ) {
            StyleCard(
                label      = "Feminine",
                imageRes   = R.drawable.feminine,
                glowColor  = GlowFeminine,
                isSelected = selectedStyle == "Feminine",
                onClick    = { selectedStyle = "Feminine" },
                cardRadius = t.cardRadius,
                modifier   = Modifier.weight(1f)
            )
            StyleCard(
                label      = "Masculine",
                imageRes   = R.drawable.masculine,
                glowColor  = GlowMasculine,
                isSelected = selectedStyle == "Masculine",
                onClick    = { selectedStyle = "Masculine" },
                cardRadius = t.cardRadius,
                modifier   = Modifier.weight(1f)
            )
            StyleCard(
                label      = "Non-binary",
                imageRes   = R.drawable.non_binary,
                glowColor  = GlowNonBinary,
                isSelected = selectedStyle == "Non-binary",
                onClick    = { selectedStyle = "Non-binary" },
                cardRadius = t.cardRadius,
                modifier   = Modifier.weight(1f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Sub-composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProgressBar(tokens: Tokens, modifier: Modifier = Modifier) {
    Row(
        modifier              = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Step 1 — active (orange)
        Box(
            modifier = Modifier
                .size(width = tokens.progActiveW, height = tokens.progH)
                .background(OrangeActive, RoundedCornerShape(tokens.progH / 2))
        )
        // Step 2 — inactive
        Box(
            modifier = Modifier
                .size(width = tokens.progInactiveW, height = tokens.progH)
                .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(tokens.progH / 2))
        )
        // Step 3 — inactive
        Box(
            modifier = Modifier
                .size(width = tokens.progInactiveW, height = tokens.progH)
                .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(tokens.progH / 2))
        )
    }
}

@Composable
private fun NextButton(
    enabled  : Boolean,
    size     : Dp,
    iconSize : Dp,
    onClick  : () -> Unit,
) {
    // Animate glow when selection is made
    val glowRadius by animateDpAsState(
        targetValue   = if (enabled) 20.dp else 0.dp,
        animationSpec = tween(350),
        label         = "nextGlow"
    )
    val btnAlpha by animateFloatAsState(
        targetValue   = if (enabled) 1f else 0.5f,
        animationSpec = tween(300),
        label         = "nextAlpha"
    )

    Surface(
        onClick          = onClick,
        enabled          = enabled,
        shape            = CircleShape,
        color            = BlueNext,
        modifier         = Modifier
            .size(size)
            .alpha(btnAlpha)
            .drawBehind {
                if (glowRadius.toPx() > 0f) {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            asFrameworkPaint().setShadowLayer(
                                glowRadius.toPx(), 0f, 0f,
                                BlueNext.copy(alpha = 0.7f).toArgb()
                            )
                        }
                        canvas.drawCircle(
                            center = androidx.compose.ui.geometry.Offset(
                                this.size.width / 2f, this.size.height / 2f
                            ),
                            radius = this.size.width / 2f,
                            paint  = paint
                        )
                    }
                }
            },
        shadowElevation  = if (enabled) 12.dp else 4.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector        = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next",
                tint               = Color.White,
                modifier           = Modifier.size(iconSize)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "📱 Phone Landscape  852×393",
    showBackground = true, widthDp = 852, heightDp = 393)
@Composable
private fun PreviewPhone() {
    PixieLookTheme { StyleSelectionScreen(onNext = {}) }
}

@Preview(name = "📟 Small Tablet  960×600",
    showBackground = true, widthDp = 960, heightDp = 600)
@Composable
private fun PreviewSmallTab() {
    PixieLookTheme { StyleSelectionScreen(onNext = {}) }
}

@Preview(name = "🖥 Tablet 10″  1280×800  ★ Primary",
    showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun PreviewTablet() {
    PixieLookTheme { StyleSelectionScreen(onNext = {}) }
}

@Preview(name = "🖥 Large Tablet 12″  1600×1000",
    showBackground = true, widthDp = 1600, heightDp = 1000)
@Composable
private fun PreviewLargeTab() {
    PixieLookTheme { StyleSelectionScreen(onNext = {}) }
}