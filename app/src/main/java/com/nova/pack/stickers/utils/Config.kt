package com.nova.pack.stickers.utils

import android.graphics.Bitmap

class Config {
    companion object{
        const val CONTENT_PROVIDER_AUTHORITY = "com.nova.pack.stickers.provider.StickerContentProvider"
//        const val BASE_URL = "https://dl.dropboxusercontent.com/"
        var isAnim=false
        var isCreate=false
        var bitmap:Bitmap?=null
        var isClickBottom=false
        var isShareApp=false
        var isPrivacyPolicy=false
        var isEmailWriting=false
        var isPlayStoreLaunch=false
    }

}