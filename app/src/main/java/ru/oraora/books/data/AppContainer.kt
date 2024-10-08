package ru.oraora.books.data

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import ru.moprules.ogson.OGsonConverterFactory
import ru.moprules.ogson.OGsonUniversalAdapter
import ru.oraora.books.data.repository.BookRepository
import ru.oraora.books.data.repository.NetworkBookRepository
import ru.oraora.books.data.network.BookApiService

interface AppContainer {
    val bookRepository: BookRepository
}

class DefaultAppContainer: AppContainer {
    private val baseUrl = "https://www.googleapis.com"

    private val gson = GsonBuilder()
        .serializeNulls()
        .registerTypeHierarchyAdapter(Any::class.java, OGsonUniversalAdapter())
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