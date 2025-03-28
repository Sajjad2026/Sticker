package com.nova.pack.stickers

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.farimarwat.zerocrash.ZeroCrash
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.nova.pack.stickers.Utils.Companion.isNetworkAvailable
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.AdsManager.AppOpenSplashBackground
import com.nova.pack.stickers.ads.AdsManager.Load_Ad_If_All_Ads_Turn_Off
import com.nova.pack.stickers.ads.AdsManager.ShowAppopenAfterEmailRating
import com.nova.pack.stickers.ads.AdsManager.ShowAppopenAfterPlaystoreRating
import com.nova.pack.stickers.ads.AdsManager.ShowAppopenAfterPrivacyPolicy
import com.nova.pack.stickers.ads.AdsManager.ShowAppopenAfterShareApp
import com.nova.pack.stickers.ads.AppOpenAdManager
import com.nova.pack.stickers.ads.AppOpenAdManager.Companion.currentActivity
import com.nova.pack.stickers.ads.Constants.Companion.checkIfPlay
import com.nova.pack.stickers.ads.Constants.Companion.ifAdNotFetch
import com.nova.pack.stickers.ads.Constants.Companion.ifConsentVisible
import com.nova.pack.stickers.ads.Constants.Companion.isConsentAccept
import com.nova.pack.stickers.ads.InAppClass
import com.nova.pack.stickers.utils.Config
import com.nova.pack.stickers.utils.Config.Companion.isPlayStoreLaunch
import com.nova.pack.stickers.utils.Config.Companion.isShareApp
import com.nova.pack.stickers.utils.CountDownTimer.showAppOpen
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class AppConfig: Application(), LifecycleObserver, Application.ActivityLifecycleCallbacks {

    var countDown: CountDownTimer?=null
    private var context: Context? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Log.d("asdadad",currentActivity?.localClassName?.lowercase().toString())
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                if (checkIfPlay){
                    if (!InAppClass.isPurchase) {
                        if (showAppOpen){
                 if (AppOpenSplashBackground && currentActivity?.localClassName?.lowercase().toString().contains("splash")){
                    currentActivity?.let { AppOpenAdManager.showAdIfAvailable("", currentActivity)}
                }else if (ShowAppopenAfterPlaystoreRating && currentActivity?.localClassName?.lowercase().toString().contains("exit") && isPlayStoreLaunch){
                    Log.d("asdadad","1")
                    currentActivity?.let { AppOpenAdManager.showAdIfAvailable("", currentActivity)}
                }else if (ShowAppopenAfterEmailRating && currentActivity?.localClassName?.lowercase().toString().contains("exit")&& Config.isEmailWriting){
                    Log.d("asdadad","2")
                    currentActivity?.let { AppOpenAdManager.showAdIfAvailable("", currentActivity)}
                }else if (ShowAppopenAfterPrivacyPolicy && currentActivity?.localClassName?.lowercase().toString().contains("setting") && Config.isPrivacyPolicy){
                    Log.d("asdadad","3")
                    currentActivity?.let { AppOpenAdManager.showAdIfAvailable("", currentActivity)}
                }else if (ShowAppopenAfterShareApp && currentActivity?.localClassName?.lowercase().toString().contains("setting") && isShareApp){
                    Log.d("asdadad","4")
                    currentActivity?.let { AppOpenAdManager.showAdIfAvailable("", currentActivity)}
                }else if (!currentActivity?.localClassName?.lowercase().toString().contains("splash") && !isShareApp && !Config.isPrivacyPolicy && !Config.isEmailWriting){
                     Log.d("asdadad",currentActivity?.localClassName?.lowercase().toString())
                     currentActivity?.let { AppOpenAdManager.showAdIfAvailable("", currentActivity)}
                 }else{
                    Log.d("asdadad", currentActivity?.localClassName!!)
                }
                  isShareApp=false
                  Config.isPrivacyPolicy=false
                  Config.isEmailWriting=false
                  isPlayStoreLaunch=false
                }
                }
                }
            },150)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseApp.initializeApp(this)
        FirebaseRemoteConfig.getInstance().reset()
        context=this
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        countDown=object : CountDownTimer(120000, 1000), LifecycleObserver {
            override fun onTick(millisUntilFinished: Long) {
                if (ifConsentVisible){
                    ifConsentVisible =false
                    countDown?.cancel()
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isConsentAccept && Load_Ad_If_All_Ads_Turn_Off) {
                            Log.d("ASJHssAD",ifConsentVisible.toString())
                            AppOpenAdManager()
                        }
                    },5000)
                }
            }
            override fun onFinish() {
            }
        }
        countDown?.start()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {  // Checking for versions below Android 9 (Pie)
            activity.window.decorView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        ZeroCrash.Builder(this)
        currentActivity=activity
        if (isNetworkAvailable(activity)){
            if (!ifAdNotFetch &&  !activity.localClassName.lowercase().contains("splash")){
                ifAdNotFetch=true
                AdsManager.fetchRemoteConfigValues(activity)
            }
        }
        Log.d("Adjashad", activity.toString())
        val shared: SharedPreferences = activity.getSharedPreferences("myLanguages", 0)
        val languages = shared.getString("language", "en")
        if (languages != "en") {
            val config: Configuration = activity.getResources().getConfiguration()
            val locale = Locale(languages)
            Locale.setDefault(locale)
            config.locale = locale
            // Set layout direction to right-to-left
            config.setLayoutDirection(locale)
            activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics())
            // Adjust the layout direction of your views if needed
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL)
            Log.d("ahgdad", "yes$languages")
        } else {
            val config: Configuration = activity.getResources().getConfiguration()
            val locale = Locale(languages)
            Locale.setDefault(locale)
            config.locale = locale

            // Set layout direction to left-to-right
            config.setLayoutDirection(locale)
            activity.getResources()
                .updateConfiguration(config, activity.getResources().getDisplayMetrics())

            // Adjust the layout direction of your views if needed
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR)
            Log.d("ahgdad", "no$languages")
        }
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

}