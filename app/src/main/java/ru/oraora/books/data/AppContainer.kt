package ru.oraora.books.data

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.ogson.OGsonConverterFactory
import ru.oraora.books.data.models.IdListResponse
import ru.oraora.books.data.models.IdListResponseDeserializer
import ru.oraora.books.data.repository.BookRepository
import ru.oraora.books.data.repository.NetworkBookRepository
import ru.oraora.books.data.network.BookApiService

interface AppContainer {
    val bookRepository: BookRepository
}

class DefaultAppContainer: AppContainer {
    private val baseUrl = "https://www.googleapis.com"

    private val gson = GsonBuilder()
        .registerTypeAdapter(IdListResponse::class.java, IdListResponseDeserializer())
        .create()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(OGsonConverterFactory.create(gson))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }

    override val bookRepository: BookRepository by lazy {
        NetworkBookRepository(retrofitService)
    }
}