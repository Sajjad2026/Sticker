package com.nova.pack.stickers.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.nova.pack.stickers.ads.AdsManager.AllAdsToast
import com.nova.pack.stickers.ads.AdsManager.AppOpen_ID
import com.nova.pack.stickers.ads.AdsManager.Enable_Applovin_Interstitial_After_FrequencyCapReached
import com.nova.pack.stickers.ads.AdsManager.ShowAppopenInterstitialOrNot
import com.nova.pack.stickers.ads.AdsManager.sendFirebaseAnalyticsKey
import com.nova.pack.stickers.ads.Constants.Companion.frequencyLimit
import com.nova.pack.stickers.utils.CountDownTimer.startTimerAppOpen
import com.nova.pack.stickers.utils.CountDownTimer.startTimerInterstitial
import com.nova.pack.stickers.utils.SharedPreferenceHelper

class AppOpenAdManager {
    init {
        fetchAd()
    }
    companion object {
        var appOpenAd: AppOpenAd? = null
        var isShowingAds = false
        var loadCallback: AppOpenAd.AppOpenAdLoadCallback? = null
        var isSplashFirst = false
        var currentActivity: Activity? = null
        var counterAppOpen=0

        fun fetchAd() {
            if (InAppClass.isPurchase) return

            if (!SharedPreferenceHelper.getSession("mySession", false)) {
                if (isAdAvailable) return

                loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(loadedAd: AppOpenAd) {
                        appOpenAd = loadedAd
                        frequencyLimit = false
                        counterAppOpen++
//                        Toast.makeText(currentActivity, "AppOpenAd: $counterAppOpen", Toast.LENGTH_LONG).show()
                        logAdStatus("AppOpenAd loaded successfully")
                        showAdToast("AppOpenAd: Ad Loaded", true)
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        handleAdFailure(loadAdError)
                    }
                }

                val adRequest = adRequest
                AppOpenAd.load(
                    currentActivity ?: return,
                    AppOpen_ID!!,
                    adRequest,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    loadCallback as AppOpenAd.AppOpenAdLoadCallback
                )
                logAdStatus("Fetching AppOpenAd with ID: $AppOpen_ID")
            } else {
                logAdStatus("Ad loading skipped due to session settings")
            }
        }

        private fun handleAdFailure(loadAdError: LoadAdError) {
            if (loadAdError.message.lowercase().contains("frequency cap reached") && Enable_Applovin_Interstitial_After_FrequencyCapReached) {
                frequencyLimit = true
            }
            logAdStatus("AppOpenAd failed to load: ${loadAdError.message}", true)
            showAdToast("AppOpenAd: Ad Failed To Load", true)
        }

        fun showAdIfAvailable(ifSplash: String, context: Context?) {
            if (frequencyLimit){
                return
            }
            if (!isShowingAds) {
                val fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        handleAdFailureToShow(adError, context, ifSplash)
                        if (adError.message=="No fill."){
                            fetchAd()
                        }
                    }

                    override fun onAdShowedFullScreenContent() {
                        logAdStatus("AppOpenAd is being shown")
                        sendFirebaseAnalyticsKey(currentActivity!!, "admob_ad", "appopen show")
                        isShowingAds = true
                        appOpenAd = null
                    }

                    override fun onAdDismissedFullScreenContent() {
                        logAdStatus("AppOpenAd dismissed")
                        isShowingAds = false
                        fetchAd()
                        startTimerAppOpen()
                        startTimerInterstitial()
                        if (ifSplash == "splash") {
                            Constants.startButtonTask(context!!)
                        }
                    }

                    override fun onAdClicked() {
                        Constants.clickCounter++
                        if (Constants.clickCounter >= AdsManager.ClickCountLimit && !SharedPreferenceHelper.getSession("mySession", false)) {
                            SharedPreferenceHelper.saveSession("mySession", true)
                        }
                    }
                }

                if (isAdAvailable) {
                    appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
                    appOpenAd?.show(currentActivity!!)
                } else {
                    handleAdNotAvailable(ifSplash, context)
                }
            }
        }

        private fun handleAdFailureToShow(adError: AdError, context: Context?, ifSplash: String) {
            logAdStatus("AppOpenAd failed to show: ${adError.message}", true)
            if (ShowAppopenInterstitialOrNot) {
                InterstitialAdManager.showInterstitial(context as? AppCompatActivity ?: return, currentActivity!!, "","","","","", 1)
            } else if (ifSplash == "splash") {
                Constants.startButtonTask(context!!)
            }
            fetchAd()
        }

        private fun handleAdNotAvailable(ifSplash: String, context: Context?) {
            if (SharedPreferenceHelper.getSession("mySession", false)) {
                if (ifSplash == "splash" && !isSplashFirst) {
                    Constants.startButtonTask(context!!)
                }
            } else if (ShowAppopenInterstitialOrNot) {
                InterstitialAdManager.showInterstitial(context as? AppCompatActivity ?: return, currentActivity!!, "","","","","", 1)
            } else if (ifSplash == "splash") {
                Constants.startButtonTask(context!!)
            }
            isSplashFirst = true
        }

        private val adRequest: AdRequest
            get() = AdRequest.Builder().build()

        val isAdAvailable: Boolean
            get() = appOpenAd != null

        private fun logAdStatus(message: String, isError: Boolean = false) {
            if (isError) {
                Log.e("AppOpenAdManager", message)
            } else {
                Log.d("AppOpenAdManager", message)
            }
        }

        private fun showAdToast(message: String, showToast: Boolean) {
            if (AllAdsToast && showToast) {
                Toast.makeText(currentActivity, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
