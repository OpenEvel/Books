package ru.oraora.books.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.oraora.books.ui.screens.AdviceScreen
import ru.oraora.books.ui.screens.BookInfoScreen
import ru.oraora.books.ui.screens.FavoriteScreen
import ru.oraora.books.ui.screens.SearchScreen
import ru.oraora.books.ui.screens.copy
import ru.oraora.books.viewmodel.BookUiState
import ru.oraora.books.viewmodel.BookViewModel
import ru.oraora.books.viewmodel.Routes

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    bookViewModel: BookViewModel,
    uiState: BookUiState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {

    val enterVertical = remember {
        slideInVertically(
            animationSpec = tween(600),
            initialOffsetY = { it }
        )
    }

    val exitVertical = remember {
        slideOutVertically(
            animationSpec = tween(600),
            targetOffsetY = { it }
        )
    }

    val enterLeft = remember {
        slideInHorizontally(
            animationSpec = tween(400),
            initialOffsetX = { it }
        )
    }

    val enterRight = remember {
        slideInHorizontally(
            animationSpec = tween(400),
            initialOffsetX = { -it }
        )
    }

    val exitLeft = remember {
        slideOutHorizontally(
            animationSpec = tween(400),
            targetOffsetX = { -it }
        )
    }

    val exitRight = remember {
        slideOutHorizontally(
            animationSpec = tween(400),
            targetOffsetX = { it }
        )
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(
            route = Routes.ADVICE,
            enterTransition = { enterVertical },
            exitTransition = { exitVertical }
        ) {
            AdviceScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding.copy(top = 0.dp))
            )
        }

        composable(
            route = Routes.SEARCH,
            enterTransition = { if (initialState.destination.isCurrent(Routes.BOOK_INFO)) enterRight else enterVertical },
            exitTransition = { if (targetState.destination.isCurrent(Routes.BOOK_INFO)) exitLeft else exitVertical },
        ) {
            SearchScreen(
                bookViewModel = bookViewModel,
                uiState = uiState,
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding.copy(top = 0.dp))
            )
        }

        composable(
            route = Routes.FAVORITE,
            enterTransition = { enterVertical },
            exitTransition = { exitVertical }
        ) {
            FavoriteScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding.copy(top = 0.dp))
            )
        }
        composable(
            route = Routes.BOOK_INFO,
            enterTransition = { enterLeft },
            exitTransition = { exitRight }
        ) {
            BookInfoScreen(
                book = uiState.selectedBook,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding.copy(top = 0.dp))
            )
        }
    }

}