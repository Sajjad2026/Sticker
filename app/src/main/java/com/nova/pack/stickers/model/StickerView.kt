package com.nova.pack.stickers.model
import android.os.Parcelable
import java.io.Serializable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StickerView(
    val emojis: List<String>,
    val imageFile: String,
    val imageFileThum: String
) : Serializable, Parcelable