package com.nova.pack.stickers.viewmodel
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nova.pack.stickers.ads.AdsManager.RETURNING_HOME_STICKERS_JSON
import com.nova.pack.stickers.ads.AdsManager.STICKER_PACK_JSON
import com.nova.pack.stickers.api.StickerJson
import com.nova.pack.stickers.model.StickerPackEntity
import com.nova.pack.stickers.model.StickerPackView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StickersViewModel @Inject constructor(private val context: Context) : ViewModel() {
    var sticker = MutableLiveData<List<StickerPackView>>(emptyList())
    var allStickers = MutableLiveData<List<StickerPackView>>(emptyList())
    var allStickerSearch = ArrayList<StickerPackView>()
    private val previewDataState = MutableLiveData<UiPreviewDataState>()
    val uiState: LiveData<UiPreviewDataState> get() = previewDataState

    fun getHomeStickers(activity:String) {
        viewModelScope.launch {
            emitPreviewUiState(isLoading = true)
            try {
                Log.d("sdjhsd","call")
                val packsEntities =if (activity=="return") {
                    Log.d("sdkjsf",RETURNING_HOME_STICKERS_JSON.toString())
                    StickerJson.parseJsonToCategories(RETURNING_HOME_STICKERS_JSON!!)
                } else{
                    Log.d("sdkjsf","2")
                    StickerJson.parseJsonToCategories(STICKER_PACK_JSON!!)
                }

                val allStickerList: List<StickerPackEntity> = packsEntities?.stickerPacks!!
                val allStickerPackViews = allStickerList.map { it.toStickersPack().toStickersPackView() }
                val uniqueStickerPackViews = allStickerPackViews.toSet()
                allStickerSearch.clear()
                allStickerSearch.addAll(uniqueStickerPackViews)
                allStickers.value=allStickerPackViews
               if (activity=="return") {
                   if (packsEntities.returnStickerPacksHome.isNotEmpty()) {
                       val newStickerList: List<StickerPackEntity> = packsEntities.returnStickerPacksHome
                       val currentStickerList = sticker.value ?: emptyList()
                       val combinedList = (currentStickerList + newStickerList.map {
                           it.toStickersPack().toStickersPackView()
                       }).distinctBy { it.identifier } // Assuming StickerPackView has an 'id' property
                       sticker.value = combinedList
                   } else {
                       emitPreviewUiState(error = "Error in JSON Response")
                   }
               }else{
                   if (packsEntities.stickerPacksHome.isNotEmpty()) {
                       val newStickerList: List<StickerPackEntity> = packsEntities.stickerPacksHome
                       val currentStickerList = sticker.value ?: emptyList()
                       val combinedList = (currentStickerList + newStickerList.map {
                           it.toStickersPack().toStickersPackView()
                       }).distinctBy { it.identifier } // Assuming StickerPackView has an 'id' property
                       sticker.value = combinedList
                   } else {
                       emitPreviewUiState(error = "Error in JSON Response")
                   }
               }
                emitPreviewUiState(isLoading = false)
            } catch (exception: Exception) {
                emitPreviewUiState(error = exception.message)
                emitPreviewUiState(isLoading = false)
                Log.d("StickersViewModel", exception.message.toString())
            }
        }
    }

    suspend fun setStickerCategory(category: String) {
        emitPreviewUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val packsEntityList = StickerJson.parseJsonToCategories(STICKER_PACK_JSON!!)
                val stickerList: List<StickerPackEntity> = packsEntityList?.stickerPacks!!
                Log.d("Sfdkhsfs", stickerList.toString())

                if (stickerList.isNotEmpty()) {
                    val newStickerList = stickerList.map {
                        it.toStickersPack().toStickersPackView()
                    }
                    val currentStickerList = sticker.value ?: emptyList()
                    val combinedList = (currentStickerList + newStickerList).distinctBy { it.identifier }

                    sticker.value = combinedList
                } else {
                    emitPreviewUiState(error = "No sticker packs found for category: $category")
                }

                emitPreviewUiState(isLoading = false)
            } catch (exception: Exception) {
                emitPreviewUiState(error = exception.message)
                emitPreviewUiState(isLoading = false)
                Log.d("StickersViewModel", "Error: ${exception.message}")
            }
        }
    }



/*    suspend fun getAllCategoryStickers(): List<StickerPackView> {
        emitPreviewUiState(isLoading = true)
        val list = mutableListOf<StickerPackView>()
        try {
            // Get all files from assets directory
            val fileNames = context.assets.list("") ?: emptyArray()

            for (fileName in fileNames) {
                if (fileName.endsWith(".json")) {
                    val jsonString = loadJsonFromAssets(fileName)
                    val jsonObject = JSONObject(jsonString)
                    val packsEntityList = StickerJson.parseJsonToCategories(jsonObject)

                    if (packsEntityList.stickerPacks.isNotEmpty() && packsEntityList.stickerPacks.isNotEmpty()) {
                        val stickerList: List<StickerPackEntity> = packsEntityList.stickerPacks
                        stickerSearch.value = stickerList.map {
                            it.toStickersPack().toStickersPackView()
                        }
                        val stickerPackViews = stickerList.map {
                            it.toStickersPack().toStickersPackView()
                        }
                        Log.d("asdhjmndas",stickerSearch.toString())
                        list.addAll(stickerPackViews)
                    } else {
                        emitPreviewUiState(error = "Error in JSON Response for file: $fileName")
                    }
                }
            }
        } catch (exception: Exception) {
            emitPreviewUiState(error = exception.message)
            Log.d("StickersViewModel", "Error: ${exception.message}")
        } finally {
            emitPreviewUiState(isLoading = false)
        }
        return list
    }*/

    private fun emitPreviewUiState(
        isLoading: Boolean = false,
        error: String? = null,
    ) {
        val dataState = UiPreviewDataState(isLoading, error)
        previewDataState.postValue(dataState)
    }

    private fun loadJsonFromAssets(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}

data class UiPreviewDataState(
    val isLoading: Boolean,
    val error: String?,
)
