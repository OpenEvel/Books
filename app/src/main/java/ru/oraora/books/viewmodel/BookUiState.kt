package ru.oraora.books.viewmodel

import ru.oraora.books.data.models.Book

enum class SearchFrame {
    FIRST_ENTER,
    LOADING,
    SUCCESS,
    ERROR
}

object Routes {
    const val SEARCH = "route_search"
    const val ADVICE = "route_advice"
    const val FAVORITE = "route_favorite"
    const val BOOK_INFO = "route_book_info"
}

//enum class Routes {
//    Search,
//    Advice,
//    Favorite,
//    BookInfo,
//}

data class BookUiState(
    val query: String = "",
    val isSearchActive: Boolean = false,
    val searchFrame: SearchFrame = SearchFrame.FIRST_ENTER,
    val selectedBook: Book? = null,
    val isShowingBookPage: Boolean = false,
)