package com.nova.pack.stickers.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class StickerPackViewSimplified(
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
