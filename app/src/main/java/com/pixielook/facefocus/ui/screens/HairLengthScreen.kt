package com.pixielook.facefocus.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixielook.facefocus.R
import com.pixielook.facefocus.ui.theme.PixieLookTheme

// ─── Colors ──────────────────────────────────────────────────────────────────
private val ScreenBackground = Color(0xFF050505)
private val CardBackground   = Color(0xFF0D0D0F)
private val MainCardBg       = Color(0xFF080808)

@Composable
fun HairLengthScreen(
    onNext: () -> Unit = {}
) {
    val configuration   = LocalConfiguration.current
    val screenWidth     = configuration.screenWidthDp.dp
    val isTablet        = screenWidth > 900.dp

    var selectedOption by remember { mutableStateOf("Chin to Shoulder Length") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(
                horizontal = if (isTablet) 34.dp else 20.dp,
                vertical   = if (isTablet) 26.dp else 16.dp
            )
    ) {

        // ── OUTER CARD ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(30.dp))
                .background(MainCardBg)
                .border(
                    width  = 1.3.dp,
                    brush  = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF4DA6).copy(alpha = 0.45f),
                            Color(0xFF6E8DFF).copy(alpha = 0.45f)
                        )
                    ),
                    shape  = RoundedCornerShape(30.dp)
                )
                .padding(
                    horizontal = if (isTablet) 34.dp else 20.dp,
                    vertical   = if (isTablet) 28.dp else 20.dp
                )
        ) {

            Row(modifier = Modifier.fillMaxSize()) {

                // ── LEFT: IMAGE ───────────────────────────────────────────────
                Box(
                    modifier        = Modifier
                        .weight(0.42f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {

                    // Outer glow — blurred gradient behind the frame
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.82f)
                            .fillMaxWidth(0.82f)
                            .blur(22.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFF4DB8),
                                        Color(0xFF4D8DFF)
                                    )
                                ),
                                shape = RoundedCornerShape(26.dp)
                            )
                    )

                    // Frame: layered — dark bg → gradient border stroke → image inside
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.82f)
                            .fillMaxWidth(0.82f)
                    ) {
                        // 1. Dark background of the frame
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(26.dp))
                                .background(Color(0xFF0A0A0C))
                        )

                        // 2. Gradient border stroke only — drawn on top
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    width = 2.6.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFFF4FC3),
                                            Color(0xFF6B4DFF),
                                            Color(0xFF5A8DFF)
                                        )
                                    ),
                                    shape = RoundedCornerShape(26.dp)
                                )
                        )

                        // 3. Image clipped inside with padding so it never bleeds over border
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                                .clip(RoundedCornerShape(24.dp))
                        ) {
                            Image(
                                painter            = painterResource(id = R.drawable.hair_length),
                                contentDescription = "Hair Length",
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                // ── VERTICAL DIVIDER ─────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Color.White.copy(alpha = 0.08f))
                )

                Spacer(modifier = Modifier.width(34.dp))

                // ── RIGHT: CONTENT ────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight()
                ) {

                    Spacer(modifier = Modifier.height(4.dp))

                    // Progress indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(72.dp)
                                .height(5.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFF629B), Color(0xFFFF8B73))
                                    )
                                )
                        )
                        repeat(2) {
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.18f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Title
                    Text(
                        text       = "Your Hair Length",
                        color      = Color.White,
                        fontSize   = if (isTablet) 36.sp else 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 40.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Subtitle
                    Text(
                        text       = "Not all styles work for all hair types! Let us know what we're working with",
                        color      = Color.White.copy(alpha = 0.62f),
                        fontSize   = if (isTablet) 17.sp else 14.sp,
                        lineHeight = if (isTablet) 28.sp else 22.sp,
                        fontWeight = FontWeight.Medium,
                        modifier   = Modifier.fillMaxWidth(0.86f)
                    )

                    Spacer(modifier = Modifier.height(46.dp))

                    // ── OPTIONS 2×2 GRID ──────────────────────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {

                        Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {

                            HairLengthOption(
                                title    = "Chin to Shoulder Length",
                                colors   = listOf(Color(0xFFFF4E9A), Color(0xFF9B59FF)),
                                selected = selectedOption == "Chin to Shoulder Length",
                                modifier = Modifier.weight(1f)
                            ) { selectedOption = "Chin to Shoulder Length" }

                            HairLengthOption(
                                title    = "Collarbone to Mid-Back Length",
                                colors   = listOf(Color(0xFF8F59FF), Color(0xFFFF972D)),
                                selected = selectedOption == "Collarbone to Mid-Back Length",
                                modifier = Modifier.weight(1f)
                            ) { selectedOption = "Collarbone to Mid-Back Length" }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {

                            HairLengthOption(
                                title    = "Knee to Ankle Length",
                                colors   = listOf(Color(0xFFFF4B91), Color(0xFFFFA640)),
                                selected = selectedOption == "Knee to Ankle Length",
                                modifier = Modifier.weight(1f)
                            ) { selectedOption = "Knee to Ankle Length" }

                            HairLengthOption(
                                title    = "Waist Length",
                                colors   = listOf(Color(0xFF5D8CFF), Color(0xFF20E1FF)),
                                selected = selectedOption == "Waist Length",
                                modifier = Modifier.weight(1f)
                            ) { selectedOption = "Waist Length" }
                        }
                    }
                }
            }

            // ── NEXT BUTTON (top-right) ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 30.dp, end = 18.dp)
            ) {
                // Blue glow
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .blur(18.dp)
                        .background(Color(0xFF325CFF), CircleShape)
                )

                Surface(
                    onClick   = onNext,
                    shape     = CircleShape,
                    color     = Color.Transparent,
                    modifier  = Modifier
                        .size(72.dp)
                        .border(
                            width  = 1.5.dp,
                            brush  = Brush.radialGradient(
                                colors = listOf(Color(0xFF5378FF), Color(0xFF2040D6))
                            ),
                            shape  = CircleShape
                        )
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF3F68FF), Color(0xFF1D34D1))
                            ),
                            shape = CircleShape
                        )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector        = Icons.Rounded.KeyboardArrowRight,
                            contentDescription = "Next",
                            tint               = Color.White,
                            modifier           = Modifier.size(34.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Single Option Card ───────────────────────────────────────────────────────
@Composable
fun HairLengthOption(
    title    : String,
    colors   : List<Color>,
    selected : Boolean,
    modifier : Modifier = Modifier,
    onClick  : () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Full gradient — used for border & glow
    val gradientBrush = Brush.horizontalGradient(colors)

    // Selected inner fill — vivid gradient tint
    val selectedFill = Brush.horizontalGradient(
        listOf(colors[0].copy(alpha = 0.38f), colors[1].copy(alpha = 0.28f))
    )

    // Unselected inner fill — barely visible ghost tint
    val unselectedFill = Brush.horizontalGradient(
        listOf(colors[0].copy(alpha = 0.07f), colors[1].copy(alpha = 0.05f))
    )

    // Dimmed border for unselected
    val dimBorder = Brush.horizontalGradient(
        listOf(colors[0].copy(alpha = 0.50f), colors[1].copy(alpha = 0.50f))
    )

    Box(modifier = modifier.height(118.dp)) {

        // ── Outer glow (spreads wider & brighter on selection) ────────────
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(if (selected) 24.dp else 10.dp)
                .graphicsLayer { alpha = if (selected) 1f else 0.30f }
                .background(gradientBrush, RoundedCornerShape(24.dp))
        )

        // ── Card shell: clipped, dark base ───────────────────────────────
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .clickable(
                    interactionSource = interactionSource,
                    indication        = null,
                    onClick           = onClick
                )
        ) {

            // Gradient colour fill inside the card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (selected) selectedFill else unselectedFill)
            )

            // Top sheen — brighter highlight on selected
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (selected) 0.12f else 0.03f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Border drawn last so it sits on top of the fill
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = if (selected) 2.4.dp else 1.5.dp,
                        brush = if (selected) gradientBrush else dimBorder,
                        shape = RoundedCornerShape(24.dp)
                    )
            )

            // Label
            Text(
                text       = title,
                color      = if (selected) Color.White else Color.White.copy(alpha = 0.72f),
                fontSize   = 20.sp,
                lineHeight = 28.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                textAlign  = androidx.compose.ui.text.style.TextAlign.Center,
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
    name           = "Tablet Landscape"
)
@Composable
fun HairLengthTabletPreview() {
    PixieLookTheme {
        HairLengthScreen(onNext = {})
    }
}