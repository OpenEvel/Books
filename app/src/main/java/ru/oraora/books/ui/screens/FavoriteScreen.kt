package ru.oraora.books.ui.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import org.burnoutcrew.reorderable.ItemPosition
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.reorderable
import ru.oraora.books.data.models.Book
import ru.oraora.books.data.models.FavoriteBook
import ru.oraora.books.ui.screens.obook.BookImage
import ru.oraora.books.ui.screens.ogrid.ShadowLine
import ru.oraora.books.viewmodel.BookUiState
import ru.oraora.books.viewmodel.BookViewModel
import ru.oraora.books.viewmodel.Routes

@Composable
fun FavoriteScreen(
    bookViewModel: BookViewModel,
    uiState: BookUiState,
    navController: NavHostController,
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

    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarHeight)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Favorites",
                    color = LocalTextStyle.current.color.takeOrElse {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    AnimatedVisibility(
                        visible = uiState.showDelOptions,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut(),
                        modifier = Modifier
                            .padding(start = 16.dp)
                    ) {
                        IconButton(onClick = { bookViewModel.delOptionsChange(false) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }

                    }
                }
            }
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
            onRemoveFavorite = bookViewModel::removeFavorite,
            onMoveFavorite = bookViewModel::moveFavorite,
            clearFavorite = bookViewModel::clearFavorite,
            showDelOptions = uiState.showDelOptions,
            onDelOptionsChange = bookViewModel::delOptionsChange,
            showDelAllBtn = uiState.showDelAllBtn,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun FavoriteBookGrid(
    favoriteBooks: List<FavoriteBook>,
    columnsCount: Int,
    onBookSelect: (Book) -> Unit,
    onRemoveFavorite: (bookId: String, timeStop: Long) -> Unit,
    onMoveFavorite: (from: ItemPosition, to: ItemPosition) -> Unit,
    showDelOptions: Boolean,
    onDelOptionsChange: (Boolean) -> Unit,
    showDelAllBtn: Boolean,
    clearFavorite: (start: Int, end: Int, timeStop: Long) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val state = rememberReorderableLazyGridState(onMove = onMoveFavorite)
    val detectModifier = if (showDelOptions) {
        Modifier.detectReorderAfterLongPress(state)
    } else {
        Modifier
    }

    val cellWidth =
        (LocalConfiguration.current.screenWidthDp.dp - 16.dp - 8.dp - 16.dp) / columnsCount
    val cellHeight = 1.5 * cellWidth

    LaunchedEffect(favoriteBooks.size, showDelOptions) {
        if (showDelOptions && favoriteBooks.isEmpty()) {
            onDelOptionsChange(false)
        }
    }

    Box(contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        if (favoriteBooks.isNotEmpty()) {
                            onDelOptionsChange(true)
                        }
                    }
                )
            }
    ) {

        LazyVerticalGrid(
            state = state.gridState,
            columns = GridCells.Fixed(columnsCount),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .reorderable(state)
        ) {
            itemsIndexed(
                favoriteBooks,
                key = { _, favBook -> favBook.book.id }) { index, favBook ->
                val firstLineModifier = if (index < columnsCount) {
                    Modifier.padding(top = 8.dp)
                } else {
                    Modifier
                }

                val cntCardOnLastLine = columnsCount - favoriteBooks.size % columnsCount
                val lastLineModifier =
                    if (index > favoriteBooks.lastIndex - cntCardOnLastLine) {
                        Modifier.padding(bottom = 8.dp)
                    } else {
                        Modifier
                    }
                val borderColor by animateColorAsState(
                    targetValue = if (!showDelOptions) {
                        Color.Transparent
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                    }, label = ""
                )

                Box(
                    modifier = Modifier
                        .then(firstLineModifier)
                        .then(lastLineModifier)
                        .size(cellWidth, cellHeight)
                        .border(
                            width = 0.5.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(20.dp)
                        )
                )

                ReorderableItem(state, key = favBook.book.id) { isDragging ->

                    val cardPadding by animateDpAsState(
                        if (!showDelOptions) {
                            0.dp
                        } else if (isDragging) {
                            12.dp
                        } else {
                            24.dp
                        },
                        label = ""
                    )

                    val delButtonPadding by animateDpAsState(
                        if (isDragging) {
                            0.dp
                        } else {
                            12.dp
                        },
                        label = ""
                    )

                    val elevation by animateDpAsState(
                        if (isDragging) 8.dp else 1.dp,
                        label = ""
                    )

                    AnimatedVisibility(
                        visible = favBook.isVisibleState.value,
                        exit = fadeOut() + scaleOut(),
                    ) {
                        Box(
                            contentAlignment = Alignment.TopEnd,
                            modifier = Modifier
                                .then(detectModifier)
                                .then(firstLineModifier)
                                .then(lastLineModifier)
                                .size(cellWidth, cellHeight)
                        ) {
                            BookImage(
                                imageBookLink = favBook.book.imageLink,
                                modifier = Modifier
                                    .padding(cardPadding)
                                    .fillMaxSize()
                                    .then(
                                        if (!showDelOptions) {
                                            Modifier.pointerInput(Unit) {
                                                detectTapGestures(
                                                    onTap = { onBookSelect(favBook.book) },
                                                    onLongPress = {
                                                        onDelOptionsChange(true)
                                                    }
                                                )
                                            }
                                        } else {
                                            Modifier
                                        }
                                    ),
                                onFinishModifier = Modifier
                                    .shadow(elevation)
                            )

                            AnimatedVisibility(
                                visible = showDelOptions && !isDragging,
                                enter = scaleIn(),
                                exit = scaleOut(),
                            ) {

                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .padding(delButtonPadding)
                                        .size(24.dp)
                                        .shadow(1.dp, shape = CircleShape)
                                        .background(
                                            color = Color.White,
                                            shape = CircleShape
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            onRemoveFavorite(favBook.book.id, 300)
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }

        val isGridTop by remember {
            derivedStateOf {
                state.gridState.firstVisibleItemIndex == 0 &&
                        state.gridState.firstVisibleItemScrollOffset == 0
            }
        }

        ShadowLine(
            isShow = !isGridTop,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        AnimatedVisibility(
            visible = showDelAllBtn,
            enter = fadeIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.padding(bottom = 4.5.dp)
        ) {
            Button(
                onClick = {
                    val start =
                        state.gridState.firstVisibleItemIndex
                    val end =
                        state.gridState.layoutInfo.visibleItemsInfo.last().index
                    clearFavorite(start, end, 300)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(),
                    contentColor = Color.White
                ),
                shape = CircleShape,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )

                    Text(
                        text = "Удалить все",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}
