package com.nova.pack.stickers.activites

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager

//import coil.ImageLoader
//import coil.request.ImageRequest
import com.android.billingclient.BuildConfig.APPLICATION_ID
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.jeluchu.jchucomponents.ktx.strings.getLastBitFromUrl
import com.nova.pack.stickers.R
import com.nova.pack.stickers.adapter.DetailsStickerAdapter
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivityStickerDetailBinding
import com.nova.pack.stickers.fragments.HomeFragment
import com.nova.pack.stickers.model.StickerPackView
import com.nova.pack.stickers.utils.Config
import com.nova.pack.stickers.utils.SharedPreferenceHelper
import com.nova.pack.stickers.viewmodel.StickersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class StickerDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStickerDetailBinding
    private var stickerPackView: StickerPackView? = null
    private lateinit var viewModel: StickersViewModel
    private var mDetailsStickerAdapter: DetailsStickerAdapter?= null
    private var isBackPressed=false
    private val preferences by lazy { SharedPreferenceHelper() }
    private var isBackPress=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStickerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        try {
            viewModel = ViewModelProvider(this)[StickersViewModel::class.java]
            mDetailsStickerAdapter = DetailsStickerAdapter(this)
            // Ensure intent has the extra before casting
            val stickerPackExtra = intent.getSerializableExtra(HomeFragment.EXTRA_STICKERPACK)
            if (stickerPackExtra != null && stickerPackExtra is StickerPackView) {
                Log.e("StickerDetailActivitypoas", stickerPackExtra.toString())
                stickerPackView = stickerPackExtra
            } else {
                // Handle the case where the extra is missing or invalid
                Log.e("StickerDetailActivitypoas", "StickerPackView extra is missing or invalid")
                // You can either finish the activity or provide a default value
                finish() // Optionally finish the activity
                return // Exit the onCreate method
            }

            val list = ArrayList<StickerPackView>()
            list.clear()
            list.add(stickerPackView!!)
            preferences.saveObjectsList("sticker_packs", list as List<StickerPackView>)
            try {
                binding.identifierTextview.text = stickerPackView!!.name
            } catch (e: Exception) {
                Log.d("Error", e.toString())
            }
            kotlin.runCatching {
                CoroutineScope(Dispatchers.IO).launch {
                    getStickerPack()
                }
            }.onSuccess {

            }.onFailure {
//            binding.progressBar.visibility = View.GONE
//            binding.detailStickersRecyclerview.visibility = View.VISIBLE
            }

            setUpRecyclerView()
            binding.whatsappBtn.setOnClickListener {
                binding.whatsappBtn.visibility = View.INVISIBLE
                preferences.saveObjectsList("sticker_packs", list as List<StickerPackView>)
                Log.d("StickerPackInfo", list.toString())
                        val stickerPackDir = File(HomeFragment.path, stickerPackView?.identifier ?: "")
                        val trayDir = File(stickerPackDir, "try")
                        val trayImageFileName = stickerPackView?.trayImageFile
                            ?.getLastBitFromUrl()
                            ?.replace(".png", "")
                            ?.replace(" ", "_") + ".png"

                        val trayImageFile = File(trayDir, trayImageFileName ?: "")
                        // Check if tray image and sticker files exist
                        if (trayImageFile.exists()) {
                            Log.d("askjihad","1")
                            // Intent to enable the sticker pack
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent().apply {
                                    action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
                                    setPackage("com.whatsapp")
                                    putExtra(
                                        HomeFragment.EXTRA_STICKER_PACK_ID,
                                        stickerPackView?.identifier.toString()
                                    )
                                    putExtra(
                                        HomeFragment.EXTRA_STICKER_PACK_AUTHORITY,
                                        Config.CONTENT_PROVIDER_AUTHORITY
                                    )
                                    putExtra(
                                        HomeFragment.EXTRA_STICKER_PACK_NAME,
                                        stickerPackView?.name
                                    )
                                    Log.d(
                                        "StickerPackIntent",
                                        "ID: ${stickerPackView?.identifier}, " +
                                                "Authority: ${Config.CONTENT_PROVIDER_AUTHORITY}, " +
                                                "Name: ${stickerPackView?.name}"
                                    )
                                }

                                try {
                                    if (isBackPressed){
                                        return@postDelayed
                                    }
                                    startActivityForResult(intent, 200)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(
                                        this@StickerDetailActivity,
                                        getString(R.string.whatsapp_not_install),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }finally {
                                    binding.whatsappBtn.visibility = View.VISIBLE
                                }
                            },4000)
                        } else {
                            Log.d("askjihad","2")
                            val trayImageUrl = stickerPackView?.trayImageFile
                            if (!trayImageFile.exists()) {
                                Log.d("askjihad","3")
                                Glide.with(this@StickerDetailActivity)
                                    .load(trayImageUrl) // URL of the tray image
                                    .placeholder(R.drawable.ic_loading) // Optional placeholder
                                    .into(object : CustomTarget<Drawable>() {
                                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                            // Save the image after loading it
                                                Log.d("askjihad","4")
                                                trayDir.mkdirs()
                                                try {
                                                    val out = FileOutputStream(trayImageFile)
                                                    (resource as BitmapDrawable).bitmap.compress(
                                                        Bitmap.CompressFormat.PNG, 10, out
                                                    )
                                                    out.close()
                                                    for (sticker in stickerPackView!!.stickers) {
                                                        val regex = Regex("""/(\d+)\.webp""")
                                                        val matchResult = regex.find(sticker.imageFile)
                                                        val digitBeforeWebp = matchResult?.groupValues?.get(1)
                                                        val imageFile = sticker.imageFile
                                                        stickerPackDir.mkdirs()
                                                        val file = File(stickerPackDir, "${digitBeforeWebp}.webp")
                                                        if (file.exists()) file.delete()
                                                        val outputFile = File(stickerPackDir, "${digitBeforeWebp}.webp")
                                                        // Check if the file exists before trying to save
                                                        val parentFile = outputFile.parentFile
                                                        if (parentFile != null && !parentFile.exists()) {
                                                            parentFile.mkdirs()  // Create the parent directories if they don't exist
                                                        }
                                                        imageFile.saveImage(outputFile)
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e("GlideError", "Error saving tray image: ${e.message}")
                                                }
                                        }

                                        override fun onLoadCleared(placeholder: Drawable?) {
                                            // Handle any cleanup if needed
                                        }
                                    })
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent().apply {
                                    action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
                                    setPackage("com.whatsapp")
                                    putExtra(
                                        HomeFragment.EXTRA_STICKER_PACK_ID,
                                        stickerPackView?.identifier.toString()
                                    )
                                    putExtra(
                                        HomeFragment.EXTRA_STICKER_PACK_AUTHORITY,
                                        Config.CONTENT_PROVIDER_AUTHORITY
                                    )
                                    putExtra(
                                        HomeFragment.EXTRA_STICKER_PACK_NAME,
                                        stickerPackView?.name
                                    )
                                    Log.d(
                                        "StickerPackIntent",
                                        "ID: ${stickerPackView?.identifier}, " +
                                                "Authority: ${Config.CONTENT_PROVIDER_AUTHORITY}, " +
                                                "Name: ${stickerPackView?.name}"
                                    )
                                }

                                try {
                                    if (isBackPressed){
                                        return@postDelayed
                                    }
                                    startActivityForResult(intent, 200)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(
                                        this@StickerDetailActivity,
                                        getString(R.string.whatsapp_not_install),
                                        Toast.LENGTH_LONG
                                    ).show()
                                } finally {
                                    binding.whatsappBtn.visibility = View.VISIBLE
                                }
                            },5000)
                        }

                // Start checking files after 1 second
            }
            binding.whatsappBusinessBtn.setOnClickListener {
                binding.whatsappBusinessBtn.visibility = View.INVISIBLE
                preferences.saveObjectsList("sticker_packs", list as List<StickerPackView>)
                Log.d("StickerPackInfo", list.toString())
                        val stickerPackDir = File(HomeFragment.path, stickerPackView?.identifier ?: "")
                        val trayDir = File(stickerPackDir, "try")
                        val trayImageFileName = stickerPackView?.trayImageFile
                            ?.getLastBitFromUrl()
                            ?.replace(".png", "")
                            ?.replace(" ", "_") + ".png"
                        val trayImageFile = File(trayDir, trayImageFileName ?: "")
                        // Check if tray image and sticker files exist
                        if (trayImageFile.exists()) {

                            // Intent to enable the sticker pack
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent().apply {
                                    action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
                                    setPackage("com.whatsapp.w4b")
                                    putExtra(
                                        HomeFragment.EXTRA_STICKER_PACK_ID,
                                        stickerPackView?.identifier.toString()
                                    )
                                    putExtra(
                                        HomeFragment.EXTRA_STICKER_PACK_AUTHORITY,
                                        Config.CONTENT_PROVIDER_AUTHORITY
                                    )
                                    putExtra(
                                        HomeFragment.EXTRA_STICKER_PACK_NAME,
                                        stickerPackView?.name
                                    )
                                    Log.d(
                                        "StickerPackIntent",
                                        "ID: ${stickerPackView?.identifier}, " +
                                                "Authority: ${Config.CONTENT_PROVIDER_AUTHORITY}, " +
                                                "Name: ${stickerPackView?.name}"
                                    )
                                }

                                try {
                                    if (isBackPressed){
                                        return@postDelayed
                                    }
                                    startActivityForResult(intent, 200)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(
                                        this@StickerDetailActivity,
                                        getString(R.string.whatsapp_not),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }finally {
                                    binding.whatsappBusinessBtn.visibility = View.VISIBLE
                                }
                            },4000)
                        } else {
                            val trayImageUrl = stickerPackView?.trayImageFile
                            if (!trayImageFile.exists()) {
                                Glide.with(this@StickerDetailActivity)
                                    .load(trayImageUrl) // URL of the tray image
                                    .placeholder(R.drawable.ic_loading) // Optional placeholder
                                    .into(object : CustomTarget<Drawable>() {
                                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                            // Save the image after loading it
                                                trayDir.mkdirs()
                                                try {
                                                    val out = FileOutputStream(trayImageFile)
                                                    (resource as BitmapDrawable).bitmap.compress(
                                                        Bitmap.CompressFormat.PNG, 40, out
                                                    )
                                                    out.close()
                                                    for (sticker in stickerPackView!!.stickers) {
                                                        val regex = Regex("""/(\d+)\.webp""")
                                                        val matchResult = regex.find(sticker.imageFile)
                                                        val digitBeforeWebp = matchResult?.groupValues?.get(1)
                                                        val imageFile = sticker.imageFile
                                                        stickerPackDir.mkdirs()
                                                        val file = File(stickerPackDir, "${digitBeforeWebp}.webp")
                                                        if (file.exists()) file.delete()
                                                        val outputFile = File(stickerPackDir, "${digitBeforeWebp}.webp")
                                                        // Check if the file exists before trying to save
                                                        val parentFile = outputFile.parentFile
                                                        if (parentFile != null && !parentFile.exists()) {
                                                            parentFile.mkdirs()  // Create the parent directories if they don't exist
                                                        }
                                                        imageFile.saveImage(outputFile)
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e("GlideError", "Error saving tray image: ${e.message}")
                                                }
                                        }

                                        override fun onLoadCleared(placeholder: Drawable?) {
                                            // Handle any cleanup if needed
                                        }
                                    })
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent().apply {
                                    action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
                                    setPackage("com.whatsapp.w4b")
                                    putExtra(
                                        HomeFragment.EXTRA_STICKER_PACK_ID,
                                        stickerPackView?.identifier.toString()
                                    )
                                    putExtra(
                                        HomeFragment.EXTRA_STICKER_PACK_AUTHORITY,
                                        Config.CONTENT_PROVIDER_AUTHORITY
                                    )
                                    putExtra(
                                        HomeFragment.EXTRA_STICKER_PACK_NAME,
                                        stickerPackView?.name
                                    )
                                    Log.d(
                                        "StickerPackIntent",
                                        "ID: ${stickerPackView?.identifier}, " +
                                                "Authority: ${Config.CONTENT_PROVIDER_AUTHORITY}, " +
                                                "Name: ${stickerPackView?.name}"
                                    )
                                }

                                try {
                                    if (isBackPressed){
                                        return@postDelayed
                                    }
                                    startActivityForResult(intent, 200)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(
                                        this@StickerDetailActivity,
                                        getString(R.string.whatsapp_not_install),
                                        Toast.LENGTH_LONG
                                    ).show()
                                } finally {
                                    binding.whatsappBusinessBtn.visibility = View.VISIBLE
                                }
                            },5000)
                        }
            }


            // Start checking files after 1 second


                binding.shareWithFriendsBtn.setOnClickListener {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                    var shareMessage = "\nLet me recommend you this application\n\n"
                    shareMessage =
                        """
                    ${shareMessage + "https://play.google.com/store/apps/details?id=" + APPLICATION_ID}
                 
                    """.trimIndent()
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    startActivity(Intent.createChooser(shareIntent, "choose one"))
                } catch (e: Exception) {
                    Log.d("Error", e.toString())
                }
            }

            binding.backBtn.setOnClickListener {
                if (!isBackPressed) {
                    isBackPressed = true
                    loadInterstitialAd()
                }
            }

            onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!isBackPressed) {
                        isBackPressed = true
                        loadInterstitialAd()
                    }
                }
            })
            loadTopBanner()
            loadBottomBanner()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun loadTopBanner() {
        BannerAdManager.loadBannerAd(this, binding.bannerLayout,binding.bannerArea,binding.nativeContainer,
            AdsManager.BannerTopStickerDetailActivity
        )

    }

    private fun loadBottomBanner() {
        BannerAdManager.loadBannerAd(this, binding.bannerLayout2,binding.bannerArea2,binding.nativeContainer2, AdsManager.BannerBottomStickerDetailActivity)
    }

    private fun loadInterstitialAd() {
        if (intent.extras?.containsKey("isSearch") == true){
            InterstitialAdManager.showInterstitial(this@StickerDetailActivity, SearchStickersActivity(),"","","","", "finishes", AdsManager.BackInterstitialStickerDetailActivity)
        }else{
        InterstitialAdManager.showInterstitial(this@StickerDetailActivity, MainActivity(),"","","","", "finishes", AdsManager.BackInterstitialStickerDetailActivity)
    }
    }

    private fun setUpRecyclerView(){
        val gridLayoutManager = GridLayoutManager(this@StickerDetailActivity, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (mDetailsStickerAdapter?.getItemViewType(position) == DetailsStickerAdapter.VIEW_TYPE_AD) {
                    gridLayoutManager.spanCount // Make the ad span across all columns
                } else {
                    1 // Regular items take one span
                }
            }
        }
        binding.detailStickersRecyclerview.layoutManager = gridLayoutManager
        binding.detailStickersRecyclerview.adapter = mDetailsStickerAdapter
        mDetailsStickerAdapter!!.setList(stickerPackView!!.stickers)

    }

    private suspend fun getStickerPack() {
        try {
            // Ensure activity is not destroyed before proceeding
            if (isFinishing || isDestroyed) {
                return // Skip the Glide load if the activity is not in a valid state
            }

            HomeFragment.path = "${filesDir}/stickers_asset/"
            val myDir = File(HomeFragment.path, stickerPackView!!.identifier)

            if (!myDir.exists() && !myDir.isDirectory) {
                val trayImageFile = stickerPackView!!.trayImageFile.getLastBitFromUrl()

                Log.d("URL Debug", "Attempting to load tray image: ${stickerPackView?.trayImageFile}")

                Glide.with(this@StickerDetailActivity)
                    .load(stickerPackView?.trayImageFile)
                    .listener(object : RequestListener<Drawable> {

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            val trayDir = File("${HomeFragment.path}/${stickerPackView!!.identifier}/try")
                            trayDir.mkdirs()
                            val imageName = trayImageFile.replace(".png", "").replace(" ", "_") + ".png"
                            val file = File(trayDir, imageName)
                            if (file.exists()) file.delete()
                            try {
                                val out = FileOutputStream(file)
                                (resource as BitmapDrawable).bitmap.compress(Bitmap.CompressFormat.PNG, 40, out)
                                out.close()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            return true
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .submit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
    fun String.saveImage(destinationFile: File) {
        Log.d("askjihad","5")
        runCatching {
            Thread {
                try {
                    val url = URL(this)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 10_000
                    connection.readTimeout = 10_000
                    connection.connect()

                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = connection.inputStream
                        val os = FileOutputStream(destinationFile)
                        val buffer = ByteArray(2048)
                        var length: Int
                        while (inputStream.read(buffer).also { length = it } != -1) {
                            os.write(buffer, 0, length)
                        }
                        inputStream.close()
                        os.close()
                    } else {
                        println("HTTP error code: ${connection.responseCode}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }.getOrElse {
            it.printStackTrace()
        }
    }

}