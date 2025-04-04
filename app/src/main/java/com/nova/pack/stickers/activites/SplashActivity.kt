package com.nova.pack.stickers.activites


import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.BounceInterpolator
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleObserver
import com.applovin.sdk.AppLovinSdk
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.analytics.FirebaseAnalytics
import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager.AppLink
import com.nova.pack.stickers.ads.AdsManager.AppOpenSplash
import com.nova.pack.stickers.ads.AdsManager.BannerAndNativeBottomSplashActivity
import com.nova.pack.stickers.ads.AdsManager.BannerAndNativeTopSplashActivity
import com.nova.pack.stickers.ads.AdsManager.IfAppSuspend
import com.nova.pack.stickers.ads.AdsManager.PriorityAdmobOrApplovin
import com.nova.pack.stickers.ads.AdsManager.ThirdPartyFlag
import com.nova.pack.stickers.ads.AdsManager.fetchRemoteConfigValues
import com.nova.pack.stickers.ads.AppOpenAdManager
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.Constants.Companion.checkIfPlay
import com.nova.pack.stickers.ads.Constants.Companion.ifConsentVisible
import com.nova.pack.stickers.ads.Constants.Companion.isConsentAccept
import com.nova.pack.stickers.ads.Constants.Companion.isDataFetch
import com.nova.pack.stickers.ads.Constants.Companion.isSplashBannerLoad
import com.nova.pack.stickers.ads.Constants.Companion.startButtonTask
import com.nova.pack.stickers.ads.InAppClass
import com.nova.pack.stickers.ads.NativeAdManager
import com.nova.pack.stickers.databinding.ActivitySplashBinding
import com.nova.pack.stickers.utils.SharedPreferenceHelper


class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    var sharedPreferences: SharedPreferences? = null
    var countDown: CountDownTimer? = null
    var n: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mainInit()
    }

    private fun mainInit() {
        Log.d("Sdshkd", "spalsh")
        val bounceAnimator = ObjectAnimator.ofFloat(binding.imgSplash, "translationY", 0f, -10f, 0f)
        bounceAnimator.duration = 1000
        bounceAnimator.repeatCount = ObjectAnimator.INFINITE
        bounceAnimator.interpolator = BounceInterpolator()
        bounceAnimator.start()
        checkUpdates()
        sharedPreferences = getSharedPreferences("myValue", 0)

        binding.progressBar.visibility = View.VISIBLE
        if (isNetworkAvailable()) {
            InAppClass.setupBillingBlient(this)
            //    initializeMobileAdsSdk(this)
            if (PriorityAdmobOrApplovin) {
                initializeMobileAdsSdk(this)
            }
        } else {
            if (sharedPreferences?.getString("value", "0").equals("3")) {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.progressBar.visibility = View.GONE
                    binding.btnGetStarted.visibility = View.VISIBLE
                }, 4000)
            } else if (sharedPreferences?.getString("value", "0").equals("1")) {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.progressBar.visibility = View.GONE
                    binding.btnGetStarted.visibility = View.VISIBLE
                }, 4000)
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
        binding.btnGetStarted.setOnClickListener {
            launchActivity()
        }
        binding.btnGetStarted.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.scaleX = 0.9f
                    view.scaleY = 0.9f
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.scaleX = 1.0f
                    view.scaleY = 1.0f
                }
            }
            false
        }
        loadConsentInfo()
//        initializeMobileAdsSdk(this)
        onBackPressedDispatcher.addCallback {
        }
    }

    private fun loadConsentInfo() {
        Log.d("paiasd", "Starting consent information load")

        try {
            val params = ConsentRequestParameters.Builder().build()
            var consentInformation = UserMessagingPlatform.getConsentInformation(this)

            if (consentInformation.canRequestAds()) {
                Log.d("paiasd", "User can request ads")
                initializeMobileAdsSdk(this)
                return
            }

            Log.d("paiasd", "Requesting consent info update")

            consentInformation.requestConsentInfoUpdate(
                this@SplashActivity,
                params,
                {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                        this@SplashActivity,
                        { loadAndShowError ->
                            if (loadAndShowError != null) {
                                Log.e(
                                    "Sdjddjhdjh",
                                    "Error loading consent form: ${loadAndShowError.message}"
                                )
                            } else {
                                val consentStatus = consentInformation.consentStatus
                                sharedPreferences?.edit()
                                    ?.putString("value", consentStatus.toString())?.apply()

                                Log.d("Sdjddjhdjh", "Consent status: $consentStatus")

                                if (consentInformation.canRequestAds()) {
                                    Log.d("Sdjdjhdjh", "User accepted consent")
                                    initializeMobileAdsSdk(this)
                                } else {
                                    Log.d("Sdjdjhdjh", "User rejected consent")
                                }
                            }
                        }
                    )
                },
                { requestConsentError ->
                    Log.e(
                        "TAsdsdG",
                        "Failed to update consent info: ${requestConsentError.message}"
                    )
                }
            )

            Log.d("paiasd", "Consent info update requested")
        } catch (e: Exception) {
            Log.e("paiasd", "Exception occurred", e)
        }
    }

    private fun initializeMobileAdsSdk(context: Context) {
        try {
            Log.d("Sdkjf", "initiale")
            ifConsentVisible = true
            fetchRemoteConfigValues(this)
            Log.d("Sdkjf", "initiale 2")
            if (SharedPreferenceHelper.getSession("mySession", false)) {
                Log.d("adfkjs", "1")
                n = 4000
            } else {
                Log.d("adfkjs", "2")
                n = 18000
            }

            countDown = object : CountDownTimer(n, 1000), LifecycleObserver {
                override fun onTick(millisUntilFinished: Long) {
                    if (isDataFetch) {
                        Log.d("askjs", "isSplashBannerLoad.toString()")
                        isDataFetch = false
                        isConsentAccept = true
                        if (ThirdPartyFlag) {
                            checkAppSourceData()
                        }
                        if (!InAppClass.isPurchase) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                AppOpenAdManager.currentActivity = context as Activity?
                            }, 4000)
                            MobileAds.initialize(context)
                            Log.d("osdidid", "initilize")
                            AppLovinSdk.getInstance(context).mediationProvider = "max"
                            AppLovinSdk.initializeSdk(
                                context,
                                AppLovinSdk.SdkInitializationListener {
                                })
                        } else {
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.progressBar.visibility = View.GONE
                                binding.btnGetStarted.visibility = View.VISIBLE
                            }, 4000)
                            countDown?.cancel()
                            return
                        }

                        Handler(Looper.getMainLooper()).postDelayed({
                            if (!checkIfPlay) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    binding.progressBar.visibility = View.GONE
                                    binding.btnGetStarted.visibility = View.VISIBLE
                                }, 4000)
                                return@postDelayed
                            }
                        }, 1000)
                        suspendedDialog()
                        if (AppOpenSplash == 1) {
                            binding.containAds.visibility = View.VISIBLE
                        } else {
                            binding.containAds.visibility = View.INVISIBLE
                        }
                        if (BannerAndNativeTopSplashActivity == 1) {
                            Log.d("ISsPLAHS", "progress")
                            binding.bannerLayout.visibility = View.VISIBLE
                            binding.bannerArea.visibility = View.VISIBLE
                            BannerAdManager.loadBannerAd(
                                this@SplashActivity,
                                binding.bannerLayout,
                                binding.bannerArea,
                                binding.nativeContainer,
                                1
                            )
                        } else if (BannerAndNativeTopSplashActivity == 2) {
                            Log.d("ISsPLAHS", "progress")
                            binding.bannerLayout.visibility = View.VISIBLE
                            binding.bannerArea.visibility = View.VISIBLE
                            BannerAdManager.loadCollapsibleBanner(
                                this@SplashActivity,
                                binding.bannerArea,
                                "top"
                            )
                        } else if (BannerAndNativeTopSplashActivity == 3) {
                            binding.nativeContainer.visibility = View.VISIBLE
                            NativeAdManager.nativeWithoutMedia(
                                this@SplashActivity,
                                binding.nativeContainer,
                                window,
                                "splash",
                                "top",
                                binding.bannerArea
                            )
                        } else if (BannerAndNativeTopSplashActivity == 4) {
                            Log.d("sjfhdsf", "4")
                            binding.nativeContainer.visibility = View.VISIBLE
                            NativeAdManager.nativeWithoutMedia(
                                this@SplashActivity,
                                binding.nativeContainer,
                                window,
                                "splash",
                                "bottom",
                                binding.bannerArea
                            )
                        } else if (BannerAndNativeTopSplashActivity == 5) {
                            binding.nativeContainer.visibility = View.VISIBLE
                            binding.nativeContainer2.visibility = View.GONE
                            NativeAdManager.nativeAdmob(
                                this@SplashActivity,
                                binding.nativeContainer,
                                window,
                                1
                            )
                        } else if (BannerAndNativeTopSplashActivity == 6) {
                            binding.nativeContainer.visibility = View.VISIBLE
                            NativeAdManager.nativeWithoutMedia(
                                this@SplashActivity,
                                binding.nativeContainer,
                                window,
                                "splash",
                                "small",
                                binding.bannerArea
                            )
                        } else if (BannerAndNativeTopSplashActivity == 7) {
                            binding.nativeContainer.visibility = View.VISIBLE
                            NativeAdManager.nativeWithoutMedia(
                                this@SplashActivity,
                                binding.nativeContainer,
                                window,
                                "splash",
                                "random",
                                binding.bannerArea
                            )
                        } else if (BannerAndNativeTopSplashActivity == 8) {
                            Log.d("sjfhdsf", "4")
                            binding.nativeContainer.visibility = View.VISIBLE
                            NativeAdManager.nativeWithoutMedia(
                                this@SplashActivity,
                                binding.nativeContainer,
                                window,
                                "splash",
                                "bottomN",
                                binding.bannerArea
                            )
                        } else if (BannerAndNativeTopSplashActivity == 9) {
                            binding.nativeContainer.visibility = View.VISIBLE
                            binding.nativeContainer2.visibility = View.GONE
                            NativeAdManager.nativeAdmob(
                                this@SplashActivity,
                                binding.nativeContainer,
                                window,
                                6
                            )
                        } else if (BannerAndNativeTopSplashActivity == 10) {
                            binding.nativeContainer.visibility = View.VISIBLE
                            binding.nativeContainer2.visibility = View.GONE
                            NativeAdManager.nativeAdmob(
                                this@SplashActivity,
                                binding.nativeContainer,
                                window,
                                8
                            )
                        } else if (BannerAndNativeTopSplashActivity == 11) {
                            binding.nativeContainer.visibility = View.VISIBLE
                            binding.nativeContainer2.visibility = View.GONE
                            NativeAdManager.nativeAdmob(
                                this@SplashActivity,
                                binding.nativeContainer,
                                window,
                                9
                            )
                        } else if (BannerAndNativeTopSplashActivity == 12) {
                            binding.nativeContainer.visibility = View.VISIBLE
                            binding.nativeContainer2.visibility = View.GONE
                            NativeAdManager.nativeAdmob(
                                this@SplashActivity,
                                binding.nativeContainer,
                                window,
                                10
                            )
                        } else {
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.progressBar.visibility = View.GONE
                                binding.btnGetStarted.visibility = View.VISIBLE
                            }, 8000)
                        }

                        if (BannerAndNativeBottomSplashActivity == 1) {
                            Log.d("ISsPLAHS", "progress")
                            binding.bannerLayout2.visibility = View.VISIBLE
                            binding.bannerArea2.visibility = View.VISIBLE
                            BannerAdManager.loadBannerAd(
                                this@SplashActivity,
                                binding.bannerLayout2,
                                binding.bannerArea2,
                                binding.nativeContainer2,
                                1
                            )
                        } else if (BannerAndNativeBottomSplashActivity == 2) {
                            Log.d("ISsPLAHS", "progress")
                            binding.bannerLayout2.visibility = View.VISIBLE
                            binding.bannerArea2.visibility = View.VISIBLE
                            BannerAdManager.loadCollapsibleBanner(
                                this@SplashActivity,
                                binding.bannerArea2,
                                "bottom"
                            )
                        } else if (BannerAndNativeBottomSplashActivity == 3) {
                            binding.nativeContainer2.visibility = View.VISIBLE
                            NativeAdManager.nativeWithoutMedia(
                                this@SplashActivity,
                                binding.nativeContainer2,
                                window,
                                "splash",
                                "top",
                                binding.bannerArea2
                            )
                        } else if (BannerAndNativeBottomSplashActivity == 4) {
                            Log.d("sjfhdsf", "bottom 4")
                            binding.nativeContainer2.visibility = View.VISIBLE
                            NativeAdManager.nativeWithoutMedia(
                                this@SplashActivity,
                                binding.nativeContainer2,
                                window,
                                "splash",
                                "bottom",
                                binding.bannerArea2
                            )
                        } else if (BannerAndNativeBottomSplashActivity == 5) {
                            binding.nativeContainer2.visibility = View.VISIBLE
                            binding.nativeContainer.visibility = View.GONE
                            NativeAdManager.nativeAdmob(
                                this@SplashActivity,
                                binding.nativeContainer2,
                                window,
                                1
                            )
                        } else if (BannerAndNativeBottomSplashActivity == 6) {
                            binding.nativeContainer.visibility = View.VISIBLE
                            NativeAdManager.nativeWithoutMedia(
                                this@SplashActivity,
                                binding.nativeContainer,
                                window,
                                "splash",
                                "small",
                                binding.bannerArea
                            )
                        } else if (BannerAndNativeBottomSplashActivity == 7) {
                            binding.nativeContainer.visibility = View.VISIBLE
                            NativeAdManager.nativeWithoutMedia(
                                this@SplashActivity,
                                binding.nativeContainer,
                                window,
                                "splash",
                                "random",
                                binding.bannerArea
                            )
                        } else if (BannerAndNativeBottomSplashActivity == 8) {
                            Log.d("sjfhdsf", "bottom 4")
                            binding.nativeContainer2.visibility = View.VISIBLE
                            NativeAdManager.nativeWithoutMedia(
                                this@SplashActivity,
                                binding.nativeContainer2,
                                window,
                                "splash",
                                "bottomN",
                                binding.bannerArea2
                            )
                        } else if (BannerAndNativeBottomSplashActivity == 9) {
                            binding.nativeContainer2.visibility = View.VISIBLE
                            binding.nativeContainer.visibility = View.GONE
                            NativeAdManager.nativeAdmob(
                                this@SplashActivity,
                                binding.nativeContainer2,
                                window,
                                6
                            )
                        } else if (BannerAndNativeBottomSplashActivity == 10) {
                            binding.nativeContainer2.visibility = View.VISIBLE
                            binding.nativeContainer.visibility = View.GONE
                            NativeAdManager.nativeAdmob(
                                this@SplashActivity,
                                binding.nativeContainer2,
                                window,
                                8
                            )
                        } else if (BannerAndNativeBottomSplashActivity == 11) {
                            binding.nativeContainer2.visibility = View.VISIBLE
                            binding.nativeContainer.visibility = View.GONE
                            NativeAdManager.nativeAdmob(
                                this@SplashActivity,
                                binding.nativeContainer2,
                                window,
                                9
                            )
                        } else if (BannerAndNativeBottomSplashActivity == 12) {
                            binding.nativeContainer2.visibility = View.VISIBLE
                            binding.nativeContainer.visibility = View.GONE
                            NativeAdManager.nativeAdmob(
                                this@SplashActivity,
                                binding.nativeContainer2,
                                window,
                                10
                            )
                        } else {
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.progressBar.visibility = View.GONE
                                binding.btnGetStarted.visibility = View.VISIBLE
                            }, 8000)
                        }
                    }
                    if (isSplashBannerLoad) {
                        isSplashBannerLoad = false
                        Log.d("askjs", "truee")
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding.progressBar.visibility = View.GONE
                            binding.btnGetStarted.visibility = View.VISIBLE
                        }, 4000)
                    }
                }

                override fun onFinish() {
                    binding.progressBar.visibility = View.GONE
                    binding.btnGetStarted.visibility = View.VISIBLE
                }
            }
            countDown?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun launchActivity() {
        if (AppOpenSplash == 1) {
            if (checkIfPlay) {
                AppOpenAdManager.showAdIfAvailable("splash", this)
            } else {
                startButtonTask(this)
            }
        } else {
            startButtonTask(this)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun checkUpdates() {
        try {
            val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(this)

            val appUpdateInfoTask = appUpdateManager.appUpdateInfo

            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            202
                        )
                    } catch (e: Exception) {
                    }
                } else {
                    Log.d("TAdfgdfG", "No Update available")
                }
            }

            appUpdateInfoTask.addOnFailureListener {
                Log.d("Update Exception", it.toString())
            }
        } catch (ex: Exception) {
            Log.d("Exception", ex.toString())
        }
    }

    private fun checkAppSourceData() {
        val packageManager = packageManager
        try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            checkIfPlay =
                if ("com.android.vending" == packageManager.getInstallerPackageName(applicationInfo.packageName)) {
                    true
                } else {
                    sendFirebaseAnalyticsKey(this, "third_party_app", "this is third party app.")
                    true
                }
            /* if (!checkIfPlay) {
                  startActivity(Intent(this, ThirdPartyActivity::class.java))
                 finish()
             }*/
        } catch (e: PackageManager.NameNotFoundException) {
            checkIfPlay = false
            e.printStackTrace()
        }
    }

    fun sendFirebaseAnalyticsKey(context: Context, key: String, text: String) {
        var mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle()
        params.putString(key, text)
        mFirebaseAnalytics!!.logEvent(key, params)
    }

    fun suspendedDialog() {
        if (IfAppSuspend) {
            var dialog = Dialog(this)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(R.layout.suspend_dialog)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            var button = dialog.findViewById<AppCompatButton>(R.id.btn_done)
            button.setOnClickListener {
                dialog.dismiss()
                val appPackageName = packageName
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppLink)))
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppLink)))
                }
            }
        }
    }
}