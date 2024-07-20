package ru.oraora.books.ui.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
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
import ru.oraora.books.ui.navigation.isCurrent
import ru.oraora.books.ui.screens.obook.BookImage
import ru.oraora.books.ui.screens.osearch.OSearchBarDefaults
import ru.oraora.books.ui.screens.osearch.OSearchBarDefaults.AnimationEnterFloatSpec
import ru.oraora.books.ui.screens.osearch.OSearchBarDefaults.AnimationExitFloatSpec
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

    BackHandler {
        if (uiState.showDelOptions) {
            bookViewModel.delOptionsChange(false)
        } else {
            navController.popBackStack()
        }
    }

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
            showDelOptions = uiState.showDelOptions,
            onDelOptionsChange = bookViewModel::delOptionsChange,
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
    onRemoveFavorite: (bookId: String, timeStop: Long) -> Unit,
    showDelOptions: Boolean,
    onDelOptionsChange: (Boolean) -> Unit,
    scrollState: LazyGridState = rememberLazyGridState(),
    topHeight: Dp = 0.dp,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onRemoveAllFavorite: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    onDelOptionsChange(true)
                }
            )
        }
    ) {
        val cellWidth =
            (LocalConfiguration.current.screenWidthDp.dp - 16.dp - 8.dp - 16.dp) / columnsCount
        val cellHeight = 1.5 * cellWidth

        val borderColor by animateColorAsState(
            targetValue = if (showDelOptions) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f) else Color.Transparent,
            label = "",
        )

        val cardOnDeletePadding by animateDpAsState(
            targetValue = if (showDelOptions) 24.dp else 0.dp,
            label = "",
        )

        val delButtonColor by animateColorAsState(
            targetValue = if (showDelOptions) Color.White else Color.Transparent,
            label = "",
        )

        val delButtonIconColor by animateColorAsState(
            targetValue = if (showDelOptions) Color.Black else Color.Transparent,
            label = "",
        )

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
                    Box(
                        contentAlignment = Alignment.TopEnd,
                        modifier = Modifier
                            .size(cellWidth, cellHeight)
                            .border(
                                width = 2.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        BookImage(
                            imageBookLink = book.imageLink,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(cardOnDeletePadding)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { onBookSelect(book) },
                                        onLongPress = {
                                            onDelOptionsChange(true)
                                        }
                                    )
                                }
                        )

                        AnimatedVisibility(
                            visible = showDelOptions,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp)
                                .background(color = delButtonColor, shape = CircleShape)
                                .border(
                                    width = 0.5.dp,
                                    color = delButtonIconColor,
                                    shape = CircleShape
                                )

                        ) {
                            IconButton(
                                onClick = {
                                    isVisible = false
                                    onRemoveFavorite(book.id, 300)
                                    if (favoriteBooks.size - 1 == 0 && showDelOptions) {
                                        onDelOptionsChange(false)
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = delButtonIconColor,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp),
                                )
                            }
                        }
                    }
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
            visible = showDelOptions,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        onRemoveAllFavorite()
                    }
                    onDelOptionsChange(false)
                },
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete all")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Удалить все")
                }

            }
        }
    }
}
