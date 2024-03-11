package ru.oraora.books.viewmodel

import ru.oraora.books.data.models.Book

enum class NetworkState {
    LOADING,
    SUCCESS,
    ERROR
}

data class BookUiState(
    val searchText: String = "jazz history",
    val networkState: NetworkState = NetworkState.LOADING,
    val books: List<Book> = emptyList(),
    val selectedBook: Book? = null,
    val isShowingBookPage: Boolean = false,
)