package dev.daniza.draflix.ui.screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoadingItemRectangle(
    showShimmer: Boolean,
    modifier: Modifier
) {
    AnimatedVisibility(visible = showShimmer, modifier = modifier) {
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(24.dp))
                .background(color = Color.LightGray)
                .height(200.dp)
                .fillMaxWidth()
                .shimmerLoadingAnimation()

        )
    }
}

@Composable
fun LoadingItemSquare(
    showShimmer: Boolean,
) {
    AnimatedVisibility(visible = showShimmer) {
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(24.dp))
                .background(color = Color.LightGray)
                .height(100.dp)
                .shimmerLoadingAnimation()

        )
    }
}

@Composable
fun LoadingRectangleLineLong(
    showShimmer: Boolean,
) {
    AnimatedVisibility(visible = showShimmer) {
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = Color.LightGray)
                .size(height = 100.dp, width = 200.dp)
                .shimmerLoadingAnimation()

        )
    }
}

fun Modifier.shimmerLoadingAnimation(): Modifier {
    return composed {

        val shimmerColors = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 1.0f),
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 0.3f),
        )

        this.background(
            brush = Brush.linearGradient(
                colors = shimmerColors,
                start = Offset(x = 100f, y = 0.0f),
                end = Offset(x = 400f, y = 270f),
            ),
        )
    }
}