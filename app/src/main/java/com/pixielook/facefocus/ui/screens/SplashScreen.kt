package com.pixielook.facefocus.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixielook.facefocus.R
import com.pixielook.facefocus.ui.theme.PixieLookTheme
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
//  Font families
//  Swap FontFamily.Serif / SansSerif with your res/font/ files when available.
//  e.g.  private val PixieSerif = FontFamily(Font(R.font.your_italic_serif))
// ─────────────────────────────────────────────────────────────────────────────
private val PixieSerif = FontFamily.Serif
private val PixieSans  = FontFamily.SansSerif

// ─────────────────────────────────────────────────────────────────────────────
//  Device class — derived purely from landscape width (dp)
//
//  LANDSCAPE widths (this app is landscape-only):
//    Phone landscape  :  568 – 759 dp
//    Small tablet 7″  :  600 – 899 dp  (often 960 × 600)
//    Tablet 10″       :  960 – 1199 dp (e.g. 1280 × 800 → 1280 dp wide)
//    Large tablet 12″ : 1200 + dp
// ─────────────────────────────────────────────────────────────────────────────
private enum class DeviceClass { PHONE, SMALL_TABLET, TABLET, LARGE_TABLET }

private fun classifyDevice(widthDp: Int, heightDp: Int): DeviceClass {
    // Smallest dimension determines form-factor independent of orientation
    val smallestDp = minOf(widthDp, heightDp)
    return when {
        smallestDp >= 720 -> DeviceClass.LARGE_TABLET
        smallestDp >= 600 -> DeviceClass.TABLET
        smallestDp >= 480 -> DeviceClass.SMALL_TABLET
        else              -> DeviceClass.PHONE
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Per-device-class design tokens
//  All sizes are tuned for LANDSCAPE orientation.
// ─────────────────────────────────────────────────────────────────────────────
private data class SplashTokens(
    val logoSize        : Dp,       // square size for the logo image
    val pixieFontSp     : TextUnit, // "pixie" italic text
    val lookFontSp      : TextUnit, // "L O O K" caps text
    val lookLetterSp    : TextUnit, // letter-spacing for LOOK
    val dotSize         : Dp,       // sparkle dot diameter
    val spacerLogoText  : Dp,       // gap between logo and "pixie"
    val spacerInnerRow  : Dp,       // gap between "pixie", sparkle row, "LOOK"
)

private fun tokensFor(device: DeviceClass): SplashTokens = when (device) {

    DeviceClass.PHONE -> SplashTokens(
        logoSize       = 160.dp,
        pixieFontSp    = 38.sp,
        lookFontSp     = 15.sp,
        lookLetterSp   = 6.sp,
        dotSize        = 5.dp,
        spacerLogoText = 14.dp,
        spacerInnerRow = 5.dp,
    )

    DeviceClass.SMALL_TABLET -> SplashTokens(
        logoSize       = 210.dp,
        pixieFontSp    = 48.sp,
        lookFontSp     = 19.sp,
        lookLetterSp   = 7.sp,
        dotSize        = 6.dp,
        spacerLogoText = 18.dp,
        spacerInnerRow = 6.dp,
    )

    DeviceClass.TABLET -> SplashTokens(      // ← primary target  (10″, 1280×800)
        logoSize       = 300.dp,
        pixieFontSp    = 64.sp,
        lookFontSp     = 24.sp,
        lookLetterSp   = 10.sp,
        dotSize        = 8.dp,
        spacerLogoText = 24.dp,
        spacerInnerRow = 8.dp,
    )

    DeviceClass.LARGE_TABLET -> SplashTokens(  // 12″ / large landscape displays
        logoSize       = 380.dp,
        pixieFontSp    = 80.sp,
        lookFontSp     = 30.sp,
        lookLetterSp   = 12.sp,
        dotSize        = 10.dp,
        spacerLogoText = 30.dp,
        spacerInnerRow = 10.dp,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  SplashScreen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    val config  = LocalConfiguration.current
    val device  = classifyDevice(config.screenWidthDp, config.screenHeightDp)
    val tokens  = tokensFor(device)

    // ── Animations ────────────────────────────────────────────────────────
    val contentAlpha = remember { Animatable(0f) }
    val logoScale    = remember { Animatable(0.90f) }

    LaunchedEffect(Unit) {
        // Parallel: fade-in + subtle pop-in scale
        contentAlpha.animateTo(
            targetValue   = 1f,
            animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
        )
        logoScale.animateTo(
            targetValue   = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        delay(1500)
        onTimeout()
    }

    // ── Root ──────────────────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize()) {

        // Layer 1 — galaxy background (always full-bleed, cropped to screen)
        Image(
            painter            = painterResource(id = R.drawable.bg_spashscreen),
            contentDescription = null,
            modifier           = Modifier.fillMaxSize(),
            contentScale       = ContentScale.Crop
        )

        // Layer 2 — centred logo group
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha.value),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {

            // Logo icon mark
            Image(
                painter            = painterResource(id = R.drawable.logo),
                contentDescription = "Pixie Look icon",
                modifier           = Modifier
                    .size(tokens.logoSize)
                    .scale(logoScale.value),
                contentScale       = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(tokens.spacerLogoText))

            // "pixie" — italic serif
            Text(
                text          = "pixie",
                fontFamily    = PixieSerif,
                fontStyle     = FontStyle.Italic,
                fontWeight    = FontWeight.Light,
                fontSize      = tokens.pixieFontSp,
                color         = Color.White,
                textAlign     = TextAlign.Center,
                letterSpacing = (tokens.pixieFontSp.value * 0.025f).sp
            )

            Spacer(modifier = Modifier.height(tokens.spacerInnerRow))

            // Sparkle separator:  ·  ·  ✦  ·  ·
            SparkleRow(dotSize = tokens.dotSize, starSp = tokens.dotSize.value * 1.8f)

            Spacer(modifier = Modifier.height(tokens.spacerInnerRow))

            // "LOOK" — spaced bold caps
            Text(
                text          = "L O O K",
                fontFamily    = PixieSans,
                fontWeight    = FontWeight.Bold,
                fontSize      = tokens.lookFontSp,
                color         = Color.White,
                textAlign     = TextAlign.Center,
                letterSpacing = tokens.lookLetterSp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Sparkle row  ·  ·  ✦  ·  ·
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SparkleRow(dotSize: Dp, starSp: Float) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dotSize)
    ) {
        repeat(2) {
            Box(
                modifier = Modifier
                    .size(dotSize * 0.5f)
                    .background(
                        color = Color.White.copy(alpha = 0.65f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
        Text(
            text       = "✦",
            fontSize   = starSp.sp,
            color      = Color.White.copy(alpha = 0.9f),
            lineHeight = starSp.sp
        )
        repeat(2) {
            Box(
                modifier = Modifier
                    .size(dotSize * 0.5f)
                    .background(
                        color = Color.White.copy(alpha = 0.65f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Previews — landscape only (force widthDp > heightDp)
// ─────────────────────────────────────────────────────────────────────────────

// Phone landscape  (e.g. Pixel 7 rotated)
@Preview(name = "📱 Phone Landscape  852×393",
    showBackground = true, widthDp = 852, heightDp = 393)
@Composable
private fun PreviewPhoneLandscape() {
    PixieLookTheme { SplashScreen(onTimeout = {}) }
}

// Small tablet 7″ landscape  (e.g. Nexus 7)
@Preview(name = "📟 Small Tablet Landscape  960×600",
    showBackground = true, widthDp = 960, heightDp = 600)
@Composable
private fun PreviewSmallTabletLandscape() {
    PixieLookTheme { SplashScreen(onTimeout = {}) }
}

// 10″ tablet landscape — PRIMARY TARGET  (e.g. Pixel Tablet, Samsung Tab S)
@Preview(name = "🖥 Tablet 10″ Landscape  1280×800",
    showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun PreviewTablet10Landscape() {
    PixieLookTheme { SplashScreen(onTimeout = {}) }
}

// 10″ tablet — alternate resolution
@Preview(name = "🖥 Tablet 10″ Landscape  1200×752",
    showBackground = true, widthDp = 1200, heightDp = 752)
@Composable
private fun PreviewTablet10AltLandscape() {
    PixieLookTheme { SplashScreen(onTimeout = {}) }
}

// 12″ large tablet landscape  (e.g. Samsung Tab S9 Ultra)
@Preview(name = "🖥 Large Tablet 12″ Landscape  1600×1000",
    showBackground = true, widthDp = 1600, heightDp = 1000)
@Composable
private fun PreviewLargeTabletLandscape() {
    PixieLookTheme { SplashScreen(onTimeout = {}) }
}