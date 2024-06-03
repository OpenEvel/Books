package ru.oraora.books.ui.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.oraora.books.data.models.Book

@Composable
fun BookInfoScreen(
    book: Book? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(text = book?.title ?: "[ДАННЫЕ УДАЛЕНЫ]")
    }
}