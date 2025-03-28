package com.nova.pack.stickers.ads


import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdkUtils
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager.BANNER_APPLOVIN_ID
import com.nova.pack.stickers.ads.AdsManager.BANNER_ID
import com.nova.pack.stickers.ads.AdsManager.ClickCountLimit
import com.nova.pack.stickers.ads.AdsManager.SessionCountLimit
import com.nova.pack.stickers.ads.AdsManager.ShowApplovinAdsAfter_Limit_Reached
import com.nova.pack.stickers.ads.AdsManager.ShowBannerStroke
import com.nova.pack.stickers.ads.AdsManager.sendFirebaseAnalyticsKey
import com.nova.pack.stickers.ads.Constants.Companion.checkIfPlay
import com.nova.pack.stickers.ads.Constants.Companion.clickCounter
import com.nova.pack.stickers.ads.Constants.Companion.isSplashBannerLoad
import com.nova.pack.stickers.ads.NativeAdManager.nativeAdmob
import com.nova.pack.stickers.ads.NativeAdManager.nativeWithoutMedia
import com.nova.pack.stickers.utils.SharedPreferenceHelper.Companion.getSession
import com.nova.pack.stickers.utils.SharedPreferenceHelper.Companion.getSessionNumber
import com.nova.pack.stickers.utils.SharedPreferenceHelper.Companion.saveSession

object BannerAdManager {

    fun loadBannerAd(context: AppCompatActivity, layout: RelativeLayout, banner1: LinearLayout, native: FrameLayout, parameter: Int) {
        if (checkIfPlay) {
            if (!InAppClass.isPurchase) {
                var sessionLimit = getSessionNumber("sessionNum", 0)
                var session = getSession("mySession", false)
                if (ShowBannerStroke) {
                    layout.setBackgroundResource(R.drawable.native_stroke)
                }
                if (parameter == 1) {
                    if (ShowBannerStroke) {
                        layout.setBackgroundResource(R.drawable.native_stroke)
                    }
                    layout.visibility = View.VISIBLE
                    if (sessionLimit <= SessionCountLimit && session) {
                        layout.visibility = View.VISIBLE
                        loadBannerApplovin(context, banner1,layout)
                    } else {
                        var adView = AdView(context)
                        adView.adUnitId = BANNER_ID!!
                        var adSize = getAdaptiveAdSize(context)
                        adView.setAdSize(adSize)
                        Log.d("Banner", "Ad Size: ${adSize.width} x ${adSize.height}")
                        val layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        adView.layoutParams = layoutParams
                        banner1.addView(adView)
                        val adRequest = AdRequest.Builder().build()

                        adView.adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                sendFirebaseAnalyticsKey(AppOpenAdManager.currentActivity!!, "admob_ad", "banner show")
                                isSplashBannerLoad = true
                            }

                            override fun onAdFailedToLoad(p0: LoadAdError) {
                                super.onAdFailedToLoad(p0)
                                Log.e("Banner", "Ad failed to load: $p0")
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                clickCounter++
                                if (clickCounter >= ClickCountLimit && !getSession("mySession", false)) {
                                    saveSession("mySession", true)
                                }
                            }
                        }
                        adView.loadAd(adRequest)
                        Log.d("Banner", "Ad Request made")
                    }
                } else if (parameter == 2) {
                    layout.visibility = View.VISIBLE
                    loadBannerApplovin(context, banner1,layout)
                }else if (parameter == 3) {
                    if (ShowBannerStroke) {
                        layout.setBackgroundResource(R.drawable.native_stroke)
                    }
                    layout.visibility = View.VISIBLE
                    if (sessionLimit <= SessionCountLimit && session) {
                        layout.visibility = View.VISIBLE
                        loadBannerApplovin(context, banner1,layout)
                    } else {
                        if (banner1.toString().lowercase().contains("banner_area2")) {
                            loadCollapsibleBanner(context, banner1, "bottom")
                        } else {
                            loadCollapsibleBanner(context, banner1, "top")
                        }
                    }
                } else if (parameter == 4) {
                    native.visibility = View.VISIBLE
                    if (sessionLimit <= SessionCountLimit && session) {
                        layout.visibility = View.VISIBLE
                        loadBannerApplovin(context, banner1,layout)
                    } else {
                        native.minimumHeight = getHeightForNativeNoMedia(context)
                        if (context.localClassName.lowercase().contains("language")) {
                            nativeWithoutMedia(context, native, context.window, "lang", "top", banner1)
                        } else if (context.localClassName.lowercase().contains("intro")) {
                            nativeWithoutMedia(context, native, context.window, "intro", "top", banner1)
                        } else {
                            nativeWithoutMedia(context, native, context.window, "other", "top", banner1)
                        }
                    }
                } else if (parameter == 5) {
                    if (sessionLimit <= SessionCountLimit && session) {
                        layout.visibility = View.VISIBLE
                        loadBannerApplovin(context, banner1,layout)
                    } else {
                        native.minimumHeight = getHeightForNativeNoMedia(context)
                        native.visibility = View.VISIBLE
                        if (context.localClassName.lowercase().contains("language")) {
                            nativeWithoutMedia(context, native, context.window, "lang", "bottom", banner1)
                        } else if (context.localClassName.lowercase().contains("intro")) {
                            nativeWithoutMedia(context, native, context.window, "intro", "bottom", banner1)
                        } else {
                            nativeWithoutMedia(context, native, context.window, "other", "bottom", banner1)
                        }
                    }
                }else if (parameter == 9) {
                    if (sessionLimit <= SessionCountLimit && session) {
                        layout.visibility = View.VISIBLE
                        loadBannerApplovin(context, banner1,layout)
                    } else {
                        native.minimumHeight = getHeightForNativeNoMedia(context)
                        native.visibility = View.VISIBLE
                        if (context.localClassName.lowercase().contains("language")) {
                            nativeWithoutMedia(context, native, context.window, "lang", "random", banner1)
                        } else if (context.localClassName.lowercase().contains("intro")) {
                            nativeWithoutMedia(context, native, context.window, "intro", "random", banner1)
                        } else {
                            nativeWithoutMedia(context, native, context.window, "other", "random", banner1)
                        }
                    }
                }else if (parameter == 6) {
                    if (sessionLimit <= SessionCountLimit && session) {
                        layout.visibility = View.VISIBLE
                        loadBannerApplovin(context, banner1,layout)
                    } else {
                        native.minimumHeight = getHeightForSmallNative(context)
                        native.visibility = View.VISIBLE
                        if (context.localClassName.lowercase().contains("language")) {
                            nativeWithoutMedia(context, native, context.window, "lang", "small", banner1)
                        } else if (context.localClassName.lowercase().contains("intro")) {
                            nativeWithoutMedia(context, native, context.window, "intro", "small", banner1)
                        } else {
                            nativeWithoutMedia(context, native, context.window, "other", "small", banner1)
                        }
                    }
                } else if (parameter == 7) {
                    if (sessionLimit <= SessionCountLimit && session) {
                        layout.visibility = View.VISIBLE
                        loadBannerApplovin(context, banner1,layout)
                    } else {
                        native.minimumHeight = getHeightForSmallNative(context)
                        native.visibility = View.VISIBLE
                        if (context.localClassName.lowercase().contains("language")) {
                            nativeWithoutMedia(context, native, context.window, "lang", "small2", banner1)
                        } else if (context.localClassName.lowercase().contains("intro")) {
                            nativeWithoutMedia(context, native, context.window, "intro", "small2", banner1)
                        } else {
                            nativeWithoutMedia(context, native, context.window, "other", "small2", banner1)
                        }
                    }
                } else if (parameter == 8) {
                    if (context.localClassName.lowercase().contains("language")) {
                        native.minimumHeight = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._160sdp)
                    }
                    native.visibility = View.VISIBLE
                    if (sessionLimit <= SessionCountLimit && session) {
                    } else {
                        nativeAdmob(context, native, context.window, 1)
                    }
                } else {
                    layout.visibility = View.GONE
                    native.visibility = View.GONE
                }

            }else{
                layout.visibility = View.GONE
                native.visibility = View.GONE
            }
        }else{
            layout.visibility = View.GONE
            native.visibility = View.GONE
        }
    }

    fun loadCollapsibleBanner(activity: Context, nativeAd4: LinearLayout, position: String) {
        if (checkIfPlay) {
            if (!InAppClass.isPurchase) {
                var sessionLimit = getSessionNumber("sessionNum", 0)
                var session = getSession("mySession", false)
                if (sessionLimit <= SessionCountLimit && session) {
                    loadBannerApplovin(
                        activity as AppCompatActivity,
                        nativeAd4, null
                    )
                } else {
                    val adViewMedium = AdView(activity)
                    adViewMedium.setAdSize(getCollapsibleAdSize(activity))
                    adViewMedium.adUnitId = BANNER_ID!!
                    var extras = Bundle()
                    extras.putString("collapsible", position)
                    var adRequest = AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build();
                    adViewMedium.adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            sendFirebaseAnalyticsKey(AppOpenAdManager.currentActivity!!, "admob_ad", "collapsible banner show")
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
                    adViewMedium.loadAd(adRequest)
                    nativeAd4.addView(adViewMedium)
                }
            }
        }
    }
    private fun getAdaptiveAdSize(context: Context): AdSize {
        val display = context.getSystemService(WindowManager::class.java).defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        var adWidthPixels: Float = outMetrics.widthPixels.toFloat()

        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }
    private fun getCollapsibleAdSize(context: Context): AdSize {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }
    fun getHeightForNativeNoMedia(context: Context) : Int {
        return context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._80sdp)
    }

    fun getHeightForSmallNative(context: Context) : Int {
        return context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp)
    }
    fun loadBannerApplovin(activity: AppCompatActivity, frameLayout: LinearLayout, layout: RelativeLayout?) {
        if (!InAppClass.isPurchase) {
            if (ShowApplovinAdsAfter_Limit_Reached) {
                Log.d("adkjahd", activity.localClassName)
                var adViewBannerAppLovin = MaxAdView(BANNER_APPLOVIN_ID, activity)
                adViewBannerAppLovin!!.setListener(object :
                    MaxAdViewAdListener {
                    override fun onAdExpanded(ad: MaxAd) {}
                    override fun onAdCollapsed(ad: MaxAd) {}
                    override fun onAdLoaded(ad: MaxAd) {
                        Log.d("adkjahd", "AppLovin Banner Loaded ")
//                    Toast.makeText(activity, "Applovin Banner loaded", Toast.LENGTH_LONG).show()
                    }

                    override fun onAdDisplayed(ad: MaxAd) {
                        Log.d("adkjahd", "AppLovin Banner Loaded ")
                    }

                    override fun onAdHidden(ad: MaxAd) {}
                    override fun onAdClicked(ad: MaxAd) {}
                    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                        Log.d("adkjahd", "AppLovin Banner Failed $error")
                    }

                    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
                })
                val width = ViewGroup.LayoutParams.MATCH_PARENT
                val heightDp = MaxAdFormat.BANNER.getAdaptiveSize(activity).height
                val heightPx = AppLovinSdkUtils.dpToPx(activity, heightDp)
                adViewBannerAppLovin!!.setLayoutParams(
                    FrameLayout.LayoutParams(
                        width,
                        heightPx,
                        Gravity.CENTER
                    )
                )
//                adViewBannerAppLovin!!.setBackgroundColor(Color.WHITE)
                frameLayout.visibility = View.VISIBLE
                frameLayout.removeAllViews()
                frameLayout.addView(adViewBannerAppLovin)
                adViewBannerAppLovin!!.loadAd()
            }else{
                layout?.visibility=View.GONE
                frameLayout?.visibility=View.GONE
            }
        }
    }
}