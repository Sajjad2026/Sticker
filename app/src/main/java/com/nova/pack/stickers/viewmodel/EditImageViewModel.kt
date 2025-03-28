package com.nova.pack.stickers.viewmodel


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jeluchu.jchucomponents.ktx.any.isNull
import com.nova.pack.stickers.fragments.HomeFragment
import com.nova.pack.stickers.model.DeleteStickerItem
import com.nova.pack.stickers.model.StickerEntity
import com.nova.pack.stickers.model.StickerPackEntity
import com.nova.pack.stickers.repository.LocalDatabaseRepository
import com.nova.pack.stickers.room.StickerDatabase
import com.nova.pack.stickers.utils.Config
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import okhttp3.OkHttp
//import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Inject


@HiltViewModel
class EditImageViewModel @Inject constructor(private val context: Context): ViewModel()  {
    val editedImage = MutableLiveData<Bitmap>()
    val originalImage = MutableLiveData<Bitmap>()
    private lateinit var db:LocalDatabaseRepository
    var stickerList = MutableLiveData<List<StickerPackEntity>>()
    var currentIdentifier = ""

    suspend fun addStickerPack(identifier:String,imageFilePath:String,trayImageFilePath:String){
        db = LocalDatabaseRepository(StickerDatabase(context))
        val emojiList = ArrayList<String>()
        emojiList.add(" ")
        emojiList.add(" ")
        val sticker = StickerEntity(imageFile = imageFilePath, emojis = emojiList, imageFileThum = "")
        val stickersList = ArrayList<StickerEntity>()
        stickersList.add(sticker)
        val pack = StickerPackEntity(category_name = "", icon = "", androidPlayStoreLink = "", iosAppStoreLink = "", publisherEmail = "test@gmail.com", privacyPolicyWebsite = "", licenseAgreementWebsite = " "
            , telegram_url = " ", identifier = identifier, trayImageFile = trayImageFilePath, animatedStickerPack = false, publisher = "nova", name = "You", id = 0, publisherWebsite = "", stickers = stickersList)
//        val pack = StickerPackEntity(androidPlayStoreLink = "", iosAppStoreLink = "", publisherEmail = "test@gmail.com", privacyPolicyWebsite = "", licenseAgreementWebsite = " "
//            , telegram_url = " ", identifier = identifier, trayImageFile = trayImageFilePath, animatedStickerPack = false, publisher = "nova", name = "You", id = 0, publisherWebsite = "", stickers = stickersList)
        kotlin.runCatching {
            CoroutineScope(Dispatchers.IO).launch {
                db.addStickersPack(pack)
            }
        }.onSuccess {

        }.onFailure {
            Toast.makeText(context,it.toString(),Toast.LENGTH_SHORT).show()
        }
    }
    fun getStickers()
    {
        db = LocalDatabaseRepository(StickerDatabase(context))
        stickerList.postValue(db.getStickers())
    }

    fun addCustomPackToWhatsappByIdentifier(identifier: String, context: Activity) {
//        var okHttpClient:OkHttp
        for (i in stickerList.value!!.indices) {
            if (stickerList.value!![i].identifier.toString() == identifier) {
                val stickerPackView = stickerList.value!![i].toStickersPack().toStickersPackView()
                if (stickerPackView.stickers.size >= 3) {
                    val intent = Intent().apply {
                        action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
                        putExtra(HomeFragment.EXTRA_STICKER_PACK_ID, stickerPackView.identifier.toString())
                        putExtra(HomeFragment.EXTRA_STICKER_PACK_AUTHORITY, Config.CONTENT_PROVIDER_AUTHORITY)
                        putExtra(HomeFragment.EXTRA_STICKER_PACK_NAME, stickerPackView.name)
                    }
                    try {
                        Log.d("Sdjgsff", stickerPackView.identifier.toString()+" "+stickerPackView.name)
                        context.startActivityForResult(intent, 200)
                    } catch (e: ActivityNotFoundException) {
                        Log.d("Error", e.toString())
                        Toast.makeText(
                            context,
                            e.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Create Minimum 3 Stickers To Add To Whatsapp",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }



    fun addCustomPackToWhatsapp(identifier: String,context: Context){
        for(i in stickerList.value!!.indices)
        {
            if(stickerList.value!![i].identifier.toString() == currentIdentifier) {
                val stickerPackView = stickerList.value!![i].toStickersPack().toStickersPackView()
                if(stickerPackView.stickers.size>=3) {
                    Log.d("spoindent",stickerPackView.identifier+" "+stickerPackView.name+" "+Config.CONTENT_PROVIDER_AUTHORITY)
                    val intent = Intent().apply {
                        action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
                        putExtra(
                            HomeFragment.EXTRA_STICKER_PACK_ID,
                            stickerPackView.identifier.toString()
                        )
                        putExtra(
                            HomeFragment.EXTRA_STICKER_PACK_AUTHORITY,
                            Config.CONTENT_PROVIDER_AUTHORITY
                        )
                        putExtra(HomeFragment.EXTRA_STICKER_PACK_NAME, stickerPackView.name)
                    }
                    try {
                        Log.d("Sdjgsff", stickerPackView.identifier.toString()+" "+stickerPackView.name)
                        (context as Activity).startActivityForResult(intent, 200)
                    } catch (e: ActivityNotFoundException) {
                        Log.d("Error", e.toString())
                        Toast.makeText(
                            context,
                            e.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }else {
                    Toast.makeText(context, "Create Minimum 3 Stickers To Add To Whatsapp", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "Create Minimum 3 Stickers To Add To Whatsapp", Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun addCustomPackToWhatsappBusiness(identifier: String,context: Context){
        for(i in stickerList.value!!.indices){
            if(stickerList.value!![i].identifier.toString() == currentIdentifier) {
                val stickerPackView = stickerList.value!![i].toStickersPack().toStickersPackView()
                if(stickerPackView.stickers.size>=3) {
                    val intent = Intent().apply {
                        setPackage("com.whatsapp.w4b")
                        action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
                        putExtra(
                            HomeFragment.EXTRA_STICKER_PACK_ID,
                            stickerPackView.identifier
                        )
                        putExtra(
                            HomeFragment.EXTRA_STICKER_PACK_AUTHORITY,
                            Config.CONTENT_PROVIDER_AUTHORITY
                        )
                        putExtra(HomeFragment.EXTRA_STICKER_PACK_NAME, stickerPackView.name)
                    }
                    try {
                        (context as Activity).startActivityForResult(intent, 200)
                    } catch (e: ActivityNotFoundException) {
                        Log.d("Error", e.toString())
                        Toast.makeText(
                            context,
                            e.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else {
                    Toast.makeText(
                        context,
                        "Create Minimum 3 Stickers To Add To Whatsapp",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                Toast.makeText(context, "Create Minimum 3 Stickers To Add To Whatsapp", Toast.LENGTH_SHORT).show()
            }
        }

    }

    suspend fun checkIdentifierAlreadyExist(identifier: String):Boolean
    {
        val stickerPack = db.getStickerByIdentifier(identifier)
        return !stickerPack.isNull()
    }

    fun refreshStickerList() {
        // Logic to reload or refresh sticker list
        // For example, you could call a method to re-fetch the data from the database or API
        getStickers()
    }
    suspend fun changeIdentifier(oldStickerPackIdentifier: String,newStickerPackIdentifier:String)
    {
        Log.d("test7766",oldStickerPackIdentifier)
        val stickerPack = db.getStickerByIdentifier(oldStickerPackIdentifier)
        stickerPack?.identifier = newStickerPackIdentifier
        if (stickerPack != null) {
            for(i in stickerPack.stickers?.indices!!)
            {
                val urlString = stickerPack.stickers[i].imageFile
                val lastIndex = urlString?.lastIndexOf('/')
                val filename = if (lastIndex != -1) urlString?.substring(lastIndex?.plus(1) ?: 0) else ""
                stickerPack.stickers[i].imageFile = "${context.filesDir}/stickers_asset/${newStickerPackIdentifier}/${filename}"
            }
        }
        stickerPack?.let { db.updateStickerPack(it) }
        currentIdentifier = newStickerPackIdentifier
        try {
            renameFolder(oldStickerPackIdentifier,newStickerPackIdentifier,"${context.filesDir}/stickers_asset")
        }catch (e:Exception)
        {
            Log.d("Error123",e.toString())
        }
    }
    private fun renameFolder(oldName: String, newName: String, rootDirectory: String) {
        val oldFolder = File(rootDirectory, oldName)
        if (oldFolder.exists() && oldFolder.isDirectory) {
            val newFolder = File(rootDirectory, newName)
            if (!newFolder.exists()) {
                val success = oldFolder.renameTo(newFolder)
                if (success) {
                    MediaScannerConnection.scanFile(context, arrayOf(newFolder.absolutePath), null, null)
                } else {
                    Log.d("Error123","1")
                }
            } else {
                Log.d("Error123","2")

            }
        } else {
            Log.d("Error123","3")
        }
    }

    suspend fun addStickerToPack(stickerPackIdentifier: String, sticker: StickerEntity) {
        val stickerPack = db.getStickerByIdentifier(stickerPackIdentifier)
        if (stickerPack != null) {
            // Create a new list with the existing stickers and the new one
            val updatedStickers = stickerPack.stickers.orEmpty().toMutableList().apply {
                add(sticker)
            }
            // Create a new StickerPackEntity with the updated sticker list
            val updatedStickerPack = stickerPack.copy(stickers = updatedStickers)
            // Update the sticker pack in the database
            db.updateStickerPack(updatedStickerPack)
        }
    }
    suspend fun deleteStickerToPack(stickerPackIdentifier: String, deleteStickerList: List<DeleteStickerItem>):Boolean {
        var deletedStickerPack = false
        val stickerPack = db.getStickerByIdentifier(stickerPackIdentifier)
        val updatedStickers = stickerPack?.stickers.orEmpty().toMutableList()
        if (stickerPack != null) {
            val idsToDelete = deleteStickerList.map { it.id }
            if(updatedStickers.size == idsToDelete.size)
            {
                val mainPath = File(context.filesDir, "stickers_asset")
                val dir = File(mainPath,stickerPackIdentifier)

                //Delete Stickers
                if (dir.isDirectory) {
                    //Delete Try Image
                    val tryDir = File(dir,"try")
                    if (tryDir.isDirectory) {
                        val children = tryDir.list()
                        if (children != null) {
                            for (i in children.indices) {
                                File(tryDir, children[i]).delete()
                            }
                        }
                    }
                    //Delete Stickers
                    val children = dir.list()
                    if (children != null) {
                        for (i in children.indices) {
                            File(dir, children[i]).delete()
                        }
                    }
                }
                dir.delete()
                MediaScannerConnection.scanFile(context, arrayOf(mainPath.absolutePath), null, null)
                db.deleteStickerPack(stickerPackIdentifier)
                deletedStickerPack = true
            }
            else {
                deletedStickerPack = false
                idsToDelete.sortedDescending().forEach { id ->
                    if (id in 0 until updatedStickers.size) {
                        updatedStickers.removeAt(id)
                    }
                }
                val updatedStickerPack = stickerPack.copy(stickers = updatedStickers)
                // Update the sticker pack in the database
                db.updateStickerPack(updatedStickerPack)
            }
            stickerList.postValue(db.getStickers())
        }
        return deletedStickerPack
    }

}