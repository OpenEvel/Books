package ru.oraora.books.ui

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.oraora.books.R
import ru.oraora.books.ui.screens.osearch.SearchScreen
import ru.oraora.books.ui.theme.BooksTheme
import ru.oraora.books.viewmodel.BookViewModel


@Composable
fun BookApp() {
    val bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
    val uiState by bookViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { MyNavigationBar() }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchScreen(
                bookViewModel = bookViewModel,
                uiState = uiState,
                contentPadding = it,
            )
        }
    }
}

@Composable
fun MyNavigationBar() {
    var selectedItem by remember { mutableIntStateOf(1) }
    NavigationBar {
        NavigationBarItem(selected = selectedItem == 0, onClick = { selectedItem = 0 }, icon = {
            Icon(
                painter = painterResource(
                    id = R.drawable.asterisk
                ), contentDescription = null
            )
        })

        NavigationBarItem(selected = selectedItem == 1, onClick = { selectedItem = 1 }, icon = {
            Icon(
                Icons.Default.Search, contentDescription = null
            )
        })

        NavigationBarItem(selected = selectedItem == 2, onClick = { selectedItem = 2 }, icon = {
            val bookmarksIcon: ImageVector =
                if (selectedItem == 2) Icons.Default.Bookmarks else Icons.Outlined.Bookmarks
            Icon(bookmarksIcon, contentDescription = null)
        })
    }
}

@Preview("Light Theme")
@Preview("Dark Theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MyNavigationBarPreview() {
    BooksTheme {
        MyNavigationBar()
    }
}