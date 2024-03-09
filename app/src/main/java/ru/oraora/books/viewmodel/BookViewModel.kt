package ru.oraora.books.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import ru.oraora.books.BookApplication
import ru.oraora.books.data.BookRepository
import java.io.IOException

sealed interface BookUiState {
    data class Success(val idList: List<String>) : BookUiState
    object Error : BookUiState
    object Loading : BookUiState
}

class BookViewModel(private val bookRepository: BookRepository) : ViewModel() {
    var bookUiState : BookUiState by mutableStateOf(BookUiState.Loading)
        private set

    init {
        getIdList("jazz+history")
    }

    fun getIdList(query : String) {
        viewModelScope.launch {
            bookUiState = try {
                BookUiState.Success(bookRepository.getIdBooks(query))
            } catch (e: IOException) {
                BookUiState.Error
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