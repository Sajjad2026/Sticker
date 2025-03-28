package com.nova.pack.stickers.model

data class Sticker(
    val emojis: List<String>?,
    val imageFile: String?,
    val imageFileThum: String?
) {

    fun toStickerView(): StickerView =
        StickerView(
            emojis ?: emptyList(),
            imageFile ?: "",
            imageFileThum ?: ""
        )

}