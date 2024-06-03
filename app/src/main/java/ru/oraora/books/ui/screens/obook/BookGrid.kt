package ru.oraora.books.ui.screens.obook

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.oraora.books.R
import ru.oraora.books.data.models.Book
import ru.oraora.books.ui.navigation.myNavigate
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

        val cellWidth = LocalConfiguration.current.screenWidthDp.dp / columnsCount
        val cellHeight = 1.5 * cellWidth

        LazyVerticalGrid(
            state = scrollState,
            columns = GridCells.Fixed(columnsCount),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()

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
                    modifier = Modifier
                        .width(cellWidth)
                        .height(cellHeight)
                        .clickable {
                            onBookSelect(book)
                        }
                )
            }
        }
    }
}

@Composable
fun BookCard(
    book: Book,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(book.imageLink)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        error = painterResource(id = R.drawable.ic_broken_image),
        placeholder = painterResource(id = R.drawable.loading_img)
    )
}