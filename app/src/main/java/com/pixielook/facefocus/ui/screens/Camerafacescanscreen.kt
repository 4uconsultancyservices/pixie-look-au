package com.pixielook.facefocus.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixielook.facefocus.R
import com.pixielook.facefocus.ui.theme.PixieLookTheme

// ─── Colors ───────────────────────────────────────────────────────────────────
private val ScreenBg         = Color(0xFF050505)
private val OvalStrokeColor  = Color.White
private val InstructionColor = Color.White
private val NavIconColor     = Color.White.copy(alpha = 0.82f)
private val NavHomeColor     = Color.White.copy(alpha = 0.82f)

// Bottom gradient scrim — fades photo into dark at bottom for readability
private val BottomScrim = Brush.verticalGradient(
    colorStops = arrayOf(
        0.00f to Color.Transparent,
        0.55f to Color.Transparent,
        0.72f to Color(0xFF050505).copy(alpha = 0.55f),
        0.84f to Color(0xFF050505).copy(alpha = 0.82f),
        1.00f to Color(0xFF050505)
    )
)

// Side vignette — darkens edges so face is the focal point
private val LeftVignette = Brush.horizontalGradient(
    listOf(Color.Black.copy(alpha = 0.65f), Color.Transparent)
)
private val RightVignette = Brush.horizontalGradient(
    listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
)
private val TopVignette = Brush.verticalGradient(
    listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent)
)

@Composable
fun CameraFaceScanScreen(
    onBack : () -> Unit = {},
    onHome : () -> Unit = {}
) {
    val screenWidth  = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val isTablet     = screenWidth > 900.dp

    // Oval pulse animation — stroke breathes subtly
    val infiniteTransition = rememberInfiniteTransition(label = "ovalPulse")

    val ovalStrokeWidth by infiniteTransition.animateFloat(
        initialValue  = 3.0f,
        targetValue   = 4.5f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "strokeWidth"
    )

    val ovalAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.85f,
        targetValue   = 1.00f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ovalAlpha"
    )

    // Oval dimensions — portrait oval, taller than wide
    // As seen in screenshot: oval is roughly 28% of screen width, 52% of screen height
    val ovalWidthFraction  = if (isTablet) 0.26f else 0.38f
    val ovalHeightFraction = if (isTablet) 0.68f else 0.72f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {

        // ── FULL BLEED BACKGROUND PHOTO ───────────────────────────────────────
        // Image fills entire screen, face centered
        Image(
            painter            = painterResource(id = R.drawable.feminine),
            contentDescription = "Face scan model",
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )

        // ── TOP VIGNETTE ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.22f)
                .align(Alignment.TopCenter)
                .background(TopVignette)
        )

        // ── LEFT VIGNETTE ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.22f)
                .align(Alignment.CenterStart)
                .background(LeftVignette)
        )

        // ── RIGHT VIGNETTE ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.22f)
                .align(Alignment.CenterEnd)
                .background(RightVignette)
        )

        // ── BOTTOM GRADIENT SCRIM ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BottomScrim)
        )

        // ── WHITE OVAL FACE GUIDE ─────────────────────────────────────────────
        // Drawn as Canvas oval with animated stroke width
        // Positioned: centered horizontally, upper-center vertically
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val ovalW = size.width  * ovalWidthFraction
                    val ovalH = size.height * ovalHeightFraction

                    // Center the oval at ~42% from top (face is in upper half)
                    val centerX = size.width  * 0.50f
                    val centerY = size.height * 0.42f

                    val ovalLeft   = centerX - ovalW / 2f
                    val ovalTop    = centerY - ovalH / 2f

                    onDrawBehind {
                        drawOval(
                            color  = OvalStrokeColor.copy(alpha = ovalAlpha),
                            topLeft = Offset(ovalLeft, ovalTop),
                            size   = Size(ovalW, ovalH),
                            style  = Stroke(
                                width = ovalStrokeWidth.dp.toPx(),
                                cap   = StrokeCap.Round
                            )
                        )
                    }
                }
        )

        // ── INSTRUCTION TEXT + BOTTOM NAV ─────────────────────────────────────
        Column(
            modifier              = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = if (isTablet) 24.dp else 16.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Bottom
        ) {

            // "Please fit your face within the circle."
            Text(
                text       = "Please fit your face within the circle.",
                color      = InstructionColor,
                fontSize   = if (isTablet) 26.sp else 21.sp,
                fontWeight = FontWeight.Normal,
                textAlign  = TextAlign.Center,
                letterSpacing = 0.3.sp,
                modifier   = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(if (isTablet) 28.dp else 20.dp))

            // ── BOTTOM NAV ────────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isTablet) 12.dp else 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Menu / hamburger
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

                // Home
                Icon(
                    imageVector        = Icons.Rounded.Home,
                    contentDescription = "Home",
                    tint               = NavHomeColor,
                    modifier           = Modifier
                        .size(30.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = onHome
                        )
                )

                Spacer(Modifier.width(72.dp))

                // Back
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

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(
    showBackground = true,
    widthDp        = 1456,
    heightDp       = 816,
    name           = "Camera Face Scan Screen – Tablet Landscape"
)
@Composable
fun CameraFaceScanScreenPreview() {
    PixieLookTheme {
        CameraFaceScanScreen(
            onBack = {},
            onHome = {}
        )
    }
}