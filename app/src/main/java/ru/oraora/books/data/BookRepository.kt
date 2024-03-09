package ru.oraora.books.data

import ru.oraora.books.network.BookApiService

interface BookRepository {
    suspend fun getIdBooks(query : String): List<String>
}

class NetworkBookRepository(
    private val bookApiService: BookApiService,
): BookRepository {
    override suspend fun getIdBooks(query: String): List<String> {
        val idListResponse =  bookApiService.getIdBooks(query)
        return idListResponse.ids
    }
}