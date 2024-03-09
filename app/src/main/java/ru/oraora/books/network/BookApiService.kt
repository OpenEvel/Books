package ru.oraora.books.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.oraora.books.entity.IdListResponse

interface BookApiService {
    @GET("volumes")
    suspend fun getIdBooks(
        @Query("q") query : String
    ): IdListResponse

//    @GET("volumes/{id}")
//    suspend fun getBookById(
//        @Path("id") id: String
//    ): String
}