package com.nova.pack.stickers.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nova.pack.stickers.R
import com.nova.pack.stickers.Utils
import com.nova.pack.stickers.activites.*
import com.nova.pack.stickers.adapter.CategoryStickersAdapter
import com.nova.pack.stickers.adapter.HomeStickerAdapter
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.Constants.Companion.checkIfPlay
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.api.StickerJson
import com.nova.pack.stickers.databinding.FragmentHomeBinding
import com.nova.pack.stickers.listener.OnCategorySelectedListener
import com.nova.pack.stickers.listener.onStickerPackClickListener
import com.nova.pack.stickers.model.StickerPackView
import com.nova.pack.stickers.utils.Config
import com.nova.pack.stickers.utils.SharedPreferenceHelper
import com.nova.pack.stickers.viewmodel.StickersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), onStickerPackClickListener, OnCategorySelectedListener {
    private lateinit var viewModel: StickersViewModel
    private lateinit var binding: FragmentHomeBinding
    private var mCategoryAdapter: CategoryStickersAdapter? = null
    private var mHomeStickerAdapter: HomeStickerAdapter? = null
    private val fragmentTransactionLock = Any()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.theme?.applyStyle(R.style.Theme_Fab_Bottom_app_bar, true)
    }

    private val preferences by lazy { SharedPreferenceHelper() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[StickersViewModel::class.java]

        BannerAdManager.loadBannerAd(
            requireActivity() as AppCompatActivity, binding.bannerLayout, binding.bannerArea, binding.nativeContainer, AdsManager.BannerTopHomeFragment
        )
        BannerAdManager.loadBannerAd(
            requireActivity() as AppCompatActivity, binding.bannerLayout2, binding.bannerArea2, binding.nativeContainer2, AdsManager.BannerBottomHomeFragment
        )

        try {
            populateCategoryRecyclerView()
            loadCategories()
            populateHomeStickerRecyclerView()
            loadHomeStickers()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!Utils.isNetworkAvailable(requireContext())) {
            binding.emptyLyt.visibility = View.VISIBLE
            binding.seeAllCategory.visibility = View.INVISIBLE
        } else {
            binding.emptyLyt.visibility = View.GONE
            binding.seeAllCategory.visibility = View.VISIBLE
        }

        viewModel.getHomeStickers("home")


        setObservers()

        binding.seeAllCategory.setOnClickListener {
            if (!checkIfPlay) {
                showDialog()
            } else {
                Config.isClickBottom = false
                val fragment = CategoryStickerFragment()
                val bundle = Bundle()
                bundle.putInt("position", 0)
                fragment.arguments = bundle
                safeFragmentTransaction {
                    val fragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    fragmentTransaction.replace(R.id.mainFragment, fragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("Sfmsnf", "back")
                InterstitialAdManager.showInterstitial(
                    requireActivity() as AppCompatActivity, ExitActivity(), "", "", "", "", "finishes",
                    AdsManager.BackInterstitialMainActivity
                )
            }
        })

        return binding.root
    }

    private fun safeFragmentTransaction(action: () -> Unit) {
        synchronized(fragmentTransactionLock) {
            action()
        }
    }

    private fun loadHomeStickers() {
        viewModel.sticker.observe(viewLifecycleOwner) {
            Log.d("Sfdkhff", it.toString())
            mHomeStickerAdapter?.setList(it!!)
            preferences.saveObjectsList("sticker_packs", it)
        }
    }

    private fun populateHomeStickerRecyclerView() {
        binding.homeStickersRecyclerview.layoutManager = LinearLayoutManager(context)
        mHomeStickerAdapter = HomeStickerAdapter(requireActivity(), this)
        binding.homeStickersRecyclerview.adapter = mHomeStickerAdapter
    }

    private fun loadCategories() {
        mCategoryAdapter?.setList(StickerJson.categoryList)
        mCategoryAdapter?.notifyDataSetChanged()
    }

    private fun populateCategoryRecyclerView() {
        val horizontalLayout = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.categoryRecyclerview.layoutManager = horizontalLayout
        mCategoryAdapter = CategoryStickersAdapter(requireContext(), this)
        binding.categoryRecyclerview.adapter = mCategoryAdapter
    }

    companion object {
        const val EXTRA_STICKER_PACK_ID = "sticker_pack_id"
        const val EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority"
        const val EXTRA_STICKER_PACK_NAME = "sticker_pack_name"
        const val EXTRA_STICKERPACK = "stickerpack"

        @JvmField
        var path: String? = null
    }

    override fun OnStickerClickListener(item: StickerPackView) {
        val intent = Intent(context, StickerDetailActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(EXTRA_STICKERPACK, item)
        intent.putExtras(bundle)
        requireContext().startActivity(intent)
    }

    override fun onCategorySelectedListener(category: String, position: Int) {
        val fragment = CategoryStickerFragment()
        val bundle = Bundle()
        bundle.putInt("position", position)
        fragment.arguments = bundle
        safeFragmentTransaction {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.mainFragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    private fun setObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            val dataState = it ?: return@observe
            binding.progressBar.visibility = if (dataState.isLoading) View.VISIBLE else View.GONE
            Log.d("Error:", it.error.toString())
        }
    }

    private fun showDialog() {
        val dialog = Dialog(requireActivity())
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.suspend_dialog)
        requireActivity().window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val button = dialog.findViewById<AppCompatButton>(R.id.btn_done)
        button.setOnClickListener {
            dialog.dismiss()
            val appPackageName = requireActivity().packageName
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
        dialog.show()
    }
   /* private fun getStickerPack() {
            val trayImageFile = stickerPackView!!.trayImageFile.getLastBitFromUrl()

            val req = ImageRequest.Builder(this@StickerDetailsActivity)
                .data(getApiEndpointStickers() + stickerPackView!!.identifier + "/" + trayImageFile)
                .target {
                    val myDir =
                        File("${MainActivity.path}/${stickerPackView!!.identifier}/try")
                    myDir.mkdirs()
                    val imageName = trayImageFile.replace(".png", "").replace(" ", "_") + ".png"
                    val file = File(myDir, imageName)
                    if (file.exists()) file.delete()
                    try {
                        val out = FileOutputStream(file)
                        it.toBitmap().compress(Bitmap.CompressFormat.PNG, 40, out)
                        out.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.build()

            lifecycleScope.launch { ImageLoader(this@StickerDetailsActivity).execute(req) }

            for (s in stickerPackView!!.stickers) {
                val imageFile = s.imageFile.getLastBitFromUrl()

                val myDir = File("${MainActivity.path}/${stickerPackView!!.identifier}")
                myDir.mkdirs()
                val file = File(myDir, imageFile)
                if (file.exists()) file.delete()

                (getApiEndpointStickers() + stickerPackView!!.identifier + "/" + imageFile).saveImage(
                    File("${MainActivity.path}/${stickerPackView!!.identifier}", imageFile)
                )
            }
    }*/
}
