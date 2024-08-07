package ru.oraora.books.data.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class FavoriteBook(
    val book: Book,
    var isVisibleState: MutableState<Boolean> = mutableStateOf(true)
)