package ru.oraora.books.data.repository

import ru.oraora.books.data.models.Book
import ru.oraora.books.data.network.BookApiService

interface BookRepository {
    suspend fun getBooks(query : String): List<Book>
}

class NetworkBookRepository(
    private val bookApiService: BookApiService,
): BookRepository {
    override suspend fun getBooks(query: String): List<Book> {
        val books =  mutableListOf<Book>()
        val ids: List<String> = bookApiService.getIdBooks(query)
        ids.forEach {id ->
            books.add(bookApiService.getBookById(id))
        }
        return books
    }
}
