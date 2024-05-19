package ru.oraora.books.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
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

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState

    fun updateCurrentBook(book: Book) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedBook = book,
                )
            }
        }
    }

    fun updateCurrentScreen(screen: BookAppScreen) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currentScreen = screen,
                )
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update {
            it.copy(
                query = query
            )
        }
    }

    fun onSearchActiveChange(active: Boolean) {
        _uiState.update {
            it.copy(
                isSearchActive = active
            )
        }
    }

    fun getBooks() {
        // Обновляем состояние - начинаем загрузку книг
        _uiState.update {
            it.copy(
                searchState = SearchState.LOADING,
            )
        }

        // Начинаем загрузку книг
        viewModelScope.launch {
            var listBooks: List<Book> = emptyList()
            var networkState: SearchState = SearchState.SUCCESS
            try {
                listBooks = bookRepository.getBooks(_uiState.value.query)
            } catch (e: IOException) {
                networkState = SearchState.ERROR
            }
            _uiState.update {
                it.copy(
                    books = listBooks,
                    searchState = networkState
                )
            }
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