package ru.oraora.books.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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

val LocalSearchRequester =
    compositionLocalOf<FocusRequester> { error("No FocusRequester provided") }

@Composable
fun BookApp() {
    val bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
    val uiState by bookViewModel.uiState.collectAsState()

    val enterTransitionAnimation = slideInVertically(
        animationSpec = tween(500),
        initialOffsetY = { it }
    )

    val exitTransitionAnimation = slideOutVertically(
        animationSpec = tween(500),
        targetOffsetY = { it }
    )




    //Create NavController
    val navController = rememberNavController()

    val searchRequester = remember { FocusRequester() }
    CompositionLocalProvider(LocalSearchRequester provides searchRequester) {
        Scaffold(
            bottomBar = {
                BookNavigationBar(
                    navController = navController,
                    activateSearch = { searchRequester.requestFocus() },
                )
            }
        ) { contentPadding ->

            NavHost(
                navController = navController,
                startDestination = Routes.SEARCH
            ) {
                composable(route = Routes.ADVICE,
                    enterTransition = { enterTransitionAnimation },
                    exitTransition = { exitTransitionAnimation }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding.copy(top = 0.dp))
                    ) {
                        Text("Advice")
                    }
                }

                composable(route = Routes.SEARCH,
                    enterTransition = { slideInVertically(
                        animationSpec = tween(400),
                        initialOffsetY = { it }
                    ) },
                    exitTransition = { slideOutVertically(
                        animationSpec = tween(400),
                        targetOffsetY = { it }
                    ) }
                ) {
                    SearchScreen(
                        bookViewModel = bookViewModel,
                        uiState = uiState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding.copy(top = 0.dp))
                    )
                }

                composable(route = Routes.FAVORITE,
                    enterTransition = { enterTransitionAnimation },
                    exitTransition = { exitTransitionAnimation }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding.copy(top = 0.dp))
                    ) {
                        Text("Favorite")
                    }
                }
                composable(route = Routes.BOOK_INFO,
                    enterTransition = { enterTransitionAnimation },
                    exitTransition = { exitTransitionAnimation }
                ) {
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
}


@Composable
fun BookNavigationBar(
    navController: NavController,
    activateSearch: () -> Unit,
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.37f))
        NavigationBar(
            tonalElevation = 0.dp
        ) {

            NavigationBarItem(
                selected = currentDestination.isSelected(Routes.ADVICE),
                onClick = { navController.myNavigate(Routes.ADVICE) },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.asterisk
                        ),
                        contentDescription = null
                    )
                },
            )

            // Search Screen
            NavigationBarItem(
                selected = currentDestination.isSelected(Routes.SEARCH),
                onClick = {
                    if (currentDestination.isSelected(Routes.SEARCH)) {
                        activateSearch()
                    } else {
                        navController.myNavigate(Routes.SEARCH)
                    }
                },
                icon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                }
            )

            // Favorite Screen
            NavigationBarItem(
                selected = currentDestination.isSelected(Routes.FAVORITE),
                onClick = { navController.myNavigate(Routes.FAVORITE) },
                icon = {
                    val bookmarksIcon: ImageVector =
                        if (currentDestination.isSelected(Routes.FAVORITE)) Icons.Default.Bookmarks else Icons.Outlined.Bookmarks
                    Icon(bookmarksIcon, contentDescription = null)
                }
            )
        }
    }
}

fun NavController.myNavigate(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavDestination?.isSelected(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}