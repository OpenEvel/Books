package retrofit2.converter.ogson

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonIOException
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.converter.ogson.annotations.Extract
import retrofit2.converter.ogson.annotations.Extracts
import java.io.IOException

class OGsonResponseBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>,
    private val annotations: Array<Annotation>,
) : Converter<ResponseBody, T> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val result: T

        val jsonReader: JsonReader = gson.newJsonReader(value.charStream())
        val extracts = getExtracts(annotations)
        // Если у нас есть хотя обдна аннотация, что нужно извлечь поле
        if (extracts.isNotEmpty()) {
            // Парсим вcё в json объект
            var json: JsonElement? = value.use {
                jsonReader.use {reader ->
                    JsonParser.parseReader(reader)
                }
            }

            extracts.forEach {extr ->
                json = if (json != null && json!!.isJsonArray) {
                    val newJson = JsonArray()
                    json?.asJsonArray?.forEach {
                        newJson.add(it?.asJsonObject?.get(extr.field))
                    }
                    newJson
                } else {
                    json?.asJsonObject?.get(extr.field)
                }
            }

            result = adapter.fromJsonTree(json)

        } else {
            // Иначе стандартная обработка gson
            value.use {
                result = adapter.read(jsonReader)
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    throw JsonIOException("JSON document was not fully consumed.")
                }
            }
        }
        return result
    }

    private fun getExtracts(annotations: Array<Annotation>): List<Extract> {
        val resExtracts = mutableListOf<Extract>()
        annotations.forEach {
            when (it) {
                is Extract -> resExtracts.add(it)
                is Extracts -> resExtracts.addAll(it.value)
            }
        }
        return resExtracts
    }
}

