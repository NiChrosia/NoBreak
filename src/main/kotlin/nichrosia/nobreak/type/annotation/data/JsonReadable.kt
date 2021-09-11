package nichrosia.nobreak.type.annotation.data

import kotlinx.serialization.json.JsonElement

interface JsonReadable<R, T> where R : JsonWritable<T>, T : JsonElement {
    fun fromJson(json: T): R
}