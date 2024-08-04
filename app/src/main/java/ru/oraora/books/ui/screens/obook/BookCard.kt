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
import ru.oraora.books.ui.theme.BookCardShades

@Composable
fun NetworkImage(
    imageLink: String?,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    @SuppressLint("ModifierParameter") onFinishModifier: Modifier = Modifier,
    onLoad: @Composable (() -> Unit)? = null,
    onError: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(imageLink)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    ) {


        val finishModifier = Modifier
            .fillMaxSize()
            .then(
                if (painter.state !is AsyncImagePainter.State.Loading) {
                    onFinishModifier
                } else {
                    Modifier
                }
            )

        if (painter.state is AsyncImagePainter.State.Loading) {
            onLoad?.let { it() }
        } else {
            if (painter.state is AsyncImagePainter.State.Error) {
                Box(modifier = finishModifier) {
                    onError?.let { it() }
                }
            } else {
                SubcomposeAsyncImageContent(modifier = finishModifier)
            }
            content?.let { it() }
        }
    }
}


@Composable
fun BookImage(
    imageBookLink: String?,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    @SuppressLint("ModifierParameter") onFinishModifier: Modifier = Modifier,
    content: @Composable (() -> Unit)? = null,
) {
    NetworkImage(
        imageLink = imageBookLink,
        modifier = modifier,
        onFinishModifier = onFinishModifier,
        onLoad = {
            Shimmer(modifier = Modifier.fillMaxSize())
        },
        onError = {
            Image(
                painter = painterResource(R.drawable.ic_broken_image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )
        },
        content = content
    )
}

@Composable
fun BookCardWithBookmark(
    book: Book,
    isBookmarked: Boolean,
    onAddFavorite: (Book) -> Unit = {},
    onRemoveFavorite: (String) -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    @SuppressLint("ModifierParameter") onFinishModifier: Modifier = Modifier,
) {
    BookImage(
        imageBookLink = book.imageLink,
        modifier = modifier,
        onFinishModifier = onFinishModifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        start = Offset(0f, Float.POSITIVE_INFINITY),
                        end = Offset(Float.POSITIVE_INFINITY, 0f),
                        colorStops = BookCardShades,
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
