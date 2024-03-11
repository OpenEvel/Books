package retrofit2.converter.ogson.annotations


// Аннотация Для извлечения поля из ответа и подстановка поля вместо ответа
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ExtractField(val fieldName: String)
