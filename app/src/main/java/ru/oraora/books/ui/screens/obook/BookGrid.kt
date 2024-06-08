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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
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
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import ru.oraora.books.R
import ru.oraora.books.data.models.Book
import ru.oraora.books.ui.navigation.myNavigate
import ru.oraora.books.ui.theme.BooksTheme
import ru.oraora.books.ui.theme.ShimmerColorShades
import ru.oraora.books.viewmodel.Routes

@Composable
fun BookGrid(
    books: List<Book>,
    columnsCount: Int,
    onBookSelect: (Book) -> Unit,
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
                    imageLink = book.imageLink,
                    modifier = Modifier
                        .width(cellWidth)
                        .height(cellHeight)
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
fun BookCard(
    imageLink: String?,
    modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        model = imageLink,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        loading = {
            val transition = rememberInfiniteTransition(label = "")
            val translateAnim by transition.animateFloat(
                initialValue = 0f,
                targetValue = 2000f,
                animationSpec = infiniteRepeatable(
                    tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                    RepeatMode.Restart
                ), label = ""
            )

            val brush = Brush.linearGradient(
                colors = ShimmerColorShades,
                start = Offset(10f, 10f),
                end = Offset(translateAnim, translateAnim)
            )

            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = brush)
            )
        },
        error = {
            Image(
                painter = painterResource(R.drawable.ic_broken_image),
                contentDescription = null
            )
        },
        modifier = modifier,
    )

//    AsyncImage(
//        modifier = modifier,
//        model = ImageRequest.Builder(context = LocalContext.current)
//            .data(imageLink)
//            .crossfade(true)
//            .build(),
//        contentDescription = null,
//        contentScale = ContentScale.Crop,
//        error = painterResource(id = R.drawable.ic_broken_image),
//        placeholder = painterResource(id = R.drawable.loading_img)
//    )
}