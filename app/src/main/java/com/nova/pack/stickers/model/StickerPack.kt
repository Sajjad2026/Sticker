package com.nova.pack.stickers.model

data class StickerPack(
    val category_name: String?,
    val icon: String?,
    val androidPlayStoreLink: String?,
    val iosAppStoreLink: String?,
    val publisherEmail: String?,
    val privacyPolicyWebsite: String?,
    val licenseAgreementWebsite: String?,
    val telegram_url: String?,
    val identifier: String?,
    val name: String?,
    val publisher: String,
    val publisherWebsite: String?,
    val animatedStickerPack: Boolean?,
    val stickers: List<Sticker>?,
    val trayImageFile: String?
) {

    fun toStickersPackView(): StickerPackView =
        StickerPackView(
            category_name ?: "",
            icon ?: "",
            androidPlayStoreLink ?: "",
            iosAppStoreLink ?: "",
            publisherEmail ?: "",
            privacyPolicyWebsite ?: "",
            licenseAgreementWebsite ?: "",
            telegram_url ?: "",
            identifier ?: "",
            name ?: "",
            publisher.toString(),
            publisherWebsite ?: "",
            animatedStickerPack ?: false,
            stickers?.map { it.toStickerView() } ?: emptyList(),
            trayImageFile ?: ""
        )

}