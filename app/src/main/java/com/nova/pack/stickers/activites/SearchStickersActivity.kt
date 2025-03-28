package com.nova.pack.stickers.activites

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nova.pack.stickers.R
import com.nova.pack.stickers.adapter.HomeStickerAdapter
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivitySearchStickersBinding
import com.nova.pack.stickers.fragments.HomeFragment
import com.nova.pack.stickers.listener.onStickerPackClickListener
import com.nova.pack.stickers.model.StickerPackView
import com.nova.pack.stickers.viewmodel.StickersViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import java.util.concurrent.Executors

@AndroidEntryPoint
class SearchStickersActivity : AppCompatActivity(), onStickerPackClickListener {
    private lateinit var viewModel: StickersViewModel
    lateinit var binding: ActivitySearchStickersBinding
    private var mHomeStickerAdapter: HomeStickerAdapter?= null
  //  private var allStickerList = ArrayList<StickerPackView>()
    var activityName = ""
    var isBackPressed=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchStickersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadBottomBanner()
        viewModel = ViewModelProvider(this)[StickersViewModel::class.java]
        activityName = intent.getStringExtra("activity").toString()
        viewModel.getHomeStickers("search")


       /* viewModel.allSticker.observe(this) {
            try {
                allStickerList = it as ArrayList<StickerPackView>
                preferences.saveObjectsList("sticker_packs", it)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }*/
     //   allStickerList = viewModel.allSticker

        setAdapter(viewModel.sticker.value as ArrayList<StickerPackView>)
        addTextListener()
        binding.backBtn.setOnClickListener {
            if (!isBackPressed) {
                isBackPressed=true
                loadInterstitialAd()
            }
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isBackPressed) {
                    isBackPressed=true
                    loadInterstitialAd()
                }
            }
        })

        showSoftKeyboard()
    }
    private fun loadBottomBanner() {
        BannerAdManager.loadBannerAd(
            this,
            binding.bannerLayout,
            binding.bannerArea,
            binding.nativeContainer,
            AdsManager.BannerTopSearchStickersActivity
        )
        BannerAdManager.loadBannerAd(
            this,
            binding.bannerLayout2,
            binding.bannerArea2,
            binding.nativeContainer2,
            AdsManager.BannerBottomSearchStickersActivity
        )
    }

    private fun loadInterstitialAd() {
        InterstitialAdManager.showInterstitial(
            this@SearchStickersActivity, MainActivity(), "", "", "", "", "finishes", AdsManager.BackInterstitialSearchStickersActivity
        )
    }
    private fun setAdapter(allStickerList:ArrayList<StickerPackView>) {
        var executor = Executors.newSingleThreadExecutor()
        executor.execute {
            runOnUiThread {
                binding.homeStickersRecyclerview.layoutManager = LinearLayoutManager(this)
                mHomeStickerAdapter = HomeStickerAdapter(this,this)
                binding.homeStickersRecyclerview.adapter = mHomeStickerAdapter
                Log.d("sdfjfg",allStickerList.size.toString())
                mHomeStickerAdapter?.setList(allStickerList)
               // preferences.saveObjectsList("sticker_packs", allStickerList)
                if (mHomeStickerAdapter?.itemCount == 0) {
                    binding.emptyLyt.visibility = View.VISIBLE
                } else {
                    binding.emptyLyt.visibility = View.GONE
                }
            }
        }
    }

    fun filter(text: String) {
        val searchText = text.lowercase(Locale.getDefault())
        val stickers = viewModel.sticker.value
        Log.d("asdhsd",stickers.toString())
        if (stickers?.isNotEmpty() == true) {
            val filteredList = stickers.filter { item ->
                val fileName = item.name.lowercase()
                fileName.contains(searchText) && fileName.isNotEmpty()
            }
//            allStickerList.clear()
//            allStickerList.addAll(filteredList)
            setAdapter(filteredList as ArrayList<StickerPackView>)
        } else {
            Log.d("Sdjklsf", stickers.toString())
        }

        mHomeStickerAdapter?.notifyDataSetChanged()
    }

    fun addTextListener() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(query: CharSequence, start: Int, before: Int, count: Int) {
                if (count == 0) {
                    setAdapter(viewModel.sticker.value as ArrayList<StickerPackView>)
                } else {
                    filter(query.toString())
                }
            }
        })
    }

  /*  private fun populateStickerRecyclerView() {

    }*/
   /* private fun loadStickers() {
        viewModel.sticker.observe(this){

        }
    }*/

    override fun OnStickerClickListener(item: StickerPackView) {
        val intent = Intent(this,StickerDetailActivity::class.java)
        intent.putExtra("isSearch",true)
        val bundle = Bundle()
        bundle.putParcelable(HomeFragment.EXTRA_STICKERPACK, item)
        intent.putExtras(bundle)
        startActivity(intent)
    }
    private fun showSoftKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edtSearch, InputMethodManager.SHOW_IMPLICIT)
    }
}