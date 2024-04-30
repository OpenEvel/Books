package retrofit2.converter.ogson

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.converter.ogson.extract.getExtracts
import retrofit2.converter.ogson.extract.getSubJson
import java.io.IOException

class OGsonResponseBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>,
    private val annotations: Array<Annotation>,
) : Converter<ResponseBody, T> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T? {
        val jsonReader: JsonReader = gson.newJsonReader(value.charStream())


        // Парсим вcё в json объект
        var json: JsonElement? = value.use {
            jsonReader.use {reader ->
                JsonParser.parseReader(reader)
            }
        }

        getExtracts(annotations).forEach { extractAnnotation ->
            json = getSubJson(json, extractAnnotation.field)
        }

        if (json == null) {
            json = JsonNull.INSTANCE
        }

//        return null
        return adapter.fromJsonTree(json)
    }
}

