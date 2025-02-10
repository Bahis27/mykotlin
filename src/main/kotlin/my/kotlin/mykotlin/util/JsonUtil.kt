package my.kotlin.mykotlin.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import kotlin.reflect.KClass

object JsonUtil {
    private lateinit var mapper: ObjectMapper

    fun setMapper(mapper: ObjectMapper) {
        JsonUtil.mapper = mapper
    }

    fun <T : Any> readValues(json: String, clazz: KClass<T>): List<T> = try {
        mapper.readerFor(clazz.java).readValues<T>(json).readAll()
    } catch (e: IOException) {
        throw IllegalArgumentException("Invalid read array from JSON:\n'$json'", e)
    }


    fun <T : Any> readValue(json: String, clazz: KClass<T>): T = try {
        mapper.readValue(json, clazz.java)
    } catch (e: IOException) {
        throw IllegalArgumentException("Invalid read from JSON:\n'$json'", e)
    }

    fun <T> writeValue(obj: T): String = try {
        mapper.writeValueAsString(obj)
    } catch (e: JsonProcessingException) {
        throw IllegalStateException("Invalid write to JSON:\n'$obj'", e)
    }

    fun <T> writeAdditionProps(obj: T, addName: String, addValue: Any): String =
        writeAdditionProps(obj, mapOf(addName to addValue))

    fun <T> writeAdditionProps(obj: T, addProps: Map<String, Any>): String =
        mapper.convertValue(obj, object : TypeReference<MutableMap<String, Any>>() {})
            .run {
                putAll(addProps)
                writeValue(this)
            }
}