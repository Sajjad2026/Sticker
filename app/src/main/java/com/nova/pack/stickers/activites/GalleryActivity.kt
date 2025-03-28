package com.nova.pack.stickers.activites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivityGalleryBinding
import com.nova.pack.stickers.fragments.GalleryFolderViewFragment
import com.nova.pack.stickers.utils.Config.Companion.isAnim
import com.nova.pack.stickers.viewmodel.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {
    lateinit var binding: ActivityGalleryBinding
    private lateinit var viewModel: GalleryViewModel
    private var cameraActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var photoURI: Uri? = null
    var cameraImagePath = ""
    var isBackPressed=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this)[GalleryViewModel(this)::class.java]
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.gallery_main_frameholder, GalleryFolderViewFragment())
            .commit()

        viewModel.uiStateDropDown.observe(this){
            binding.folderTxt.text = it.title
            if(it.isOpened){
                binding.folderTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this,R.drawable.ic_down_arrow), null)
            }
            else
            {
                binding.folderTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this,R.drawable.ic_arroaw_up), null)
            }

        }
        loadBottomBanner()
        binding.lytFolderSelect.setOnClickListener{
            if(viewModel.uiStateDropDown.value?.isOpened == true){
                val fragmentManager = this.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val bundle = Bundle()
                val activityName = intent.getStringExtra("activityName").toString()
                Log.d("activityName3",activityName)
                bundle.putString("activityName", activityName)
                val fragment = GalleryFolderViewFragment()
                fragment.arguments = bundle
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                fragmentTransaction.replace(R.id.gallery_main_frameholder, fragment)
                fragmentTransaction.commit()
            }
        }
        binding.takePhotoBtn.setOnClickListener {
            takeImageFromCamera()
        }
        cameraActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = Intent(this, EditStickerImageActivity::class.java)
                intent.putExtra("imagePath", cameraImagePath)
                startActivity(intent)
                finish()
            }
        }
        binding.backBtn.setOnClickListener {
            if (!isBackPressed){
                isBackPressed=true
                val activityName = intent.getStringExtra("activityName").toString()
                InterstitialAdManager.showInterstitial(this@GalleryActivity
                    , CreateStickerActivity(),"activityName","",activityName,"", "finishes", AdsManager.BackInterstitialGalleryActivity)
            }
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isBackPressed){
                    isBackPressed=true
                val activityName = intent.getStringExtra("activityName").toString()
                InterstitialAdManager.showInterstitial(this@GalleryActivity
                    , CreateStickerActivity(),"activityName","",activityName,"", "finishes", AdsManager.BackInterstitialGalleryActivity)
            }}
        })
        //
        val fragmentManager = this.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val bundle = Bundle()
        val activityName = intent.getStringExtra("activityName").toString()
        val activityAnim = intent.getStringExtra("activityAnim").toString()
        if (intent.extras?.containsKey("activityAnim") == true){
            Log.d("shjgsff","anim")
            bundle.putString("activityName", activityAnim)
            isAnim=true
        }else{
            Log.d("shjgsff","images")
            bundle.putString("activityName", activityName)
        }
        val fragment = GalleryFolderViewFragment()
        fragment.arguments = bundle
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.gallery_main_frameholder, fragment)
        fragmentTransaction.commit()
    }
    private fun loadBottomBanner() {
        BannerAdManager.loadBannerAd(this, binding.bannerLayout,binding.bannerArea,binding.nativeContainer,
            AdsManager.BannerTopGalleryActivity
        )

        BannerAdManager.loadBannerAd(this, binding.bannerLayout2,binding.bannerArea2,binding.nativeContainer,
            AdsManager.BannerBottomGalleryActivity
        )
    }

    private fun takeImageFromCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takesPictureIntent ->
            if (takesPictureIntent.resolveActivity(packageManager) != null) {
                // Create the File where the photo should go
                try {
                    val photoFile: File? = createImageFile()
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(
                            this,
                            "com.nova.pack.stickers.provider",
                            photoFile
                        )
                        takesPictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        cameraActivityResultLauncher?.launch(takesPictureIntent)
                    }
                } catch (e: java.lang.Exception) {
                    Log.d("ejamnd",e.message.toString())
                    Toast.makeText(this, "Error$e", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val df = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_mm", Locale.getDefault())
        val formattedDate = df.format(Date())
        val imageFileName = "Image_$formattedDate"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Create the directory if it doesn't exist
        if (!storageDir?.exists()!!) {
            storageDir.mkdirs()
        }

        // Create the image file
        val image = File(storageDir, "$imageFileName.jpg")

        try {
            // Create the image file
            if (!image.exists()) {
                image.createNewFile()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        cameraImagePath = image.absolutePath
        // Save a file: path for use with ACTION_VIEW intents
        return image
    }

}