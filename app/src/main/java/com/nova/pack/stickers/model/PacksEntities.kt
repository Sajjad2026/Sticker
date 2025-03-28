package com.nova.pack.stickers.model

data class PacksEntities(
    val stickerPacks: List<StickerPackEntity>,
    val stickerPacksHome: List<StickerPackEntity>,
    val returnStickerPacksHome: List<StickerPackEntity>
)
