package com.pixielook.facefocus.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pixielook.facefocus.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    onBack: () -> Unit,
    onNavigateFaceFitness: () -> Unit,
    onNavigateRewards: () -> Unit,
    onNavigateShop: () -> Unit,
    onNavigateFashionNews: () -> Unit,
    onNavigateStudy: () -> Unit,
    onNavigateHairstyles: () -> Unit,
    onNavigateVirtualTryOn: () -> Unit,
    onNavigateAccount: () -> Unit,
    onNavigateMessage: () -> Unit // Kept for compatibility if needed, but slider handles it now
) {
    val pagerState = rememberPagerState()

    HorizontalPager(
        pageCount = 2,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> MainContent(
                onBack = onBack,
                onNavigateFaceFitness = onNavigateFaceFitness,
                onNavigateRewards = onNavigateRewards,
                onNavigateShop = onNavigateShop,
                onNavigateFashionNews = onNavigateFashionNews,
                onNavigateStudy = onNavigateStudy,
                onNavigateHairstyles = onNavigateHairstyles,
                onNavigateVirtualTryOn = onNavigateVirtualTryOn,
                onNavigateAccount = onNavigateAccount
            )
            1 -> MessageContent(
                onBack = { /* Could use pagerState.animateScrollToPage(0) */ }
            )
        }
    }
}

@Composable
fun MainContent(
    onBack: () -> Unit,
    onNavigateFaceFitness: () -> Unit,
    onNavigateRewards: () -> Unit,
    onNavigateShop: () -> Unit,
    onNavigateFashionNews: () -> Unit,
    onNavigateStudy: () -> Unit,
    onNavigateHairstyles: () -> Unit,
    onNavigateVirtualTryOn: () -> Unit,
    onNavigateAccount: () -> Unit
) {
    val imageAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        imageAlpha.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.main_screen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().alpha(imageAlpha.value),
            contentScale = ContentScale.Fit
        )

        // Navigation Overlay
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar Navigation (Left side, approx 22% width)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.22f)
            ) {
                // Top Profile/Logout Area
                NavHotspot(onClick = onNavigateAccount, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.18f))

                // Menu Items
                val menuItems = listOf(
                    onNavigateFaceFitness,
                    onNavigateRewards,
                    onNavigateShop,
                    onNavigateFashionNews,
                    onNavigateStudy,
                    onNavigateHairstyles,
                    onNavigateVirtualTryOn
                )

                menuItems.forEach { action ->
                    NavHotspot(onClick = action, modifier = Modifier.fillMaxWidth().weight(1f))
                }
                
                // Bottom Padding
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            }

            // Main Content Area (Right side)
            Box(modifier = Modifier.fillMaxSize()) {
                // Shop Now button area
                NavHotspot(
                    onClick = onNavigateShop,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 60.dp, end = 40.dp)
                        .size(width = 180.dp, height = 60.dp)
                )
                
                // Catch-all tap for general next
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }

        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}

@Composable
fun MessageContent(
    onBack: () -> Unit
) {
    val imageAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        imageAlpha.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.message_screen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().alpha(imageAlpha.value),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun NavHotspot(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.92f else 1f, label = "scale")
    val alpha by animateFloatAsState(if (isPressed) 0.2f else 0f, label = "alpha")

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(Color.White.copy(alpha = alpha))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    )
}
