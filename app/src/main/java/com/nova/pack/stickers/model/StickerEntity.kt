package com.nova.pack.stickers.model


data class StickerEntity(
    //  @SerializedName("emojis")
    val emojis: List<String>?,
    //  @SerializedName("image_file")
    var imageFile: String?,
    //  @SerializedName("image_file_thum")
    val imageFileThum: String?
) {

    fun toStickers(): Sticker =
        Sticker(
            emojis,
            imageFile,
            imageFileThum
        )

}