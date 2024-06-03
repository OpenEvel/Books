package ru.oraora.books.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.oraora.books.ui.screens.AdviceScreen
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
            animationSpec = tween(400),
            initialOffsetY = { it }
        )
    }

    val exitVertical = remember {
        slideOutVertically(
            animationSpec = tween(400),
            targetOffsetY = { it }
        )
    }

    val enterHorizontal = remember {
        slideInHorizontally(
            animationSpec = tween(400),
            initialOffsetX = { it }
        )
    }

    val exitHorizontal = remember {
        slideOutHorizontally(
            animationSpec = tween(400),
            targetOffsetX = { it }
        )
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH
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
            enterTransition = { enterVertical },
            exitTransition = { exitVertical }
        ) {
            SearchScreen(
                bookViewModel = bookViewModel,
                uiState = uiState,
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
            enterTransition = { enterHorizontal },
            exitTransition = { exitHorizontal }
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