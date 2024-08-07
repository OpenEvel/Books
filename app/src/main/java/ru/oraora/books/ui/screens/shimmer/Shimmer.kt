package ru.oraora.books.ui.screens.shimmer

import android.content.res.Configuration
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.oraora.books.ui.screens.osearch.OSearchBarDefaults
import ru.oraora.books.ui.theme.ShimmerColorShades


@Composable
fun Shimmer(
    backColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
    modifier: Modifier,
) {
    val transition = rememberInfiniteTransition(label = "")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val brush = Brush.linearGradient(
        colorStops = ShimmerColorShades,
        start = Offset(0f, 0f),
        end = Offset(translateAnim, translateAnim)
    )

    Spacer(
        modifier = modifier
            .background(backColor)
            .background(brush = brush)
    )
}

@Preview
@Composable
fun ShimmerPreview() {
    Shimmer(
        backColor = Color.Gray,
        modifier = Modifier.size(50.dp)
    )
}
