package com.sappyoak.konscriptor.common.serialization

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

fun serializeMap(map: Map<String, Any?>): String {
    val element = map.toJsonElement()
    return element.toString()
}

fun serializePrimitive(value: Any): JsonPrimitive = when (value) {
    is Number -> JsonPrimitive(value)
    is Boolean -> JsonPrimitive(value)
    else -> JsonPrimitive(value.toString())
}

fun List<*>.toJsonElement(): JsonElement {
    val list = mutableListOf<JsonElement>()
    forEach {
        val value = it ?: return@forEach
        when (value) {
            is Map<*, *> -> list.add((value).toJsonElement())
            is List<*> -> list.add(value.toJsonElement())
            else -> list.add(serializePrimitive(value))
        }
    }
    return JsonArray(list)
}

fun Map<*, *>.toJsonElement(): JsonElement {
    val map = mutableMapOf<String, JsonElement>()
    forEach {
        val key = it.key as? String ?: return@forEach
        val value = it.value ?: return@forEach
        when (value) {
            is Map<*, *> -> map[key] = value.toJsonElement()
            is List<*> -> map[key] = value.toJsonElement()
            else -> map[key] = serializePrimitive(value)
        }
    }
    return JsonObject(map)
}

