package com.nova.pack.stickers.activites

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nova.pack.stickers.BuildConfig
import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.ads.NativeAdManager
import com.nova.pack.stickers.databinding.ActivityExitActivtyBinding
import com.nova.pack.stickers.utils.Config.Companion.isEmailWriting
import com.nova.pack.stickers.utils.Config.Companion.isPlayStoreLaunch


class ExitActivity : AppCompatActivity() {
    lateinit var binding: ActivityExitActivtyBinding
    var selectedRating: Int = 0
    var star1: ImageView? = null
    var star2: ImageView? = null
    var star3: ImageView? = null
    var star4: ImageView? = null
    var star5: ImageView? = null
    var imgExit: ImageView? = null
    var isBackPressed=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityExitActivtyBinding.inflate(layoutInflater)
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
        BannerAdManager.loadBannerAd(this as AppCompatActivity, binding.bannerLayout,binding.bannerArea,binding.nativeAd, AdsManager.BannerTopExitActivity)
        BannerAdManager.loadBannerAd(this as AppCompatActivity, binding.bannerLayout2,binding.bannerArea2,binding.nativeAd2, AdsManager.BannerBottomExitActivity)
        NativeAdManager.nativeAdmob(this as AppCompatActivity,binding.nativeContainer, window, AdsManager.NativeExitActivity)

        binding.btnVideo.setOnClickListener {
            var intent=(Intent(this@ExitActivity,MainActivity::class.java))
            startActivity(intent)
            finish()
        }
        binding.btnMySticker.setOnClickListener {
            var intent=(Intent(this@ExitActivity,MainActivity::class.java))
            intent.putExtra("openActivity","mySticker")
            startActivity(intent)
            finish()
        }
        binding.btnCreate.setOnClickListener {
            var intent=(Intent(this@ExitActivity,CreateStickerActivity::class.java))
            intent.putExtra("create","")
            startActivity(intent)
            finish()
        }

        binding.btnBack.setOnClickListener {
            if (!isBackPressed) {
                isBackPressed = true
                InterstitialAdManager.showInterstitial(
                    this@ExitActivity, MainActivity(),
                    "", "", "", "", "finishes", AdsManager.BackInterstitialExitActivity
                )
            }
        }

        binding.rateUsContainer.setOnClickListener {
            showRateUsDialog()
        }

        binding.exit.setOnClickListener {
            finishAffinity()
        }

        onBackPressedDispatcher.addCallback {
            if (!isBackPressed) {
                isBackPressed = true
                InterstitialAdManager.showInterstitial(
                    this@ExitActivity, MainActivity(),
                    "", "", "", "", "finishes", AdsManager.BackInterstitialExitActivity
                )
            }
        }
    }

    private fun showRateUsDialog() {
        var dialog=Dialog(this)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.exit_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        star1 = dialog.findViewById<ImageView>(R.id.star_1)
        star2 = dialog.findViewById<ImageView>(R.id.star_2)
        star3 = dialog.findViewById<ImageView>(R.id.star_3)
        star4 = dialog.findViewById<ImageView>(R.id.star_4)
        star5 = dialog.findViewById<ImageView>(R.id.star_5)
        imgExit = dialog.findViewById<ImageView>(R.id.img_exit)
        var btnContinue = dialog.findViewById<AppCompatButton>(R.id.btn_continue)

        star1?.setImageResource(R.drawable.star_uncheck)
        star2?.setImageResource(R.drawable.star_uncheck)
        star3?.setImageResource(R.drawable.star_uncheck)
        star4?.setImageResource(R.drawable.star_uncheck)
        star5?.setImageResource(R.drawable.star_uncheck)

        btnContinue.setOnClickListener {
            dialog.dismiss()
            /*  if (selectedRating==1){
                  launchEmailIntent()
              }else if (selectedRating==2){
                  launchEmailIntent()
              }else if (selectedRating==3){
                  launchEmailIntent()
              }else if (selectedRating==4){
                  launchPlayStoreIntent()
              }else if (selectedRating==5){
                  launchPlayStoreIntent()
              }*/
        }

        star1?.setOnClickListener {
            launchEmailIntent()
            imgExit?.setImageResource(R.drawable.exit_icon_1)
            selectedRating = 1
            star1?.setImageResource(R.drawable.star_check)
            star2?.setImageResource(R.drawable.star_uncheck)
            star3?.setImageResource(R.drawable.star_uncheck)
            star4?.setImageResource(R.drawable.star_uncheck)
            star5?.setImageResource(R.drawable.star_uncheck)
        }

        star2?.setOnClickListener {
            launchEmailIntent()
            imgExit?.setImageResource(R.drawable.exit_icon_2)
            star1?.setImageResource(R.drawable.star_check)
            star2?.setImageResource(R.drawable.star_check)
            star3?.setImageResource(R.drawable.star_uncheck)
            star4?.setImageResource(R.drawable.star_uncheck)
            star5?.setImageResource(R.drawable.star_uncheck)
            selectedRating = 2

        }

        star3?.setOnClickListener {
            launchEmailIntent()
            imgExit?.setImageResource(R.drawable.exit_icon_3)
            star1?.setImageResource(R.drawable.star_check)
            star2?.setImageResource(R.drawable.star_check)
            star3?.setImageResource(R.drawable.star_check)

            star4?.setImageResource(R.drawable.star_uncheck)
            star5?.setImageResource(R.drawable.star_uncheck)
            selectedRating = 3
        }

        star4?.setOnClickListener {
            launchPlayStoreIntent()
            imgExit?.setImageResource(R.drawable.exit_icon_4)
            star1?.setImageResource(R.drawable.star_check)
            star2?.setImageResource(R.drawable.star_check)
            star3?.setImageResource(R.drawable.star_check)
            star4?.setImageResource(R.drawable.star_check)
            star5?.setImageResource(R.drawable.star_uncheck)
            selectedRating = 4
        }

        star5?.setOnClickListener {
            launchPlayStoreIntent()
            imgExit?.setImageResource(R.drawable.exit_icon_4)
            star1?.setImageResource(R.drawable.star_check)
            star2?.setImageResource(R.drawable.star_check)
            star3?.setImageResource(R.drawable.star_check)
            star4?.setImageResource(R.drawable.star_check)
            star5?.setImageResource(R.drawable.star_check)
            selectedRating = 5
        }

        dialog.show()

    }

    private fun launchPlayStoreIntent() {
        isPlayStoreLaunch=true
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    private fun launchEmailIntent() {
        isEmailWriting=true
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(AdsManager.Email_Address))
        intent.putExtra(Intent.EXTRA_SUBJECT, "FeedBack"+" ${getString(R.string.app_name)} "+BuildConfig.VERSION_CODE)
        startActivity(Intent.createChooser(intent, "Email via..."))
    }

    override fun onResume() {
        super.onResume()
        star1?.setImageResource(R.drawable.star_uncheck)
        star2?.setImageResource(R.drawable.star_uncheck)
        star3?.setImageResource(R.drawable.star_uncheck)
        star4?.setImageResource(R.drawable.star_uncheck)
        star5?.setImageResource(R.drawable.star_uncheck)
    }
}