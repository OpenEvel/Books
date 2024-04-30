package ru.oraora.books.data.repository

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ru.oraora.books.data.models.Book
import ru.oraora.books.data.network.BookApiService

interface BookRepository {
    suspend fun getBooks(query : String): List<Book>
}

class NetworkBookRepository(
    private val bookApiService: BookApiService,
): BookRepository {
    override suspend fun getBooks(query: String): List<Book> = withContext(Dispatchers.IO)  {
        // список id книг для поискового запроса query
        val ids: List<String> = bookApiService.getIdBooks(query) ?: emptyList<String>()
        // Список deferred запросов информации по каждому id
        // Как только вызываем async - запрос выполняется, не блокируя другие запросы
        val requests: List<Deferred<Book>> = ids.map {
            async { bookApiService.getBookById(it) }
        }
        // Результирующий списко книг. Чтобы получить ответ по каждому запрсоу его нужно ожидать
        val books: List<Book> = requests.map { it.await() }
        books
    }
}
