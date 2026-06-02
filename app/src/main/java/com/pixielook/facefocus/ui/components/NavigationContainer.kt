package com.pixielook.facefocus.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> NavigationContainer(
    targetState: T,
    modifier: Modifier = Modifier,
    isBack: Boolean = false,
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            if (isBack) {
                // Reverse slide: in from left, out to right
                (slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(500, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(500))) with
                (slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(500, easing = FastOutSlowInEasing)) + fadeOut(animationSpec = tween(500)))
            } else {
                // Forward slide: in from right, out to left
                (slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(500, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(500))) with
                (slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(500, easing = FastOutSlowInEasing)) + fadeOut(animationSpec = tween(500)))
            }
        },
        modifier = modifier
    ) { state ->
        var isLoading by remember(state) { mutableStateOf(true) }

        LaunchedEffect(state) {
            delay(500) // Default 0.5 sec skeleton delay
            isLoading = false
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                SkeletonScreen()
            } else {
                content(state)
            }
        }
    }
}

@Composable
fun SkeletonScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header mock
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Large content mock
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .shimmerEffect()
        )
        
        // Bottom controls mock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.05f),
        Color.White.copy(alpha = 0.15f),
        Color.White.copy(alpha = 0.05f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )
    background(brush)
}
