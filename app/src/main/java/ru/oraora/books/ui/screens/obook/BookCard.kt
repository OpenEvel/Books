package ru.oraora.books.ui.screens.obook

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import ru.oraora.books.R
import ru.oraora.books.data.models.Book
import ru.oraora.books.ui.screens.shimmer.Shimmer
import androidx.compose.runtime.remember

@Composable
fun BookImage(
    imageLink: String?,
    modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(imageLink)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        loading = {
            Shimmer(modifier = Modifier.fillMaxSize())
        },
        error = {
            Image(
                painter = painterResource(R.drawable.ic_broken_image),
                contentDescription = null
            )
        },
        modifier = modifier,
    )
}

@Composable
fun BookCard(
    book: Book,
    isBookmarked: Boolean,
    onAddFavorite: (Book) -> Unit = {},
    onRemoveFavorite: (String) -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(book.imageLink)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    ) {

        if (painter.state is AsyncImagePainter.State.Loading) {
            Shimmer(modifier = Modifier.fillMaxSize())
        } else {
            if (painter.state is AsyncImagePainter.State.Error) {
                Image(
                    painter = painterResource(R.drawable.ic_broken_image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                SubcomposeAsyncImageContent()
            }

            val colorStops = arrayOf(
                0.0f to Color.Transparent,
                0.6f to Color.Transparent,
                0.7f to Color.Black.copy(0.02f),
                0.8f to Color.Black.copy(0.06f),
                1f to Color.Black.copy(0.1f)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            start = Offset(0f, Float.POSITIVE_INFINITY),
                            end = Offset(Float.POSITIVE_INFINITY, 0f),
                            colorStops = colorStops,
                        )
                    ),
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(28.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (isBookmarked) {
                                onRemoveFavorite(book.id)
                            } else {
                                onAddFavorite(book)
                            }
                        }
                )
            }
        }
    }
}
