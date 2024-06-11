package ru.oraora.books.ui.navigation

import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

    val bottomNavDuration = 250
    val enterByBottomNav = remember {
        fadeIn(animationSpec = tween(bottomNavDuration))
    }
    val exitByBottomNav = remember {
        fadeOut(animationSpec = tween(bottomNavDuration))
    }

    val bookInfoDuration = 500

    val enterLeft = remember {
        slideInHorizontally(
            animationSpec = tween(bookInfoDuration),
            initialOffsetX = { it }
        ) + fadeIn(animationSpec = tween(bookInfoDuration))
    }

    val enterRight = remember {
        slideInHorizontally(
            animationSpec = tween(bookInfoDuration),
            initialOffsetX = { -it }
        ) + fadeIn(animationSpec = tween(bookInfoDuration))
    }

    val exitLeft = remember {
        slideOutHorizontally(
            animationSpec = tween(bookInfoDuration),
            targetOffsetX = { -it }
        ) + fadeOut(animationSpec = tween(bookInfoDuration))
    }

    val exitRight = remember {
        slideOutHorizontally(
            animationSpec = tween(bookInfoDuration),
            targetOffsetX = { it }
        ) + fadeOut(animationSpec = tween(bookInfoDuration))
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(
            route = Routes.ADVICE,
            enterTransition = { enterByBottomNav },
            exitTransition = { exitByBottomNav }
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
            enterTransition = { if (initialState.destination.isCurrent(Routes.BOOK_INFO)) enterRight else enterByBottomNav },
            exitTransition = { if (targetState.destination.isCurrent(Routes.BOOK_INFO)) exitLeft else exitByBottomNav },
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
            enterTransition = { if (initialState.destination.isCurrent(Routes.BOOK_INFO)) enterRight else enterByBottomNav },
            exitTransition = { if (targetState.destination.isCurrent(Routes.BOOK_INFO)) exitLeft else exitByBottomNav },
        ) {
            FavoriteScreen(
                bookViewModel = bookViewModel,
                uiState = uiState,
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