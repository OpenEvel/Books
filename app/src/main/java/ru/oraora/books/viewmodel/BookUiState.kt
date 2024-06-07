package ru.oraora.books.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import ru.oraora.books.data.models.Book

sealed interface SearchState {

    object FirstEnter : SearchState
    object Loading : SearchState
    object Refreshing : SearchState
    object Success : SearchState
//    data class Success(val books: List<Book>) : SearchState
    object Error : SearchState
}

object Routes {
    const val SEARCH = "route_search"
    const val ADVICE = "route_advice"
    const val FAVORITE = "route_favorite"
    const val BOOK_INFO = "route_book_info"
}


data class BookUiState(
    val query: TextFieldValue = TextFieldValue(""),
    val lastQuery: TextFieldValue = TextFieldValue(""),
    val isSearchActive: Boolean = false,
    val searchState: SearchState = SearchState.FirstEnter,
    val selectedBook: Book? = null,
    val isShowingBookPage: Boolean = false,
    val searchColumnsCount: Int = 2,
)