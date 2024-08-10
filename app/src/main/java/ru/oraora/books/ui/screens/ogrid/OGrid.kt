package ru.oraora.books.ui.screens.ogrid

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun ShadowLine(
    isShow: Boolean,
    modifier: Modifier = Modifier,
    heightLine: Dp = 8.dp
) {
    AnimatedVisibility(
        visible = isShow,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth()
                .height(heightLine)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent),
                        startY = 0f, // Начинаем градиент сверху
                        endY = Float.POSITIVE_INFINITY // Заканчиваем градиент снизу
                    )
                )
        )
    }
}