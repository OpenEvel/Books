package ru.oraora.books

import android.app.Application
import ru.oraora.books.data.AppContainer
import ru.oraora.books.data.DefaultAppContainer

class BookApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}