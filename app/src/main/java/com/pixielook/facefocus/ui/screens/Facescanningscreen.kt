package com.pixielook.facefocus.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixielook.facefocus.R
import com.pixielook.facefocus.ui.theme.PixieLookTheme

// ─── Colors ───────────────────────────────────────────────────────────────────
private val ScreenBg          = Color(0xFF050505)
private val DashedOvalColor   = Color.White.copy(alpha = 0.90f)
private val MeshLineColor     = Color.White.copy(alpha = 0.55f)
private val MeshDotColor      = Color(0xFFAADDFF).copy(alpha = 0.95f)   // light blue dots
private val MeshDotGlowColor  = Color(0xFF88CCFF).copy(alpha = 0.50f)
private val ScanTextColor     = Color.White
private val ProgressTrack     = Color(0xFF444444)   // dark grey

// Bottom gradient scrim
private val BottomScrim = Brush.verticalGradient(
    colorStops = arrayOf(
        0.00f to Color.Transparent,
        0.50f to Color.Transparent,
        0.68f to Color(0xFF050505).copy(alpha = 0.40f),
        0.82f to Color(0xFF050505).copy(alpha = 0.75f),
        1.00f to Color(0xFF050505)
    )
)
private val TopVignette = Brush.verticalGradient(
    listOf(Color.Black.copy(alpha = 0.60f), Color.Transparent)
)
private val LeftVignette  = Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent))
private val RightVignette = Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)))

// ─── Face mesh landmark points (normalised 0..1 relative to oval bounds) ─────
private val landmarkPoints = listOf(
    Offset(0.38f, 0.08f), Offset(0.50f, 0.04f), Offset(0.62f, 0.08f),
    Offset(0.32f, 0.16f), Offset(0.50f, 0.13f), Offset(0.68f, 0.16f),
    Offset(0.28f, 0.24f), Offset(0.38f, 0.22f), Offset(0.50f, 0.24f), Offset(0.62f, 0.22f), Offset(0.72f, 0.24f),
    Offset(0.27f, 0.33f), Offset(0.37f, 0.30f), Offset(0.46f, 0.33f),
    Offset(0.54f, 0.33f), Offset(0.63f, 0.30f), Offset(0.73f, 0.33f),
    Offset(0.50f, 0.38f), Offset(0.44f, 0.46f), Offset(0.50f, 0.50f), Offset(0.56f, 0.46f),
    Offset(0.26f, 0.44f), Offset(0.74f, 0.44f),
    Offset(0.42f, 0.54f), Offset(0.58f, 0.54f),
    Offset(0.38f, 0.60f), Offset(0.50f, 0.58f), Offset(0.62f, 0.60f),
    Offset(0.44f, 0.66f), Offset(0.56f, 0.66f),
    Offset(0.28f, 0.56f), Offset(0.24f, 0.66f), Offset(0.28f, 0.76f),
    Offset(0.50f, 0.80f),
    Offset(0.72f, 0.56f), Offset(0.76f, 0.66f), Offset(0.72f, 0.76f),
    Offset(0.40f, 0.84f), Offset(0.60f, 0.84f),
)

private val meshEdges = listOf(
    0 to 1, 1 to 2, 0 to 3, 2 to 5, 1 to 4, 3 to 4, 4 to 5,
    3 to 6, 5 to 10, 4 to 8,
    6 to 7, 7 to 8, 8 to 9, 9 to 10,
    6 to 11, 10 to 16,
    7 to 12, 9 to 15,
    8 to 13, 8 to 14,
    11 to 12, 12 to 13, 13 to 14, 14 to 15, 15 to 16,
    11 to 21, 16 to 22,
    12 to 17, 15 to 17,
    13 to 17, 14 to 17,
    17 to 18, 17 to 20, 18 to 19, 19 to 20,
    18 to 23, 20 to 24,
    19 to 23, 19 to 24,
    21 to 29, 22 to 33,
    21 to 18, 22 to 18,
    23 to 25, 24 to 27, 25 to 26, 26 to 27,
    25 to 28, 27 to 28, 26 to 28, 26 to 29,
    29 to 30, 30 to 31, 31 to 32,
    33 to 34, 34 to 35, 35 to 32,
    31 to 36, 35 to 37, 32 to 36, 32 to 37,
    36 to 28, 37 to 28,
    29 to 25, 33 to 27,
    31 to 36, 35 to 37,
)

@Composable
fun FaceScanningScreen(
    scanProgress : Float = 0.45f,   // 0.0 .. 1.0
    onComplete   : () -> Unit = {}
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val isTablet    = screenWidth > 900.dp

    val infiniteTransition = rememberInfiniteTransition(label = "scanning")

    val dashPhase by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 60f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashPhase"
    )

    val meshAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.45f,
        targetValue   = 0.80f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "meshAlpha"
    )

    val dotGlow by infiniteTransition.animateFloat(
        initialValue  = 0.60f,
        targetValue   = 1.00f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotGlow"
    )

    val progressAnim by animateFloatAsState(
        targetValue   = scanProgress,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label         = "progress"
    )
    val progressPercent = (progressAnim * 100).toInt()

    val ovalWidthFraction  = if (isTablet) 0.26f else 0.38f
    val ovalHeightFraction = if (isTablet) 0.65f else 0.70f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        Image(
            painter            = painterResource(id = R.drawable.feminine),
            contentDescription = "Face scanning",
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth().fillMaxHeight(0.20f)
                .align(Alignment.TopCenter)
                .background(TopVignette)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight().fillMaxWidth(0.18f)
                .align(Alignment.CenterStart)
                .background(LeftVignette)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight().fillMaxWidth(0.18f)
                .align(Alignment.CenterEnd)
                .background(RightVignette)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BottomScrim)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val ovalW  = size.width  * ovalWidthFraction
                    val ovalH  = size.height * ovalHeightFraction
                    val cx     = size.width  * 0.50f
                    val cy     = size.height * 0.42f
                    val left   = cx - ovalW / 2f
                    val top    = cy - ovalH / 2f

                    onDrawBehind {
                        drawFaceMesh(
                            left      = left,
                            top       = top,
                            ovalW     = ovalW,
                            ovalH     = ovalH,
                            alpha     = meshAlpha,
                            dotAlpha  = dotGlow
                        )
                        drawDashedOval(
                            left      = left,
                            top       = top,
                            ovalW     = ovalW,
                            ovalH     = ovalH,
                            color     = DashedOvalColor,
                            dashPhase = dashPhase
                        )
                    }
                }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    start = if (isTablet) 80.dp else 40.dp,
                    end = if (isTablet) 80.dp else 40.dp,
                    bottom = if (isTablet) 72.dp else 56.dp
                ),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Scanning... $progressPercent%",
                color = ScanTextColor,
                fontSize = if (isTablet) 18.sp else 15.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isTablet) 6.dp else 5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(ProgressTrack)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressAnim)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFFCC00),
                                    Color(0xFFFF9900)
                                )
                            )
                        )
                )
            }

            if (scanProgress >= 1.0f) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isTablet) 58.dp else 52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFAA00),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Complete Scan",
                        fontSize = if (isTablet) 18.sp else 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawDashedOval(
    left      : Float,
    top       : Float,
    ovalW     : Float,
    ovalH     : Float,
    color     : Color,
    dashPhase : Float
) {
    val path = Path().apply {
        addOval(
            Rect(
                left   = left,
                top    = top,
                right  = left + ovalW,
                bottom = top  + ovalH
            )
        )
    }

    drawPath(
        path   = path,
        color  = color,
        style  = Stroke(
            width      = 3.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(14.dp.toPx(), 7.dp.toPx()),
                phase     = dashPhase.dp.toPx()
            ),
            cap        = StrokeCap.Round
        )
    )
}

private fun DrawScope.drawFaceMesh(
    left     : Float,
    top      : Float,
    ovalW    : Float,
    ovalH    : Float,
    alpha    : Float,
    dotAlpha : Float
) {
    fun pt(i: Int): Offset {
        val p = landmarkPoints[i]
        return Offset(left + p.x * ovalW, top + p.y * ovalH)
    }

    meshEdges.forEach { (a, b) ->
        if (a < landmarkPoints.size && b < landmarkPoints.size) {
            drawLine(
                color       = MeshLineColor.copy(alpha = alpha * 0.70f),
                start       = pt(a),
                end         = pt(b),
                strokeWidth = 1.2.dp.toPx(),
                cap         = StrokeCap.Round
            )
        }
    }

    landmarkPoints.forEachIndexed { i, _ ->
        val pos = pt(i)
        drawCircle(
            color  = MeshDotGlowColor.copy(alpha = dotAlpha * 0.55f),
            radius = 5.dp.toPx(),
            center = pos
        )
        drawCircle(
            color  = MeshDotColor.copy(alpha = dotAlpha),
            radius = 2.2.dp.toPx(),
            center = pos
        )
    }
}

@Preview(
    showBackground = true,
    widthDp        = 1456,
    heightDp       = 816,
    name           = "Face Scanning Screen – Tablet Landscape"
)
@Composable
fun FaceScanningScreenPreview() {
    PixieLookTheme {
        FaceScanningScreen(
            scanProgress = 1.0f,
            onComplete   = {}
        )
    }
}
