package com.pixielook.facefocus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import com.pixielook.facefocus.R
import com.pixielook.facefocus.ui.theme.PixieLookTheme

// ─── Screen colors ────────────────────────────────────────────────────────────
private val ScreenBg = Color(0xFF090909)

// ─── Card accent colors (border + gradient tint) ──────────────────────────────
// Row 1
private val AccentStudent   = Color(0xFFFF3399)   // hot pink / magenta
private val AccentFormal    = Color(0xFFCCA800)   // gold / amber
private val AccentTravel    = Color(0xFF00BBFF)   // cyan-blue

// Row 2
private val AccentRetired   = Color(0xFFFF3399)   // hot pink / magenta (same as student)
private val AccentStayHome  = Color(0xFF00AAFF)   // blue-cyan
private val AccentOthers    = Color(0xFF1188CC)   // muted blue

// ─── Card fill base colors (dark tinted bg matching photo mood) ───────────────
private val FillStudent     = Color(0xFF1A0820)   // dark purple-pink
private val FillFormal      = Color(0xFF1A1200)   // dark olive/gold
private val FillTravel      = Color(0xFF051422)   // dark navy-cyan
private val FillRetired     = Color(0xFF1A0820)   // dark purple-pink
private val FillStayHome    = Color(0xFF051422)   // dark navy
private val FillOthers      = Color(0xFF060E18)   // very dark blue

// ─── Next button ─────────────────────────────────────────────────────────────
private val NextBtnFill      = Brush.radialGradient(listOf(Color(0xFF2A80FF), Color(0xFF0A4FCC)))
private val NextBtnGlowColor = Color(0xFF1A6FFF)

// ─── Data class ──────────────────────────────────────────────────────────────
data class LifestyleOption(
    val id          : String,
    val label       : String,
    val imageResId  : Int?,          // null = "Others" (no photo)
    val accentColor : Color,
    val fillColor   : Color,
    // gradient direction: photo is on LEFT side, color dissolves RIGHT→CENTER
    val photoOnLeft : Boolean = true
)

@Composable
fun LifestyleScreen(
    isFeminine : Boolean = false,
    onNext     : () -> Unit = {},
    onBack     : () -> Unit = {},
    onHome     : () -> Unit = {}
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val isTablet    = screenWidth > 900.dp

    var selected by remember { mutableStateOf("student") }

    // ── Build options using the correct drawable set ──────────────────────────
    // drawable naming convention:
    //   feminine_student.png, feminine_formal.png, feminine_travel.png,
    //   feminine_retired.png, feminine_stayhome.png
    //   masculine_student.png, masculine_formal.png, masculine_travel.png,
    //   masculine_retired.png, masculine_stayhome.png
    val prefix = if (isFeminine) "feminine" else "masculine"

    val options = listOf(
        // Row 1
        LifestyleOption(
            id          = "student",
            label       = "I am a student",
            imageResId  = getDrawableId("${prefix}_student"),
            accentColor = AccentStudent,
            fillColor   = FillStudent,
            photoOnLeft = true
        ),
        LifestyleOption(
            id          = "formal",
            label       = "I work in a\nformal workspace",
            imageResId  = getDrawableId("${prefix}_formal"),
            accentColor = AccentFormal,
            fillColor   = FillFormal,
            photoOnLeft = true
        ),
        LifestyleOption(
            id          = "travel",
            label       = "I frequently\ntravel",
            imageResId  = getDrawableId("${prefix}_travel"),
            accentColor = AccentTravel,
            fillColor   = FillTravel,
            photoOnLeft = true
        ),
        // Row 2
        LifestyleOption(
            id          = "retired",
            label       = "I am retired",
            imageResId  = getDrawableId("${prefix}_retired"),
            accentColor = AccentRetired,
            fillColor   = FillRetired,
            photoOnLeft = true
        ),
        LifestyleOption(
            id          = "stayhome",
            label       = "I am a stay-at-\nhome parent",
            imageResId  = getDrawableId("${prefix}_stayhome"),
            accentColor = AccentStayHome,
            fillColor   = FillStayHome,
            photoOnLeft = true
        ),
        LifestyleOption(
            id          = "others",
            label       = "Others",
            imageResId  = null,           // silhouette icon
            accentColor = AccentOthers,
            fillColor   = FillOthers,
            photoOnLeft = false
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start  = if (isTablet) 52.dp else 20.dp,
                    end    = if (isTablet) 52.dp else 20.dp,
                    top    = if (isTablet) 28.dp else 16.dp,
                    bottom = 0.dp
                )
        ) {

            // ── PROGRESS PILLS ────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Active pill
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
                            .background(Color.White.copy(alpha = 0.22f))
                    )
                    Spacer(Modifier.width(6.dp))
                }
            }

            Spacer(Modifier.height(if (isTablet) 24.dp else 16.dp))

            // ── HEADER ROW ────────────────────────────────────────────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Left: title + underline + subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = "Your Lifestyle",
                        color      = Color.White,
                        fontSize   = if (isTablet) 40.sp else 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 44.sp
                    )
                    // Underline accent below title
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(if (isTablet) 56.dp else 44.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.60f))
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text       = "Certain hairstyles are best for certain lifestyles, so let us know yours!",
                        color      = Color.White.copy(alpha = 0.52f),
                        fontSize   = if (isTablet) 15.sp else 13.sp,
                        lineHeight = if (isTablet) 24.sp else 20.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Right: Next button — solid blue circle
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .blur(24.dp)
                            .graphicsLayer { alpha = 0.70f }
                            .background(NextBtnGlowColor, CircleShape)
                    )
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

            Spacer(Modifier.height(if (isTablet) 28.dp else 18.dp))

            // ── CARDS GRID ────────────────────────────────────────────────────
            val cardHeight: Dp = if (isTablet) 170.dp else 148.dp

            // Row 1
            Row(
                modifier              = Modifier.fillMaxWidth().height(cardHeight),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                options.subList(0, 3).forEach { opt ->
                    LifestyleCard(
                        option   = opt,
                        selected = selected == opt.id,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) { selected = opt.id }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Row 2
            Row(
                modifier              = Modifier.fillMaxWidth().height(cardHeight),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                options.subList(3, 6).forEach { opt ->
                    LifestyleCard(
                        option   = opt,
                        selected = selected == opt.id,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) { selected = opt.id }
                }
            }

            // Push bottom nav to bottom
            Spacer(Modifier.weight(1f))

            // ── BOTTOM NAV BAR ────────────────────────────────────────────────
            BottomNavBar(
                onMenu = {},
                onHome = onHome,
                onBack = onBack
            )

            Spacer(Modifier.height(if (isTablet) 16.dp else 10.dp))
        }
    }
}

// ─── Lifestyle Card ───────────────────────────────────────────────────────────
@Composable
fun LifestyleCard(
    option   : LifestyleOption,
    selected : Boolean,
    modifier : Modifier = Modifier,
    onClick  : () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val accent            = option.accentColor

    // Border: bottom-heavy neon gradient
    val borderBrush = Brush.verticalGradient(
        listOf(
            accent.copy(alpha = if (selected) 0.35f else 0.22f),
            accent.copy(alpha = if (selected) 0.60f else 0.42f),
            accent.copy(alpha = if (selected) 1.00f else 0.80f)
        )
    )

    Box(modifier = modifier) {

        // ── Glow blob below card ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.72f)
                .height(26.dp)
                .blur(if (selected) 22.dp else 14.dp)
                .graphicsLayer {
                    alpha        = if (selected) 0.90f else 0.55f
                    translationY = 10f
                }
                .background(accent.copy(alpha = 0.85f), RoundedCornerShape(50))
        )

        // ── Card body ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(18.dp))
                .background(option.fillColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication        = null,
                    onClick           = onClick
                )
        ) {

            if (option.imageResId != null) {
                // ── PHOTO CARD ────────────────────────────────────────────

                // 1. Full-bleed photo on LEFT half
                androidx.compose.foundation.Image(
                    painter            = painterResource(id = option.imageResId),
                    contentDescription = option.label,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.55f)   // photo takes left ~55%
                        .align(Alignment.CenterStart)
                )

                // 2. Horizontal fade: photo → fill color (dissolve effect)
                //    Left edge: transparent, right edge: fillColor — creates
                //    a smooth "photo disappears into color" look
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colorStops = arrayOf(
                                    0.00f to Color.Transparent,
                                    0.28f to Color.Transparent,
                                    0.50f to option.fillColor.copy(alpha = 0.55f),
                                    0.68f to option.fillColor.copy(alpha = 0.88f),
                                    0.78f to option.fillColor
                                )
                            )
                        )
                )

                // 3. Top-to-bottom dark vignette (deepens top/bottom edges)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.28f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.36f)
                                )
                            )
                        )
                )

                // 4. Label — right-aligned, centred vertically
                Text(
                    text       = option.label,
                    color      = if (selected) Color.White else Color.White.copy(alpha = 0.90f),
                    fontSize   = when {
                        option.label.length > 20 -> 18.sp
                        option.label.length > 14 -> 20.sp
                        else                     -> 22.sp
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center,
                    lineHeight = 28.sp,
                    modifier   = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxWidth(0.54f)   // right half for text
                        .padding(end = 16.dp)
                )

            } else {
                // ── OTHERS CARD (silhouette icon) ─────────────────────────

                // Subtle radial glow in center-left for the icon
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    accent.copy(alpha = 0.18f),
                                    Color.Transparent
                                ),
                                radius = 300f
                            )
                        )
                )

                Row(
                    modifier          = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Silhouette person icon with blue glow
                    Box(
                        modifier         = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Glow behind icon
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .blur(18.dp)
                                .background(accent.copy(alpha = 0.55f), CircleShape)
                        )
                        Icon(
                            imageVector        = Icons.Rounded.Person,
                            contentDescription = "Others",
                            tint               = accent.copy(alpha = 0.90f),
                            modifier           = Modifier.size(60.dp)
                        )
                    }

                    Text(
                        text       = "Others",
                        color      = Color.White.copy(alpha = if (selected) 1f else 0.88f),
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier.weight(1f)
                    )
                }
            }

            // ── Neon border on top of everything ─────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = if (selected) 2.dp else 1.5.dp,
                        brush = borderBrush,
                        shape = RoundedCornerShape(18.dp)
                    )
            )
        }
    }
}

// ─── Bottom Navigation Bar ────────────────────────────────────────────────────
@Composable
fun BottomNavBar(
    onMenu : () -> Unit = {},
    onHome : () -> Unit = {},
    onBack : () -> Unit = {}
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Menu icon
        Icon(
            imageVector        = Icons.Rounded.Menu,
            contentDescription = "Menu",
            tint               = Color.White.copy(alpha = 0.80f),
            modifier           = Modifier
                .size(28.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onMenu
                )
        )

        Spacer(Modifier.width(72.dp))

        // Home icon — amber/orange tint to match screenshot
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

        // Back arrow
        Icon(
            imageVector        =  Icons.Rounded.KeyboardArrowRight,
            contentDescription = "Back",
            tint               = Color.White.copy(alpha = 0.80f),
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

// ─── Helper: resolve drawable resource ID by name at runtime ─────────────────
// Usage: getDrawableId("masculine_student") → R.drawable.masculine_student
@Composable
fun getDrawableId(name: String): Int {
    val context = androidx.compose.ui.platform.LocalContext.current
    return context.resources.getIdentifier(name, "drawable", context.packageName)
        .takeIf { it != 0 } ?: R.drawable.feminine  // fallback
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(
    showBackground = true,
    widthDp        = 1456,
    heightDp       = 816,
    name           = "Lifestyle Screen – Tablet Landscape"
)
@Composable
fun LifestyleScreenPreview() {
    PixieLookTheme {
        LifestyleScreen(
            isFeminine = false,
            onNext     = {},
            onBack     = {},
            onHome     = {}
        )
    }
}