package com.nova.pack.stickers.model

//import com.google.gson.annotations.SerializedName

data class PacksEntity(
//    @SerializedName("sticker_packs")
    val stickerPacks: List<StickerPackEntity> = emptyList()
)