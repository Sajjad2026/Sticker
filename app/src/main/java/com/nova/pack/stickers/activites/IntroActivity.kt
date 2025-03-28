package com.nova.pack.stickers.activites

import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import com.nova.pack.stickers.R
import com.nova.pack.stickers.Utils
import com.nova.pack.stickers.adapter.IntroViewPagerAdapter
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivityIntroBinding
import com.nova.pack.stickers.model.IntroScreenItem

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private var introViewPagerAdapter: IntroViewPagerAdapter? = null
    private var position = 0

    private var currentPage = 0
    var isStart=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupViewPager()
        loadTopBanner()
        onBackPressedDispatcher.addCallback {  }
    }

    private fun loadTopBanner() {
        BannerAdManager.loadBannerAd(this, binding.bannerLayout, binding.bannerArea, binding.nativeContainer, AdsManager.BannerTopIntroActivity)
        BannerAdManager.loadBannerAd(this, binding.bannerLayout2, binding.bannerArea2, binding.nativeAd2, AdsManager.BannerBottomIntroActivity)
    }

    private fun setupViewPager() {
        // fill list screen
        val mList: MutableList<IntroScreenItem> = ArrayList()
        mList.add(IntroScreenItem(resources.getString(R.string.intro_title1), resources.getString(R.string.intro_description1), R.drawable.intro_icon1))
        mList.add(IntroScreenItem(resources.getString(R.string.intro_title2), resources.getString(R.string.intro_description2), R.drawable.intro_icon2))
        mList.add(IntroScreenItem(resources.getString(R.string.intro_title3), resources.getString(R.string.intro_description3), R.drawable.intro_icon3))
        mList.add(IntroScreenItem(resources.getString(R.string.intro_title4), resources.getString(R.string.intro_description4), R.drawable.intro_icon4))
        mList.add(IntroScreenItem(resources.getString(R.string.intro_title5), resources.getString(R.string.intro_description5), R.drawable.intro_icon5))

        // setup viewpager
        introViewPagerAdapter = IntroViewPagerAdapter(this, mList)
        binding.introViewPager.adapter = introViewPagerAdapter

        // setup tabLayout with viewpager
        binding.dotsIndicator.setViewPager(binding.introViewPager)

        // next button click Listener
        binding.btnNext.setOnClickListener {
            position = binding.introViewPager.currentItem
            if (position == 4) {
                if (AdsManager.ShowInAppAfterIntro) {
                    InterstitialAdManager.showInterstitial(this, InAppActivity(), "", "", ""!!, "", "finishes", AdsManager.InterstitialIntroToMainActivity)
                } else {
                    InterstitialAdManager.showInterstitial(this, MainActivity(), "", "", ""!!, "", "finishes", AdsManager.InterstitialIntroToMainActivity)
                }
                Utils.savePrefsData(this)
            }
            if (position < mList.size) {
                position++
                binding.introViewPager.currentItem = position
            }
            if (position == mList.size - 1) { // when we reach to the last screen
              //  loadLastScreen()
            }
        }

        // tabLayout add change listener
        binding.introViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position == 5) {
                    Log.d("asdkjhad","4")
                    isStart=true
                    // binding.btnStart?.setVisibility(View.VISIBLE)
                    //binding.btnSkip?.setVisibility(View.GONE)
                } else {
                    isStart=false
                    //  binding.btnStart?.setVisibility(View.GONE)
                    // binding.btnSkip?.setVisibility(View.VISIBLE)
                }
            }

            override fun onPageSelected(position: Int) {
                currentPage = position
                if (currentPage == introViewPagerAdapter!!.count - 1) {
                    isStart=true
                } else {
                    isStart=false
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    // show the GET STARTED Button and hide the indicator and the next button

}
