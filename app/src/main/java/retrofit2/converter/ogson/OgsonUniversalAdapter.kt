package retrofit2.converter.ogson

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.converter.ogson.extract.getExtracts
import retrofit2.converter.ogson.extract.getSubJson
import retrofit2.converter.ogson.https.isContainsHttpsAnnotation
import retrofit2.converter.ogson.https.toHttps
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.javaType

class OgsonUniversalAdapter : JsonDeserializer<Any?> {

    @OptIn(ExperimentalStdlibApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Any? {
        return when {
            json.isJsonPrimitive -> {
                val prim = json.asJsonPrimitive
                when {
                    prim.isString -> prim.asString
                    prim.isBoolean -> prim.asBoolean
                    prim.isNumber -> {
                        when (typeOfT) {
                            Byte::class.java -> prim.asByte
                            Short::class.java -> prim.asShort
                            Int::class.java -> prim.asInt
                            Long::class.java -> prim.asLong
                            BigInteger::class.java -> prim.asBigInteger
                            Float::class.java -> prim.asFloat
                            Double::class.java -> prim.asDouble
                            BigDecimal::class.java -> prim.asBigDecimal;
                            else -> null
                        }

                    }
                    else -> null
                }
            }

            json.isJsonArray -> {
                val elementType = (typeOfT as ParameterizedType).actualTypeArguments[0]
                val jsonArray = json.asJsonArray
                val list = mutableListOf<Any?>()
                jsonArray.forEach { list.add(context.deserialize(it, elementType)) }
                list
            }

            json.isJsonObject -> {
                val clazz = (typeOfT as Class<*>)
                val constructor = clazz.kotlin.primaryConstructor ?: return null

                // Создаем список значений аргументов из десериализованного JSON
                val args = mutableListOf<Any?>()

                constructor.parameters.forEach {field ->
                    // Получаем имя целевого поля
                    var fieldName = field.name ?: ""
                    // Аннотации для целевого поля
                    val annotations = clazz.getDeclaredField(fieldName).annotations

                    // Если значение поля нужно извлечь из вложенных объектов json
                    // Извлекаем json объекты вниз по дереву
                    var subJson: JsonElement? = json
                    getExtracts(annotations).forEach {extractAnnotation ->
                        subJson = getSubJson(subJson, extractAnnotation.field)
                    }

                    // Если у поля внутри вложенного объекта другое имя, то задаём его
                    val customNames = annotations.filterIsInstance<SerializedName>()
                    if (customNames.isNotEmpty()) {
                        fieldName = customNames[0].value
                    }

                    // Получаем значение поля в виде JsonElement
                    subJson = getSubJson(subJson, fieldName)

                    // Получаем тип поля, если он в виже generic, то его нужно преобразовать в параметризированный
                    val actualType: Type = field.type.javaType

                    // Десериализируем json в нужный тип
                    var fieldValue = context.deserialize<Any?>(subJson, actualType)

                    if (fieldValue is String && isContainsHttpsAnnotation(annotations)) {
                        fieldValue = toHttps(fieldValue)
                    }

                    // Добавляем новый аргумент для конструктора data класса
                    args.add(fieldValue)
                }

                // Возвращаем экземпляр класса
                return constructor.call(*args.toTypedArray())
            }
            else -> null
        }
    }
}
