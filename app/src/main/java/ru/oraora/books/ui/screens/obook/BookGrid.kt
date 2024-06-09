package ru.oraora.books.ui.screens.obook

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import ru.oraora.books.R
import ru.oraora.books.data.models.Book
import ru.oraora.books.ui.navigation.myNavigate
import ru.oraora.books.ui.screens.shimmer.Shimmer
import ru.oraora.books.ui.theme.BooksTheme
import ru.oraora.books.ui.theme.ShimmerColorShades
import ru.oraora.books.viewmodel.Routes

import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.composed
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BookGrid(
    books: List<Book>,
    columnsCount: Int,
    onBookSelect: (Book) -> Unit,
    favoriteBooks: List<Book>,
    onAddFavorite: (Book) -> Unit = {},
    onRemoveFavorite: (String) -> Unit = {},
    scrollState: LazyGridState = rememberLazyGridState(),
    topHeight: Dp = 0.dp,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {

        val cellWidth =
            (LocalConfiguration.current.screenWidthDp.dp - 12.dp - 8.dp - 12.dp) / columnsCount
        val cellHeight = 1.5 * cellWidth

        LazyVerticalGrid(
            state = scrollState,
            columns = GridCells.Fixed(columnsCount),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)

        ) {

            if (topHeight > 0.dp) {
                items(columnsCount) {
                    Spacer(
                        modifier = Modifier.height(topHeight)
                    )
                }
            }

            items(books) { book ->
                BookCard(
                    book = book,
                    isBookmarked = favoriteBooks.any { it.id == book.id },
                    onAddFavorite = onAddFavorite,
                    onRemoveFavorite = onRemoveFavorite,
                    modifier = Modifier
                        .size(cellWidth, cellHeight)
                        .clickable {
                            onBookSelect(book)
                        }
                )
            }

            items(columnsCount) {
                Spacer(
                    modifier = Modifier.height(5.dp)
                )
            }

        }
    }
}

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
    isBookmarked: Boolean = false,
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
