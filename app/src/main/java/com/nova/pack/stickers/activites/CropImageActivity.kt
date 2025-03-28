package com.nova.pack.stickers.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.jeluchu.jchucomponents.ktx.any.isNull
import com.nova.pack.stickers.R
import com.nova.pack.stickers.adapter.ShapesAdapter
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivityCropImageBinding
import com.nova.pack.stickers.listener.OnShapeClickListener
import com.nova.pack.stickers.viewmodel.EditImageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CropImageActivity : AppCompatActivity(),OnShapeClickListener {
    lateinit var binding: ActivityCropImageBinding
    private lateinit var viewModel: EditImageViewModel
    private var mShapeAdapter: ShapesAdapter?= null
    private var isOval = false
    private var isBackPressed=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropImageBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[EditImageViewModel(this)::class.java]
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if(viewModel.editedImage.value.isNull()) {
            binding.cropImageView.setImageBitmap(viewModel.originalImage.value)
        }
        else
        {
            binding.cropImageView.setImageBitmap(viewModel.editedImage.value)
        }
        binding.btnFlipH.setOnClickListener {
            binding.cropImageView.flipImageHorizontally()
        }
        binding.btnFlipV.setOnClickListener {
            binding.cropImageView.flipImageVertically()
        }
        binding.doneBtn.setOnClickListener {
           loadDoneButtonInterstitial()
        }
        binding.backBtn.setOnClickListener {
            if (!isBackPressed){
                isBackPressed=true
                loadBackButtonInterstitial()
            }
        }
        binding.btnShapes.setOnClickListener{
            binding.lytEdit.visibility = View.INVISIBLE
            binding.lytShapes.visibility = View.VISIBLE
        }
        binding.shapeImageview.setOnClickListener {
            binding.lytEdit.visibility = View.VISIBLE
            binding.lytShapes.visibility = View.INVISIBLE
        }
        loadShapesRecyclerView()
        loadShapes()
        loadBottomBanner()

        onBackPressedDispatcher.addCallback {
            if (!isBackPressed){
                isBackPressed=true
            loadBackButtonInterstitial()
        }
        }
    }

    private fun loadDoneButtonInterstitial() {
        var croppedBitmap = binding.cropImageView.getCroppedImage()
        if(isOval) {
            croppedBitmap = binding.cropImageView.getCroppedImage()
                ?.let { it1 -> CropImage.toOvalBitmap(it1) }
        }
        viewModel.editedImage.postValue(croppedBitmap).let {
            try {
                val activityName = intent.getStringExtra("activityName")
                InterstitialAdManager.showInterstitial(this@CropImageActivity, EditStickerImageActivity(),"activityName","",
                    activityName!!,"", "finishes",
                    AdsManager.InterstitialCropImageToEditStickerImageActivity
                )
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun loadBackButtonInterstitial(){
        InterstitialAdManager.showInterstitial(this@CropImageActivity, CropImageActivity(),"","",
            ""!!,"", "finish",
            AdsManager.BackInterstitialCropImageActivity
        )
    }
    private fun loadBottomBanner() {
        BannerAdManager.loadBannerAd(this, binding.bannerLayout,binding.bannerArea,binding.nativeContainer, AdsManager.BannerTopCropImageActivity)
        BannerAdManager.loadBannerAd(this, binding.bannerLayout2,binding.bannerArea2,binding.nativeContainer2, AdsManager.BannerBottomCropImageActivity)
    }

    private fun loadShapes() {
        val list = ArrayList<Int>()
        list.add(R.drawable.ic_crop_square)
        list.add(R.drawable.ic_oval)
        mShapeAdapter!!.setList(list)
    }

    private fun loadShapesRecyclerView() {
        binding.shapesRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        mShapeAdapter = ShapesAdapter(this,this)
        binding.shapesRecyclerView.adapter = mShapeAdapter
    }

    override fun onShapeClick(position: Int) {
        when(position){
            0->{
                binding.cropImageView.cropShape = CropImageView.CropShape.RECTANGLE
                isOval = false
            }
            1->{
                isOval = true
                binding.cropImageView.cropShape = CropImageView.CropShape.OVAL
            }
        }
    }
}