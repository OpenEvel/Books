package ru.oraora.books.ui.screens.osearch


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.oraora.books.R
import ru.oraora.books.data.models.Book
import ru.oraora.books.viewmodel.BookUiState
import ru.oraora.books.viewmodel.BookViewModel
import ru.oraora.books.viewmodel.SearchFrame

@Composable
fun SearchScreen(
    bookViewModel: BookViewModel,
    uiState: BookUiState,
    modifier: Modifier = Modifier,
    scrollState: LazyGridState = rememberLazyGridState(),
) {
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            TopSearchBar(
                firstLine = {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = LocalTextStyle.current.color.takeOrElse {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                    )
                },
                query = uiState.query,
                lastQuery = uiState.lastQuery,
                onQueryChange = bookViewModel::onQueryChange,
                onSearch = bookViewModel::searchBooks,
                active = uiState.isSearchActive,
                onActiveChange = bookViewModel::onSearchActiveChange,
                animationProgress = OSearchBarDefaults.animationProgress(active = uiState.isSearchActive),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = { Icon(Icons.Default.Cancel, contentDescription = "Cancel icon") },
                searchHistory = bookViewModel.searchHistory,
                addHistory = bookViewModel::addHistory,
                deletedHistory = bookViewModel.deletedSearchHistory,
                removeHistory = { bookViewModel.removeHistory(it) },
                realRemoveHistory =  bookViewModel::realRemoveHistory,
                clearHistory = bookViewModel::clearHistory,
                scrollState = scrollState,
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            val topHeight =
                OSearchBarDefaults.topHeight + WindowInsets
                    .statusBars.asPaddingValues().calculateTopPadding()

            val cellWidth = LocalConfiguration.current.screenWidthDp.dp / uiState.searchColumnsCount
            val cellHeight = 1.5 * cellWidth

            LazyVerticalGrid(
                state = scrollState,
                columns = GridCells.Fixed(uiState.searchColumnsCount),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()

            ) {

                items(uiState.searchColumnsCount) {
                    Spacer(
                        modifier = Modifier.height(topHeight)
                    )
                }

                when (uiState.searchFrame) {
                    is SearchFrame.FirstEnter -> FirstEnterFrame()
                    is SearchFrame.Loading -> LoadingFrame()
                    is SearchFrame.Error -> ErrorFrame(
                        retryAction = { bookViewModel.searchBooks() }
                    )

                    is SearchFrame.Success -> BooksList(
                        books = uiState.searchFrame.books,
                        cellWidth = cellWidth,
                        cellHeight = cellHeight,
                    )
                }
            }
        }
    }
}


@Composable
@Stable
fun PaddingValues.copy(
    start: Dp? = null,
    top: Dp? = null,
    end: Dp? = null,
    bottom: Dp? = null
): PaddingValues {
    return PaddingValues(
        start = start ?: this.calculateStartPadding(LocalLayoutDirection.current),
        top = top ?: this.calculateTopPadding(),
        end = end ?: this.calculateEndPadding(LocalLayoutDirection.current),
        bottom = bottom ?: this.calculateBottomPadding()
    )
}


fun LazyGridScope.FirstEnterFrame() {
    item {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Book,
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                tint = MaterialTheme.colorScheme.inversePrimary,
            )
        }
    }
}


fun LazyGridScope.LoadingFrame() {
    item {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(200.dp),
                painter = painterResource(R.drawable.loading_img),
                contentDescription = stringResource(R.string.loading)
            )
        }

    }
}


fun LazyGridScope.ErrorFrame(
    retryAction: () -> Unit,
) {
    item {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_connection_error),
                contentDescription = ""
            )
            Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
            Button(onClick = retryAction) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}


fun LazyGridScope.BooksList(
    books: List<Book>,
    cellWidth: Dp,
    cellHeight: Dp,
) {
    items(books) { book ->
        BookCard(
            book = book,
            cellWidth = cellWidth,
            cellHeight = cellHeight,
        )
    }
}

@Composable
fun BookCard(
    book: Book,
    cellWidth: Dp,
    cellHeight: Dp,
) {
    AsyncImage(
        modifier = Modifier
            .width(cellWidth)
            .height(cellHeight),
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