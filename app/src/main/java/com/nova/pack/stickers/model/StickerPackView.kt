package com.nova.pack.stickers.model

import android.os.Parcelable
import java.io.Serializable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StickerPackView(
    val category_name: String?=null,
    val icon: String?=null,
    val androidPlayStoreLink: String?,
    val iosAppStoreLink: String?,
    val publisherEmail: String?,
    val privacyPolicyWebsite: String?,
    val licenseAgreementWebsite: String?,
    val telegram_url: String,
    val identifier: String,
    val name: String,
    val publisher: String,
    val publisherWebsite: String,
    val animatedStickerPack: Boolean,
    val stickers: List<StickerView>,
    val trayImageFile: String
) : Serializable, Parcelable