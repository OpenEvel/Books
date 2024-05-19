package ru.oraora.books.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.oraora.books.BookApplication
import ru.oraora.books.data.models.Book
import ru.oraora.books.data.repository.BookRepository
import java.io.IOException

class BookViewModel(private val bookRepository: BookRepository) : ViewModel() {

//    private val _uiState = MutableStateFlow(BookUiState())
//    val uiState: StateFlow<BookUiState> = _uiState

    var query: String by mutableStateOf("")
        private set

    var isSearchActive: Boolean by mutableStateOf(false)
        private set

    var searchHistory: List<String> by mutableStateOf(emptyList())

    var searchFrame: SearchFrame by mutableStateOf(SearchFrame.FIRST_ENTER)
        private set

    var books: List<Book> by mutableStateOf(emptyList())
        private set

    var selectedBook: Book? by mutableStateOf(null)
        private set

    var currentScreen: Routes by mutableStateOf(Routes.Search)
        private set

    fun changeSelectedBook(book: Book) {
        viewModelScope.launch {
            selectedBook = book
        }
    }

    fun changeCurrentScreen(screen: Routes) {
        viewModelScope.launch {
            currentScreen = screen
        }
    }

    fun onQueryChange(newQuery: String) {
        viewModelScope.launch {
            query = newQuery
        }
    }

    fun onSearchActiveChange(active: Boolean) {
        viewModelScope.launch {
            isSearchActive = active
        }
    }

    fun getBooks() {
        // Обновляем состояние - начинаем загрузку книг
        searchFrame = SearchFrame.LOADING

        // Начинаем загрузку книг
        viewModelScope.launch {
            var listBooks: List<Book> = emptyList()
            var networkState: SearchFrame = SearchFrame.SUCCESS
            try {
                listBooks = bookRepository.getBooks(query)
            } catch (e: IOException) {
                networkState = SearchFrame.ERROR
            }

            books = listBooks
            searchFrame = networkState
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookApplication)
                val bookRepository = application.container.bookRepository
                BookViewModel(bookRepository = bookRepository)
            }
        }
    }

}