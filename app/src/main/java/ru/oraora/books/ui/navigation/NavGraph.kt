package ru.oraora.books.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
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
    modifier: Modifier
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

        bottomNavGraph(
            navController = navController,
            bookViewModel = bookViewModel,
            uiState = uiState,
        )

        composable(
            route = Routes.BOOK_INFO,
            enterTransition = { enterLeft },
            exitTransition = { exitRight }
        ) {
            BookInfoScreen(
                book = uiState.selectedBook,
            )
        }
    }
}