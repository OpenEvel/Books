package retrofit2.converter.ogson

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonWriter
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.StandardCharsets.UTF_8

class OGsonRequestBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<T, RequestBody> {
    private val MEDIA_TYPE: MediaType = "application/json; charset=UTF-8".toMediaType()

    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer: Writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        val jsonWriter: JsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        return buffer.readByteString().toRequestBody(MEDIA_TYPE)
    }
}
