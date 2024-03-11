package ru.oraora.books.data.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class Book(
    val title: String,
//    val authors: List<String>,
//    val description: String,
//    val pageCount: Int,
    val language: String,
//    val pdf: Boolean,
//    val epub: Boolean,
//    val publisher: String,
//    val date: String,
//    val imgLink: ImageLink
)

data class ImageLink(
    val small: String,
    val medium: String,
    val large: String,
)

//class BookDeserializer: JsonDeserializer<Book> {
//    override fun deserialize(
//        json: JsonElement?,
//        typeOfT: Type?,
//        context: JsonDeserializationContext?
//    ): Book {
//        val itemsIds = mutableListOf<String>()
//        val itemsJson = json?.asJsonObject?.get("items")
//        if (itemsJson != null && itemsJson.isJsonArray) {
//            itemsJson.asJsonArray.forEach {
//                if (!it.isJsonNull && it.isJsonObject) {
//                    val idElement = it.asJsonObject.get("id")
//                    if (idElement.isJsonPrimitive) {
//                        idElement?.asString?.let { id -> itemsIds.add(id) }
//                    }
//                }
//            }
//        }
//        return Book(
//            title = "",
//            authors = mutableListOf("12", "12"),
//            description = "",
//            pageCount = 123,
//            language = "",
//            pdf = true,
//            epub = false,
//            publisher = "",
//            date = "",
//            imgLink = ImageLink(small = "", medium = "", large = "")
//        )
//    }
//}