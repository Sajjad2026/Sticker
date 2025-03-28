package com.nova.pack.stickers.activites


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nova.pack.stickers.R
import com.nova.pack.stickers.adapter.HomeStickerAdapter
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.AdsManager.NativeReturningHomeActivity
import com.nova.pack.stickers.ads.AdsManager.ShowInAppAfterReturning
import com.nova.pack.stickers.ads.AdsManager.addSession
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.ads.NativeAdManager
import com.nova.pack.stickers.databinding.ActivityReturningHomeScreenBinding
import com.nova.pack.stickers.fragments.HomeFragment.Companion.EXTRA_STICKERPACK
import com.nova.pack.stickers.listener.onStickerPackClickListener
import com.nova.pack.stickers.model.StickerPackView
import com.nova.pack.stickers.utils.Config.Companion.isCreate
import com.nova.pack.stickers.utils.SharedPreferenceHelper
import com.nova.pack.stickers.viewmodel.StickersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReturningHomeScreen : AppCompatActivity(), onStickerPackClickListener {
    lateinit var binding:ActivityReturningHomeScreenBinding
    private lateinit var viewModel: StickersViewModel
    private var mHomeStickerAdapter: HomeStickerAdapter? = null
    private val preferences by lazy { SharedPreferenceHelper() }
    var isBackPressed=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityReturningHomeScreenBinding.inflate(layoutInflater)
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
        viewModel = ViewModelProvider(this)[StickersViewModel::class.java]
        viewModel.getHomeStickers("return")
        addSession()
        try {
            populateHomeStickerRecyclerView()
            loadHomeStickers()
        } catch (e: Exception) {
            e.printStackTrace()
        }

       /* binding.video.setOnClickListener {
            if (ShowInAppAfterReturning){
                startActivity(Intent(this@ReturningHomeScreen,InAppActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this@ReturningHomeScreen,MainActivity::class.java))
                finish()
            }
        }
        binding.btnClick.setOnClickListener {
            if (ShowInAppAfterReturning){
                startActivity(Intent(this@ReturningHomeScreen,InAppActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this@ReturningHomeScreen,MainActivity::class.java))
                finish()
            }
        }*/

        NativeAdManager.nativeAdmob(this as AppCompatActivity, binding.nativeContainer, window,NativeReturningHomeActivity)

        binding.btnHome.setOnClickListener {
            if (ShowInAppAfterReturning){
                InterstitialAdManager.showInterstitial(this@ReturningHomeScreen, InAppActivity(),"","",
                    "","", "finishes", AdsManager.InterstialReturningHomeToMainActivity)
            }else{
                InterstitialAdManager.showInterstitial(this@ReturningHomeScreen, MainActivity(),"","",
                    "","", "finishes", AdsManager.InterstialReturningHomeToMainActivity)
            }
        }

        binding.btnSticker.setOnClickListener {
            isCreate=true
            if (ShowInAppAfterReturning){
                InterstitialAdManager.showInterstitial(this@ReturningHomeScreen, InAppActivity(),"","",
                    "","", "finishes", AdsManager.InterstitialReturningHomeToCreateFragment)
            }else{
                InterstitialAdManager.showInterstitial(this@ReturningHomeScreen, MainActivity(),"return","",
                    "mySticker","", "finishes", AdsManager.InterstitialReturningHomeToCreateFragment)
            }
        }

        binding.btnAddSticker.setOnClickListener {
            isCreate=true
            if (ShowInAppAfterReturning){
                InterstitialAdManager.showInterstitial(this@ReturningHomeScreen, InAppActivity(),"","",
                    "","", "finishes", AdsManager.InterstitialReturningHomeToCreateFragment)
            }else{
                InterstitialAdManager.showInterstitial(this@ReturningHomeScreen, CreateStickerActivity(),"","",
                    "create","", "finishes", AdsManager.InterstitialReturningHomeToCreateStickerActivity)
            }
        }

        onBackPressedDispatcher.addCallback {
        }
    }
    private fun loadHomeStickers() {
        viewModel.sticker.observe(this) {
            Log.d("Sfdkhff", it.toString())
            mHomeStickerAdapter?.setList(it!!)
            preferences.saveObjectsList("sticker_packs", it)
        }
    }

    private fun populateHomeStickerRecyclerView() {
        binding.homeStickersRecyclerview.layoutManager = LinearLayoutManager(this)
        mHomeStickerAdapter = HomeStickerAdapter(this, this)
        binding.homeStickersRecyclerview.adapter = mHomeStickerAdapter
    }

    override fun OnStickerClickListener(item: StickerPackView) {
        val intent = Intent(this, StickerDetailActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(EXTRA_STICKERPACK, item)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}