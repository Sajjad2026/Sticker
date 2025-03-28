package com.nova.pack.stickers.adapter
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
//import coil.ImageLoader
//import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jeluchu.jchucomponents.ktx.strings.getLastBitFromUrl
import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager.HomeNativeAdCounter
import com.nova.pack.stickers.ads.AdsManager.NativeHomeAndCategoryRecyclerView
import com.nova.pack.stickers.ads.Constants.Companion.exportDialog
import com.nova.pack.stickers.ads.InAppClass
import com.nova.pack.stickers.ads.NativeAdManager
import com.nova.pack.stickers.databinding.HomeStickerItemBinding
import com.nova.pack.stickers.databinding.NativeAdBinding
import com.nova.pack.stickers.fragments.HomeFragment
import com.nova.pack.stickers.listener.onStickerPackClickListener
import com.nova.pack.stickers.model.StickerPackView
import com.nova.pack.stickers.utils.Config
import com.nova.pack.stickers.utils.SharedPreferenceHelper
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.random.Random

class HomeStickerAdapter(
    private val context: Activity,
    private val onStickerClickListener: onStickerPackClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var stickerList = ArrayList<StickerPackView>()
    private val preferences by lazy { SharedPreferenceHelper() }
    private val AD_TYPE = 1
    private val NORMAL_TYPE = 2
    private var isExporting=false
    private var listener: onStickerPackClickListener = onStickerClickListener
    var isCategoryFragment=false
    fun setFragment(isCategoryFragment: Boolean) {
        this.isCategoryFragment = isCategoryFragment
    }
    fun setList(stickerList: List<StickerPackView>) {
       // this.stickerList.clear()1
        Log.d("skjsff",stickerList.size.toString())
        stickerList.forEach { i->
            Log.d("Sfdhksf",i.name)
        }
        this.stickerList.clear()
        this.stickerList.addAll(stickerList)
//        Log.d("sfdjksf",stickerList[2].name)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            AD_TYPE -> {
                val binding = NativeAdBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                NativeAdViewHolder(binding)
            }
            else -> {
                val binding = HomeStickerItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                StickerViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        // Calculate item count based on ad insertion
        return if (HomeNativeAdCounter > 0 && context.localClassName!="activites.SearchStickersActivity" &&
            !SharedPreferenceHelper.getSession("mySession", false) && !InAppClass.isPurchase&&NativeHomeAndCategoryRecyclerView!=0) {
            val additionalItems = (stickerList.size / HomeNativeAdCounter)
            stickerList.size + additionalItems
        } else {
            stickerList.size // No native ads to insert
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (HomeNativeAdCounter > 0 && (position + 1) % (HomeNativeAdCounter + 1) == 0 && context.localClassName!="activites.SearchStickersActivity" &&
            !SharedPreferenceHelper.getSession("mySession", false) && !InAppClass.isPurchase   &&NativeHomeAndCategoryRecyclerView!=0) {
            AD_TYPE
        } else {
            NORMAL_TYPE
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("apoadda",position.toString())
        if (getItemViewType(position) == AD_TYPE) {
            if (context.localClassName!="activites.SearchStickersActivity" && !SharedPreferenceHelper.getSession("mySession", false) && !InAppClass.isPurchase
                &&NativeHomeAndCategoryRecyclerView!=0) {
                // Bind Native Ad
                if (position != itemCount - 1) {
                    (holder as NativeAdViewHolder).bind()
                }
            }
        } else {
            // Bind Sticker Pack
            if (context.localClassName!="activites.SearchStickersActivity") {
                val stickerPosition = calculateStickerPosition(position)
                if (stickerPosition >= 0 && stickerPosition < stickerList.size) {
                    (holder as StickerViewHolder).bind(stickerList[stickerPosition])
                } else {
                    Log.e("RecyclerViewAdapter", "Invalid sticker position: $stickerPosition")
                }
            } else {
                if (position >= 0 && position < stickerList.size) {
                    (holder as StickerViewHolder).bind(stickerList[position])
                } else {
                    Log.e("RecyclerViewAdapter", "Invalid sticker position: $position")
                }
            }
        }
    }


    private fun calculateStickerPosition(position: Int): Int {
        return if (HomeNativeAdCounter > 0 && context.localClassName!="activites.SearchStickersActivity" &&
            !SharedPreferenceHelper.getSession("mySession", false) && !InAppClass.isPurchase &&NativeHomeAndCategoryRecyclerView!=0){
            position - (position / (HomeNativeAdCounter + 1))
        } else {
            position
        }
    }


    inner class StickerViewHolder(val binding: HomeStickerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stickerPackView: StickerPackView) {
            binding.apply {
                if (!isCategoryFragment) {
                    Log.d("Sdkjsfdf", stickerPackView.name+" "+stickerList.size)
                    val colors = listOf(
                        context.resources.getColor(R.color.pack_1),
                        context.resources.getColor(R.color.pack_2),
                        context.resources.getColor(R.color.pack_3),
                        context.resources.getColor(R.color.pack_4)
                    )
                    cardBg.setCardBackgroundColor(colors[position % colors.size])
                }

                tvPackStickerName.text = stickerPackView.name
// Assuming stickerPackView is an instance of StickerPackView
                if (stickerPackView.stickers.size > 4) {
                    // Shuffle the stickers
                    //     Collections.shuffle(stickerPackView.stickers)
                    // Load the shuffled stickers into the ImageViews
                    Glide.with(itemView.context)
                        .load(stickerPackView.stickers[0].imageFile)
                        .placeholder(R.drawable.ic_loading)
                        .dontAnimate()
                        .into(ivFirstSticker)

                    Glide.with(itemView.context)
                        .load(stickerPackView.stickers[1].imageFile)
                        .placeholder(R.drawable.ic_loading)
                        .dontAnimate()
                        .into(ivSecondSticker)

                    Glide.with(itemView.context)
                        .load(stickerPackView.stickers[2].imageFile)
                        .placeholder(R.drawable.ic_loading)
                        .dontAnimate()
                        .into(ivThirdSticker)

                    Glide.with(itemView.context)
                        .load(stickerPackView.stickers[3].imageFile)
                        .placeholder(R.drawable.ic_loading)
                        .dontAnimate()
                        .into(ivFourSticker)

                    // Set up the click listeners
                    seeAllBtn.setOnClickListener {
                        listener.OnStickerClickListener(stickerPackView)
                    }

                    cardBg.setOnClickListener {
                        listener.OnStickerClickListener(stickerPackView)
                    }
                }


                val savedValue = SharedPreferenceHelper.getSavedRandomValue(context)

                if (savedValue == null) {
                    // First time launch, generate and save a new random value
                    val random = Random.Default
                    val randomDouble = 1.0 + random.nextDouble() * (99.99 - 1.0)
                    val formattedDouble = String.format(Locale.US, "%.2f", randomDouble)

                    // Save the new value
                    SharedPreferenceHelper.saveRandomValue(context, formattedDouble)

                    // Update TextViews
                    val formattedText = buildString {
                        append(formattedDouble)
                        append("K")
                    }
                    downloadCountText.text = formattedText
                    favouriteCountText.text = formattedText
                } else {
                    // Not the first launch, use the saved value
                    val formattedText = buildString {
                        append(savedValue)
                        append("K")
                    }
                    downloadCountText.text = formattedText
                    favouriteCountText.text = formattedText
                }


                val progressbar = progressbar
                exportBtn.setOnClickListener {
                    try {
                        exportDialog(context){
                            if (it.equals("whatsapp")){
                                launchDialog("com.whatsapp",stickerPackView,progressbar,exportBtn)
                            }else{
                                launchDialog("com.whatsapp.w4b",stickerPackView,progressbar,exportBtn)
                            }
                        }

                    } catch (e: FileNotFoundException) {
                        Log.e("StickerPack", "File not found: ${e.message}")
                    } catch (e: IOException) {
                        Log.e("StickerPack", "IO Error: ${e.message}")
                    } catch (e: Exception) {
                        Log.e("StickerPack", "General Error: ${e.message}")
                    }
                }
            }
        }
            }

    fun launchDialog(key:String, stickerPackView: StickerPackView, progressbar: View, exportBtn: View){
        Log.d("alksdac","call")
        if (!isExporting){
            isExporting=true
        progressbar.visibility = View.VISIBLE
        exportBtn.visibility = View.INVISIBLE
        val list = ArrayList<StickerPackView>()
        list.clear()
        val newStickerPack = stickerPackView.copy(category_name = null, icon = null)
        list.add(newStickerPack)
        preferences.saveObjectsList("sticker_packs", list as List<StickerPackView>)
            HomeFragment.path = "${context.filesDir}/stickers_asset/"
            //val stickerPackDir = File(HomeFragment.path, stickerPackView.identifier)
            val stickerPackDir = File(HomeFragment.path, stickerPackView?.identifier ?: "")
            val trayDir = File(stickerPackDir, "try")
            val trayImageFileName = stickerPackView?.trayImageFile
                ?.getLastBitFromUrl()
                ?.replace(".png", "")
                ?.replace(" ", "_") + ".png"

            val trayImageFile = File(trayDir, trayImageFileName ?: "")
            if (trayImageFile.exists()) {
                Log.d("asjuyadhyu","1")
                Handler(Looper.getMainLooper()).postDelayed({
                    Log.d(
                        "StickerPack",
                        "Sticker pack already exists: ${stickerPackView.identifier}"
                    )
                    val intent = Intent().apply {
                        setPackage(key)
                        action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
                        putExtra(HomeFragment.EXTRA_STICKER_PACK_ID, stickerPackView.identifier)
                        putExtra(
                            HomeFragment.EXTRA_STICKER_PACK_AUTHORITY,
                            Config.CONTENT_PROVIDER_AUTHORITY
                        )
                        putExtra(HomeFragment.EXTRA_STICKER_PACK_NAME, stickerPackView.name)
                    }

                    try {
                        (context as? Activity)?.startActivityForResult(intent, 200)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, context.getString(R.string.whatsapp_not_install), Toast.LENGTH_LONG).show()
                    } finally {
                        isExporting=false
                        progressbar.visibility = View.INVISIBLE
                        exportBtn.visibility = View.VISIBLE
                    }
                },4000)
            } else {
                val trayImageUrl = stickerPackView?.trayImageFile
                if (!trayImageFile.exists()) {
                                Glide.with(context)
                                    .load(trayImageUrl) // URL of the tray image
                                    .placeholder(R.drawable.ic_loading) // Optional placeholder
                                    .into(object : CustomTarget<Drawable>() {
                                        override fun onResourceReady(
                                            resource: Drawable,
                                            transition: Transition<in Drawable>?
                                        ) {
                                            // Save the image after loading it
                                                trayDir.mkdirs()
                                                try {
                                                    Log.d("asjuyadhyu", "2")
                                                    val out = FileOutputStream(trayImageFile)
                                                    (resource as BitmapDrawable).bitmap.compress(Bitmap.CompressFormat.PNG, 10, out)
                                                    out.close()
                                                    for (sticker in stickerPackView.stickers) {
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
                                                    Log.e(
                                                        "GlideError",
                                                        "Error saving tray image: ${e.message}"
                                                    )
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
                                        setPackage(key)
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
                                        (context as? Activity)?.startActivityForResult(intent, 200)
                                    } catch (e: ActivityNotFoundException) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.whatsapp_not_install),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } finally {
                                        isExporting = false
                                        progressbar.visibility = View.INVISIBLE
                                        exportBtn.visibility = View.VISIBLE
                                    }
                            }, 5000)
            }
            }else{
                Toast.makeText(context, context.getString(R.string.please_wait), Toast.LENGTH_SHORT).show()
            }
    }
    inner class NativeAdViewHolder(val binding: NativeAdBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            // Load native ad implementation here
            // Example:
            if (!InAppClass.isPurchase) {
                if (context.localClassName!="activites.SearchStickersActivity") {
                binding.nativeContainer.visibility = View.VISIBLE
                Log.d("sjhsff",context.localClassName)
                if (NativeHomeAndCategoryRecyclerView==1){
                NativeAdManager.nativeAdmob(context as AppCompatActivity, binding.nativeContainer, context.window, 1)
            }else if (NativeHomeAndCategoryRecyclerView==2){
                NativeAdManager.nativeWithoutMedia(context as AppCompatActivity, binding.nativeContainer, context.window, "home_sticker","top", binding.linear)
            }else if (NativeHomeAndCategoryRecyclerView==3){
                    NativeAdManager.nativeWithoutMedia(context as AppCompatActivity, binding.nativeContainer, context.window, "home_sticker","bottom",binding.linear)
            }else if (NativeHomeAndCategoryRecyclerView==4){
                binding.nativeContainer.minimumHeight=context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp)
                NativeAdManager.nativeWithoutMedia(context as AppCompatActivity, binding.nativeContainer, context.window, "home_sticker","small",binding.linear)
            }else if (NativeHomeAndCategoryRecyclerView==5){
                    NativeAdManager.nativeWithoutMedia(context as AppCompatActivity, binding.nativeContainer, context.window, "home_sticker","random",binding.linear)
            }else{
                Log.d("sfkljsf","adapter")
                binding.nativeContainer.visibility = View.GONE
            }
        } else{
                binding.nativeContainer.clearFocus()
                Log.d("sjhsff","not equal")
                //binding.nativeContainer.minimumHeight=context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)
        }
            }else{
                binding.nativeContainer.visibility = View.GONE
            }
        }
    }
    fun String.saveImage(destinationFile: File) {
        runCatching {
            Thread {
                try {
                    Log.d("Aslasda","saving")
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
