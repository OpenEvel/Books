package ru.oraora.books.viewmodel

import androidx.compose.runtime.mutableStateListOf
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
import java.util.Collections

class BookViewModel(private val bookRepository: BookRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState

    private val _searchHistory = mutableStateListOf<String>("A", "B", "C")
    val searchHistory: List<String> get() = Collections.unmodifiableList(_searchHistory)

    fun addHistory(query: String) {
        viewModelScope.launch {
            if (query !in _searchHistory) {
                _searchHistory.add(query)
            }
        }
    }

    fun removeHistory(index: Int) {
        viewModelScope.launch {
            if (index >= 0 && index < _searchHistory.size) {
                _searchHistory.removeAt(index)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            _searchHistory.clear()
        }
    }

    private val _books = mutableStateListOf<Book>()
    val books: List<Book> get() = Collections.unmodifiableList(_books)

    fun getBooks() {
        // Обновляем состояние - начинаем загрузку книг

        // Начинаем загрузку книг
        viewModelScope.launch {
            // Ставим состояние что мы загружаем информацию
            _uiState.update {
                it.copy(searchFrame = SearchFrame.LOADING)
            }

            _uiState.update {
                var queryState: SearchFrame = SearchFrame.SUCCESS
                try {
                    // Очищаем список книг
                    _books.clear()
                    _books.addAll(bookRepository.getBooks(it.query))
                } catch (e: IOException) {
                    queryState = SearchFrame.ERROR
                }

                it.copy(searchFrame = queryState)
            }
        }
    }

    fun changeSelectedBook(book: Book) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(selectedBook = book)
            }
        }
    }

    fun onQueryChange(query: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    query = query
                )
            }
        }
    }

    fun onSearchActiveChange(active: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSearchActive = active
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

