package com.nova.pack.stickers.api

import android.util.Log
import com.nova.pack.stickers.model.CategoryStickers
import com.nova.pack.stickers.model.PacksEntities
import com.nova.pack.stickers.model.StickerEntity
import com.nova.pack.stickers.model.StickerPackEntity
import org.json.JSONArray
import org.json.JSONObject

object StickerJson {
    val stickerData = mutableListOf<String>()
    val categoryList = mutableListOf<CategoryStickers>()
    val stickerPacks = mutableListOf<StickerPackEntity>()
    val stickerPacksHome = mutableListOf<StickerPackEntity>()
    val returnStickerPacksHome = mutableListOf<StickerPackEntity>()
    fun parseJsonToCategories(jsonObject: JSONObject): PacksEntities? {
        try {
            val packsArray = jsonObject.getJSONArray("sticker_packs")
            for (i in 0 until packsArray.length()) {
                val categoryObject = packsArray.getJSONObject(i)
                for (categoryKey in categoryObject.keys()) {
                    val categoryArray = categoryObject.getJSONArray(categoryKey)
                     // Log the category name

                    for (j in 0 until categoryArray.length()) {
                        val packJson = categoryArray.getJSONObject(j)
                        val stickersJsonArray = packJson.getJSONArray("stickers")
                        val stickersList = extractStickers(stickersJsonArray)
                        if (!stickerData.contains(categoryKey)) {
                            if (categoryKey != "Home_Stickers" && categoryKey != "Return_Home_Stickers") {
                                Log.d("StickerJson", "Category Name: $categoryKey")
                                stickerData.add(categoryKey)
                                categoryList.add(
                                    CategoryStickers(
                                        categoryKey,
                                        packJson.optString("icon")
                                    )
                                )
                                Log.d("StickerJsodn", "1")
                            }else{
                                Log.d("StickerJson", "2")
                                Log.d("StickerJsodn", "2")
                            }
                        }

                        val stickerPack = StickerPackEntity(
                            id = packJson.optInt("identifier", 0),
                            category_name = packJson.optString("category_name"),
                            icon = packJson.optString("icon"),
                            androidPlayStoreLink = packJson.optString("androidPlayStoreLink"),
                            iosAppStoreLink = packJson.optString("iosAppStoreLink"),
                            publisherEmail = packJson.optString("publisherEmail"),
                            privacyPolicyWebsite = packJson.optString("privacy_policy_website"),
                            licenseAgreementWebsite = packJson.optString("license_agreement_website"),
                            telegram_url = packJson.optString("telegram_url"),
                            identifier = packJson.optString("identifier"),
                            name = packJson.optString("name"),
                            publisher = packJson.optString("publisher"),
                            publisherWebsite = packJson.optString("publisher_website"),
                            animatedStickerPack = packJson.optBoolean("animated_sticker_pack"),
                            stickers = stickersList,
                            trayImageFile = packJson.optString("tray_image_file"))

                        if (categoryKey == "Home_Stickers") {
                            stickerPacksHome.add(stickerPack)
                            Log.d("dfhjdfg", stickerPacksHome.size.toString())
                        }else if (categoryKey == "Return_Home_Stickers") {
                            Log.d("dfhjdfg", stickerPack.stickers.toString())
                            returnStickerPacksHome.add(stickerPack)
                        }else {
                            stickerPacks.add(stickerPack)
                            Log.d("dfhjdfg", stickerPacks.size.toString())
                        }
                    }
                }
            }

            return PacksEntities(stickerPacks, stickerPacksHome, returnStickerPacksHome)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }


    private fun extractStickers(stickersJsonArray: JSONArray): List<StickerEntity> {
        val stickersList = mutableListOf<StickerEntity>()
        for (i in 0 until stickersJsonArray.length()) {
            val stickerJson = stickersJsonArray.getJSONObject(i)
            val emojisArray = stickerJson.optJSONArray("emojis")
            val emojis = mutableListOf<String>()
            emojisArray?.let {
                for (j in 0 until it.length()) {
                    emojis.add(it.getString(j))
                }
            }
            val sticker = StickerEntity(
                emojis = emojis,
                imageFile = stickerJson.optString("image_file"),
                imageFileThum = stickerJson.optString("image_file_thum")
            )
            stickersList.add(sticker)
        }
        return stickersList
    }
}
