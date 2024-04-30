package ru.oraora.books.ui.screens.osearch


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.oraora.books.R
import ru.oraora.books.data.models.Book
import ru.oraora.books.viewmodel.BookUiState
import ru.oraora.books.viewmodel.BookViewModel
import ru.oraora.books.viewmodel.SearchState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    bookViewModel: BookViewModel,
    uiState: BookUiState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
//    query: String,
//    onQueryChange: (String) -> Unit,
//    active: Boolean,
//    onActiveChange: (Boolean) -> Unit,
//    onSearch: () -> Unit,
    scrollState: LazyListState = rememberLazyListState(),
    searchHistory: MutableList<String> = rememberSaveable { mutableListOf<String>() }

) {
    Scaffold(
        topBar = {
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
                onQueryChange = { bookViewModel.onQueryChange(it) },
                onSearch = { bookViewModel.getBooks() },
                active = uiState.isSearchActive,
                onActiveChange = { bookViewModel.onSearchActiveChange(it) },
                animationProgress = OSearchBarDefaults.animationProgress(active = uiState.isSearchActive),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Close icon") },
                searchHistory = searchHistory,
                scrollState = scrollState,
            )
        }
    ) { contentPadding ->

        val framePadding =
            contentPadding.copy(top = 0.dp, bottom = contentPadding.calculateTopPadding())

        OLazyColumn(
            contentPadding = contentPadding,
            state = scrollState,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            when (uiState.searchState) {
                SearchState.FIRST_ENTER -> FirstEnterFrame(framePadding)
                SearchState.LOADING -> LoadingFrame(framePadding)
                SearchState.ERROR -> ErrorFrame(
                    contentPadding = framePadding,
                    retryAction = { bookViewModel.getBooks() }
                )

                SearchState.SUCCESS -> BooksList(books = uiState.books)
            }
        }
    }
}

@Composable
fun OLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    content: LazyListScope.() -> Unit
) {
    val isOnTheTop by remember {
        derivedStateOf {
            state.firstVisibleItemIndex == 0 &&
                    state.firstVisibleItemScrollOffset == 0
        }
    }

    var spaceHeight by remember {
        mutableStateOf(0.dp)
    }

    if (isOnTheTop) {
        val newHeight = contentPadding.calculateTopPadding()
        if (spaceHeight == 0.dp) {
            spaceHeight = newHeight
        }
    }

    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding.copy(top = 0.dp),
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        content = {
            item {
                Spacer(modifier = Modifier.height(spaceHeight))
            }
            content()
        }
    )

}

@Composable
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


fun LazyListScope.FirstEnterFrame(contentPadding: PaddingValues) {
    item {
        Box(
            modifier = Modifier
                .fillParentMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Book,
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
            )
        }
    }
}


fun LazyListScope.LoadingFrame(contentPadding: PaddingValues) {
    item {
        Box(
            modifier = Modifier
                .fillParentMaxSize()
                .padding(contentPadding),
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
    contentPadding: PaddingValues,
    retryAction: () -> Unit,
) {
    item {
        Column(
            modifier = Modifier
                .fillParentMaxSize()
                .padding(contentPadding),
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
