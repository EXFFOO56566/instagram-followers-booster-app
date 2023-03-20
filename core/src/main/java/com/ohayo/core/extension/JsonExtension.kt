package com.ohayo.core.extension

import com.google.gson.JsonObject

fun JsonObject.compareMsg(msg: String): Boolean {
    return msg == this.get("msg")?.asString
}

fun JsonObject.compareId(id: String): Boolean {
    return id == this.get("id")?.asString
}

fun JsonObject.compareSubs(id: String): Boolean {
    if (this.has("subs").not()) {
        return false
    }

    val jsonArray = this.getAsJsonArray("subs")
    return id == jsonArray.get(0).asString
}

fun JsonObject.compareCollection(collection: String): Boolean {
    return collection == this.get("collection")?.asString
}