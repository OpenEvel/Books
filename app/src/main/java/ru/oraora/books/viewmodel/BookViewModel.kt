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

    init {
        getBooks()
    }
    fun updateCurrentBook(book: Book) {
        _uiState.update {
            it.copy(
                selectedBook = book,
            )
        }
    }

    fun getBooks() {
        // Обновляем состояние - начинаем загрузку книг
        _uiState.update {
            it.copy(
                networkState = NetworkState.LOADING,
            )
        }

        // Начинаем загрузку книг
        viewModelScope.launch {
            var listBooks: List<Book> = emptyList()
            var networkState: NetworkState = NetworkState.SUCCESS
            try {
                listBooks = bookRepository.getBooks(_uiState.value.searchText)
            } catch (e: IOException) {
                networkState = NetworkState.ERROR
            }
            _uiState.update {
                it.copy(
                    books = listBooks,
                    networkState = networkState
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookApplication)
                val bookRepository = application.container.bookRepository
                BookViewModel(bookRepository = bookRepository)
            }
        }
    }

}