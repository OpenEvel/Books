package ru.oraora.books.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.oraora.books.R
import ru.oraora.books.ui.screens.osearch.SearchScreen
import ru.oraora.books.ui.screens.osearch.copy
import ru.oraora.books.ui.theme.BooksTheme
import ru.oraora.books.viewmodel.BookViewModel
import ru.oraora.books.viewmodel.Routes
import kotlin.coroutines.CoroutineContext

//@Composable
//fun BookApp1() {
//    val bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
//    val uiState by bookViewModel.uiState.collectAsState()
//
//
//    //Create NavController
//    val navController = rememberNavController()
//
//    Scaffold(
//        bottomBar = {
//            MyNavigationBar(
//                navigateTo = { navController.navigate(it) },
//                activateSearch = { bookViewModel.onSearchActiveChange(true) }
//            )
//        }
//    ) { contentPadding ->
//        NavHost(
//            navController = navController,
//            startDestination = BookAppScreen.Search.title
//        ) {
//            composable(route = BookAppScreen.Advice.title) {
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(contentPadding.copy(top = 0.dp))
//                ) {
//                    Text("Advice")
//                }
//            }
//
//            composable(route = BookAppScreen.Search.title) {
//                SearchScreen(
//                    bookViewModel = bookViewModel,
//                    uiState = uiState,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(contentPadding.copy(top = 0.dp))
//                )
//            }
//
//            composable(route = BookAppScreen.Favorite.title) {
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(contentPadding.copy(top = 0.dp))
//                ) {
//                    Text("Favorite")
//                }
//            }
//            composable(route = BookAppScreen.BookInfo.title) {
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(contentPadding.copy(top = 0.dp))
//                ) {
//                    Text("BookInfo")
//                }
//
//            }
//        }
//
////        SearchScreen(
////            bookViewModel = bookViewModel,
////            uiState = uiState,
////            contentPadding = it,
////        )
//    }
//}

val LocalSearchRequester =
    compositionLocalOf<FocusRequester> { error("No FocusRequester provided") }

@Composable
fun BookApp() {
    val bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)

    val searchRequester = remember { FocusRequester() }
    CompositionLocalProvider(LocalSearchRequester provides searchRequester) {

        Scaffold(
            bottomBar = {
                BookNavigationBar(
                    currentScreen = bookViewModel.currentScreen,
                    changeCurrentScreen = { bookViewModel.changeCurrentScreen(it) },
                    activateSearch = {
                        searchRequester.requestFocus();
                        bookViewModel.onSearchActiveChange(true);
                    },
                )
            }
        ) { contentPadding ->

            when (bookViewModel.currentScreen) {
                Routes.Advice ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding.copy(top = 0.dp))
                    ) {
                        Text("Advice")
                    }

                Routes.Search ->
                    SearchScreen(
                        bookViewModel = bookViewModel,
                        searchRequester = searchRequester,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding.copy(top = 0.dp))
                    )

                Routes.Favorite ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding.copy(top = 0.dp))
                    ) {
                        Text("Favorite")
                    }

                Routes.BookInfo ->
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
    }
}


@Composable
fun BookNavigationBar(
    currentScreen: Routes,
    changeCurrentScreen: (Routes) -> Unit,
    activateSearch: () -> Unit,
) {
    Surface(
        shadowElevation = 12.dp, // play with the elevation values
    ) {
        NavigationBar(
            tonalElevation = 0.dp
        ) {

            // Advice Screen
            NavigationBarItem(
                selected = currentScreen == Routes.Advice,
                onClick = {
                    changeCurrentScreen(Routes.Advice)
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
                selected = currentScreen == Routes.Search,
                onClick = {
                    if (currentScreen == Routes.Search) {
                        activateSearch()
                    } else {
                        changeCurrentScreen(Routes.Search)
                    }
                },
                icon = {
                    Icon(
                        Icons.Default.Search, contentDescription = null
                    )
                })

            // Favorite Screen
            NavigationBarItem(
                selected = currentScreen == Routes.Favorite,
                onClick = {
                    changeCurrentScreen(Routes.Favorite)
                },
                icon = {
                    val bookmarksIcon: ImageVector =
                        if (currentScreen == Routes.Favorite) Icons.Default.Bookmarks else Icons.Outlined.Bookmarks
                    Icon(bookmarksIcon, contentDescription = null)
                })
        }
    }
}
