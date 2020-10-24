package com.github.basva923.garminphoneactivity.garmin

import com.google.gson.Gson

data class GarminMessage(val command: String, var params: Map<String, String>) {
    fun toDict() = mapOf(
        "command" to command,
        "params" to params
    )

    override fun toString(): String {
        return Gson().toJson(this)
    }


    companion object {
        fun fromDict(map: Map<String, Any>): GarminMessage {
            return GarminMessage(map["command"] as String, map["params"] as Map<String, String>)
        }
    }
}