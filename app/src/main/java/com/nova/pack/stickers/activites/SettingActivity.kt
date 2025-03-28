package com.nova.pack.stickers.activites

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.AdsManager.Is_InApp_Screen_Show_Or_Not
import com.nova.pack.stickers.ads.AdsManager.Is_Show_Non_Functional_Items
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivityDrawerMenuBinding
import com.nova.pack.stickers.utils.Config.Companion.isPrivacyPolicy
import com.nova.pack.stickers.utils.Config.Companion.isShareApp

class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivityDrawerMenuBinding
    private var isBackPressed=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerMenuBinding.inflate(layoutInflater)
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
        BannerAdManager.loadBannerAd(this, binding.bannerLayout,binding.bannerArea,binding.nativeContainer, AdsManager.BannerTopSettingActivity)
        BannerAdManager.loadBannerAd(this, binding.bannerLayout2,binding.bannerArea2,binding.nativeContainer2, AdsManager.BannerBottomSettingActivity)
        if (Is_Show_Non_Functional_Items){
            binding.shimmer.visibility= View.VISIBLE
        }
        binding.btnLanguage.setOnClickListener {
            InterstitialAdManager.showInterstitial(this, LanguageSelectActivity(),"setting","","","", "", AdsManager.InterstitialSettingToLanguageActivity)
        }
        binding.btnRating.setOnClickListener {
            InterstitialAdManager.showInterstitial(this, ExitActivity(),"","","","", "", AdsManager.InterstitialSettingToExitActivity)
        }
        binding.btnPremiu.setOnClickListener {
            if (Is_InApp_Screen_Show_Or_Not) {
                val intent = Intent(this, InAppActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this@SettingActivity,getString(R.string.comming_soon), Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnPremium.setOnClickListener {
            if (Is_InApp_Screen_Show_Or_Not) {
                val intent = Intent(this, InAppActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this@SettingActivity,getString(R.string.comming_soon),Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnPrivacy.setOnClickListener {
            isPrivacyPolicy=true
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://absoluinc.blogspot.com/2019/10/privacy-policy.html")))
        }
        binding.btnShare.setOnClickListener {
            isShareApp=true
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Video Player")
            var shareMessage = " Let me recommend you this application  "
            shareMessage = """  ${shareMessage}https://play.google.com/store/apps/details?id=${packageName}    """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        }
        binding.backBtn.setOnClickListener {
            if (!isBackPressed) {
                isBackPressed=true
                InterstitialAdManager.showInterstitial(this@SettingActivity, MainActivity(),"","","","", "finishes", AdsManager.BackInterstitialSettingActivity)
            }
        }
        onBackPressedDispatcher.addCallback {
            if (!isBackPressed) {
                isBackPressed=true
            InterstitialAdManager.showInterstitial(this@SettingActivity, MainActivity(),"","","","", "finishes", AdsManager.BackInterstitialSettingActivity)
        }
        }
    }
}