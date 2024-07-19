package ru.oraora.books.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.oraora.books.data.models.Book
import ru.oraora.books.ui.screens.obook.BookCard
import ru.oraora.books.viewmodel.BookUiState
import ru.oraora.books.viewmodel.BookViewModel
import ru.oraora.books.viewmodel.Routes

@Composable
fun FavoriteScreen(
    bookViewModel: BookViewModel,
    uiState: BookUiState,
    navController: NavHostController,
    scrollState: LazyGridState = rememberLazyGridState(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {

    val topBarHeight = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()

    Box(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Spacer(
                modifier = Modifier.height(topBarHeight)
            )

            Text(
                text = "Favorites",
                color = LocalTextStyle.current.color.takeOrElse {
                    MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .wrapContentSize(Alignment.Center)
            )
        }
        FavoriteBookGrid(
            favoriteBooks = bookViewModel.favoriteBooks,
            columnsCount = uiState.searchColumnsCount,
            onBookSelect = { book ->
                bookViewModel.changeSelectedBook(book)
                navController.navigate(Routes.BOOK_INFO) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            scrollState = scrollState,
            onRemoveFavorite = bookViewModel::removeFavorite,
            onRemoveAllFavorite = bookViewModel::clearFavorite,
            topHeight = topBarHeight + 56.dp,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun FavoriteBookGrid(
    favoriteBooks: List<Book>,
    columnsCount: Int,
    onBookSelect: (Book) -> Unit,
    onRemoveFavorite: (String) -> Unit = {},
    scrollState: LazyGridState = rememberLazyGridState(),
    topHeight: Dp = 0.dp,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onRemoveAllFavorite: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var showDeleteOptions by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    showDeleteOptions = true
                }
            )
        }
    ) {
        val cellWidth =
            (LocalConfiguration.current.screenWidthDp.dp - 16.dp - 8.dp - 16.dp) / columnsCount
        val cellHeight = 1.5 * cellWidth

        LazyVerticalGrid(
            state = scrollState,
            columns = GridCells.Fixed(columnsCount),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)

        ) {

            if (topHeight > 0.dp) {
                items(columnsCount) {
                    Spacer(
                        modifier = Modifier.height(topHeight - 8.dp)
                    )
                }
            }

            items(favoriteBooks, key = { it.id }) { book ->

                var isVisible by remember { mutableStateOf(true) }

                AnimatedVisibility(
                    visible = isVisible,
                    exit = fadeOut() + shrinkVertically(),
                    modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                ) {
                    BookCard(
                        book = book,
                        isBookmarked = true,
                        onRemoveFavorite = {
                            isVisible = false
                            coroutineScope.launch {
                                // Duration of the fadeOut animation
//                                delay(300)
                                onRemoveFavorite(it)
                            }
                        },
                        modifier = Modifier
                            .size(cellWidth, cellHeight)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { onBookSelect(book) },
                                    onLongPress = {
                                        showDeleteOptions = true
                                    }
                                )
                            }
                    )
                }
            }

            if (favoriteBooks.isNotEmpty()) {
                items(columnsCount) {
                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )
                }
            }

        }

        AnimatedVisibility(
            visible = showDeleteOptions,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        onRemoveAllFavorite()
                    }
                    showDeleteOptions = false
                },
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete all")
            }
        }
    }
}
