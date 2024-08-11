package ru.oraora.books.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import ru.oraora.books.data.models.Book

sealed interface SearchState {
    data object FirstEnter : SearchState
    data object Loading : SearchState
    data object Success : SearchState
    data object Error : SearchState
}

object Routes {
    const val SEARCH = "search"
    const val ADVICE = "advice"
    const val FAVORITE = "favorite"
    const val BOOK_INFO = "book_info"
}


data class BookUiState(
    val query: TextFieldValue = TextFieldValue(""),
    val lastQuery: TextFieldValue = TextFieldValue(""),
    val isSearchActive: Boolean = false,
    val searchState: SearchState = SearchState.FirstEnter,
    val isSearchRefresh: Boolean = false,
    val selectedBook: Book? = null,
    val isShowingBookPage: Boolean = false,
    val searchColumnsCount: Int = 2,
    val showDelOptions: Boolean = false,
    val showDelAllBtn: Boolean = false,
)