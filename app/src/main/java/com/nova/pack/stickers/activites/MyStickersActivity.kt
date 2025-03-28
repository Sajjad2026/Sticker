package com.nova.pack.stickers.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nova.pack.stickers.R
import com.nova.pack.stickers.adapter.SavedStickerAdapter
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.AdsManager.BannerBottomMyStickersActivity
import com.nova.pack.stickers.ads.AdsManager.BannerTopMyStickersActivity
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivityMyStickersBinding
import com.nova.pack.stickers.listener.OnMyStickerExportButtonClick
import com.nova.pack.stickers.listener.onStickerPackClickListener
import com.nova.pack.stickers.model.StickerPackView
import com.nova.pack.stickers.utils.SharedPreferenceHelper
import com.nova.pack.stickers.viewmodel.EditImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyStickersActivity : AppCompatActivity(),onStickerPackClickListener,OnMyStickerExportButtonClick {
    lateinit var binding: ActivityMyStickersBinding
    private var savedStickerAdapter: SavedStickerAdapter?= null
    private lateinit var viewModel: EditImageViewModel
    private val preferences by lazy { SharedPreferenceHelper() }
    private var isBackPressed=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyStickersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        BannerAdManager.loadBannerAd(this, binding.bannerLayout,binding.bannerArea,binding.nativeContainer,BannerTopMyStickersActivity)
        BannerAdManager.loadBannerAd(this, binding.bannerLayout2,binding.bannerArea2,binding.nativeContainer2,BannerBottomMyStickersActivity)

        viewModel = ViewModelProvider(this)[EditImageViewModel(this)::class.java]
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getStickers()
        }
        populateStickerRecyclerView()
        loadStickers()
    }
    private fun loadStickers() {
        viewModel.stickerList.observe(this){
            val list = it.map {it1->
                it1.toStickersPack()
            }.map { either ->
                either.toStickersPackView()
            }
            if(list.isEmpty())
            {
                binding.emptyLyt.visibility = View.VISIBLE
                binding.savedStickerRecyclerview.visibility = View.GONE

            }
            else
            {
                binding.emptyLyt.visibility = View.GONE
                binding.savedStickerRecyclerview.visibility = View.VISIBLE

            }
            savedStickerAdapter?.setList(list)
            preferences.saveObjectsList("sticker_packs", list)
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isBackPressed){
                    isBackPressed=true
                    InterstitialAdManager.showInterstitial(this@MyStickersActivity, MainActivity(),"","",
                        ""!!,"", "finish",
                        AdsManager.BackInterstitialMainActivity
                    )
                }
            }
        })
    }

    private fun populateStickerRecyclerView() {
        binding.savedStickerRecyclerview.layoutManager = LinearLayoutManager(this)
        savedStickerAdapter = SavedStickerAdapter(this,this,this)
        binding.savedStickerRecyclerview.adapter = savedStickerAdapter
    }

    override fun OnStickerClickListener(item: StickerPackView) {
        viewModel.currentIdentifier = item.identifier
        val intent = Intent(this,CreateStickerActivity::class.java)
        intent.putExtra("activityName","mySticker")
        startActivity(intent)
    }

    override fun onClick(identifier: String) {
        viewModel.addCustomPackToWhatsappByIdentifier(identifier,this)
    }
}