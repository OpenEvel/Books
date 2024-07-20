package ru.oraora.books.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
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

    private val _searchingBooks = mutableStateListOf<Book>()
    val searchingBooks: List<Book> get() = Collections.unmodifiableList(_searchingBooks)

    private val _favoriteBooks = mutableStateListOf<Book>()
    val favoriteBooks: List<Book> get() = Collections.unmodifiableList(_favoriteBooks)

    fun addHistory(query: String, timeStop: Long = 0) {
        viewModelScope.launch {
            delay(timeStop)
            if (query.trim().isNotEmpty() && query !in _searchHistory) {
                _searchHistory.add(0, query)
            }
        }
    }

    fun removeHistory(history: String, timeStop: Long = 0) {
        viewModelScope.launch {
            delay(timeStop)
            _searchHistory.remove(history)
        }
    }

    fun clearHistory(timeStop: Long = 0) {
        viewModelScope.launch {
            delay(timeStop)
            _searchHistory.clear()
        }
    }

    fun addFavorite(book: Book, timeStop: Long = 0) {
        viewModelScope.launch {
            delay(timeStop)
            _favoriteBooks.add(0, book)
        }
    }

    fun removeFavorite(bookId: String, timeStop: Long = 0) {
        viewModelScope.launch {
            delay(timeStop)
            _favoriteBooks.removeIf { it.id == bookId }
        }
    }

    fun clearFavorite(timeStop: Long = 0) {
        viewModelScope.launch {
            delay(timeStop)
            _favoriteBooks.clear()
        }
    }

    fun loadBooks() {
        viewModelScope.launch {
            // Ставим состояние, что мы загружаем информацию
            // а также сохраняем последний запрос
            _uiState.update {
                it.copy(
                    searchState = SearchState.Loading,
                    lastQuery = it.query
                )
            }

            var newBooks: List<Book> = emptyList()

            val resState = try {
                newBooks = bookRepository.getBooks(_uiState.value.query.text)
                SearchState.Success
            } catch (e: IOException) {
                SearchState.Error
            } catch (e: HttpException) {
                SearchState.Error
            }

            if (resState is SearchState.Success) {
                _searchingBooks.clear()
                _searchingBooks.addAll(newBooks)
            }

            // Загружаем данные с сервера
            // Если в процессе загрузки возникает ошибка то показываем экран ошибки
            _uiState.update {
                it.copy(
                    searchState = resState
                )
            }
        }
    }

    fun refreshBooks() {
        viewModelScope.launch {
            // Ставим состояние, что мы обновляем информацию
            _uiState.update {
                it.copy(
                    isSearchRefresh = true,
                )
            }

            var newBooks: List<Book> = emptyList()


            // Если в процессе загрузки возникает ошибка то показываем экран ошибки
            val resState = try {
                // Загружаем данные с сервера
                newBooks = bookRepository.getBooks(_uiState.value.query.text)
                SearchState.Success
            } catch (e: IOException) {
                SearchState.Error
            } catch (e: HttpException) {
                SearchState.Error
            }

            if (resState is SearchState.Success) {
                _searchingBooks.clear()
                _searchingBooks.addAll(newBooks)
            }

            // Данные загружены, убираем инжикатор обновления
            _uiState.update {
                it.copy(
                    isSearchRefresh = false,
                )
            }

            // Показываем нужный экран
            _uiState.update {
                it.copy(
                    searchState = resState,
                )
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

    fun onQueryChange(newQuery: TextFieldValue) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    query = newQuery
                )
            }
        }
    }

    fun selectAllQuery() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    query = it.query.copy(selection = TextRange(0, it.query.text.length))
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

    fun delOptionsChange(isVisible: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showDelOptions = isVisible
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
