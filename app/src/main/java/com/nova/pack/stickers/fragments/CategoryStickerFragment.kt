package com.nova.pack.stickers.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nova.pack.stickers.R
import com.nova.pack.stickers.activites.MainActivity
import com.nova.pack.stickers.activites.SearchStickersActivity
import com.nova.pack.stickers.activites.StickerDetailActivity
import com.nova.pack.stickers.adapter.CategoryPagerAdapter
import com.nova.pack.stickers.adapter.HomeStickerAdapter
import com.nova.pack.stickers.ads.AdsManager.BackInterstitialCategoryStickerFragment
import com.nova.pack.stickers.ads.AdsManager.BannerBottomCategoryStickerFragment
import com.nova.pack.stickers.ads.AdsManager.BannerTopCategoryStickerFragment
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.api.StickerJson.stickerData
import com.nova.pack.stickers.databinding.FragmentCategoryStickerBinding
import com.nova.pack.stickers.listener.ItemClick
import com.nova.pack.stickers.listener.onStickerPackClickListener
import com.nova.pack.stickers.model.StickerPackView
import com.nova.pack.stickers.utils.Config.Companion.isClickBottom
import com.nova.pack.stickers.viewmodel.StickersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryStickerFragment : Fragment(), onStickerPackClickListener {
    private lateinit var binding: FragmentCategoryStickerBinding
    private lateinit var viewModel: StickersViewModel
    private var mHomeStickerAdapter: HomeStickerAdapter? = null
    lateinit var itemClick: ItemClick

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemClick) {
            itemClick = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.theme?.applyStyle(R.style.Theme_Fab_Bottom_app_bar, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCategoryStickerBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[StickersViewModel::class.java]

        CoroutineScope(Dispatchers.IO).launch {
            // viewModel.getAllCategoryStickers()
        }
        loadHomeStickers()
        loadBottomBanner()
        populateHomeStickerRecyclerView()

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = CategoryPagerAdapter(requireFragmentManager(), lifecycle)
        viewPager.adapter = adapter

        arguments?.getInt("position", 0)?.let {
            viewPager.setCurrentItem(it, true)
        }

        binding.stickersSearchView.setOnClickListener {
            val intent = Intent(context, SearchStickersActivity::class.java)
            intent.putExtra("activity", "category")
            startActivity(intent)
        }

        /* this is invoked when you click on the input field */
        binding.stickersSearchView.setOnQueryTextFocusChangeListener { view, b ->
            val intent = Intent(context, SearchStickersActivity::class.java)
            intent.putExtra("activity", "category")
            startActivity(intent)
            binding.stickersSearchView.setOnQueryTextFocusChangeListener(null)
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTab = LayoutInflater.from(tabLayout.context)
                .inflate(R.layout.custom_tab, null) as TextView
            customTab.text = stickerData[position]
            tab.customView = customTab
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.customView?.let { view ->
                    val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.2f)
                    val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.2f)
                    AnimatorSet().apply {
                        playTogether(scaleUpX, scaleUpY)
                        duration = 300
                        start()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.let { view ->
                    val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1.0f)
                    val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1.0f)
                    AnimatorSet().apply {
                        playTogether(scaleDownX, scaleDownY)
                        duration = 300
                        start()
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Optional: Handle reselection animations if needed.
            }
        })





        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isClickBottom=false
                loadBackButtonInterstitial()
                Log.d("sfdhfdsa", "back click")
            }
        })

        return binding.root
    }

    private fun loadBackButtonInterstitial() {
        itemClick.onClickListener("category")
         InterstitialAdManager.showInterstitial(requireActivity() as AppCompatActivity, MainActivity(), "", "", "", "", "fragment", BackInterstitialCategoryStickerFragment)
    }

    private fun loadBottomBanner() {
        // Delay loading of banner ads until any previous transaction completes
        Handler(Looper.getMainLooper()).post {
            activity?.supportFragmentManager?.executePendingTransactions()

            BannerAdManager.loadBannerAd(
                requireActivity() as AppCompatActivity,
                binding.bannerLayout,
                binding.bannerArea,
                binding.nativeContainer,
                BannerTopCategoryStickerFragment
            )
            BannerAdManager.loadBannerAd(
                requireActivity() as AppCompatActivity,
                binding.bannerLayout2,
                binding.bannerArea2,
                binding.nativeContainer2,
                BannerBottomCategoryStickerFragment
            )
        }
    }

    private fun loadHomeStickers() {
        viewModel.sticker.observe(viewLifecycleOwner) {
            mHomeStickerAdapter?.setList(it!!)
            // preferences.saveObjectsList("sticker_packs", it)
        }
    }

    private fun populateHomeStickerRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mHomeStickerAdapter = HomeStickerAdapter(requireActivity(), this)
        mHomeStickerAdapter?.setFragment(true)
        binding.recyclerView.adapter = mHomeStickerAdapter
    }

    override fun OnStickerClickListener(item: StickerPackView) {
        // Delay the fragment transaction to avoid overlapping
        Handler(Looper.getMainLooper()).post {
            val intent = Intent(context, StickerDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(HomeFragment.EXTRA_STICKERPACK, item)
            intent.putExtras(bundle)

            // Ensure fragment transactions are completed
            activity?.supportFragmentManager?.executePendingTransactions()

            requireContext().startActivity(intent)
        }
    }
}
