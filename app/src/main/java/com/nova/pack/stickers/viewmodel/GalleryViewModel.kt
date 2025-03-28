package com.nova.pack.stickers.viewmodel

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nova.pack.stickers.model.ImagesModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(private val context: Context): ViewModel() {
    private var imagesList = ArrayList<ImagesModel>()
    var images = MutableLiveData<ArrayList<ImagesModel>>()
    private var videosList = ArrayList<ImagesModel>()
    var videos = MutableLiveData<ArrayList<ImagesModel>>()
    private var booleanFolder = false
    //
    private val previewDataState = MutableLiveData<UiPreviewDataState>()
    val uiState: LiveData<UiPreviewDataState> get() = previewDataState
    //
    private val previewDropDownState = MutableLiveData<UiFolderDrpDown>()
    val uiStateDropDown: LiveData<UiFolderDrpDown> get() = previewDropDownState

    suspend fun getAllImagesPath() {
        emitPreviewUiState(isLoading = true)
        kotlin.runCatching {
            if (imagesList.size == 0) {
                val cursor: Cursor?
                val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val projection = arrayOf(
                    MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                )
                val orderBy = MediaStore.Images.Media.DATE_TAKEN
                cursor = context.contentResolver!!.query(
                    uri, projection, null, null,
                    "$orderBy DESC"
                )
                val columnIndexData: Int =
                    cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val columnIndexFolderName: Int =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val absolutePathOfImage = cursor.getString(columnIndexData)
                    if (absolutePathOfImage != null && (absolutePathOfImage.endsWith(".png", true) || absolutePathOfImage.endsWith(".jpg", true))) {
                        val folderName = cursor.getString(columnIndexFolderName)
                        val existingFolderIndex = imagesList.indexOfFirst { it.folder == folderName }

                        if (existingFolderIndex != -1) {
                            // Folder already exists, add the image path to the existing folder
                            imagesList[existingFolderIndex].imagePathList.add(absolutePathOfImage)
                        } else {
                            // Folder does not exist, create a new folder and add the image path
                            val pathList = ArrayList<String>()
                            pathList.add(absolutePathOfImage)
                            val imgModel = ImagesModel(folderName, pathList)
                            imagesList.add(imgModel)
                        }
                    }
                }
                cursor.close()
                images.postValue(imagesList)
            }
        // Handle the exception
        }.onSuccess {
            emitPreviewUiState(isLoading = false)
        }.onFailure {
            emitPreviewUiState(isLoading = false)
        }
    }

    suspend fun getAllVideosPath() {
        emitPreviewUiState(isLoading = true)
        kotlin.runCatching {
            if (videosList.size == 0) {
                val cursor: Cursor?
                val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val orderBy = MediaStore.Video.Media.DATE_TAKEN
                cursor = context.contentResolver!!.query(
                    uri, projection, null, null,
                    "$orderBy DESC"
                )
                val columnIndexData: Int =
                    cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val columnIndexFolderName: Int =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val absolutePathOfImage = cursor.getString(columnIndexData)
                    if (absolutePathOfImage != null && (absolutePathOfImage.endsWith(".mp4", true) ||
                                absolutePathOfImage.endsWith(".mkv", true))
                    ) {
                        val folderName = cursor.getString(columnIndexFolderName)
                        val existingFolderIndex = videosList.indexOfFirst { it.folder == folderName }

                        if (existingFolderIndex != -1) {
                            // Folder already exists, add the image path to the existing folder
                            Log.d("asdshd",absolutePathOfImage)
                            videosList[existingFolderIndex].imagePathList.add(absolutePathOfImage)
                        } else {
                            Log.d("asdshd",absolutePathOfImage)
                            // Folder does not exist, create a new folder and add the image path
                            val pathList = ArrayList<String>()
                            pathList.add(absolutePathOfImage)
                            val imgModel = ImagesModel(folderName, pathList)
                            videosList.add(imgModel)
                        }
                    }
                }
                cursor.close()
                videos.postValue(videosList)
            }
        // Handle the exception
        }.onSuccess {
            emitPreviewUiState(isLoading = false)
        }.onFailure {
            emitPreviewUiState(isLoading = false)
        }
    }

    private fun emitPreviewUiState(
        isLoading: Boolean = false,
        error: String? = null,
    )
    {
        val dataState = UiPreviewDataState(isLoading, error)
        previewDataState.postValue(dataState)
    }

    fun emitFolderDropDownUi(isOpened: Boolean = false, title: String? = "Folder")
    {
        val state = UiFolderDrpDown(isOpened,title)
        previewDropDownState.postValue(state)
    }

    data class UiPreviewDataState(
        val isLoading: Boolean,
        val error: String?,
    )

    data class UiFolderDrpDown(
        val isOpened: Boolean,
        val title: String?
    )
}