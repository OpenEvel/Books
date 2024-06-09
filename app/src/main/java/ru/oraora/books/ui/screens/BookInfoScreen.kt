package ru.oraora.books.ui.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import ru.oraora.books.data.models.Book
import ru.oraora.books.ui.screens.obook.BookImage

@Composable
fun BookInfoScreen(
    book: Book? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {

    if (book != null) {
        BookInfo(book = book, modifier = modifier)

    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
        ) {
            Text("[ДАННЫЕ УДАЛЕНЫ]")
        }
    }
}

@Composable
fun BookInfo(
    book: Book,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val topHeight =
        WindowInsets.statusBars.asPaddingValues()
            .calculateTopPadding()+12.dp
    val backgroundHeight = topHeight + 320.dp

    BookImage(
        imageLink = book.imageLink,
        modifier = Modifier
            .fillMaxWidth()
            .height(backgroundHeight)
            .blur(radiusX = 20.dp, radiusY = 20.dp)
            .alpha(0.37f)
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(topHeight))

        BookImage(
            imageLink = book.imageLink,
            modifier = Modifier
                .size(200.dp, 300.dp)
                .shadow(10.dp)
        )
    }
}