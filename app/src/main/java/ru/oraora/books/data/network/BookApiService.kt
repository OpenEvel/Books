package ru.oraora.books.data.network

import retrofit2.converter.ogson.annotations.ExtractField
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.oraora.books.data.models.Book
import ru.oraora.books.data.models.BookResponse
import ru.oraora.books.data.models.IdListResponse

interface BookApiService {
    @GET("/books/v1/volumes")
    suspend fun getIdBooks(
        @Query("q") query : String
    ): IdListResponse

    @GET("/books/v1/volumes/{id}")
    @ExtractField("volumeInfo")
    suspend fun getBookById(
        @Path("id") id: String
    ): Book
}