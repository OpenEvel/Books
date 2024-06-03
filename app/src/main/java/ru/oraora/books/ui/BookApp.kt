package ru.oraora.books.ui


import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ru.oraora.books.ui.navigation.BottomNavigationBar
import ru.oraora.books.ui.navigation.NavGraph
import ru.oraora.books.viewmodel.BookViewModel
import ru.oraora.books.viewmodel.Routes

val LocalSearchRequester =
    compositionLocalOf<FocusRequester> { error("No FocusRequester provided") }

@Composable
fun BookApp() {
    val bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
    val uiState by bookViewModel.uiState.collectAsState()

    //Create NavController
    val navController = rememberNavController()

    val searchRequester = remember { FocusRequester() }
    CompositionLocalProvider(LocalSearchRequester provides searchRequester) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    activateSearch = {
                        searchRequester.requestFocus()
                        bookViewModel.selectAllQuery()
                    },
                )
            }
        ) { contentPadding ->

            NavGraph(
                navController = navController,
                startDestination = Routes.SEARCH,
                bookViewModel = bookViewModel,
                uiState = uiState,
                contentPadding = contentPadding
            )

        }
    }
}
