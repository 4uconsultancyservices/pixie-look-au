package com.pixielook.facefocus.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.pixielook.facefocus.R
import com.pixielook.facefocus.ui.theme.PixieLookTheme

@Composable
fun IntroScreen1(
    onNext: () -> Unit
) {

    // Smooth fade animation
    val imageAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        imageAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
    }

    // Root
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onNext
            ),
        contentAlignment = Alignment.Center
    ) {

        // Fullscreen image
        Image(
            painter = painterResource(id = R.drawable.intro1),
            contentDescription = "Intro 1",
            modifier = Modifier
                .fillMaxSize()
                .alpha(imageAlpha.value),
            contentScale = ContentScale.Fit
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEWS
// ─────────────────────────────────────────────────────────────────────────────

@Preview(
    name = "Phone",
    widthDp = 393,
    heightDp = 852,
    showBackground = true
)
@Composable
private fun PreviewPhone() {
    PixieLookTheme {
        IntroScreen1 {}
    }
}

@Preview(
    name = "Tablet",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true
)
@Composable
private fun PreviewTablet() {
    PixieLookTheme {
        IntroScreen1 {}
    }
}
