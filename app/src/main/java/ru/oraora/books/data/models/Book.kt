package ru.oraora.books.data.models

import com.google.gson.annotations.SerializedName
import retrofit2.converter.ogson.extract.Extract
import retrofit2.converter.ogson.https.Https


data class Book(
    val id: String,
    @Extract("volumeInfo")
    val title: String?,
    @Extract("volumeInfo")
    val authors: List<String>?,
    @Extract("volumeInfo")
    val publisher: String?,
    @Extract("volumeInfo")
    val publishedDate: String?,
    @Extract("volumeInfo")
    val description: String?,
    @Extract("volumeInfo")
    val pageCount: Long?,
    @Extract("volumeInfo")
    val language: String?,
    @Extract("accessInfo")
    @Extract("pdf")
    @SerializedName("isAvailable")
    val pdf: Boolean,
    @Extract("accessInfo")
    @Extract("epub")
    @SerializedName("isAvailable")
    val epub: Boolean,
    @Extract("volumeInfo")
    @Extract("imageLinks")
    @SerializedName("thumbnail")
    @Https
    val imageLink: String?,
)