package nichrosia.nobreak.type.annotation.data

import kotlinx.serialization.json.JsonElement

interface JsonWritable<T> where T : JsonElement {
    fun toJson(): T
}