package ru.oraora.books.ui.screens.osearch


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.oraora.books.R
import ru.oraora.books.data.models.Book
import ru.oraora.books.viewmodel.BookViewModel
import ru.oraora.books.viewmodel.SearchFrame

@Composable
fun SearchScreen(
    bookViewModel: BookViewModel,
    searchRequester: FocusRequester,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    searchHistory: MutableList<String> = rememberSaveable { mutableListOf<String>() }
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
                query = bookViewModel.query,
                onQueryChange = { bookViewModel.onQueryChange(it) },
                onSearch = { bookViewModel.getBooks() },
                active = bookViewModel.isSearchActive,
                onActiveChange = { bookViewModel.onSearchActiveChange(it) },
                searchRequester = searchRequester,
                animationProgress = OSearchBarDefaults.animationProgress(active = bookViewModel.isSearchActive),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = { Icon(Icons.Default.Cancel, contentDescription = "Cancel icon") },
                searchHistory = searchHistory,
                scrollState = scrollState,
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                item {
                    Spacer(
                        modifier = Modifier.height(
                            OSearchBarDefaults.topHeight + WindowInsets
                                .statusBars.asPaddingValues().calculateTopPadding()
                        )
                    )
                }

                when (bookViewModel.searchFrame) {
                    SearchFrame.FIRST_ENTER -> FirstEnterFrame()
                    SearchFrame.LOADING -> LoadingFrame()
                    SearchFrame.ERROR -> ErrorFrame(
                        retryAction = { bookViewModel.getBooks() }
                    )

                    SearchFrame.SUCCESS -> BooksList(books = bookViewModel.books)
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


fun LazyListScope.FirstEnterFrame() {
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


fun LazyListScope.LoadingFrame() {
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


fun LazyListScope.ErrorFrame(
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


fun LazyListScope.BooksList(
    books: List<Book>,
) {
    items(items = books) { book ->
        Text(
            text = book.title ?: "[ДАННЫЕ УДАЛЕНЫ]",
            style = MaterialTheme.typography.bodyLarge,
            color = LocalTextStyle.current.color.takeOrElse {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary, // Цвет рамки, замените на нужный
                    shape = RoundedCornerShape(8.dp) // Форма рамки, здесь используется закругленная форма
                )
                .padding(16.dp)
        )
    }
}


