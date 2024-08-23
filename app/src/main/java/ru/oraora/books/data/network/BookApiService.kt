package ru.oraora.books.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.moprules.ogson.extract.Extract
import ru.oraora.books.data.models.Book


interface BookApiService {
    @GET("/books/v1/volumes")
    @Extract("items")
    @Extract("id")
    suspend fun getIdBooks(
        @Query("q") query : String
    ): List<String>

    @GET("/books/v1/volumes/{id}")
    suspend fun getBookById(
        @Path("id") id: String
    ): Book
}
