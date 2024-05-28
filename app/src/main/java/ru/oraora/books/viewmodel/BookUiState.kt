package ru.oraora.books.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import ru.oraora.books.data.models.Book

sealed interface SearchFrame {

    object FirstEnter : SearchFrame
    object Loading : SearchFrame
    data class Success(val books: List<Book>) : SearchFrame
    object Error : SearchFrame
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
    val searchFrame: SearchFrame = SearchFrame.FirstEnter,
    val selectedBook: Book? = null,
    val isShowingBookPage: Boolean = false,
    val searchColumnsCount: Int = 2,
)