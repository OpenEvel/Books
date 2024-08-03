package ru.oraora.books

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.oraora.books.ui.BookApp
import ru.oraora.books.ui.theme.BooksTheme
import ru.oraora.books.viewmodel.BookViewModel

class MainActivity : ComponentActivity() {
    private lateinit var bookViewModel: BookViewModel
    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookViewModel = ViewModelProvider(this, BookViewModel.Factory)[BookViewModel::class.java]

        Log.i("ostate", "create")
        setContent {
            BooksTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookApp(bookViewModel)
                }
            }
        }
    }
}
