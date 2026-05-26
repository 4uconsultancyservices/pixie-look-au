package com.pixielook.facefocus.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixielook.facefocus.R
import com.pixielook.facefocus.ui.theme.PixieLookTheme
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ─── Colors ───────────────────────────────────────────────────────────────────
private val ScreenBg          = Color(0xFF060606)
private val SuccessGradStart  = Color(0xFFCC66FF)   // purple
private val SuccessGradEnd    = Color(0xFFFFAA33)   // amber/orange
private val SubtitleColor     = Color(0xFFCCCCCC)
private val ProceedBorderTop  = Color(0xFFFFCC44)   // bright amber top
private val ProceedBorderBot  = Color(0xFFFF7700)   // deeper orange bottom
private val ProceedTextColor  = Color(0xFFFFCC55)   // amber text
private val ProceedFill       = Color(0xFF14102A)   // very dark purple fill
private val DividerGlow       = Color(0xFF6633AA)   // purple glow divider

// Confetti colours (matches screenshot: blue, cyan, orange, yellow, pink, purple, red)
private val confettiColors = listOf(
    Color(0xFF3388FF),
    Color(0xFF00DDFF),
    Color(0xFFFF6600),
    Color(0xFFFFCC00),
    Color(0xFFFF44AA),
    Color(0xFFAA44FF),
    Color(0xFFFF3344),
    Color(0xFF44FF88),
    Color(0xFFFFDD44),
)

// ─── Confetti piece data ──────────────────────────────────────────────────────
private data class ConfettiPiece(
    val color       : Color,
    val angle       : Float,   // degrees from center
    val distance    : Float,   // 0..1 normalised orbit radius
    val size        : Float,   // dp
    val rotSelf     : Float,   // initial self-rotation
    val shape       : Int,     // 0=rect, 1=square, 2=circle
    val speedOffset : Float    // phase offset for animation
)

private fun generateConfetti(count: Int = 38): List<ConfettiPiece> {
    val rng = Random(42)
    return List(count) {
        ConfettiPiece(
            color       = confettiColors[rng.nextInt(confettiColors.size)],
            angle       = rng.nextFloat() * 360f,
            distance    = 0.38f + rng.nextFloat() * 0.48f,
            size        = 7f + rng.nextFloat() * 10f,
            rotSelf     = rng.nextFloat() * 360f,
            shape       = rng.nextInt(3),
            speedOffset = rng.nextFloat()
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun SuccessScreen(
    onNext : () -> Unit = {},
    onBack    : () -> Unit = {},
    onHome    : () -> Unit = {}
) {
    val screenWidth  = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val isTablet     = screenWidth > 900.dp

    // ── Trophy float animation (gentle up-down bob) ───────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "trophy")

    val trophyOffsetY by infiniteTransition.animateFloat(
        initialValue   = 0f,
        targetValue    = -18f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophyFloat"
    )

    // Trophy scale pulse (very subtle)
    val trophyScale by infiniteTransition.animateFloat(
        initialValue  = 1.00f,
        targetValue   = 1.04f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophyScale"
    )

    // Trophy glow intensity pulse
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.40f,
        targetValue   = 0.80f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Confetti orbit angle — pieces slowly orbit around trophy
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation  = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiOrbit"
    )

    // Individual confetti self-rotation
    val confettiRot by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation  = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiSelfRot"
    )

    // Proceed button glow pulse
    val btnGlow by infiniteTransition.animateFloat(
        initialValue  = 0.55f,
        targetValue   = 1.00f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btnGlow"
    )

    val confetti = remember { generateConfetti(38) }

    val trophySize : Dp = if (isTablet) 260.dp else 220.dp
    val orbitRadius     = if (isTablet) 180f else 155f   // px-ish, will be scaled by density

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier              = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = if (isTablet) 80.dp else 32.dp,
                    vertical   = if (isTablet) 28.dp else 16.dp
                ),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.SpaceBetween
        ) {

            // ── TOP SPACER ────────────────────────────────────────────────────
            Spacer(Modifier.height(if (isTablet) 32.dp else 16.dp))

            // ── TROPHY + CONFETTI ZONE ────────────────────────────────────────
            Box(
                modifier         = Modifier
                    .size(if (isTablet) 400.dp else 340.dp),
                contentAlignment = Alignment.Center
            ) {

                // Confetti particles — drawn via Canvas on a full-size overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawConfetti(
                                pieces      = confetti,
                                orbitAngle  = orbitAngle,
                                selfRot     = confettiRot,
                                orbitRadius = orbitRadius,
                                center      = Offset(size.width / 2f, size.height / 2f)
                            )
                        }
                )

                // Radial glow behind trophy
                Box(
                    modifier = Modifier
                        .size(if (isTablet) 280.dp else 230.dp)
                        .blur(40.dp)
                        .graphicsLayer { alpha = glowAlpha }
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFFFFAA00).copy(alpha = 0.55f),
                                    Color(0xFFFF6600).copy(alpha = 0.20f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Trophy image — floating animation
                androidx.compose.foundation.Image(
                    painter            = painterResource(id = R.drawable.logo),
                    contentDescription = "Trophy",
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier
                        .size(trophySize)
                        .graphicsLayer {
                            translationY = trophyOffsetY
                            scaleX       = trophyScale
                            scaleY       = trophyScale
                        }
                )
            }

            // ── TEXT BLOCK ────────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // "Success" — gradient text (purple → amber)
                Text(
                    text       = "Success",
                    fontSize   = if (isTablet) 64.sp else 52.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign  = TextAlign.Center,
                    style      = androidx.compose.ui.text.TextStyle(
                        brush = Brush.horizontalGradient(
                            listOf(SuccessGradStart, SuccessGradEnd)
                        )
                    )
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text       = "Thank you for sharing your preferences!\nLet's move on to the next step, Face Analysis",
                    color      = SubtitleColor,
                    fontSize   = if (isTablet) 17.sp else 15.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign  = TextAlign.Center,
                    lineHeight = if (isTablet) 28.sp else 24.sp
                )
            }

            // ── PROCEED BUTTON ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (isTablet) 0.55f else 0.88f)
                    .height(if (isTablet) 66.dp else 58.dp)
            ) {
                // Outer amber glow shadow behind button
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .blur(18.dp)
                        .graphicsLayer { alpha = btnGlow * 0.65f }
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    ProceedBorderTop.copy(alpha = 0.70f),
                                    ProceedBorderBot.copy(alpha = 0.70f)
                                )
                            ),
                            RoundedCornerShape(12.dp)
                        )
                )

                // Button body
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ProceedFill)
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    ProceedBorderTop.copy(alpha = btnGlow),
                                    ProceedBorderBot.copy(alpha = btnGlow)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = onNext
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "Proceed",
                        color      = ProceedTextColor,
                        fontSize   = if (isTablet) 22.sp else 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Center
                    )
                }
            }

            // ── DIVIDER GLOW LINE ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                DividerGlow.copy(alpha = 0.25f),
                                DividerGlow.copy(alpha = 0.70f),
                                DividerGlow.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Glow blur behind divider
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(6.dp)
                    .blur(10.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                DividerGlow.copy(alpha = 0.55f),
                                DividerGlow.copy(alpha = 0.90f),
                                DividerGlow.copy(alpha = 0.55f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // ── BOTTOM NAV BAR ────────────────────────────────────────────────
            SuccessBottomNavBar(
                onMenu = {},
                onHome = onHome,
                onBack = onBack
            )
        }
    }
}

// ─── Confetti Draw Extension ──────────────────────────────────────────────────
private fun DrawScope.drawConfetti(
    pieces      : List<ConfettiPiece>,
    orbitAngle  : Float,
    selfRot     : Float,
    orbitRadius : Float,
    center      : Offset
) {
    pieces.forEach { piece ->
        val angleDeg = piece.angle + orbitAngle * (0.4f + piece.speedOffset * 0.6f)
        val angleRad = Math.toRadians(angleDeg.toDouble())
        val radius   = orbitRadius * piece.distance * size.minDimension / 340f

        val x = center.x + (cos(angleRad) * radius).toFloat()
        val y = center.y + (sin(angleRad) * radius).toFloat()

        val sizePx   = piece.size.dp.toPx()
        val rotation = piece.rotSelf + selfRot * (0.5f + piece.speedOffset)

        rotate(rotation, pivot = Offset(x, y)) {
            when (piece.shape) {
                0 -> drawRect(          // rectangle
                    color    = piece.color,
                    topLeft  = Offset(x - sizePx * 0.35f, y - sizePx * 0.6f),
                    size     = androidx.compose.ui.geometry.Size(sizePx * 0.7f, sizePx * 1.2f)
                )
                1 -> drawRect(          // square
                    color   = piece.color,
                    topLeft = Offset(x - sizePx / 2, y - sizePx / 2),
                    size    = androidx.compose.ui.geometry.Size(sizePx, sizePx)
                )
                else -> drawCircle(     // dot
                    color  = piece.color,
                    radius = sizePx * 0.45f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

// ─── Bottom Nav Bar ───────────────────────────────────────────────────────────
@Composable
private fun SuccessBottomNavBar(
    onMenu : () -> Unit,
    onHome : () -> Unit,
    onBack : () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = Icons.Rounded.Menu,
            contentDescription = "Menu",
            tint               = Color.White.copy(alpha = 0.75f),
            modifier           = Modifier
                .size(28.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onMenu
                )
        )

        Spacer(Modifier.width(72.dp))

        Icon(
            imageVector        = Icons.Rounded.Home,
            contentDescription = "Home",
            tint               = Color(0xFFFF9900),
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
            imageVector        = Icons.Rounded.KeyboardArrowRight,
            contentDescription = "Back",
            tint               = Color.White.copy(alpha = 0.75f),
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

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(
    showBackground = true,
    widthDp        = 1456,
    heightDp       = 816,
    name           = "Success Screen – Tablet Landscape"
)
@Composable
fun SuccessScreenPreview() {
    PixieLookTheme {
        SuccessScreen(
            onNext = {},
            onBack    = {},
            onHome    = {}
        )
    }
}