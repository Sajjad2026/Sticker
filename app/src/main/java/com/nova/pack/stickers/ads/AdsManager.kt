package com.nova.pack.stickers.ads

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.nova.pack.stickers.ads.Constants.Companion.checkIfPlay
import com.nova.pack.stickers.ads.Constants.Companion.ifAdNotFetch
import com.nova.pack.stickers.ads.Constants.Companion.isDataFetch
import com.nova.pack.stickers.ads.InterstitialAdManager.loadApplovinInterstitial
import com.nova.pack.stickers.ads.InterstitialAdManager.loadInterstitial
import com.nova.pack.stickers.utils.SharedPreferenceHelper
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object AdsManager{
//    test
//    var BANNER_ID: String? = "ca-app-pub-3940256099942544/9214589741"
//    var INTERSTITIAL_ID: String? = "ca-app-pub-3940256099942544/1033173712"
//    var AppOpen_ID: String? = "ca-app-pub-3940256099942544/9257395921"
//    var NATIVE_ID: String? = "ca-app-pub-3940256099942544/2247696110"
    //real
    var BANNER_ID: String? = ""
    var INTERSTITIAL_ID: String? = ""
    var AppOpen_ID: String? = ""
    var NATIVE_ID: String? = ""

 //   var OPENAPP_INTERSTITIAL_ID: String? = ""
  //  var BANNER_ID_SPLASH: String? = ""
  //  var CollapsibleBanner_ID_Splash: String? = ""
//    var NATIVE_ID_SPLASH: String? = ""
//    var NATIVE_ID_SPLASH_2: String? = ""
//    var NATIVE_ID_WITHOUT_MEDIA: String? = ""

    var INTERESTIAL_ID_APPLOVIN = "6cd3cacdae4c33bd"
    var BANNER_APPLOVIN_ID = "1396a017da035e55"
    var NATIVE_APPLOVIN_ID = "01e87f9fb370f0c4"

    var STICKER_PACK_JSON:JSONObject?=null
    var RETURNING_HOME_STICKERS_JSON:JSONObject?=null
//  others
    var SplashNativeActionColor:String?=""
    var LanguageNativeActionColor:String?=""
    var IntroNativeActionColor:String?=""
    var NativeActionColorAll:String?=""
    var NativeBgSplash:String?=""
    var NativeBgLanguage:String?=""
    var NativeBgIntro:String?=""
    var NativeBgOther:String?=""
    var AppLink:String?=""
    //price discount
    var OneWeekOff=""
    var OneMonthOff=""
    var ThreeMonthOff=""
    var LifetimeOff=""

    var PriorityAdmobOrApplovin=false
    var NativeReturningHomeActivity=0
    var ShowBannerStroke=false
    var IsLanguageSelectionFirstTime=false
    var IsLanguageNextButtonShow=false
    var TimerLanguagesSelection=0
    var ShowUpdateDialog= false
    var ShowInAppAfterIntro= false
    var ShowInAppAfterReturning= false
    var InterstitialDialogShowTime=0
    var AppOpenSplash=0
    var NativeExitActivity=0
    var AllAdsToast=false
    var Is_InApp_Screen_Show_Or_Not=false
    var ShowAppopenInterstitialOrNot=false
    var BannerAndNativeTopSplashActivity=8
    var BannerAndNativeBottomSplashActivity=0
    var BannerBottomLanguagesActivity=0
    var BannerTopMainActivity=0
    var BannerBottomMainActivity=0
    var BannerTopLanguagesActivity=0
    var BannerTopCategoryStickerFragment=0
    var BannerBottomCategoryStickerFragment=0
    var BannerTopSearchStickersActivity=0
    var BannerBottomSearchStickersActivity=0
    var BannerTopMyStickersActivity=0
    var BannerBottomMyStickersActivity=0
    var BannerTopSettingActivity=0
    var BannerBottomSettingActivity=0
    var BannerTopCutOutActivity=0
    var BannerBottomCutOutActivity=0
    var BannerTopCropImageActivity=0
    var BannerTopHomeFragment=0
    var BannerTopMyStickerFragment=0
    var BannerBottomMyStickerFragment=0
    var BannerBottomHomeFragment=0
    var BannerBottomCropImageActivity=0
    var BannerTopCreateStickerActivity=0
    var BannerBottomCreateStickerActivity=0
    var BannerTopCreatedStickerActivity=0
    var BannerBottomCreatedStickerActivity=0
    var BackInterstitialLanguagesToSettings=0
    var BackInterstitialStickerDetailActivity=0
    var BackInterstitialCategoryStickerFragment=0
    var InterstitialLanguagesToSetting=0
    var InterstitialLanguageToIntro=0
    var BackInterstitialCreateStickerActivity=0
    var InterstitialCropImageToEditStickerImageActivity=0
    var BackInterstitialCropImageActivity=0
    var InterstitialCutOutToEditStickerImageActivity=0
    var BackInterstitialCutOutActivity=0
    var BackInterstitialDeleteStickerActivity=0
    var BannerTopDeleteStickerActivity=0
    var BannerBottomDeleteStickerActivity=0
    var BannerTopEditStickerImageActivity=0
    var BannerTopIntroActivity=0
    var BannerBottomIntroActivity=0
    var BannerTopExitActivity=0
    var BannerBottomExitActivity=0
    var BackInterstitialExitActivity=0
    var InterstitialEditStickerImageToCreateStickerActivity=0
    var InterstitialIntroToMainActivity=0
    var BannerBottomEditStickerImageActivity=0
    var BackInterstitialEditStickerActivity=0
    var BackInterstitialCreatedStickerActivity=0
    var InterstitialSettingToLanguageActivity=0
    var InterstitialSettingToExitActivity=0
    var BackInterstitialSettingActivity=0
    var BannerBottomGalleryActivity=0
    var BannerTopGalleryActivity=0
    var BackInterstitialGalleryActivity=0
    var BannerTopStickerDetailActivity=0
    var BannerBottomStickerDetailActivity=0
    var InterstialReturningHomeToMainActivity=0
    var InterstitialReturningHomeToCreateFragment=0
    var InterstitialReturningHomeToCreateStickerActivity=0
    var BackInterstitialInAppActivity=0
    var BackInterstitialMainActivity=0
    var InterstitialBottomNavHome=0
    var BottomNavInterstitialCounter=0
    var HomeNativeAdCounter=0
    var InterstitialBottomNavSticker=0
    var NativeHomeAndCategoryRecyclerView=0
    var NativeStickers_Detail_Activity_RecyclerView=0
    var BackInterstitialSearchStickersActivity=0
    var ClickCountLimit=0
    var SessionCountLimit=0
    var SetNativeBG_Or_Stroke_Splash=0
    var SetNativeBG_Or_Stroke_Intro=0
    var SetNativeBG_Or_Stroke_Languages=0
    var SetNativeBG_Or_Stroke_Others=0
    var ShowAppopenAfterTime=0
    var ShowInterstitialAfterTime=0
    var InterstitialBottomNavFirstTime= false
    var IfAppSuspend= false
    var ShowApplovinAdsAfter_Limit_Reached= false
    var ThirdPartyFlag= false
    var Is_Show_Non_Functional_Items= true
    var Load_Ad_If_All_Ads_Turn_Off= false

    var ShowAppopenAfterEmailRating= false
    var ShowAppopenAfterPlaystoreRating= false
    var ShowAppopenAfterPrivacyPolicy= false
    var ShowAppopenAfterShareApp= false
    var Enable_Applovin_Interstitial_After_FrequencyCapReached= false

    //ads param
    var AppOpenSplashBackground=false

    fun fetchRemoteConfigValues(context: Context){
        Log.d("akddd","load")
        try {
            val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build()
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener {
                if (it.isSuccessful){
                    Log.d("opasis","messages")
                    isDataFetch=true
                    ifAdNotFetch=true

                    val stickerPackJsonString = mFirebaseRemoteConfig.getString("STICKER_PACK_JSON")
                    val RETURNING_HOME_STICKERS = mFirebaseRemoteConfig.getString("RETURNING_HOME_STICKERS")
                    if (RETURNING_HOME_STICKERS.isNotEmpty()){
                        try {
                            RETURNING_HOME_STICKERS_JSON = JSONObject(RETURNING_HOME_STICKERS)
                        } catch (e: JSONException) {
                            Log.e("JSONError", "Failed to parse sticker pack JSON", e)
                            // Handle the JSON parsing error
                        }
                    }
                    if (stickerPackJsonString.isNotEmpty()) {
                        try {
                            STICKER_PACK_JSON = JSONObject(stickerPackJsonString)
                        } catch (e: JSONException) {
                            Log.e("JSONError", "Failed to parse sticker pack JSON", e)
                            // Handle the JSON parsing error
                        }
                    } else {
                        Log.e("JSONError", "Sticker pack JSON string is empty")
                        // Handle the empty JSON string case
                    }
                    var allAdsIds = mFirebaseRemoteConfig.getString("ADMOB_ADS_IDS")
                    if (allAdsIds.isNotEmpty()){
                        var json_ids = JSONObject(allAdsIds)
                        BANNER_ID = json_ids.getString("BANNER_ID").toString()
                        INTERSTITIAL_ID = json_ids.getString("INTERSTITIAL_ID").toString()
                        AppOpen_ID = json_ids.getString("OPEN_APP_ID").toString()
                        NATIVE_ID = json_ids.getString("NATIVE_ID").toString()
                        //applovin
                        INTERESTIAL_ID_APPLOVIN = json_ids.getString("INTERESTIAL_ID_APPLOVIN").toString()
                        BANNER_APPLOVIN_ID = json_ids.getString("BANNER_APPLOVIN_ID").toString()
                        NATIVE_APPLOVIN_ID = json_ids.getString("NATIVE_APPLOVIN_ID").toString()
                    }

                    var other_Parameters=mFirebaseRemoteConfig.getString("OTHER_PARAMETER")
                    if (other_Parameters.isNotEmpty()){
                        var otherJson=JSONObject(other_Parameters)
                        OneWeekOff=otherJson.getString("OneWeekOff")
                        OneMonthOff=otherJson.getString("OneMonthOff")
                        ThreeMonthOff=otherJson.getString("ThreeMonthOff")
                        LifetimeOff=otherJson.getString("LifetimeOff")
                        AllAdsToast = otherJson.getBoolean("AllAdsToast")
                        InterstitialDialogShowTime=otherJson.getInt("InterstitialDialogShowTime")
                        PriorityAdmobOrApplovin=otherJson.getBoolean("PriorityAdmobOrApplovin")
                        Is_InApp_Screen_Show_Or_Not=otherJson.getBoolean("Is_InApp_Screen_Show_Or_Not")
                        TimerLanguagesSelection=otherJson.getInt("TimerLanguagesSelection")
                        ShowBannerStroke=otherJson.getBoolean("ShowBannerStroke")
                        IsLanguageSelectionFirstTime=otherJson.getBoolean("IsLanguageSelectionFirstTime")
                        IsLanguageNextButtonShow=otherJson.getBoolean("IsLanguageNextButtonShow")
                        ShowUpdateDialog=otherJson.getBoolean("ShowUpdateDialog")
                        ShowInAppAfterIntro=otherJson.getBoolean("ShowInAppAfterIntro")
                        ShowInAppAfterReturning=otherJson.getBoolean("ShowInAppAfterReturning")
                        SplashNativeActionColor=otherJson.getString("SplashNativeActionColor")
                        LanguageNativeActionColor=otherJson.getString("LanguageNativeActionColor")
                        IntroNativeActionColor=otherJson.getString("IntroNativeActionColor")
                        NativeActionColorAll=otherJson.getString("NativeActionColorAll")
                        NativeBgSplash=otherJson.getString("NativeBgSplash")
                        NativeBgLanguage=otherJson.getString("NativeBgLanguage")
                        NativeBgIntro=otherJson.getString("NativeBgIntro")
                        NativeBgOther=otherJson.getString("NativeBgOther")
                        AppLink=otherJson.getString("AppLink")
                        InterstitialBottomNavFirstTime=otherJson.getBoolean("InterstitialBottomNavFirstTime")
                        IfAppSuspend=otherJson.getBoolean("IfAppSuspend")
                        ShowAppopenInterstitialOrNot=otherJson.getBoolean("ShowAppopenInterstitialOrNot")
                        ShowApplovinAdsAfter_Limit_Reached=otherJson.getBoolean("ShowApplovinAdsAfter_Limit_Reached")
                        ThirdPartyFlag=otherJson.getBoolean("ThirdPartyFlag")
                        BottomNavInterstitialCounter=otherJson.getInt("BottomNavInterstitialCounter")
                        HomeNativeAdCounter=otherJson.getInt("HomeNativeAdCounter")
                        SessionCountLimit=otherJson.getInt("SessionCountLimit")
                        ClickCountLimit=otherJson.getInt("ClickCountLimit")
                        SetNativeBG_Or_Stroke_Splash=otherJson.getInt("SetNativeBG_Or_Stroke_Splash")
                        SetNativeBG_Or_Stroke_Intro=otherJson.getInt("SetNativeBG_Or_Stroke_Intro")
                        SetNativeBG_Or_Stroke_Languages=otherJson.getInt("SetNativeBG_Or_Stroke_Languages")
                        SetNativeBG_Or_Stroke_Others=otherJson.getInt("SetNativeBG_Or_Stroke_Others")
                        ShowAppopenAfterTime=otherJson.getInt("ShowAppopenAfterTime")
                        ShowInterstitialAfterTime=otherJson.getInt("ShowInterstitialAfterTime")
                        ShowAppopenAfterEmailRating=otherJson.getBoolean("ShowAppopenAfterEmailRating")
                        ShowAppopenAfterPlaystoreRating=otherJson.getBoolean("ShowAppopenAfterPlaystoreRating")
                        ShowAppopenAfterPrivacyPolicy=otherJson.getBoolean("ShowAppopenAfterPrivacyPolicy")
                        ShowAppopenAfterShareApp=otherJson.getBoolean("ShowAppopenAfterShareApp")
                        Enable_Applovin_Interstitial_After_FrequencyCapReached=otherJson.getBoolean("Enable_Applovin_Interstitial_After_FrequencyCapReached")
                     //   Is_Show_Non_Functional_Items=otherJson.getBoolean("Is_Show_Non_Functional_Items")
                        Load_Ad_If_All_Ads_Turn_Off=otherJson.getBoolean("Load_Ad_If_All_Ads_Turn_Off")
                    }

                    var BANNER_ADS=mFirebaseRemoteConfig.getString("BANNER_ADS_FLAGS")
                    if (BANNER_ADS.isNotEmpty()){
                        var json_values=JSONObject(BANNER_ADS)
//                        BannerAndNativeTopSplashActivity=json_values.getInt("BannerAndNativeTopSplashActivity")
                        BannerBottomLanguagesActivity=json_values.getInt("BannerBottomLanguagesActivity")
                        BannerTopLanguagesActivity=json_values.getInt("BannerTopLanguagesActivity")
                        BannerTopDeleteStickerActivity=json_values.getInt("BannerTopDeleteStickerActivity")
                        BannerBottomDeleteStickerActivity=json_values.getInt("BannerBottomDeleteStickerActivity")
                        BannerTopEditStickerImageActivity=json_values.getInt("BannerTopEditStickerImageActivity")
                        BannerBottomEditStickerImageActivity=json_values.getInt("BannerBottomEditStickerImageActivity")
                        BannerBottomGalleryActivity=json_values.getInt("BannerBottomGalleryActivity")
                        BannerTopGalleryActivity=json_values.getInt("BannerTopGalleryActivity")
                        BannerTopCreatedStickerActivity=json_values.getInt("BannerTopCreatedStickerActivity")
                        BannerBottomCreatedStickerActivity=json_values.getInt("BannerBottomCreatedStickerActivity")
                        BannerTopCreateStickerActivity=json_values.getInt("BannerTopCreateStickerActivity")
                        BannerBottomCreateStickerActivity=json_values.getInt("BannerBottomCreateStickerActivity")
                        BannerTopCropImageActivity=json_values.getInt("BannerTopCropImageActivity")
                        BannerBottomCropImageActivity=json_values.getInt("BannerBottomCropImageActivity")
                        BannerTopCutOutActivity=json_values.getInt("BannerTopCutOutActivity")
                        BannerBottomCutOutActivity=json_values.getInt("BannerBottomCutOutActivity")
                        BannerTopSettingActivity=json_values.getInt("BannerTopSettingActivity")
                        BannerBottomSettingActivity=json_values.getInt("BannerBottomSettingActivity")
                        BannerTopExitActivity=json_values.getInt("BannerTopExitActivity")
                        BannerBottomExitActivity=json_values.getInt("BannerBottomExitActivity")
                        BannerTopIntroActivity=json_values.getInt("BannerTopIntroActivity")
                        BannerBottomIntroActivity=json_values.getInt("BannerBottomIntroActivity")
                        BannerTopMainActivity =json_values.getInt("BannerTopMainActivity")
                        BannerBottomMainActivity=json_values.getInt("BannerBottomMainActivity")
                        BannerTopMyStickersActivity=json_values.getInt("BannerTopMyStickersActivity")
                        BannerBottomMyStickersActivity=json_values.getInt("BannerBottomMyStickersActivity")
                        BannerTopSearchStickersActivity=json_values.getInt("BannerTopSearchStickersActivity")
                        BannerBottomSearchStickersActivity=json_values.getInt("BannerBottomSearchStickersActivity")
                        BannerTopStickerDetailActivity=json_values.getInt("BannerTopStickerDetailActivity")
                        BannerBottomStickerDetailActivity=json_values.getInt("BannerBottomStickerDetailActivity")
                        BannerTopCategoryStickerFragment=json_values.getInt("BannerTopCategoryStickerFragment")
                        BannerBottomCategoryStickerFragment=json_values.getInt("BannerBottomCategoryStickerFragment")
                        BannerTopHomeFragment=json_values.getInt("BannerTopHomeFragment")
                        BannerBottomHomeFragment=json_values.getInt("BannerBottomHomeFragment")
                        BannerTopMyStickerFragment=json_values.getInt("BannerTopMyStickerFragment")
                        BannerBottomMyStickerFragment=json_values.getInt("BannerBottomMyStickerFragment")
                        BannerAndNativeBottomSplashActivity=json_values.getInt("BannerAndNativeBottomSplashActivity")
                    }

                    var INTERSTITIAL_ADS=mFirebaseRemoteConfig.getString("INTERSTITIAL_ADS_FLAG")
                    if (INTERSTITIAL_ADS.isNotEmpty()){
                        var json_values=JSONObject(INTERSTITIAL_ADS)
                        BackInterstitialLanguagesToSettings=json_values.getInt("BackInterstitialLanguagesToSettings")
                        InterstitialLanguagesToSetting=json_values.getInt("InterstitialLanguagesToSetting")
                        InterstitialLanguageToIntro=json_values.getInt("InterstitialLanguageToIntro")
                        BackInterstitialCreateStickerActivity=json_values.getInt("BackInterstitialCreateStickerActivity")
                        InterstitialCropImageToEditStickerImageActivity=json_values.getInt("InterstitialCropImageToEditStickerImageActivity")
                        BackInterstitialCropImageActivity=json_values.getInt("BackInterstitialCropImageActivity")
                        InterstitialCutOutToEditStickerImageActivity=json_values.getInt("InterstitialCutOutToEditStickerImageActivity")
                        BackInterstitialCutOutActivity=json_values.getInt("BackInterstitialCutOutActivity")
                        BackInterstitialDeleteStickerActivity=json_values.getInt("BackInterstitialDeleteStickerActivity")
                        InterstitialEditStickerImageToCreateStickerActivity=json_values.getInt("InterstitialEditStickerImageToCreateStickerActivity")
                        BackInterstitialEditStickerActivity=json_values.getInt("BackInterstitialEditStickerActivity")
                        BackInterstitialCreatedStickerActivity=json_values.getInt("BackInterstitialCreatedStickerActivity")
                        InterstitialSettingToLanguageActivity=json_values.getInt("InterstitialSettingToLanguageActivity")
                        InterstitialSettingToExitActivity=json_values.getInt("InterstitialSettingToExitActivity")
                        BackInterstitialSettingActivity=json_values.getInt("BackInterstitialSettingActivity")
                        BackInterstitialExitActivity=json_values.getInt("BackInterstitialExitActivity")
                        BackInterstitialGalleryActivity=json_values.getInt("BackInterstitialGalleryActivity")
                        InterstitialIntroToMainActivity=json_values.getInt("InterstitialIntroToMainActivity")
                        InterstitialReturningHomeToCreateStickerActivity=json_values.getInt("InterstitialReturningHomeToCreateStickerActivity")
                        BackInterstitialStickerDetailActivity=json_values.getInt("BackInterstitialStickerDetailActivity")
                        InterstialReturningHomeToMainActivity=json_values.getInt("InterstialReturningHomeToMainActivity")
                        InterstitialReturningHomeToCreateFragment=json_values.getInt("InterstitialReturningHomeToCreateFragment")
                        BackInterstitialInAppActivity=json_values.getInt("BackInterstitialInAppActivity")
                        BackInterstitialMainActivity=json_values.getInt("BackInterstitialMainActivity")
                        InterstitialBottomNavHome=json_values.getInt("InterstitialBottomNavHome")
                        InterstitialBottomNavSticker=json_values.getInt("InterstitialBottomNavSticker")
                        BackInterstitialSearchStickersActivity=json_values.getInt("BackInterstitialSearchStickersActivity")
                        BackInterstitialCategoryStickerFragment=json_values.getInt("BackInterstitialCategoryStickerFragment")
                    }
                    var APPOPEN_AND_NATIVE_ADS=mFirebaseRemoteConfig.getString("APPOPEN_AND_NATIVE_ADS")
                    if (APPOPEN_AND_NATIVE_ADS.isNotEmpty()){
                        var json_values=JSONObject(APPOPEN_AND_NATIVE_ADS)
                        AppOpenSplashBackground=json_values.getBoolean("AppOpenSplashBackground")
                        AppOpenSplash=json_values.getInt("AppOpenSplash")
                        NativeExitActivity=json_values.getInt("NativeExitActivity")
                        NativeReturningHomeActivity=json_values.getInt("NativeReturningHomeActivity")
                        NativeHomeAndCategoryRecyclerView=json_values.getInt("NativeHomeAndCategoryRecyclerView")
                        NativeStickers_Detail_Activity_RecyclerView=json_values.getInt("NativeStickers_Detail_Activity_RecyclerView")
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (checkIfPlay) {
                            Log.d("adkljad", INTERSTITIAL_ID.toString())
                            if (Load_Ad_If_All_Ads_Turn_Off) {
                                loadInterstitial(context)
                                loadApplovinInterstitial(context)
                            }
                        }
                    },3000)
                  //  myAdSession()
                    checkSession()
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun checkSession() {
        val prefsHelper = SharedPreferenceHelper() // Ensure you pass the correct context here
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastSessionDate = prefsHelper.getLastSessionDate()

        if (currentDate != lastSessionDate) {
            // Dates are different, clear the session values
            Log.d("ajhdad", "clear")
            SharedPreferenceHelper.saveSessionNumber("sessionNum", 0)
            SharedPreferenceHelper.saveSession("mySession", false)
            prefsHelper.saveLastSessionDate(currentDate)
        } else {
            // Dates are the same, proceed with the session
            Log.d("ajhdad", "saving")
            var num = SharedPreferenceHelper.getSessionNumber("sessionNum", 0)
            val sessionLimit = SessionCountLimit
            if (SharedPreferenceHelper.getSession("mySession", false)) {
                if (num == sessionLimit) {
                    // Session limit reached, reset values
                    Log.d("ajhdad", "clear")
                    SharedPreferenceHelper.saveSessionNumber("sessionNum", 0)
                    SharedPreferenceHelper.saveSession("mySession", false)
                    prefsHelper.saveLastSessionDate(currentDate)
                } else {
//                    num++
//                    SharedPreferenceHelper.saveSessionNumber("sessionNum", num)
                }
            }
        }
    }
    fun addSession(){
        var num = SharedPreferenceHelper.getSessionNumber("sessionNum", 0)
        val sessionLimit = SessionCountLimit
        if (SharedPreferenceHelper.getSession("mySession", false)) {
            if (num != sessionLimit) {
                num++
                SharedPreferenceHelper.saveSessionNumber("sessionNum", num)
            }
        }
    }
    fun sendFirebaseAnalyticsKey(context: Context, key: String, text: String) {
        var mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle()
        params.putString(key, text)
        mFirebaseAnalytics!!.logEvent(key, params)
    }
}
