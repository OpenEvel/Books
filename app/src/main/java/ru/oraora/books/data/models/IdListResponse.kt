package ru.oraora.books.data.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class IdListResponse(
    val ids: List<String>
)

class IdListResponseDeserializer: JsonDeserializer<IdListResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): IdListResponse {
        val itemsIds = mutableListOf<String>()
        val itemsJson = json?.asJsonObject?.get("items")
        if (itemsJson != null && itemsJson.isJsonArray) {
            itemsJson.asJsonArray.forEach {
                if (!it.isJsonNull && it.isJsonObject) {
                    val idElement = it.asJsonObject.get("id")
                    if (idElement.isJsonPrimitive) {
                        idElement?.asString?.let { id -> itemsIds.add(id) }
                    }
                }
            }
        }
        return IdListResponse(itemsIds)
    }
}