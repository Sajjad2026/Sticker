package com.nova.pack.stickers.provider


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import com.nova.pack.stickers.model.StickerPackView
import com.nova.pack.stickers.model.StickerView
import com.nova.pack.stickers.utils.Config.Companion.bitmap

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL

class StickerPackValidator {

    companion object {
        private const val STICKER_FILE_SIZE_LIMIT_KB = 100
        private const val EMOJI_LIMIT = 3
        private const val IMAGE_HEIGHT = 512
        private const val IMAGE_WIDTH = 512
        private const val STICKER_SIZE_MIN = 3
        private const val STICKER_SIZE_MAX = 30
        private const val CHAR_COUNT_MAX = 128
        private const val ONE_KIBIBYTE = 8 * 1024
        private const val TRAY_IMAGE_FILE_SIZE_MAX_KB = 50
        private const val TRAY_IMAGE_DIMENSION_MIN = 24
        private const val TRAY_IMAGE_DIMENSION_MAX = 512

        /**
         * Checks whether a sticker pack contains valid data
         */
        @JvmStatic
        @Throws(IllegalStateException::class)
        fun verifyStickerPackValidity(context: Context, stickerPack: StickerPackView) {
            Log.d("StickerPackValidation", "Starting validation for sticker pack: ${stickerPack.identifier}")

            if (TextUtils.isEmpty(stickerPack.identifier)) {
                Log.e("StickerPackValidation", "Validation failed: sticker pack identifier is empty")
                throw IllegalStateException("sticker pack identifier is empty")
            }
            if (stickerPack.identifier.length > CHAR_COUNT_MAX) {
                Log.e("StickerPackValidation", "Validation failed: sticker pack identifier cannot exceed $CHAR_COUNT_MAX characters")
                throw IllegalStateException("sticker pack identifier cannot exceed $CHAR_COUNT_MAX characters")
            }
            checkStringValidity(stickerPack.identifier)

            if (TextUtils.isEmpty(stickerPack.publisher)) {
                Log.e("StickerPackValidation", "Validation failed: sticker pack publisher is empty, sticker pack identifier: ${stickerPack.identifier}")
                throw IllegalStateException("sticker pack publisher is empty, sticker pack identifier: ${stickerPack.identifier}")
            }
            if (stickerPack.publisher.length > CHAR_COUNT_MAX) {
                Log.e("StickerPackValidation", "Validation failed: sticker pack publisher cannot exceed $CHAR_COUNT_MAX characters, sticker pack identifier: ${stickerPack.identifier}")
                throw IllegalStateException("sticker pack publisher cannot exceed $CHAR_COUNT_MAX characters, sticker pack identifier: ${stickerPack.identifier}")
            }
            if (TextUtils.isEmpty(stickerPack.name)) {
                Log.e("StickerPackValidation", "Validation failed: sticker pack name is empty, sticker pack identifier: ${stickerPack.identifier}")
                throw IllegalStateException("sticker pack name is empty, sticker pack identifier: ${stickerPack.identifier}")
            }
            if (stickerPack.name.length > CHAR_COUNT_MAX) {
                Log.e("StickerPackValidation", "Validation failed: sticker pack name cannot exceed $CHAR_COUNT_MAX characters, sticker pack identifier: ${stickerPack.identifier}")
                throw IllegalStateException("sticker pack name cannot exceed $CHAR_COUNT_MAX characters, sticker pack identifier: ${stickerPack.identifier}")
            }
            if (TextUtils.isEmpty(stickerPack.trayImageFile)) {
                Log.e("StickerPackValidation", "Validation failed: sticker pack tray id is empty, sticker pack identifier: ${stickerPack.identifier}")
                throw IllegalStateException("sticker pack tray id is empty, sticker pack identifier: ${stickerPack.identifier}")
            }
            try {
                val bytes = getImageBytes(stickerPack.trayImageFile.toString())
                if (bytes.size > TRAY_IMAGE_FILE_SIZE_MAX_KB * ONE_KIBIBYTE) {
                    Log.e("StickerPackValidation", "Validation failed: tray image should be less than $TRAY_IMAGE_FILE_SIZE_MAX_KB KB, tray image file: ${stickerPack.trayImageFile}")
                    throw IllegalStateException("tray image should be less than $TRAY_IMAGE_FILE_SIZE_MAX_KB KB, tray image file: ${stickerPack.trayImageFile}")
                }
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (bitmap.height > TRAY_IMAGE_DIMENSION_MAX || bitmap.height < TRAY_IMAGE_DIMENSION_MIN) {
                    Log.e("StickerPackValidation", "Validation failed: tray image height should be between $TRAY_IMAGE_DIMENSION_MIN and $TRAY_IMAGE_DIMENSION_MAX pixels, current height: ${bitmap.height}, tray image file: ${stickerPack.trayImageFile}")
                    throw IllegalStateException("tray image height should be between $TRAY_IMAGE_DIMENSION_MIN and $TRAY_IMAGE_DIMENSION_MAX pixels, current height: ${bitmap.height}, tray image file: ${stickerPack.trayImageFile}")
                }
                if (bitmap.width > TRAY_IMAGE_DIMENSION_MAX || bitmap.width < TRAY_IMAGE_DIMENSION_MIN) {
                    Log.e("StickerPackValidation", "Validation failed: tray image width should be between $TRAY_IMAGE_DIMENSION_MIN and $TRAY_IMAGE_DIMENSION_MAX pixels, current width: ${bitmap.width}, tray image file: ${stickerPack.trayImageFile}")
                    throw IllegalStateException("tray image width should be between $TRAY_IMAGE_DIMENSION_MIN and $TRAY_IMAGE_DIMENSION_MAX pixels, current width: ${bitmap.width}, tray image file: ${stickerPack.trayImageFile}")
                }
            } catch (e: IOException) {
                Log.e("StickerPackValidation", "Validation failed: Cannot open tray image, ${stickerPack.trayImageFile}", e)
                throw IllegalStateException("Cannot open tray image, ${stickerPack.trayImageFile}", e)
            }

            val stickers = stickerPack.stickers
            if (stickers.size < STICKER_SIZE_MIN || stickers.size > STICKER_SIZE_MAX) {
                Log.e("StickerPackValidation", "Validation failed: sticker pack sticker count should be between $STICKER_SIZE_MIN and $STICKER_SIZE_MAX inclusive, it currently has ${stickers.size}, sticker pack identifier: ${stickerPack.identifier}")
                throw IllegalStateException("sticker pack sticker count should be between $STICKER_SIZE_MIN and $STICKER_SIZE_MAX inclusive, it currently has ${stickers.size}, sticker pack identifier: ${stickerPack.identifier}")
            }

            Log.d("StickerPackValidation", "Validation passed for sticker pack: ${stickerPack.identifier}")
        }


        private fun validateSticker(identifier: String, sticker: StickerView) {
            if (sticker.emojis.size > EMOJI_LIMIT) {
                throw IllegalStateException("emoji count exceed limit, sticker pack identifier:$identifier, filename:${sticker.imageFile}")
            }
            if (TextUtils.isEmpty(sticker.imageFile)) {
                throw IllegalStateException("no file path for sticker, sticker pack identifier:$identifier")
            }
            //  validateStickerFile(context, identifier, sticker.imageFile!!)
        }

        private fun validateStickerFile(context: Context, identifier: String, fileName: String) {
            try {
                val bytes = getImageBytes(fileName)
                if (bytes.size > STICKER_FILE_SIZE_LIMIT_KB * ONE_KIBIBYTE) {
                    throw IllegalStateException("sticker should be less than $STICKER_FILE_SIZE_LIMIT_KB KB, sticker pack identifier:$identifier, filename:$fileName")
                }
                try {
                    val outputStream = ByteArrayOutputStream()
                    bitmap!!.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)

//                    if (outputStream.!= IMAGE_HEIGHT) {
//                        throw IllegalStateException("sticker height should be $IMAGE_HEIGHT, sticker pack identifier:$identifier, filename:$fileName")
//                    }
//                    if (outputStream.width != IMAGE_WIDTH) {
//                        throw IllegalStateException("sticker width should be $IMAGE_WIDTH, sticker pack identifier:$identifier, filename:$fileName")
//                    }
                } catch (e: IllegalArgumentException) {
                    throw IllegalStateException(
                        "Error parsing webp image, sticker pack identifier:$identifier, filename:$fileName",
                        e
                    )
                }
            } catch (e: IOException) {
                throw IllegalStateException(
                    "cannot open sticker file: sticker pack identifier:$identifier, filename:$fileName",
                    e
                )
            }
        }

        private fun checkStringValidity(string: String) {
            val pattern = "[\\w-.,'\\s]+"
            if (!string.matches(pattern.toRegex())) {
                throw IllegalStateException("$string contains invalid characters, allowed characters are a to z, A to Z, _ , ' - . and space character")
            }
            if (string.contains("..")) {
                throw IllegalStateException("$string cannot contain ..")
            }
        }


        private fun getImageBytes(imageUrl: String): ByteArray {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()

            val input: BufferedInputStream = BufferedInputStream(connection.getInputStream())
            val output = ByteArrayOutputStream()

            try {
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }
                return output.toByteArray()
            } finally {
                input.close()
                output.close()
            }
        }
    }


}
