package ru.oraora.books.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.oraora.books.ui.screens.osearch.SearchScreen
import ru.oraora.books.viewmodel.BookViewModel


@Composable
fun BookApp() {
    val bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
    val uiState by bookViewModel.uiState.collectAsState()

    Scaffold {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchScreen(
                bookViewModel = bookViewModel,
                uiState = uiState,
                contentPadding = it,
            )
        }
    }
}
