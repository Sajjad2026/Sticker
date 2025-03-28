package com.nova.pack.stickers.activites

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.LinearLayoutManager
import com.nova.pack.stickers.R
import com.nova.pack.stickers.adapter.LanguagesAdapter
import com.nova.pack.stickers.ads.AdsManager.BackInterstitialLanguagesToSettings
import com.nova.pack.stickers.ads.AdsManager.BannerBottomLanguagesActivity
import com.nova.pack.stickers.ads.AdsManager.BannerTopLanguagesActivity
import com.nova.pack.stickers.ads.AdsManager.InterstitialLanguageToIntro
import com.nova.pack.stickers.ads.AdsManager.InterstitialLanguagesToSetting
import com.nova.pack.stickers.ads.AdsManager.IsLanguageNextButtonShow
import com.nova.pack.stickers.ads.AdsManager.IsLanguageSelectionFirstTime
import com.nova.pack.stickers.ads.AdsManager.TimerLanguagesSelection
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.Constants.Companion.isSplashBannerLoad
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivityLanguageSelectBinding
import com.nova.pack.stickers.listener.CheckLanguage
import com.nova.pack.stickers.model.Languages
import java.util.*

class LanguageSelectActivity : AppCompatActivity(), CheckLanguage {

    private lateinit var binding: ActivityLanguageSelectBinding
    private var arrayList: ArrayList<Languages>? = null
    private var adapter: LanguagesAdapter? = null
    private var selectedLanguages: ArrayList<Languages>? = null
    private var isBackPressed=false
    private var isLanguageSelect=false
    private var isTimerEnd=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectedLanguages = ArrayList()
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setAdapter()
        buttonClick()
        if (intent.extras?.containsKey("setting") == true) {
            binding.language.text=getString(R.string.change_langauge)
            Log.d("Adshaknd","sett")
            isLanguageSelect=true
            binding.relativeTick.visibility = View.VISIBLE
            binding.btnNext.imageTintList = getColorStateList(R.color.bg_color)
        }
        if (IsLanguageNextButtonShow){
            Log.d("sdksf","green")
            binding.btnNext.imageTintList = getColorStateList(R.color.bg_color)
            if (IsLanguageSelectionFirstTime) {
                binding.relativeTick.visibility = View.VISIBLE
            }
            isTimerEnd=true
        }else{
            Log.d("sdksf","grey")
            Handler(Looper.getMainLooper()).postDelayed({
                if (isLanguageSelect) {
                    binding.relativeTick.visibility = View.VISIBLE
                }
                binding.btnNext.imageTintList = getColorStateList(R.color.bg_color)
                isTimerEnd=true
            }, TimerLanguagesSelection.toLong())
        }
        Toast.makeText(
            this,
            getString(R.string.chose_language),
            Toast.LENGTH_SHORT
        ).show()


        var countDown = object : CountDownTimer(TimerLanguagesSelection.toLong(), 100),
            LifecycleObserver {
            override fun onTick(millisUntilFinished: Long) {
                if (isSplashBannerLoad){
                    cancel()
                    Log.d("Asakjd",isSplashBannerLoad.toString())
                    binding.btnNext.imageTintList = getColorStateList(R.color.bg_color)
                }
            }

            override fun onFinish() {
                Log.d("Adskajd","finish")
                binding.relativeTick.stopShimmer()
                binding.btnNext.imageTintList=getColorStateList(R.color.bg_color)
            }
        }
        countDown.start()
        val prefss = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstLaunch = prefss.getBoolean("isFirstLaunchLanguage", true)
        if (!isFirstLaunch) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.relativeTick.visibility=View.VISIBLE
                binding.btnNext.imageTintList=getColorStateList(R.color.bg_color)
            }, TimerLanguagesSelection.toLong())
        }
        BannerAdManager.loadBannerAd(this, binding.bannerLayout,binding.bannerArea,binding.nativeContainer,BannerTopLanguagesActivity)
        BannerAdManager.loadBannerAd(this, binding.bannerLayout2,binding.bannerArea2,binding.nativeContainer2,BannerBottomLanguagesActivity)

        onBackPressedDispatcher.addCallback {
            if (!isBackPressed) {
                isBackPressed=true
                if (intent.extras?.containsKey("setting") == true) {
                    InterstitialAdManager.showInterstitial(
                        this@LanguageSelectActivity,
                        SettingActivity(),
                        "",
                        "",
                        "",
                        "",
                        "finish",
                        BackInterstitialLanguagesToSettings
                    )
                }
            }
        }

//        val fadeInAnimation = ObjectAnimator.ofFloat(binding.relativeTick, "alpha", 0.1f, 1f)
//        fadeInAnimation.duration = 3000
//        fadeInAnimation.interpolator = AccelerateDecelerateInterpolator()
//        // Delayed visibility change
//        binding.relativeTick.postDelayed({
//            isVisible=true
           // binding.relativeTick.visibility = ImageView.VISIBLE
            val bounceAnimator = ObjectAnimator.ofFloat(binding.relativeTick, "translationY", 0f, -10f, 0f)
            bounceAnimator.duration = 1000
            bounceAnimator.repeatCount = ObjectAnimator.INFINITE
            bounceAnimator.interpolator = BounceInterpolator()
            bounceAnimator.start()
//        }, 3000) // Delay in milliseconds

//        fadeInAnimation.start()
    }

    private fun setAdapter() {
        arrayList = ArrayList()
        arrayList?.add(Languages("Arabic", "ar","sa",resources.getDrawable(com.hbb20.R.drawable.flag_saudi_arabia)))
        arrayList?.add(Languages("Chinese", "zh","cn",resources.getDrawable(com.hbb20.R.drawable.flag_china)))
        arrayList?.add(Languages("Dutch", "nl","nl",resources.getDrawable(com.hbb20.R.drawable.flag_netherlands)))
        arrayList?.add(Languages("English", "en","us",resources.getDrawable(com.hbb20.R.drawable.flag_united_states_of_america)))
        arrayList?.add(Languages("French", "fr","fr",resources.getDrawable(com.hbb20.R.drawable.flag_france)))
        arrayList?.add(Languages("German", "de","de",resources.getDrawable(com.hbb20.R.drawable.flag_germany)))
        arrayList?.add(Languages("Indonesian", "in","id",resources.getDrawable(com.hbb20.R.drawable.flag_indonesia)))
        arrayList?.add(Languages("Italian", "it","it",resources.getDrawable(com.hbb20.R.drawable.flag_italy)))
        arrayList?.add(Languages("Japan", "ja","jp",resources.getDrawable(com.hbb20.R.drawable.flag_japan)))
        arrayList?.add(Languages("Korean", "ko","kp",resources.getDrawable(com.hbb20.R.drawable.flag_south_korea)))
        arrayList?.add(Languages("Persian", "fa","pe",resources.getDrawable(com.hbb20.R.drawable.flag_iran)))
        arrayList?.add(Languages("Portuguese", "pt","pt",resources.getDrawable(com.hbb20.R.drawable.flag_portugal)))
        arrayList?.add(Languages("Russia", "ru","ru",resources.getDrawable(com.hbb20.R.drawable.flag_russian_federation)))
        arrayList?.add(Languages("Spanish", "es","es",resources.getDrawable(com.hbb20.R.drawable.flag_spain)))
        arrayList?.add(Languages("Thai", "th","th",resources.getDrawable(com.hbb20.R.drawable.flag_thailand)))
        arrayList?.add(Languages("Turkey", "tr","tr",resources.getDrawable(com.hbb20.R.drawable.flag_turkey)))

        adapter = LanguagesAdapter(this, arrayList!!, this)
        binding.recycler.layoutManager=LinearLayoutManager(this)
        binding.recycler.adapter=adapter
    }

    private fun setLocale(lang: String?) {
        val config = resources.configuration
        val locale = Locale(lang)
        Locale.setDefault(locale)
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    override fun isLanguage(b: Boolean, flag: String, name: String) {
        isLanguageSelect=true
        binding.relativeTick.visibility=View.VISIBLE
        if (isTimerEnd){
            binding.btnNext.imageTintList = getColorStateList(R.color.bg_color)
        }
        if (b) {
            selectedLanguages?.add(Languages(name, flag,"",resources.getDrawable(com.hbb20.R.drawable.flag_united_states_of_america)))
        } else {
            selectedLanguages?.remove(Languages(name, flag,"",resources.getDrawable(R.drawable.arrow_left)))
        }
    }
    fun buttonClick(){
        binding.relativeTick.setOnClickListener {
            if (isLanguageSelect){
                val prefss = PreferenceManager.getDefaultSharedPreferences(this@LanguageSelectActivity)
                prefss.edit().putBoolean("isFirstLaunchLanguage", false).apply()
                selectedLanguages?.forEach { data ->
                    setLocale(data.languages)
                    Log.d("sdksjd", data.languages)
                    var sharedPreferences = getSharedPreferences("myLanguages", 0)
                    sharedPreferences.edit().putString("language", data.languages).apply()
                }
                if (intent.extras?.containsKey("setting") == true) {
                    InterstitialAdManager.showInterstitial(this@LanguageSelectActivity,SettingActivity(),"setting","","","","finishes",InterstitialLanguagesToSetting)
                } else {
                    InterstitialAdManager.showInterstitial(this@LanguageSelectActivity,IntroActivity(),"","","","","finishes",InterstitialLanguageToIntro)
                }
            }
        }
    }
}