package com.nova.pack.stickers.ads


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager.ClickCountLimit
import com.nova.pack.stickers.ads.AdsManager.IntroNativeActionColor
import com.nova.pack.stickers.ads.AdsManager.LanguageNativeActionColor
import com.nova.pack.stickers.ads.AdsManager.NATIVE_APPLOVIN_ID
import com.nova.pack.stickers.ads.AdsManager.NATIVE_ID
import com.nova.pack.stickers.ads.AdsManager.NativeActionColorAll
import com.nova.pack.stickers.ads.AdsManager.NativeBgIntro
import com.nova.pack.stickers.ads.AdsManager.NativeBgLanguage
import com.nova.pack.stickers.ads.AdsManager.NativeBgOther
import com.nova.pack.stickers.ads.AdsManager.NativeBgSplash
import com.nova.pack.stickers.ads.AdsManager.SessionCountLimit
import com.nova.pack.stickers.ads.AdsManager.SetNativeBG_Or_Stroke_Intro
import com.nova.pack.stickers.ads.AdsManager.SetNativeBG_Or_Stroke_Languages
import com.nova.pack.stickers.ads.AdsManager.SetNativeBG_Or_Stroke_Others
import com.nova.pack.stickers.ads.AdsManager.SetNativeBG_Or_Stroke_Splash
import com.nova.pack.stickers.ads.AdsManager.ShowApplovinAdsAfter_Limit_Reached
import com.nova.pack.stickers.ads.AdsManager.SplashNativeActionColor
import com.nova.pack.stickers.ads.AdsManager.sendFirebaseAnalyticsKey
import com.nova.pack.stickers.ads.BannerAdManager.loadBannerApplovin
import com.nova.pack.stickers.ads.Constants.Companion.checkIfPlay
import com.nova.pack.stickers.ads.Constants.Companion.clickCounter
import com.nova.pack.stickers.ads.Constants.Companion.isSplashBannerLoad
import com.nova.pack.stickers.databinding.NativeAdmobBinding
import com.nova.pack.stickers.databinding.NativeSmallBinding
import com.nova.pack.stickers.databinding.NativeSmallBottomBinding
import com.nova.pack.stickers.databinding.NativeSmallTopBinding
import com.nova.pack.stickers.databinding.SmallNative2Binding
import com.nova.pack.stickers.utils.SharedPreferenceHelper.Companion.getSession
import com.nova.pack.stickers.utils.SharedPreferenceHelper.Companion.getSessionNumber
import com.nova.pack.stickers.utils.SharedPreferenceHelper.Companion.saveSession
import kotlin.random.Random


object NativeAdManager {
    private var nativeAdLoader: MaxNativeAdLoader? = null
    var nativeAd: MaxAd? = null

    fun nativeWithoutMedia(context: AppCompatActivity, ad_layout: FrameLayout, window: Window, isSplash: String, topBottom: String, banner: LinearLayout?) {
        if (checkIfPlay) {
            if (!InAppClass.isPurchase) {
                var sessionLimit = getSessionNumber("sessionNum", 0)
                var session = getSession("mySession", false)
                if (sessionLimit <= SessionCountLimit && session) {
                    ad_layout.visibility = View.GONE
                    if (banner!=null) {
                        loadBannerApplovin(context, banner!!,null)
                    }
                } else {
                    Log.d("ajhdad", "no")
                    val builder: AdLoader.Builder
                    builder = AdLoader.Builder(context, NATIVE_ID!!)

                    builder.forNativeAd { nativeAdNew ->
                        var activityDestroyed = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            activityDestroyed = context.isDestroyed
                        }
                        if (activityDestroyed || context.isFinishing || context.isChangingConfigurations) {
                            nativeAdNew.destroy()
                            return@forNativeAd
                        }
                        if (topBottom=="top"){
                            val unifiedAdBinding = NativeSmallTopBinding.inflate(window.layoutInflater)
                            populateTopNativeAdView(context,nativeAdNew, unifiedAdBinding,isSplash,ad_layout)
                            ad_layout.removeAllViews()
                            ad_layout.addView(unifiedAdBinding.root)
                        }else if (topBottom=="bottom"){
                            val unifiedAdBinding = NativeSmallBottomBinding.inflate(window.layoutInflater)
                            populateBottomNativeAdView(context,nativeAdNew, unifiedAdBinding,isSplash,ad_layout)
                            ad_layout.removeAllViews()
                            ad_layout.addView(unifiedAdBinding.root)
                        }else if (topBottom=="random"){
                            var random= Random.nextInt(1,3)
                            if (random==1){
                                val unifiedAdBinding = NativeSmallTopBinding.inflate(window.layoutInflater)
                                populateTopNativeAdView(context,nativeAdNew, unifiedAdBinding,isSplash,ad_layout)
                                ad_layout.removeAllViews()
                                ad_layout.addView(unifiedAdBinding.root)
                            }else if (random==2){
                                val unifiedAdBinding = NativeSmallBottomBinding.inflate(window.layoutInflater)
                                populateBottomNativeAdView(context,nativeAdNew, unifiedAdBinding,isSplash,ad_layout)
                                ad_layout.removeAllViews()
                                ad_layout.addView(unifiedAdBinding.root)
                            }else{
                                val unifiedAdBinding = NativeSmallBinding.inflate(window.layoutInflater)
                                populateSmallNativeAdView(context,nativeAdNew, unifiedAdBinding,isSplash,ad_layout)
                                ad_layout.removeAllViews()
                                ad_layout.addView(unifiedAdBinding.root)
                            }
                        }else if (topBottom=="small2"){
                            val unifiedAdBinding = SmallNative2Binding.inflate(window.layoutInflater)
                            populateSmallStrokeNativeAdView(context,nativeAdNew, unifiedAdBinding,isSplash,ad_layout)
                            ad_layout.removeAllViews()
                            ad_layout.addView(unifiedAdBinding.root)
                        }else{
                            val unifiedAdBinding = NativeSmallBinding.inflate(window.layoutInflater)
                            populateSmallNativeAdView(context,nativeAdNew, unifiedAdBinding,isSplash,ad_layout)
                            ad_layout.removeAllViews()
                            ad_layout.addView(unifiedAdBinding.root)
                        }
                    }

                    val videoOptions =
                        VideoOptions.Builder().build()

                    val adOptions =
                        NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

                    builder.withNativeAdOptions(adOptions)

                    val adLoader =
                        builder
                            .withAdListener(
                                object : AdListener() {
                                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                    }

                                    override fun onAdLoaded() {
                                        super.onAdLoaded()
                                        sendFirebaseAnalyticsKey(AppOpenAdManager.currentActivity!!, "admob_ad", "no media native show")
                                        isSplashBannerLoad = true
                                    }

                                    override fun onAdClicked() {
                                        super.onAdClicked()
                                        clickCounter++
                                        if (clickCounter >= ClickCountLimit && !getSession("mySession", false)) {
                                            Log.d("ajhdad", "save")
                                            saveSession("mySession", true)
                                        } else {
                                            Log.d("ajhdad", "not save")
                                        }
                                    }
                                }
                            )
                            .build()

                    adLoader.loadAd(AdRequest.Builder().build())
                }
            }else{
                Log.d("Adjkhadd","1")
                ad_layout.visibility=View.GONE
            }
        }
    }
    fun nativeAdmob(context: AppCompatActivity, ad_layout: FrameLayout, window: Window, key: Int) {
        if (checkIfPlay) {
            if (!InAppClass.isPurchase) {
                if (key == 1) {
                    ad_layout.minimumHeight = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._170sdp)
                    var sessionLimit = getSessionNumber("sessionNum", 0)
                    var session = getSession("mySession", false)
                    if (sessionLimit <= SessionCountLimit && session) {
                        Log.d("sdkjf","native 1")
                        ad_layout.visibility = View.GONE
                    } else {
                        Log.d("sdkjf","native")
                        ad_layout.visibility = View.VISIBLE
                        val builder = AdLoader.Builder(context, NATIVE_ID.toString())
                        builder.forNativeAd { nativeAdNew ->
                            var activityDestroyed = false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                activityDestroyed = context.isDestroyed
                            }
                            if (activityDestroyed || context.isFinishing || context.isChangingConfigurations) {
                                nativeAdNew.destroy()
                                return@forNativeAd
                            }
                            val unifiedAdBinding = NativeAdmobBinding.inflate(window.layoutInflater)
                            populateNativeAdView(context, nativeAdNew, unifiedAdBinding, ad_layout)
                            ad_layout.removeAllViews()
                            ad_layout.addView(unifiedAdBinding.root)
                        }

                        val videoOptions =
                            VideoOptions.Builder().build()

                        val adOptions =
                            NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

                        builder.withNativeAdOptions(adOptions)

                        val adLoader =
                            builder
                                .withAdListener(
                                    object : AdListener() {
                                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                        }

                                        override fun onAdLoaded() {
                                            super.onAdLoaded()
                                            sendFirebaseAnalyticsKey(AppOpenAdManager.currentActivity!!, "admob_ad", "with media native show")
                                            isSplashBannerLoad = true
                                        }

                                        override fun onAdClicked() {
                                            super.onAdClicked()
                                            clickCounter++
                                            if (clickCounter >= ClickCountLimit && !getSession("mySession", false)) {
                                                Log.d("ajhdad", "save")
                                                saveSession("mySession", true)
                                            } else {
                                                Log.d("ajhdad", "not save")
                                            }
                                        }
                                    }
                                )
                                .build()

                        adLoader.loadAd(AdRequest.Builder().build())
                    }
                }else if (key == 2) {
                    ad_layout.minimumHeight = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._170sdp)
                    ad_layout.visibility = View.VISIBLE
                    nativeApplovin(context, ad_layout)
                }else if (key == 3) {
                    ad_layout.minimumHeight = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._80sdp)
                    ad_layout.visibility = View.VISIBLE
                    nativeWithoutMedia(context, ad_layout, context.window, "other", "top", null)
                }else if (key == 4) {
                    ad_layout.minimumHeight = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._80sdp)
                    ad_layout.visibility = View.VISIBLE
                    nativeWithoutMedia(context, ad_layout, context.window, "other", "bottom", null)
                }else if (key == 5) {
                    ad_layout.minimumHeight = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp)
                    ad_layout.visibility = View.VISIBLE
                    nativeWithoutMedia(context, ad_layout, context.window, "other", "small", null)
                } else {
                    ad_layout.visibility = View.GONE
                }
            }else{
                Log.d("Adjkhadd","2")
                ad_layout.visibility=View.GONE
            }
        }
    }
    private fun populateNativeAdView(context: Context, nativeAdNew: NativeAd, unifiedAdBinding: NativeAdmobBinding, frameLayout: FrameLayout) {

        val nativeAdView = unifiedAdBinding.root

        nativeAdView.mediaView = unifiedAdBinding.adMedia

        nativeAdView.headlineView = unifiedAdBinding.adHeadline
        nativeAdView.bodyView = unifiedAdBinding.adBody
        nativeAdView.callToActionView = unifiedAdBinding.adCallToAction
        nativeAdView.iconView = unifiedAdBinding.adAppIcon
        nativeAdView.priceView = unifiedAdBinding.adPrice
        nativeAdView.starRatingView = unifiedAdBinding.adStars
        nativeAdView.storeView = unifiedAdBinding.adStore
        nativeAdView.advertiserView = unifiedAdBinding.adAdvertiser
        Log.d("casdsad",context.javaClass.simpleName)
        if (context.javaClass.simpleName=="LanguageSelectActivity"){
            unifiedAdBinding.adMedia.layoutParams.height=410
            if (LanguageNativeActionColor!="gradient") {
                val color = Color.parseColor(LanguageNativeActionColor)
                val colorStateList = ColorStateList.valueOf(color)
                unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
            }
            if (SetNativeBG_Or_Stroke_Languages ==1) {
                val backgroundColor = Color.parseColor(NativeBgLanguage)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
            }else if (SetNativeBG_Or_Stroke_Languages ==2){
                frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
            }else if (SetNativeBG_Or_Stroke_Languages ==3){
                frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                val backgroundColor = Color.parseColor(NativeBgLanguage)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
            }
        }else if (context.javaClass.simpleName=="IntroActivity"){
            if (IntroNativeActionColor!="gradient") {
                val color = Color.parseColor(IntroNativeActionColor)
                val colorStateList = ColorStateList.valueOf(color)
                unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
            }
            if (SetNativeBG_Or_Stroke_Intro ==1) {
                val backgroundColor = Color.parseColor(NativeBgIntro)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
            }else if (SetNativeBG_Or_Stroke_Intro ==2){
                frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
            }else if (SetNativeBG_Or_Stroke_Intro ==3){
                frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                val backgroundColor = Color.parseColor(NativeBgIntro)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
            }
        }else if (context.javaClass.simpleName=="SplashActivity"){
            if (SplashNativeActionColor!="gradient") {
                val color = Color.parseColor(SplashNativeActionColor)
                val colorStateList = ColorStateList.valueOf(color)
                unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
            }
            if (SetNativeBG_Or_Stroke_Splash ==1) {
                val backgroundColor = Color.parseColor(NativeBgSplash)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
            }else if (SetNativeBG_Or_Stroke_Splash ==2){
                frameLayout.background =context.resources.getDrawable(R.drawable.splash_stroke)
            }else if (SetNativeBG_Or_Stroke_Splash ==3){
                val backgroundColor = Color.parseColor(NativeBgSplash)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                frameLayout.background =context.resources.getDrawable(R.drawable.splash_stroke)
            }else if (SetNativeBG_Or_Stroke_Splash == 4){
                val backgroundColor = Color.parseColor(NativeBgSplash)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                frameLayout.background =context.resources.getDrawable(R.drawable.splash_stroke)
            }
        }else{
            if (NativeActionColorAll!="gradient") {
                val color = Color.parseColor(NativeActionColorAll)
                val colorStateList = ColorStateList.valueOf(color)
                unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
            }
            if (SetNativeBG_Or_Stroke_Others ==1) {
                Log.d("fkljdf","other 1")
                val backgroundColor = Color.parseColor(NativeBgOther)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
            }else if (SetNativeBG_Or_Stroke_Others ==2){
                frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
            }else if (SetNativeBG_Or_Stroke_Others ==3){
                frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                val backgroundColor = Color.parseColor(NativeBgOther)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
            }
        }


        unifiedAdBinding.adHeadline.text = nativeAdNew.headline
        nativeAdNew.mediaContent?.let { unifiedAdBinding.adMedia.setMediaContent(it) }

        if (nativeAdNew.body == null) {
            unifiedAdBinding.adBody.visibility = View.GONE
        } else {
            unifiedAdBinding.adBody.visibility = View.VISIBLE
            unifiedAdBinding.adBody.text = nativeAdNew.body
        }

        if (nativeAdNew.callToAction == null) {
            unifiedAdBinding.adCallToAction.visibility = View.GONE
        } else {
            unifiedAdBinding.adCallToAction.visibility = View.VISIBLE
            unifiedAdBinding.adCallToAction.text = nativeAdNew.callToAction
        }

        if (nativeAdNew.icon == null) {
            unifiedAdBinding.adAppIcon.visibility = View.GONE
        } else {
            unifiedAdBinding.adAppIcon.setImageDrawable(nativeAdNew.icon?.drawable)
            unifiedAdBinding.adAppIcon.visibility = View.VISIBLE
        }

        if (nativeAdNew.price == null) {
            unifiedAdBinding.adPrice.visibility = View.GONE
        } else {
            unifiedAdBinding.adPrice.visibility = View.VISIBLE
            unifiedAdBinding.adPrice.text = nativeAdNew.price
        }

        if (nativeAdNew.store == null) {
            unifiedAdBinding.adStore.visibility = View.GONE
        } else {
            unifiedAdBinding.adStore.visibility = View.VISIBLE
            unifiedAdBinding.adStore.text = nativeAdNew.store
        }

        if (nativeAdNew.starRating == null) {
            unifiedAdBinding.adStars.visibility = View.GONE
        } else {
            unifiedAdBinding.adStars.rating = nativeAdNew.starRating!!.toFloat()
            unifiedAdBinding.adStars.visibility = View.VISIBLE
        }

        if (nativeAdNew.advertiser == null) {
            unifiedAdBinding.adAdvertiser.visibility = View.GONE
        } else {
            unifiedAdBinding.adAdvertiser.text = nativeAdNew.advertiser
            unifiedAdBinding.adAdvertiser.visibility = View.VISIBLE
        }

        nativeAdView.setNativeAd(nativeAdNew)
        val mediaContent = nativeAdNew.mediaContent
        val vc = mediaContent?.videoController
        if (vc != null && mediaContent.hasVideoContent()) {
            vc.videoLifecycleCallbacks =
                object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                    }
                }
        } else {
            Log.d("No Video Ad", "No video ad")
        }
    }
    private fun populateSmallNativeAdView(context: Context, nativeAdNew: NativeAd, unifiedAdBinding: NativeSmallBinding, isSplash: String, frameLayout: FrameLayout) {
        val nativeAdView = unifiedAdBinding.root
        nativeAdView.headlineView = unifiedAdBinding.adHeadline
        nativeAdView.bodyView = unifiedAdBinding.adBody
        nativeAdView.callToActionView = unifiedAdBinding.adCallToAction
        nativeAdView.iconView = unifiedAdBinding.adAppIcon


        if (isSplash != "splash") {
            unifiedAdBinding.adHeadline.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adBody.setTextColor(context.resources.getColor(R.color.black))
            if (isSplash == "lang") {
                val code = LanguageNativeActionColor
                val color = Color.parseColor(code)
                val colorStateList = ColorStateList.valueOf(color)
                unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList

                if (SetNativeBG_Or_Stroke_Languages ==1) {
                    val backgroundColor = Color.parseColor(NativeBgLanguage)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                }else if (SetNativeBG_Or_Stroke_Languages ==2){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                }else if (SetNativeBG_Or_Stroke_Languages ==3){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                    val backgroundColor = Color.parseColor(NativeBgLanguage)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                }
            } else if (isSplash == "intro") {
                val code = IntroNativeActionColor
                val color = Color.parseColor(code)
                val colorStateList = ColorStateList.valueOf(color)
                unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList

                if (SetNativeBG_Or_Stroke_Intro ==1) {
                    val backgroundColor = Color.parseColor(NativeBgIntro)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }else if (SetNativeBG_Or_Stroke_Intro ==2){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                }else if (SetNativeBG_Or_Stroke_Intro ==3){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                    val backgroundColor = Color.parseColor(NativeBgIntro)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }
            } else if (isSplash == "other" || isSplash=="home_sticker") {
                unifiedAdBinding.adHeadline.setTextColor(context.resources.getColor(R.color.black))
                unifiedAdBinding.adBody.setTextColor(context.resources.getColor(R.color.black))

                val color = Color.parseColor(NativeActionColorAll)
                unifiedAdBinding.adCallToAction.backgroundTintList = ColorStateList.valueOf(color)

                if (SetNativeBG_Or_Stroke_Others ==1) {
                    Log.d("fkljdf","other 1")
                    val backgroundColor = Color.parseColor(NativeBgOther)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }else if (SetNativeBG_Or_Stroke_Others ==2){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                }else if (SetNativeBG_Or_Stroke_Others ==3){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                    val backgroundColor = Color.parseColor(NativeBgOther)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }
            }
        } else {
            val color = Color.parseColor(SplashNativeActionColor) // Assuming SplashNativeActionColor is your hexadecimal color string
            val colorStateList = ColorStateList.valueOf(color)
            unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
            unifiedAdBinding.adCallToAction.setTextColor(context.resources.getColor(R.color.white))
            unifiedAdBinding.adHeadline.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adBody.setTextColor(context.resources.getColor(R.color.black))

            if (SetNativeBG_Or_Stroke_Splash ==1) {
                val backgroundColor = Color.parseColor(NativeBgSplash)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
            }else if (SetNativeBG_Or_Stroke_Splash ==2){
                frameLayout.background =context.resources.getDrawable(R.drawable.splash_stroke)
            }else if (SetNativeBG_Or_Stroke_Splash ==3){
                val backgroundColor = Color.parseColor(NativeBgSplash)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                frameLayout.background =context.resources.getDrawable(R.drawable.splash_stroke)
            }
        }

        unifiedAdBinding.adHeadline.text = nativeAdNew.headline

        if (nativeAdNew.body == null) {
            unifiedAdBinding.adBody.visibility = View.GONE
        } else {
            unifiedAdBinding.adBody.visibility = View.VISIBLE
            unifiedAdBinding.adBody.text = nativeAdNew.body
        }

        if (nativeAdNew.callToAction == null) {
            unifiedAdBinding.adCallToAction.visibility = View.GONE
        } else {
            unifiedAdBinding.adCallToAction.visibility = View.VISIBLE
            unifiedAdBinding.adCallToAction.text = nativeAdNew.callToAction
        }

        if (nativeAdNew.icon == null) {
            unifiedAdBinding.adAppIcon.visibility= View.GONE
        } else {
//                    unifiedAdBinding.adAppIcon.mediaContent?.mainImage = nativeAdNew.icon?.drawable
            unifiedAdBinding.adAppIcon.setImageDrawable(nativeAdNew.icon!!.drawable)
            unifiedAdBinding.adAppIcon.visibility = View.VISIBLE
        }

        nativeAdView.setNativeAd(nativeAdNew)
        val mediaContent = nativeAdNew.mediaContent
        val vc = mediaContent?.videoController
        if (vc != null && mediaContent.hasVideoContent()) {
            vc.videoLifecycleCallbacks =
                object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                    }
                }
        } else {
            Log.d("No Video Ad", "No video ad")
        }
    }
    private fun populateSmallStrokeNativeAdView(context: Context, nativeAdNew: NativeAd, unifiedAdBinding: SmallNative2Binding, isSplash: String, frameLayout: FrameLayout) {
        val nativeAdView = unifiedAdBinding.root
        nativeAdView.headlineView = unifiedAdBinding.adHeadline
        nativeAdView.bodyView = unifiedAdBinding.adBody
        nativeAdView.callToActionView = unifiedAdBinding.adCallToAction
        nativeAdView.iconView = unifiedAdBinding.adAppIcon


        if (isSplash != "splash") {
            unifiedAdBinding.adHeadline.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adBody.setTextColor(context.resources.getColor(R.color.black))
            if (isSplash == "lang") {

                if (SetNativeBG_Or_Stroke_Languages ==1) {
                    val backgroundColor = Color.parseColor(NativeBgLanguage)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                }else if (SetNativeBG_Or_Stroke_Languages ==2){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                }else if (SetNativeBG_Or_Stroke_Languages ==3){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                    val backgroundColor = Color.parseColor(NativeBgLanguage)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                }
            } else if (isSplash == "intro") {
                if (SetNativeBG_Or_Stroke_Intro ==1) {
                    val backgroundColor = Color.parseColor(NativeBgIntro)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }else if (SetNativeBG_Or_Stroke_Intro ==2){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                }else if (SetNativeBG_Or_Stroke_Intro ==3){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                    val backgroundColor = Color.parseColor(NativeBgIntro)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }
            } else if (isSplash == "other" || isSplash=="home_sticker") {
                unifiedAdBinding.adHeadline.setTextColor(context.resources.getColor(R.color.black))
                unifiedAdBinding.adBody.setTextColor(context.resources.getColor(R.color.black))

                if (SetNativeBG_Or_Stroke_Others ==1) {
                    Log.d("fkljdf","other 1")
                    val backgroundColor = Color.parseColor(NativeBgOther)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }else if (SetNativeBG_Or_Stroke_Others ==2){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                }else if (SetNativeBG_Or_Stroke_Others ==3){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                    val backgroundColor = Color.parseColor(NativeBgOther)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }
            }
        } else {
            unifiedAdBinding.adHeadline.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adBody.setTextColor(context.resources.getColor(R.color.black))
            if (SetNativeBG_Or_Stroke_Splash ==1) {
                val backgroundColor = Color.parseColor(NativeBgSplash)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
            }else if (SetNativeBG_Or_Stroke_Splash ==2){
                frameLayout.background =context.resources.getDrawable(R.drawable.splash_stroke)
            }else if (SetNativeBG_Or_Stroke_Splash ==3){
                val backgroundColor = Color.parseColor(NativeBgSplash)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                frameLayout.background =context.resources.getDrawable(R.drawable.splash_stroke)
            }
        }

        unifiedAdBinding.adHeadline.text = nativeAdNew.headline

        if (nativeAdNew.body == null) {
            unifiedAdBinding.adBody.visibility = View.GONE
        } else {
            unifiedAdBinding.adBody.visibility = View.VISIBLE
            unifiedAdBinding.adBody.text = nativeAdNew.body
        }

        if (nativeAdNew.callToAction == null) {
            unifiedAdBinding.adCallToAction.visibility = View.GONE
        } else {
            unifiedAdBinding.adCallToAction.visibility = View.VISIBLE
            unifiedAdBinding.adCallToAction.text = nativeAdNew.callToAction
        }

        if (nativeAdNew.icon == null) {
            unifiedAdBinding.adAppIcon.visibility= View.GONE
        } else {
//                    unifiedAdBinding.adAppIcon.mediaContent?.mainImage = nativeAdNew.icon?.drawable
            unifiedAdBinding.adAppIcon.setImageDrawable(nativeAdNew.icon!!.drawable)
            unifiedAdBinding.adAppIcon.visibility = View.VISIBLE
        }

        nativeAdView.setNativeAd(nativeAdNew)
        val mediaContent = nativeAdNew.mediaContent
        val vc = mediaContent?.videoController
        if (vc != null && mediaContent.hasVideoContent()) {
            vc.videoLifecycleCallbacks =
                object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                    }
                }
        } else {
            Log.d("No Video Ad", "No video ad")
        }
    }
    private fun populateTopNativeAdView(context: Context, nativeAdNew: NativeAd, unifiedAdBinding: NativeSmallTopBinding, isSplash: String, frameLayout: FrameLayout) {
        val nativeAdView = unifiedAdBinding.root
        nativeAdView.headlineView = unifiedAdBinding.adHeadline
        nativeAdView.bodyView = unifiedAdBinding.adBody
        nativeAdView.callToActionView = unifiedAdBinding.adCallToAction
        nativeAdView.iconView = unifiedAdBinding.adAppIcon
        nativeAdView.priceView = unifiedAdBinding.adPrice
        nativeAdView.starRatingView = unifiedAdBinding.adStars
        nativeAdView.storeView = unifiedAdBinding.adStore
        nativeAdView.advertiserView = unifiedAdBinding.adAdvertiser

        if (isSplash != "splash") {
            unifiedAdBinding.adHeadline.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adBody.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adPrice.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adCallToAction.setTextColor(context.resources.getColor(R.color.white))
            if (isSplash == "lang") {
                if (LanguageNativeActionColor!="gradient") {
                    val color = Color.parseColor(LanguageNativeActionColor)
                    val colorStateList = ColorStateList.valueOf(color)
                    unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
                }
                if (SetNativeBG_Or_Stroke_Languages ==1) {
                    val backgroundColor = Color.parseColor(NativeBgLanguage)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                }else if (SetNativeBG_Or_Stroke_Languages ==2){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                }else if (SetNativeBG_Or_Stroke_Languages ==3){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                    val backgroundColor = Color.parseColor(NativeBgLanguage)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                }
            } else if (isSplash == "intro") {
                if (IntroNativeActionColor!="gradient") {
                    val color = Color.parseColor(IntroNativeActionColor)
                    val colorStateList = ColorStateList.valueOf(color)
                    unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
                }

                if (SetNativeBG_Or_Stroke_Intro ==1) {
                    val backgroundColor = Color.parseColor(NativeBgIntro)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }else if (SetNativeBG_Or_Stroke_Intro ==2){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                }else if (SetNativeBG_Or_Stroke_Intro ==3){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                    val backgroundColor = Color.parseColor(NativeBgIntro)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }
            } else if (isSplash == "other" || isSplash=="home_sticker") {
                unifiedAdBinding.adHeadline.setTextColor(context.resources.getColor(R.color.black))
                unifiedAdBinding.adBody.setTextColor(context.resources.getColor(R.color.black))
                unifiedAdBinding.adAdvertiser.setTextColor(context.resources.getColor(R.color.black))
                unifiedAdBinding.adPrice.setTextColor(context.resources.getColor(R.color.black))
                unifiedAdBinding.adCallToAction.setTextColor(context.resources.getColor(R.color.white))


                if (NativeActionColorAll!="gradient") {
                    val color = Color.parseColor(NativeActionColorAll)
                    val colorStateList = ColorStateList.valueOf(color)
                    unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
                }

                if (SetNativeBG_Or_Stroke_Others ==1) {
                    Log.d("fkljdf","other 1")
                    val backgroundColor = Color.parseColor(NativeBgOther)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }else if (SetNativeBG_Or_Stroke_Others ==2){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                }else if (SetNativeBG_Or_Stroke_Others ==3){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                    val backgroundColor = Color.parseColor(NativeBgOther)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }
            }
        } else {
            if (SplashNativeActionColor!="gradient") {
                val color = Color.parseColor(SplashNativeActionColor)
                val colorStateList = ColorStateList.valueOf(color)
                unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
            }
            unifiedAdBinding.adCallToAction.setTextColor(context.resources.getColor(R.color.white))
            unifiedAdBinding.adAdvertiser.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adHeadline.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adPrice.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adStore.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adBody.setTextColor(context.resources.getColor(R.color.black))

            if (SetNativeBG_Or_Stroke_Splash ==1) {
                val backgroundColor = Color.parseColor(NativeBgSplash)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
            }else if (SetNativeBG_Or_Stroke_Splash ==2){
                frameLayout.background =context.resources.getDrawable(R.drawable.splash_stroke)
            }else if (SetNativeBG_Or_Stroke_Splash ==3){
                val backgroundColor = Color.parseColor(NativeBgSplash)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                frameLayout.background =context.resources.getDrawable(R.drawable.splash_stroke)
            }
        }

        unifiedAdBinding.adHeadline.text = nativeAdNew.headline

        if (nativeAdNew.body == null) {
            unifiedAdBinding.adBody.visibility = View.GONE
        } else {
            unifiedAdBinding.adBody.visibility = View.VISIBLE
            unifiedAdBinding.adBody.text = nativeAdNew.body
        }

        if (nativeAdNew.callToAction == null) {
            unifiedAdBinding.adCallToAction.visibility = View.GONE
        } else {
            unifiedAdBinding.adCallToAction.visibility = View.VISIBLE
            unifiedAdBinding.adCallToAction.text = nativeAdNew.callToAction
        }

        if (nativeAdNew.icon == null) {
            unifiedAdBinding.adAppIcon.visibility= View.GONE
        } else {
//                    unifiedAdBinding.adAppIcon.mediaContent?.mainImage = nativeAdNew.icon?.drawable
            unifiedAdBinding.adAppIcon.setImageDrawable(nativeAdNew.icon!!.drawable)
            unifiedAdBinding.adAppIcon.visibility = View.VISIBLE
        }

        if (nativeAdNew.price == null) {
            unifiedAdBinding.adPrice.visibility = View.GONE
        } else {
            unifiedAdBinding.adPrice.visibility = View.VISIBLE
            unifiedAdBinding.adPrice.text = nativeAdNew.price
        }

        if (nativeAdNew.store == null) {
            unifiedAdBinding.adStore.visibility = View.GONE
        } else {
            unifiedAdBinding.adStore.visibility = View.VISIBLE
            unifiedAdBinding.adStore.text = nativeAdNew.store
        }

        if (nativeAdNew.starRating == null) {
            unifiedAdBinding.adStars.visibility = View.GONE
        } else {
            unifiedAdBinding.adStars.rating = nativeAdNew.starRating!!.toFloat()
            unifiedAdBinding.adStars.visibility = View.VISIBLE
        }

        if (nativeAdNew.advertiser == null) {
            unifiedAdBinding.adAdvertiser.visibility = View.GONE
        } else {
            unifiedAdBinding.adAdvertiser.text = nativeAdNew.advertiser
            unifiedAdBinding.adAdvertiser.visibility = View.VISIBLE
        }

        nativeAdView.setNativeAd(nativeAdNew)
        val mediaContent = nativeAdNew.mediaContent
        val vc = mediaContent?.videoController
        if (vc != null && mediaContent.hasVideoContent()) {
            vc.videoLifecycleCallbacks =
                object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                    }
                }
        } else {
            Log.d("No Video Ad", "No video ad")
        }
    }
    private fun populateBottomNativeAdView(context: Context, nativeAdNew: NativeAd, unifiedAdBinding: NativeSmallBottomBinding, isSplash: String, frameLayout: FrameLayout) {

        val nativeAdView = unifiedAdBinding.root
        nativeAdView.headlineView = unifiedAdBinding.adHeadline
        nativeAdView.bodyView = unifiedAdBinding.adBody
        nativeAdView.callToActionView = unifiedAdBinding.adCallToAction
        nativeAdView.iconView = unifiedAdBinding.adAppIcon
        nativeAdView.priceView = unifiedAdBinding.adPrice
        nativeAdView.starRatingView = unifiedAdBinding.adStars
        nativeAdView.storeView = unifiedAdBinding.adStore
        nativeAdView.advertiserView = unifiedAdBinding.adAdvertiser

        if (isSplash != "splash") {

            unifiedAdBinding.adHeadline.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adBody.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adPrice.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adCallToAction.setTextColor(context.resources.getColor(R.color.white))
            if (isSplash == "lang") {
                if (LanguageNativeActionColor!="gradient") {
                    val color = Color.parseColor(LanguageNativeActionColor)
                    val colorStateList = ColorStateList.valueOf(color)
                    unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
                }

                if (SetNativeBG_Or_Stroke_Languages ==1) {
                    val backgroundColor = Color.parseColor(NativeBgLanguage)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                }else if (SetNativeBG_Or_Stroke_Languages ==2){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                }else if (SetNativeBG_Or_Stroke_Languages ==3){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                    val backgroundColor = Color.parseColor(NativeBgLanguage)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                }
            } else if (isSplash == "intro") {
                if (IntroNativeActionColor!="gradient") {
                    val color = Color.parseColor(IntroNativeActionColor)
                    val colorStateList = ColorStateList.valueOf(color)
                    unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
                }

                if (SetNativeBG_Or_Stroke_Intro ==1) {
                    val backgroundColor = Color.parseColor(NativeBgIntro)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }else if (SetNativeBG_Or_Stroke_Intro ==2){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                }else if (SetNativeBG_Or_Stroke_Intro ==3){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                    val backgroundColor = Color.parseColor(NativeBgIntro)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }
            } else if (isSplash == "other" || isSplash == "home_sticker") {
                Log.d("fkljdf","other 1")
                if (NativeActionColorAll!="gradient") {
                    val color = Color.parseColor(NativeActionColorAll)
                    val colorStateList = ColorStateList.valueOf(color)
                    unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
                }
                unifiedAdBinding.adHeadline.setTextColor(context.resources.getColor(R.color.black))
                unifiedAdBinding.adBody.setTextColor(context.resources.getColor(R.color.black))
                unifiedAdBinding.adAdvertiser.setTextColor(context.resources.getColor(R.color.black))
                unifiedAdBinding.adPrice.setTextColor(context.resources.getColor(R.color.black))
                unifiedAdBinding.adCallToAction.setTextColor(context.resources.getColor(R.color.white))

                if (SetNativeBG_Or_Stroke_Others ==1) {
                    val backgroundColor = Color.parseColor(NativeBgOther)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }else if (SetNativeBG_Or_Stroke_Others ==2){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                }else if (SetNativeBG_Or_Stroke_Others ==3){
                    frameLayout.background =context.resources.getDrawable(R.drawable.native_stroke)
                    val backgroundColor = Color.parseColor(NativeBgOther)
                    val colorStateList = ColorStateList.valueOf(backgroundColor)
                    unifiedAdBinding.bgNative.backgroundTintList=(colorStateList)
                }
            }
        } else {
            if (SplashNativeActionColor!="gradient") {
                val color = Color.parseColor(SplashNativeActionColor)
                val colorStateList = ColorStateList.valueOf(color)
                unifiedAdBinding.adCallToAction.backgroundTintList = colorStateList
            }
            unifiedAdBinding.adCallToAction.setTextColor(context.resources.getColor(R.color.white))
            unifiedAdBinding.adAdvertiser.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adHeadline.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adPrice.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adStore.setTextColor(context.resources.getColor(R.color.black))
            unifiedAdBinding.adBody.setTextColor(context.resources.getColor(R.color.black))

            if (SetNativeBG_Or_Stroke_Splash ==1) {
                val backgroundColor = Color.parseColor(NativeBgSplash)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
            }else if (SetNativeBG_Or_Stroke_Splash ==2){
                frameLayout.background =context.resources.getDrawable(R.drawable.splash_stroke)
            }else if (SetNativeBG_Or_Stroke_Splash ==3){
                val backgroundColor = Color.parseColor(NativeBgSplash)
                val colorStateList = ColorStateList.valueOf(backgroundColor)
                unifiedAdBinding.bgNative.backgroundTintList = (colorStateList)
                frameLayout.background =context.resources.getDrawable(R.drawable.splash_stroke)
            }
        }

        unifiedAdBinding.adHeadline.text = nativeAdNew.headline

        if (nativeAdNew.body == null) {
            unifiedAdBinding.adBody.visibility = View.GONE
        } else {
            unifiedAdBinding.adBody.visibility = View.VISIBLE
            unifiedAdBinding.adBody.text = nativeAdNew.body
        }

        if (nativeAdNew.callToAction == null) {
            unifiedAdBinding.adCallToAction.visibility = View.GONE
        } else {
            unifiedAdBinding.adCallToAction.visibility = View.VISIBLE
            unifiedAdBinding.adCallToAction.text = nativeAdNew.callToAction
        }

        if (nativeAdNew.icon == null) {
            unifiedAdBinding.adAppIcon.visibility= View.GONE
        } else {
//                    unifiedAdBinding.adAppIcon.mediaContent?.mainImage = nativeAdNew.icon?.drawable
            unifiedAdBinding.adAppIcon.setImageDrawable(nativeAdNew.icon!!.drawable)
            unifiedAdBinding.adAppIcon.visibility = View.VISIBLE
        }

        if (nativeAdNew.price == null) {
            unifiedAdBinding.adPrice.visibility = View.GONE
        } else {
            unifiedAdBinding.adPrice.visibility = View.VISIBLE
            unifiedAdBinding.adPrice.text = nativeAdNew.price
        }

        if (nativeAdNew.store == null) {
            unifiedAdBinding.adStore.visibility = View.GONE
        } else {
            unifiedAdBinding.adStore.visibility = View.VISIBLE
            unifiedAdBinding.adStore.text = nativeAdNew.store
        }

        if (nativeAdNew.starRating == null) {
            unifiedAdBinding.adStars.visibility = View.GONE
        } else {
            unifiedAdBinding.adStars.rating = nativeAdNew.starRating!!.toFloat()
            unifiedAdBinding.adStars.visibility = View.VISIBLE
        }

        if (nativeAdNew.advertiser == null) {
            unifiedAdBinding.adAdvertiser.visibility = View.GONE
        } else {
            unifiedAdBinding.adAdvertiser.text = nativeAdNew.advertiser
            unifiedAdBinding.adAdvertiser.visibility = View.VISIBLE
        }

        nativeAdView.setNativeAd(nativeAdNew)
        val mediaContent = nativeAdNew.mediaContent
        val vc = mediaContent?.videoController
        if (vc != null && mediaContent.hasVideoContent()) {
            vc.videoLifecycleCallbacks =
                object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                    }
                }
        } else {
            Log.d("No Video Ad", "No video ad")
        }
    }
    fun nativeApplovin(context: Context, frameLayout: FrameLayout) {
        if (ShowApplovinAdsAfter_Limit_Reached) {
            nativeAdLoader = MaxNativeAdLoader(NATIVE_APPLOVIN_ID, context)
            nativeAdLoader?.loadAd(createNativeAdView(context))
//            nativeAdLoader!!.setRevenueListener(context)
            nativeAdLoader!!.setNativeAdListener(object :
                MaxNativeAdListener() {
                override fun onNativeAdLoaded(
                    nativeAdView: MaxNativeAdView?,
                    nativeAds: MaxAd
                ) {
                    // Clean up any pre-existing native ad to prevent memory leaks.
                    super.onNativeAdLoaded(nativeAdView, nativeAds)
                    if (nativeAd != null) {
                        nativeAdLoader?.destroy(nativeAd)
                    }
                    // Save ad for cleanup.
                    nativeAd = nativeAds
                    frameLayout!!.removeAllViews()
                    frameLayout!!.addView(nativeAdView)
                    Log.d("nativeopen", "load 1")

                }

                override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                    Log.d("nativeopen", error.toString())
                    // Native ad load failed.
                    // AppLovin recommends retrying with exponentially higher delays up to a maximum delay.
                }

                override fun onNativeAdClicked(nativeAd: MaxAd) {}
            })
        }else{
            frameLayout.visibility= View.GONE
        }
    }
    private fun createNativeAdView(context: Context): MaxNativeAdView {
        val binder: MaxNativeAdViewBinder =
            MaxNativeAdViewBinder.Builder(R.layout.native_applovin)
                .setTitleTextViewId(R.id.title_text_view)
                .setBodyTextViewId(R.id.body_text_view)
                .setAdvertiserTextViewId(R.id.advertiser_textView)
                .setIconImageViewId(R.id.icon_image_view)
                .setMediaContentViewGroupId(R.id.media_view_container)
                .setOptionsContentViewGroupId(R.id.ad_options_view)
                .setCallToActionButtonId(R.id.cta_button)
                .build()
        return MaxNativeAdView(binder, context)
    }
}