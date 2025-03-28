package com.nova.pack.stickers.activites

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.AdsManager.Is_Show_Non_Functional_Items
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivityEditStickerImageBinding
import com.nova.pack.stickers.fragments.AddTextDialogFragment
import com.nova.pack.stickers.fragments.StickerBottomDialogFragment
import com.nova.pack.stickers.model.StickerEntity
import com.nova.pack.stickers.utils.FileUtils
import com.nova.pack.stickers.viewmodel.EditImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.ViewType
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random


@AndroidEntryPoint
class EditStickerImageActivity : AppCompatActivity(),OnPhotoEditorListener,StickerBottomDialogFragment.StickerListener {
    private lateinit var viewModel: EditImageViewModel
    lateinit var binding: ActivityEditStickerImageBinding
    lateinit var mPhotoEditor:PhotoEditor
    private var isBackPressed=false

    private lateinit var mStickerBottomDialogFragment: StickerBottomDialogFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStickerImageBinding.inflate(layoutInflater)
        mStickerBottomDialogFragment = StickerBottomDialogFragment()
        mStickerBottomDialogFragment.setStickerListener(this)
        viewModel = ViewModelProvider(this)[EditImageViewModel(this)::class.java]
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mPhotoEditor = PhotoEditor.Builder(this, binding.imageView)
            .setPinchTextScalable(true) // set flag to make text scalable when pinch
            .build() // build photo editor sdk
        val imagePath = intent.getStringExtra("imagePath")
        loadBottomBanner()
        loadTopBanner()
        if(imagePath!=null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(imagePath))
                viewModel.originalImage.postValue(bitmap)
            } catch (e: Exception) {
                Log.d("ErrorImage",e.toString())
            }
            Glide.with(this).load(imagePath).into(binding.imageView.source).view
        }
        if (Is_Show_Non_Functional_Items){
            binding.btnBorder.visibility=View.VISIBLE
        }
        binding.btnCrop.setOnClickListener {
            binding.zoomButton. imageTintList=getColorStateList(R.color.black)
            binding.txt1.setTextColor(getColor(R.color.black))
            binding.img2.imageTintList=getColorStateList(R.color.bg_color)
            binding.txt2.setTextColor(getColor(R.color.bg_color))
            binding.img3.imageTintList=getColorStateList(R.color.black)
            binding.txt3.setTextColor(getColor(R.color.black))
            binding.img4.imageTintList=getColorStateList(R.color.black)
            binding.txt4.setTextColor(getColor(R.color.black))
            binding.img5.imageTintList=getColorStateList(R.color.black)
            binding.txt5.setTextColor(getColor(R.color.black))

            lifecycleScope.launch {
                try {
                    val saveSettings = SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build()

                    val addedStickerBitmap = mPhotoEditor.saveAsBitmap(saveSettings)

                    // Log the bitmap state before posting to ViewModel
                    Log.d("EditStickerImageActivity", "addedStickerBitmap: $addedStickerBitmap, recycled: ${addedStickerBitmap.isRecycled}")

                    if (addedStickerBitmap != null && !addedStickerBitmap.isRecycled) {
                        viewModel.editedImage.postValue(addedStickerBitmap)
                        binding.imageView.source.setImageBitmap(addedStickerBitmap)

                        runOnUiThread {
                            val activityName = intent.getStringExtra("activityName")
                            val intent = Intent(this@EditStickerImageActivity, CropImageActivity::class.java)
                            intent.putExtra("activityName", activityName)
                            startActivity(intent)
                        }
                    } else {
                        Log.e("EditStickerImageActivity", "Bitmap is null or recycled before use.")
                    }
                } catch (e: Exception) {
                    Log.e("EditStickerImageActivity", "Error while saving bitmap: ${e.message}", e)
                }
            }
        }

        viewModel.editedImage.observe(this){
            if(it!=null)
            {
                if(it.byteCount >= 128410000)
                {
                    val resized = Bitmap.createScaledBitmap(it, (it.width*0.8).roundToInt(), (it.height*0.8).roundToInt(), true);
                    binding.imageView.source.setImageBitmap(resized)
                }
                else{
                    binding.imageView.source.setImageBitmap(it)
                }
            }
        }
        binding.btnText.setOnClickListener {
            binding.zoomButton. imageTintList=getColorStateList(R.color.black)
            binding.txt1.setTextColor(getColor(R.color.black))
            binding.img2.imageTintList=getColorStateList(R.color.black)
            binding.txt2.setTextColor(getColor(R.color.black))
            binding.img3.imageTintList=getColorStateList(R.color.bg_color)
            binding.txt3.setTextColor(getColor(R.color.bg_color))
            binding.img4.imageTintList=getColorStateList(R.color.black)
            binding.txt4.setTextColor(getColor(R.color.black))
            binding.img5.imageTintList=getColorStateList(R.color.black)
            binding.txt5.setTextColor(getColor(R.color.black))

            val textEditorDialogFragment = AddTextDialogFragment.show(this)
            textEditorDialogFragment.setOnTextEditorListener(object :
                AddTextDialogFragment.TextEditorListener {
                override fun onDone(inputText: String, colorCode: Int, font: Typeface) {
                    val styleBuilder = TextStyleBuilder()
                    styleBuilder.withTextColor(colorCode)
                    styleBuilder.withTextFont(font)
                    styleBuilder.withTextSize(80.0F)
                    mPhotoEditor.addText(inputText, styleBuilder)
                }
            })
        }
        binding.btnSticker.setOnClickListener {
            binding.zoomButton. imageTintList=getColorStateList(R.color.black)
            binding.txt1.setTextColor(getColor(R.color.black))
            binding.img2.imageTintList=getColorStateList(R.color.black)
            binding.txt2.setTextColor(getColor(R.color.black))
            binding.img3.imageTintList=getColorStateList(R.color.black)
            binding.txt3.setTextColor(getColor(R.color.black))
            binding.img4.imageTintList=getColorStateList(R.color.bg_color)
            binding.txt4.setTextColor(getColor(R.color.bg_color))
            binding.img5.imageTintList=getColorStateList(R.color.black)
            binding.txt5.setTextColor(getColor(R.color.black))
            mStickerBottomDialogFragment.show(supportFragmentManager, mStickerBottomDialogFragment.tag)
        }
        binding.btnCutout.setOnClickListener {
            binding.zoomButton. imageTintList=getColorStateList(R.color.bg_color)
            binding.txt1.setTextColor(getColor(R.color.bg_color))
            binding.img2.imageTintList=getColorStateList(R.color.black)
            binding.txt2.setTextColor(getColor(R.color.black))
            binding.img3.imageTintList=getColorStateList(R.color.black)
            binding.txt3.setTextColor(getColor(R.color.black))
            binding.img4.imageTintList=getColorStateList(R.color.black)
            binding.txt4.setTextColor(getColor(R.color.black))
            binding.img5.imageTintList=getColorStateList(R.color.black)
            binding.txt5.setTextColor(getColor(R.color.black))

            lifecycleScope.launch {
                try {
                    val saveSettings = SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build()
                    val addedStickerBitmap = mPhotoEditor.saveAsBitmap(saveSettings)

                    // Post the bitmap to the ViewModel
                    viewModel.editedImage.postValue(addedStickerBitmap)
                    // Set the bitmap to the ImageView
                    binding.imageView.source.setImageBitmap(addedStickerBitmap)

                    runOnUiThread {
                        val activityName = intent.getStringExtra("activityName")
                        val intent = Intent(this@EditStickerImageActivity, CutOutActivity::class.java)
                        intent.putExtra("activityName", activityName)
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        binding.btnBorder.setOnClickListener {
            binding.zoomButton. imageTintList=resources.getColorStateList(R.color.black)
            binding.txt1.setTextColor(getColor(R.color.black))
            binding.img2.imageTintList=getColorStateList(R.color.black)
            binding.txt2.setTextColor(getColor(R.color.black))
            binding.img3.imageTintList=getColorStateList(R.color.black)
            binding.txt3.setTextColor(getColor(R.color.black))
            binding.img4.imageTintList=getColorStateList(R.color.black)
            binding.txt4.setTextColor(getColor(R.color.black))
            binding.img5.imageTintList= getColorStateList(R.color.bg_color)
            binding.txt5.setTextColor(getColor(R.color.bg_color))

            Toast.makeText(this@EditStickerImageActivity,"Coming Soon!",Toast.LENGTH_SHORT).show()
        }
        binding.createBtn.setOnClickListener {
           loadCreateStickerInterstitial()
        }
        binding.backBtn.setOnClickListener {
            if (!isBackPressed){
                isBackPressed=true
                loadBackButtonInterstitial()
            }
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isBackPressed){
                    isBackPressed=true
               loadBackButtonInterstitial()
            }
            }
        })
    }

    private fun backButtonTask(){
        viewModel.editedImage.postValue(null)
        viewModel.originalImage.postValue(null)
        val activityName = intent.getStringExtra("activityName")
        Log.d("activityName",activityName.toString())
        val intent = Intent(this@EditStickerImageActivity,CreateStickerActivity::class.java)
        intent.putExtra("activityName",activityName)
        startActivity(intent)
        finish()
    }


    private fun loadCreateStickerInterstitial(){
        viewModel.editedImage.postValue(null)
        viewModel.originalImage.postValue(null)
        if(viewModel.currentIdentifier.isEmpty()) {
            viewModel.currentIdentifier = "StickerPack${generateRandomNumber()}"
        }

        kotlin.runCatching {
            val saveSettings = SaveSettings.Builder().setClearViewsEnabled(true).setTransparencyEnabled(true).build()
            lifecycleScope.launch {
                try {
                val bitmap = mPhotoEditor.saveAsBitmap(saveSettings)
                val stickerPath = FileUtils.saveSticker(this@EditStickerImageActivity,bitmap,viewModel.currentIdentifier)
                val tryPath = FileUtils.saveTryImage(this@EditStickerImageActivity,bitmap,viewModel.currentIdentifier)
                val currentSticker = viewModel.stickerList.value?.filter { it.identifier == viewModel.currentIdentifier}
                if(currentSticker?.isEmpty() == true) {
                    viewModel.addStickerPack(viewModel.currentIdentifier, stickerPath, tryPath)
                }else{
                    viewModel.addStickerToPack(viewModel.currentIdentifier,StickerEntity(emptyList(),stickerPath,""))
                }
                runOnUiThread {
                    val activityName = intent.getStringExtra("activityName")
                    Log.d("activityName",activityName.toString())
                    InterstitialAdManager.showInterstitial(this@EditStickerImageActivity, CreateStickerActivity(),"activityName","",
                        activityName!!,"", "finishes",
                        AdsManager.InterstitialEditStickerImageToCreateStickerActivity
                    )
                }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }.onSuccess {
            Toast.makeText(this,"Sticker Created Successfully!", Toast.LENGTH_SHORT).show()

        }.onFailure {
            Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun loadBackButtonInterstitial(){
        InterstitialAdManager.showInterstitial(this@EditStickerImageActivity, CreateStickerActivity(),"","",
            ""!!,"", "finishes",
            AdsManager.BackInterstitialEditStickerActivity
        )

    }


    private fun loadTopBanner() {
        BannerAdManager.loadBannerAd(this, binding.bannerLayout,binding.bannerArea,binding.nativeContainer,
            AdsManager.BannerTopEditStickerImageActivity
        )
    }

    private fun loadBottomBanner() {
        BannerAdManager.loadBannerAd(this, binding.bannerLayout2,binding.bannerArea2,binding.nativeContainer2,
            AdsManager.BannerBottomEditStickerImageActivity
        )
    }

    override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

    }

    override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
        val textEditorDialogFragment =
            AddTextDialogFragment.show(this, text.toString(), colorCode)
        textEditorDialogFragment.setOnTextEditorListener(object :
            AddTextDialogFragment.TextEditorListener {
            override fun onDone(inputText: String, colorCode: Int, font:Typeface) {
                val styleBuilder = TextStyleBuilder()
                styleBuilder.withTextFont(font)
                styleBuilder.withTextColor(colorCode)
                if (rootView != null) {
                    mPhotoEditor.editText(rootView, inputText, styleBuilder)

                }
            }
        })

    }

    override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

    }

    override fun onStartViewChangeListener(viewType: ViewType?) {

    }

    override fun onStopViewChangeListener(viewType: ViewType?) {

    }

    override fun onTouchSourceImage(event: MotionEvent?) {

    }
    override fun onStickerClick(bitmap: Bitmap) {
        val saveSettings = SaveSettings.Builder()
            .setClearViewsEnabled(true)
            .setTransparencyEnabled(true)
            .build()
        mPhotoEditor.addImage(getResizedBitmap(bitmap,500,500))
//        lifecycleScope.launch {
//            val addedStickerBitmap = mPhotoEditor.saveAsBitmap(saveSettings)
//            viewModel.originalImage.postValue(addedStickerBitmap)
//          //  binding.imageView.source.setImageBitmap(addedStickerBitmap)
//        }

    }
    private fun generateRandomNumber(): Int {
        val random = Random(System.currentTimeMillis())
        return random.nextInt(100, 1000)
    }
    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        return resizedBitmap
    }
}