package ru.oraora.books.viewmodel

import ru.oraora.books.data.models.Book

enum class SearchState {
    FIRST_ENTER,
    LOADING,
    SUCCESS,
    ERROR
}

data class BookUiState(
    val query: String = "",
    val isSearchActive: Boolean = false,
    val searchState: SearchState = SearchState.FIRST_ENTER,
    val books: List<Book> = emptyList(),
    val selectedBook: Book? = null,
    val isShowingBookPage: Boolean = false,
)