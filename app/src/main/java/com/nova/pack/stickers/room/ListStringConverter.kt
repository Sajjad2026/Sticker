package com.nova.pack.stickers.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.jeluchu.jchucomponents.ktx.gson.fromJsonList
import com.jeluchu.jchucomponents.ktx.gson.toJson
import com.nova.pack.stickers.model.StickerEntity

class ListStringConverter {
    private val gson = Gson()

    @TypeConverter
    fun stringToListSticker(data: String?): List<StickerEntity?>? = gson.fromJsonList(data)

    @TypeConverter
    fun listStickerToString(data: List<StickerEntity?>?): String? = data.toJson()
}
