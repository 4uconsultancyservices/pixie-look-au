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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixielook.facefocus.R
import com.pixielook.facefocus.ui.theme.PixieLookTheme

// ─── Global colors ────────────────────────────────────────────────────────────
private val ScreenBg = Color(0xFF050505)
private val CardBg   = Color(0xFF080808)
private val OptionBg = Color(0xFF0A0A0A)

// Outer card + next-button: same rainbow sweep
private val RainbowSweep = Brush.sweepGradient(
    listOf(
        Color(0xFFFF3CAC), // pink  – start/end
        Color(0xFF784BA0), // violet
        Color(0xFF2B86C5), // blue
        Color(0xFF00CFFF), // cyan
        Color(0xFF2B86C5), // blue
        Color(0xFFFF8C00), // orange
        Color(0xFFFF3CAC), // pink  – close loop
    )
)

// ── Option card border gradients (exactly per image) ─────────────────────────
private val StraightBorder = listOf(Color(0xFFFF3CAC), Color(0xFF5B6FFF)) // pink → blue
private val WavyBorder     = listOf(Color(0xFF00E5FF), Color(0xFF3A7BFF)) // cyan → blue
private val CurlyBorder    = listOf(Color(0xFF00CFFF), Color(0xFF5B6FFF)) // cyan → blue
private val KinkyBorder    = listOf(Color(0xFFFF8C00), Color(0xFFCC44FF)) // orange → purple

// ── Bottom chip solid fills ───────────────────────────────────────────────────
private val StraightChip = Brush.horizontalGradient(listOf(Color(0xFF6B1040), Color(0xFF3A1060)))
private val WavyChip     = Brush.horizontalGradient(listOf(Color(0xFF0B2050), Color(0xFF0F2A68)))
private val CurlyChip    = Brush.horizontalGradient(listOf(Color(0xFF3A1E00), Color(0xFF4A2800)))
private val KinkyChip    = Brush.horizontalGradient(listOf(Color(0xFF1E0E50), Color(0xFF2A1570)))

@Composable
fun HairTypeScreen(
    onNext: () -> Unit = {}
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val isTablet    = screenWidth > 900.dp

    var selected by remember { mutableStateOf("Straight") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .padding(
                horizontal = if (isTablet) 30.dp else 16.dp,
                vertical   = if (isTablet) 22.dp else 14.dp
            )
    ) {
        // ── OUTER CARD ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(28.dp))
                .background(CardBg)
                .border(
                    width = 2.dp,
                    brush = RainbowSweep,
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            Row(modifier = Modifier.fillMaxSize()) {

                // ── LEFT IMAGE — full height bleed, no frame, no border ───────
                Box(
                    modifier = Modifier
                        .weight(0.38f)
                        .fillMaxHeight()
                        .clip(
                            RoundedCornerShape(
                                topStart    = 28.dp,
                                bottomStart = 28.dp,
                                topEnd      = 0.dp,
                                bottomEnd   = 0.dp
                            )
                        )
                ) {
                    Image(
                        painter            = painterResource(id = R.drawable.hair_type),
                        contentDescription = "Hair Type",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                }

                // ── RIGHT CONTENT ─────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(0.62f)
                        .fillMaxHeight()
                        .padding(
                            start  = if (isTablet) 36.dp else 20.dp,
                            end    = if (isTablet) 28.dp else 18.dp,
                            top    = if (isTablet) 22.dp else 16.dp,
                            bottom = if (isTablet) 22.dp else 16.dp
                        )
                ) {

                    // Progress pills — centred
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(72.dp).height(5.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFF629B), Color(0xFFFF8B73))
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

                    Spacer(Modifier.height(if (isTablet) 24.dp else 18.dp))

                    // Title
                    Text(
                        text       = "Your Hair Type",
                        color      = Color.White,
                        fontSize   = if (isTablet) 34.sp else 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 40.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    // Subtitle
                    Text(
                        text       = "Not all hair is made the same!  Help us cater to your natural weaves.",
                        color      = Color.White.copy(alpha = 0.58f),
                        fontSize   = if (isTablet) 15.sp else 13.sp,
                        lineHeight = if (isTablet) 24.sp else 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(if (isTablet) 24.dp else 18.dp))

                    // ── 2×2 GRID — each row equal weight, gap 14dp ────────────
                    Column(
                        modifier            = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            HairTypeOption(
                                title    = "Straight",
                                colors   = StraightBorder,
                                selected = selected == "Straight",
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            ) { selected = "Straight" }

                            HairTypeOption(
                                title    = "Wavy",
                                colors   = WavyBorder,
                                selected = selected == "Wavy",
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            ) { selected = "Wavy" }
                        }

                        Row(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            HairTypeOption(
                                title    = "Curly",
                                colors   = CurlyBorder,
                                selected = selected == "Curly",
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            ) { selected = "Curly" }

                            HairTypeOption(
                                title    = "Curly / Kinky",
                                colors   = KinkyBorder,
                                selected = selected == "Curly / Kinky",
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            ) { selected = "Curly / Kinky" }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // ── BOTTOM CHIPS — equally fill full width ─────────────────
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HairTypeChip(
                            label        = "Straight",
                            fill         = StraightChip,
                            borderColors = StraightBorder,
                            modifier     = Modifier.weight(1f)
                        ) { selected = "Straight" }

                        HairTypeChip(
                            label        = "Wavy",
                            fill         = WavyChip,
                            borderColors = WavyBorder,
                            modifier     = Modifier.weight(1f)
                        ) { selected = "Wavy" }

                        HairTypeChip(
                            label        = "Curly",
                            fill         = CurlyChip,
                            borderColors = CurlyBorder,
                            modifier     = Modifier.weight(1f)
                        ) { selected = "Curly" }

                        HairTypeChip(
                            label        = "Curly / Kinky",
                            fill         = KinkyChip,
                            borderColors = KinkyBorder,
                            modifier     = Modifier.weight(1f)
                        ) { selected = "Curly / Kinky" }
                    }
                }
            }

            // ── NEXT BUTTON — top-right, ZERO fill, rainbow border only ───────
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp, end = 18.dp)
            ) {
                // Glow blob behind button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .blur(22.dp)
                        .graphicsLayer { alpha = 0.75f }
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFFFF3CAC),
                                    Color(0xFF2B86C5),
                                    Color(0xFFFF8C00),
                                    Color(0xFFFF3CAC)
                                )
                            ),
                            CircleShape
                        )
                )

                // Button circle — transparent bg, rainbow border
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .border(
                            width = 2.5.dp,
                            brush = RainbowSweep,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        // NO .background() here — stays fully transparent
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
    }
}

// ─── Option Card ──────────────────────────────────────────────────────────────
@Composable
fun HairTypeOption(
    title    : String,
    colors   : List<Color>,
    selected : Boolean,
    modifier : Modifier = Modifier,
    onClick  : () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val fullBrush = Brush.linearGradient(colors)
    val dimBrush  = Brush.linearGradient(
        listOf(colors[0].copy(alpha = 0.50f), colors[1].copy(alpha = 0.50f))
    )

    Box(modifier = modifier) {

        // Glow — only visible when selected
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(20.dp)
                    .graphicsLayer { alpha = 0.85f }
                    .background(fullBrush, RoundedCornerShape(20.dp))
            )
        }

        // Card: pure black, border on top, NO color fill inside
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(20.dp))
                .background(OptionBg)          // always solid black — no tint
                .clickable(
                    interactionSource = interactionSource,
                    indication        = null,
                    onClick           = onClick
                )
        ) {
            // Border drawn as top layer inside the clip
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = if (selected) 2.2.dp else 1.8.dp,
                        brush = if (selected) fullBrush else dimBrush,
                        shape = RoundedCornerShape(20.dp)
                    )
            )

            // Label centred
            Text(
                text       = title,
                color      = Color.White,
                fontSize   = 22.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                textAlign  = TextAlign.Center,
                modifier   = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 12.dp)
            )
        }
    }
}

// ─── Bottom Chip ──────────────────────────────────────────────────────────────
@Composable
fun HairTypeChip(
    label        : String,
    fill         : Brush,
    borderColors : List<Color>,
    modifier     : Modifier = Modifier,
    onClick      : () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(fill)
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(borderColors),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            color      = Color.White,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center,
            maxLines   = 1
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(
    showBackground = true,
    widthDp        = 1280,
    heightDp       = 800,
    name           = "Hair Type – Tablet Landscape"
)
@Composable
fun HairTypeScreenPreview() {
    PixieLookTheme {
        HairTypeScreen(onNext = {})
    }
}