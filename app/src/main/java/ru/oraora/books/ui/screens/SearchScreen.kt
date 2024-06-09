package ru.oraora.books.ui.screens


import android.annotation.SuppressLint
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.oraora.books.R
import ru.oraora.books.data.models.Book
import ru.oraora.books.ui.navigation.myNavigate
import ru.oraora.books.ui.screens.obook.BookGrid
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
                deletedHistory = bookViewModel.deletedSearchHistory,
                removeHistory = bookViewModel::removeHistory,
                realRemoveHistory = bookViewModel::realRemoveHistory,
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
                    BookGrid(
                        books = bookViewModel.searchingBooks,
                        columnsCount = uiState.searchColumnsCount,
                        onBookSelect = { book ->
                            bookViewModel.changeSelectedBook(book)
                            navController.myNavigate(Routes.BOOK_INFO)
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
//        Image(
//            modifier = Modifier.size(200.dp),
//            painter = painterResource(R.drawable.loading_img),
//            contentDescription = stringResource(R.string.loading)
//        )
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
