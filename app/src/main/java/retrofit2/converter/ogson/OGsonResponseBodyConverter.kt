package retrofit2.converter.ogson

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonIOException
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.converter.ogson.annotations.ExtractField
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

        val extractFields = annotations.filterIsInstance<ExtractField>()
        // Если у нас есть хотя обдна аннотация, что нужно извлечь поле
        if (extractFields.isNotEmpty()) {
            val extractField = extractFields[0]
            println(extractField)
            val json: JsonElement = value.use {
                jsonReader.use {reader ->
                    JsonParser.parseReader(reader)
                }
            }
            val subJsonElement = json.asJsonObject?.get(extractField.fieldName)

            result = adapter.fromJsonTree(subJsonElement)

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
}

