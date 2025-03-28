package com.nova.pack.stickers.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nova.pack.stickers.R
import com.nova.pack.stickers.activites.CreateStickerActivity
import com.nova.pack.stickers.adapter.SavedStickerAdapter
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.BannerAdManager

import com.nova.pack.stickers.databinding.FragmentMyStickerBinding
import com.nova.pack.stickers.listener.ItemClick
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
class MyStickerFragment : Fragment(),onStickerPackClickListener, OnMyStickerExportButtonClick {
    private lateinit var binding : FragmentMyStickerBinding
    private var savedStickerAdapter: SavedStickerAdapter?= null
    private lateinit var viewModel: EditImageViewModel
    private val preferences by lazy { SharedPreferenceHelper() }
    lateinit var itemClick: ItemClick
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemClick) {
            itemClick = context
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.theme?.applyStyle(R.style.Theme_WhatsappStickerMaker, true);
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  FragmentMyStickerBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[EditImageViewModel(requireContext())::class.java]

        BannerAdManager.loadBannerAd(requireActivity() as AppCompatActivity, binding.bannerLayout,binding.bannerArea,binding.nativeContainer, AdsManager.BannerTopMyStickerFragment)
        BannerAdManager.loadBannerAd(requireActivity() as AppCompatActivity, binding.bannerLayout2,binding.bannerArea2,binding.nativeContainer2, AdsManager.BannerBottomMyStickerFragment)

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getStickers()
        }
        populateStickerRecyclerView()
        loadStickers()
        return binding.root
    }
    private fun loadStickers() {
        viewModel.stickerList.observe(requireActivity()){
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
        binding.createStickerPackBtn.setOnClickListener {
            viewModel.currentIdentifier = ""
            val intent = Intent(requireContext(), CreateStickerActivity::class.java)
            intent.putExtra("activityName","mySticker")
            startActivity(intent)
            requireActivity().finish()
        }
        loadNativeAd()
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                    itemClick.onClickListener("sticker")
                }
        })
    }

    private fun loadNativeAd() {
      //  NativeAdManager.nativeAdmob(requireActivity() as AppCompatActivity,binding.admobNativeAdContainer, requireActivity().window,NativeHomeActivity)
    }

    private fun populateStickerRecyclerView() {
        binding.savedStickerRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        savedStickerAdapter = SavedStickerAdapter(requireContext(),this,this)
        binding.savedStickerRecyclerview.adapter = savedStickerAdapter
    }

    override fun OnStickerClickListener(item: StickerPackView) {
        viewModel.currentIdentifier = item.identifier
        val intent = Intent(requireContext(), CreateStickerActivity::class.java)
        intent.putExtra("activityName","mySticker")
        startActivity(intent)
    }

    override fun onClick(identifier: String) {
        viewModel.addCustomPackToWhatsappByIdentifier(identifier,requireActivity())
    }
}