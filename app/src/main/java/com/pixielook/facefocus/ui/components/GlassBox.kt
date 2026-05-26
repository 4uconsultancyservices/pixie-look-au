package com.pixielook.facefocus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixielook.facefocus.ui.theme.BorderGlass
import com.pixielook.facefocus.ui.theme.SurfaceGlass

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 16,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SurfaceGlass,
                        SurfaceGlass.copy(alpha = 0.1f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BorderGlass,
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(cornerRadius.dp)
            )
            .blur(20.dp), // Note: Hardware acceleration must be enabled
        content = content
    )
}
