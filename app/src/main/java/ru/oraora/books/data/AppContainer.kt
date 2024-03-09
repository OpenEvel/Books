package ru.oraora.books.data

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.oraora.books.entity.IdListResponse
import ru.oraora.books.entity.IdListResponseDeserializer
import ru.oraora.books.network.BookApiService

interface AppContainer {
    val bookRepository: BookRepository
}

class DefaultAppContainer: AppContainer {
    private val baseUrl = "https://www.googleapis.com/books/v1/"

    private val gson = GsonBuilder()
        .registerTypeAdapter(IdListResponse::class.java, IdListResponseDeserializer())
        .create()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }

    override val bookRepository: BookRepository by lazy {
        NetworkBookRepository(retrofitService)
    }
}