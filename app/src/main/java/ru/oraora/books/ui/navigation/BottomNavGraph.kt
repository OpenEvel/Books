package ru.oraora.books.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import ru.oraora.books.ui.screens.AdviceScreen
import ru.oraora.books.ui.screens.FavoriteScreen
import ru.oraora.books.ui.screens.SearchScreen
import ru.oraora.books.ui.screens.copy
import ru.oraora.books.viewmodel.BookUiState
import ru.oraora.books.viewmodel.BookViewModel
import ru.oraora.books.viewmodel.Routes

fun NavGraphBuilder.bottomNavGraph(
    navController: NavHostController,
    bookViewModel: BookViewModel,
    uiState: BookUiState,
    enterByBottomNav: EnterTransition = fadeIn(animationSpec = tween(250)),
    exitByBottomNav: ExitTransition = fadeOut(animationSpec = tween(250)),
    bottomNavModifier: Modifier = Modifier.padding(bottom = 56.dp)
) {
    composable(
        route = BottomNavigationData.ADVICE.route,
        enterTransition = { enterByBottomNav },
        exitTransition = { exitByBottomNav }
    ) {
        AdviceScreen(
            navController = navController,
            modifier = bottomNavModifier
        )
    }

    composable(
        route = BottomNavigationData.SEARCH.route,
        enterTransition = { enterByBottomNav },
        exitTransition = { exitByBottomNav },
    ) {
        SearchScreen(
            bookViewModel = bookViewModel,
            uiState = uiState,
            navController = navController,
            modifier = bottomNavModifier
        )
    }

    composable(
        route = BottomNavigationData.FAVORITE.route,
        enterTransition = { enterByBottomNav },
        exitTransition = { exitByBottomNav },
    ) {
        FavoriteScreen(
            bookViewModel = bookViewModel,
            uiState = uiState,
            navController = navController,
            modifier = bottomNavModifier
        )
    }
}