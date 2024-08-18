package ru.oraora.books.ui


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            NavGraph(
                navController = navController,
                startDestination = Routes.SEARCH,
                bookViewModel = bookViewModel,
                uiState = uiState,
                modifier = Modifier.fillMaxSize()
            )

            BottomNavigationBar(
                navController = navController,
                activateSearch = {
                    searchRequester.requestFocus()
                    bookViewModel.selectAllQuery()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )

        }
    }
}
