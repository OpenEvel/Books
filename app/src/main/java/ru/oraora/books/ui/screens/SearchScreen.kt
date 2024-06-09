package ru.oraora.books.ui.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.oraora.books.R
import ru.oraora.books.data.models.Book
import ru.oraora.books.ui.screens.obook.BookCard
import ru.oraora.books.ui.screens.osearch.OSearchBarDefaults
import ru.oraora.books.ui.screens.osearch.TopSearchBar
import ru.oraora.books.viewmodel.BookUiState
import ru.oraora.books.viewmodel.BookViewModel
import ru.oraora.books.viewmodel.Routes
import ru.oraora.books.viewmodel.SearchState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    bookViewModel: BookViewModel,
    uiState: BookUiState,
    navController: NavHostController,
    scrollState: LazyGridState = rememberLazyGridState(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val scrollScope = rememberCoroutineScope()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.searchState is SearchState.Refreshing,
        onRefresh = bookViewModel::refreshBooks
    )

    Box(
        modifier = modifier.pullRefresh(pullRefreshState)
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
                onSearch = {
                    scrollScope.launch { scrollState.scrollToItem(0, 0) }
                    bookViewModel.loadBooks()
                },
                active = uiState.isSearchActive,
                onActiveChange = bookViewModel::onSearchActiveChange,
                animationProgress = OSearchBarDefaults.animationProgress(active = uiState.isSearchActive),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = { Icon(Icons.Default.Cancel, contentDescription = "Cancel icon") },
                searchHistory = bookViewModel.searchHistory,
                addHistory = bookViewModel::addHistory,
                removeHistory = bookViewModel::removeHistory,
                clearHistory = bookViewModel::clearHistory,
                scrollState = scrollState,
            )
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (uiState.searchState) {
                is SearchState.FirstEnter -> FirstEnterFrame()
                is SearchState.Loading -> LoadingFrame()
                is SearchState.Error -> ErrorFrame(retryAction = bookViewModel::loadBooks)
                else -> {
                    SearchBookGrid(
                        books = bookViewModel.searchingBooks,
                        columnsCount = uiState.searchColumnsCount,
                        onBookSelect = { book ->
                            bookViewModel.changeSelectedBook(book)
                            navController.navigate(Routes.BOOK_INFO) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        scrollState = scrollState,
                        favoriteBooks = bookViewModel.favoriteBooks,
                        onAddFavorite = bookViewModel::addFavorite,
                        onRemoveFavorite = bookViewModel::removeFavorite,
                        topHeight = OSearchBarDefaults.topHeight + WindowInsets.statusBars.asPaddingValues()
                            .calculateTopPadding()
                    )
                    PullRefreshIndicator(
                        refreshing = uiState.searchState is SearchState.Refreshing,
                        state = pullRefreshState,
                        modifier = Modifier
                            .padding(top = OSearchBarDefaults.topHeight + WindowInsets.statusBars.asPaddingValues()
                                .calculateTopPadding())
                            .align(Alignment.TopCenter)
                            .zIndex(2f),
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


@Composable
fun FirstEnterFrame() {

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


@Composable
fun LoadingFrame() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(120.dp)
        )
    }
}

@Composable
fun ErrorFrame(
    retryAction: () -> Unit,
) {
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

@Composable
fun SearchBookGrid(
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

            if (favoriteBooks.isNotEmpty()) {
                items(columnsCount) {
                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )
                }
            }

        }
    }
}

