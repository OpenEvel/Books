package ru.oraora.books.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.oraora.books.R
import ru.oraora.books.ui.screens.osearch.SearchScreen
import ru.oraora.books.ui.screens.osearch.copy
import ru.oraora.books.ui.theme.BooksTheme
import ru.oraora.books.viewmodel.BookAppScreen
import ru.oraora.books.viewmodel.BookViewModel

@Composable
fun BookApp() {
    val bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
    val uiState by bookViewModel.uiState.collectAsState()


    //Create NavController
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            MyNavigationBar(
                currentScreen = uiState.currentScreen,
                navigateTo = { navController.navigate(it) },
                updateCurrentScreen = { bookViewModel.updateCurrentScreen(it) } ,
                activateSearch = { bookViewModel.onSearchActiveChange(true) }
            )
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = BookAppScreen.Search.title
        ) {
            composable(route = BookAppScreen.Advice.title) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding.copy(top = 0.dp))
                ) {
                    Text("Advice")
                }
            }

            composable(route = BookAppScreen.Search.title) {
                SearchScreen(
                    bookViewModel = bookViewModel,
                    uiState = uiState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding.copy(top = 0.dp))
                )
            }

            composable(route = BookAppScreen.Favorite.title) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding.copy(top = 0.dp))
                ) {
                    Text("Favorite")
                }
            }
            composable(route = BookAppScreen.BookInfo.title) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding.copy(top = 0.dp))
                ) {
                    Text("BookInfo")
                }

            }
        }

//        SearchScreen(
//            bookViewModel = bookViewModel,
//            uiState = uiState,
//            contentPadding = it,
//        )
    }
}

@Composable
fun MyNavigationBar(
    navigateTo: (String) -> Unit,
    currentScreen: BookAppScreen,
    updateCurrentScreen: (BookAppScreen) -> Unit,
    activateSearch: () -> Unit,
) {
    Box() {

    }
    NavigationBar(
        tonalElevation = 0.dp
    ) {

        // Advice Screen
        NavigationBarItem(
            selected = currentScreen == BookAppScreen.Advice,
            onClick = {
                navigateTo(BookAppScreen.Advice.title)
                updateCurrentScreen(BookAppScreen.Advice)
            },
            icon = {
                Icon(
                    painter = painterResource(
                        id = R.drawable.asterisk
                    ), contentDescription = null
                )
            })

        // Search Screen
        NavigationBarItem(
            selected = currentScreen == BookAppScreen.Search,
            onClick = {
                if (currentScreen == BookAppScreen.Search) {
                    activateSearch()
                } else {
                    navigateTo(BookAppScreen.Search.title)
                    updateCurrentScreen(BookAppScreen.Search)
                }
            },
            icon = {
                Icon(
                    Icons.Default.Search, contentDescription = null
                )
            })

        // Favorite Screen
        NavigationBarItem(
            selected = currentScreen == BookAppScreen.Favorite,
            onClick = {
                navigateTo(BookAppScreen.Favorite.title)
                updateCurrentScreen(BookAppScreen.Favorite)
            },
            icon = {
                val bookmarksIcon: ImageVector =
                    if (currentScreen == BookAppScreen.Favorite) Icons.Default.Bookmarks else Icons.Outlined.Bookmarks
                Icon(bookmarksIcon, contentDescription = null)
            })
    }
}

//@Preview("Light Theme")
//@Preview("Dark Theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun MyNavigationBarPreview() {
//    BooksTheme {
//        MyNavigationBar()
//    }
//}