package com.munir_atef.pha_viewer.models

import androidx.compose.ui.graphics.Color
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class AppIconModel(private val iconJson: JsonObject?) {
    private val path: String? = iconJson?.get("path")?.asString
    private val colorArray: JsonArray? = iconJson?.getAsJsonArray("background")
    private val padding: Float = iconJson?.get("padding")?.asFloat ?: 0f
    private val borderRadius: Float = iconJson?.get("borderRadius")?.asFloat ?: 0f

    private val color = if (colorArray == null || colorArray.size() < 4) Color.Transparent
        else Color(
            colorArray.get(0).asInt,
            colorArray.get(1).asInt,
            colorArray.get(2).asInt,
            colorArray.get(3).asInt
        )

    override fun toString(): String {
        return iconJson.toString()
    }

    fun iconAsJson(): JsonObject? {
        return iconJson
    }

    fun iconPath(): String? { return if (path != null) "/assets$path" else null }
    fun background(): Color { return color }
    fun paddingRatio(): Float { return padding }
    fun borderRadiusRatio(): Float { return borderRadius }
}

