package com.nova.pack.stickers.ads

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager.AllAdsToast
import com.nova.pack.stickers.ads.AdsManager.ClickCountLimit
import com.nova.pack.stickers.ads.AdsManager.Enable_Applovin_Interstitial_After_FrequencyCapReached
import com.nova.pack.stickers.ads.AdsManager.INTERESTIAL_ID_APPLOVIN
import com.nova.pack.stickers.ads.AdsManager.INTERSTITIAL_ID
import com.nova.pack.stickers.ads.AdsManager.InterstitialDialogShowTime
import com.nova.pack.stickers.ads.AdsManager.SessionCountLimit
import com.nova.pack.stickers.ads.AdsManager.ShowApplovinAdsAfter_Limit_Reached
import com.nova.pack.stickers.ads.AdsManager.sendFirebaseAnalyticsKey
import com.nova.pack.stickers.ads.Constants.Companion.clickCounter
import com.nova.pack.stickers.ads.Constants.Companion.frequencyLimit
import com.nova.pack.stickers.ads.Constants.Companion.startButtonTask
import com.nova.pack.stickers.utils.CountDownTimer
import com.nova.pack.stickers.utils.SharedPreferenceHelper
import java.util.concurrent.TimeUnit

object InterstitialAdManager {
    var mInterstitialAd: InterstitialAd? = null
    var interstitialAdApplovin: MaxInterstitialAd? = null
    var retry = 0
    var counterInterstitial=0

    fun loadInterstitial(context: Context) {
        if (!InAppClass.isPurchase) {
            if (!SharedPreferenceHelper.getSession("mySession", false)) {
                var adRequest = AdRequest.Builder().build()
                InterstitialAd.load(
                    context as Context,
                    INTERSTITIAL_ID.toString(),
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            if (adError.message.lowercase().contains("frequency cap reached")) {
                                if (Enable_Applovin_Interstitial_After_FrequencyCapReached) {
                                    frequencyLimit = true
                                }
                            }
                            Log.d("sdhgSDsdt", adError.message)
                            Log.d(
                                "sdhgsdt",
                                adError.message.lowercase().contains("frequency cap reached")
                                    .toString()
                            )
                            mInterstitialAd = null
                            val error =
                                "domain: ${adError.domain}, code: ${adError.code}, " + "message: ${adError.message}"
                            Log.d("ERROR AD", "$error")
                            if (AllAdsToast) {
                                Toast.makeText(
                                    context,
                                    "Interstitial Ad : on Ad Failed To Load",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onAdLoaded(ad: InterstitialAd) {
                            Log.d("sdhgSDsdt", "load")
                            frequencyLimit = false
                            counterInterstitial++
//                        Toast.makeText(context, "Interstitial: $counterInterstitial", Toast.LENGTH_LONG).show()
                            mInterstitialAd = ad
                            if (AllAdsToast) {
                                Toast.makeText(
                                    context,
                                    "Interstitial Ad : on Ad Loaded",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                )
            }
        }
    }

    fun showInterstitial(
        context: AppCompatActivity,
        activity: Activity,
        value1: String,
        value2: String,
        value1Detail: String,
        value2Detail: String,
        finish: String,
        key: Int
    ) {
        try {
             if (!InAppClass.isPurchase){
            if (key == 1) {
                if (frequencyLimit) {
                    Log.d("aspinters", "applovin")
                    showInterstitialApplovin(context, activity, value1, value2, value1Detail, value2Detail, finish)
                    return
                }

                val sessionLimit = SharedPreferenceHelper.getSessionNumber("sessionNum", 0)
                val sessionActive = SharedPreferenceHelper.getSession("mySession", false)

                if (sessionLimit <= SessionCountLimit && sessionActive) {
                    Log.d("Sfdmhsf", "applovin")
                    showInterstitialApplovin(context, activity, value1, value2, value1Detail, value2Detail, finish)
                    return
                }

                if (!CountDownTimer.showInterstitial) {
                    launchActivity(finish, context, activity, value1, value2, value1Detail, value2Detail)
                    return
                }

                if (mInterstitialAd == null) {
                    Log.d("ContentValues.TAG", "Ad not loaded.")
                    AppOpenAdManager.isShowingAds = false
                    launchActivity(finish, context, activity, value1, value2, value1Detail, value2Detail)
                    return
                }

                // Show loading dialog if necessary
                val dialog = Dialog(context).apply {
                    setCanceledOnTouchOutside(false)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    setContentView(R.layout.ad_loading_dialog)
                }

                if (InterstitialDialogShowTime > 0) {
                    dialog.show()
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    if (dialog.isShowing && !context.isFinishing) {
                        dialog.dismiss()
                    }

                    mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d("ContentValues.TAG", "Ad was dismissed.")
                            AppOpenAdManager.isShowingAds = false
                            if (AllAdsToast) {
                                Toast.makeText(context, "Ad dismissed", Toast.LENGTH_LONG).show()
                            }

                            launchActivity(finish, context, activity, value1, value2, value1Detail, value2Detail)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.d("ContentValues.TAG", "Ad failed to show: $adError")
                            mInterstitialAd = null
                            if (AllAdsToast) {
                                Toast.makeText(context, "Ad failed to show", Toast.LENGTH_LONG).show()
                            }

                            launchActivity(finish, context, activity, value1, value2, value1Detail, value2Detail)
                        }

                        override fun onAdClicked() {
                            clickCounter++
                            if (clickCounter >= ClickCountLimit && !SharedPreferenceHelper.getSession("mySession", false)) {
                                Log.d("ajhdad", "Saving session state")
                                SharedPreferenceHelper.saveSession("mySession", true)
                            } else {
                                Log.d("ajhdad", "Session state not saved")
                            }
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d("ContentValues.TAG", "Ad showed.")
                            sendFirebaseAnalyticsKey(context, "admob_ad", "interstitial show")
                            AppOpenAdManager.isShowingAds = true

                            // Proactively load the next ad
                            loadInterstitial(context)

                            if (AllAdsToast) {
                                Toast.makeText(context, "Ad showed", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    mInterstitialAd?.show(context)
                }, InterstitialDialogShowTime.toLong())
            } else if (key == 3) {
                showInterstitialApplovin(context, activity, value1, value2, value1Detail, value2Detail, finish)
            } else {
                launchActivity(finish, context, activity, value1, value2, value1Detail, value2Detail)
            }
            }else{
                launchActivity(finish, context, activity, value1, value2, value1Detail, value2Detail)
            }
        } catch (e: Exception) {
            Log.e("showInterstitial", "Error showing interstitial", e)
        }
    }

    fun loadApplovinInterstitial(context: Context) {
        if (!InAppClass.isPurchase) {
            if (ShowApplovinAdsAfter_Limit_Reached) {
                if (INTERESTIAL_ID_APPLOVIN?.isNotEmpty() == true) {
                    interstitialAdApplovin = MaxInterstitialAd(
                        INTERESTIAL_ID_APPLOVIN,
                        context as Activity?
                    )
                    interstitialAdApplovin?.loadAd()
                } else {
                    Log.d("sdjcns", "not current activity")
                }
            }
        }
    }

    fun showInterstitialApplovin(context: AppCompatActivity, activity: Activity, value1: String, value2: String, value1Detail: String, value2Detail: String, finish: String) {
        if (!InAppClass.isPurchase) {
            if (interstitialAdApplovin?.isReady == true) {
                var dialog = Dialog(context)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.ad_loading_dialog)
                if (InterstitialDialogShowTime == 0) {

                } else {
                    dialog.show()
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                    interstitialAdApplovin?.showAd()
                }, InterstitialDialogShowTime!!.toLong())

                AppOpenAdManager.isShowingAds = true

                val adListener: MaxAdListener = object : MaxAdListener {
                    override fun onAdLoaded(ad: MaxAd) {
                        if (AllAdsToast) {
                            Toast.makeText(context, "Applovin Interstitial Ad : on Ad Loaded", Toast.LENGTH_LONG).show()
                        }
                        Log.d("applovinss", "load loaded")
                    }

                    override fun onAdDisplayed(ad: MaxAd) {
                        if (AllAdsToast) {
                            Toast.makeText(context, "Applovin Interstitial Ad : on Ad Displayed", Toast.LENGTH_LONG).show()
                        }
                        Log.d("applovinss", "ad display")
                        AppOpenAdManager.isShowingAds = true
                        //      ApplicationClass.appOpenManager = null
//                            ApplicationClass.s = true
                    }

                    override fun onAdHidden(ad: MaxAd) {
                        if (AllAdsToast) {
                            Toast.makeText(context, "Applovin Interstitial Ad : on Ad Hidden", Toast.LENGTH_LONG).show()
                        }
                        AppOpenAdManager.isShowingAds = false
                        launchActivity(finish,context,activity,value1,value2,value1Detail,value2Detail)
                        loadApplovinInterstitial(context)
                    }


                    override fun onAdClicked(ad: MaxAd) {
                        if (AllAdsToast) {
                            Toast.makeText(context, "Applovin Interstitial Ad : on Ad Clicked", Toast.LENGTH_LONG).show()
                        }
                        Log.d("applovinss", "load click")
                    }

                    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                        if (AllAdsToast) {
                            Toast.makeText(context, "Applovin Interstitial Ad : on Ad Load Failed", Toast.LENGTH_LONG).show()
                        }
                        retry++
                        val delay = TimeUnit.SECONDS.toMillis(
                            Math.pow(2.0, Math.min(6, retry).toDouble()
                            ).toLong()
                        )
                        Handler().postDelayed({
                            interstitialAdApplovin!!.loadAd()
                            Log.d("applovinss", "load failed")
                        }, delay)
                    }

                    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                        if (AllAdsToast) {
                            Toast.makeText(
                                context,
                                "Applovin Interstitial Ad : on Ad Load Failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        Log.d("applovinss", "load failed display")
                        interstitialAdApplovin = null
                        context.finish()
                    }
                }
                interstitialAdApplovin!!.setListener(adListener)
            } else {
                launchActivity(finish,context,activity,value1,value2,value1Detail,value2Detail)
            }
        }else{
            launchActivity(finish,context,activity,value1,value2,value1Detail,value2Detail)
        }
    }

    fun launchActivity(finish: String,context: AppCompatActivity, activity: Activity,value1: String,value2: String,value1Detail: String,value2Detail: String){
        Handler(Looper.getMainLooper()).postDelayed({
            if (finish == "finish") {
                context.finish()
            } else if (finish == "fragment") {

            } else if (finish == "splash") {
                startButtonTask(context)
            } else {
                val intent = Intent(context, activity::class.java)
                intent.putExtra(value1, value1Detail)
                intent.putExtra(value2, value2Detail)
                context.startActivity(intent)
                if (finish == "finishes") {
                    context.finish()
                }
            }
        },100)
    }
}