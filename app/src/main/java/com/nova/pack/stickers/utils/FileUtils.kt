package com.nova.pack.stickers.utils


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

class FileUtils {
    companion object{
        fun resizeBitmap(context: Context, resourceId: Int, targetWidth: Int, targetHeight: Int): Bitmap? {
            try {
                // Load the original bitmap from the resource
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeResource(context.resources, resourceId, options)

                // Calculate the inSampleSize to resize the image
                options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)

                // Decode the bitmap with the calculated inSampleSize
                options.inJustDecodeBounds = false
                val resizedBitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

                return resizedBitmap
            } catch (e: Exception) {
                Log.e("ImageResizer", "Error resizing bitmap: ${e.message}")
                return null
            }
        }
        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2

                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        fun saveSticker(context:Context,bitmap: Bitmap,identifier: String):String{
            // Create a random name for the file
            val random = Random(System.currentTimeMillis()) // Seed with current time

            // Generate a random positive integer

            val name = "${random.nextInt(Int.MAX_VALUE) + 1}.webp"
            // Resize the bitmap
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, true)
            // Compress the bitmap to meet the size constraint
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
            var quality = 90
            while (outputStream.toByteArray().size / 1024 > 100 && quality > 0) {
                outputStream.reset()
                resizedBitmap.compress(Bitmap.CompressFormat.WEBP, quality, outputStream)
                quality -= 10
            }
            // Define the directory paths
            val mainPath = File(context.filesDir, "stickers_asset")
            val root = File(mainPath, identifier)
            // Create directories if they don't exist
            mainPath.mkdirs()
            root.mkdirs()
            // Create the file
            val file = File(root, name)
            if (file.exists()) file.delete()
            // Save the resized and compressed bitmap as a WebP file
            try {
                val out = FileOutputStream(file)
                out.write(outputStream.toByteArray())
                out.flush()
                out.close()
                MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file.absolutePath
        }
        fun saveTryImage(context: Context,bitmap: Bitmap, identifier: String):String {
            // Resize the bitmap to 96x96 pixels
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 96, 96, true)
            // Compress the bitmap to meet the size constraint
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            var quality = 90
            while (outputStream.toByteArray().size / 1024 > 50 && quality > 0) {
                outputStream.reset()
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
                quality -= 10
            }
            // Define the directory paths
            val mainPath = File(context.filesDir, "stickers_asset")

            val root = File(mainPath, "${identifier}/try")
            // Create directories if they don't exist
            mainPath.mkdirs()
            root.mkdirs()
            // Create the file
            val name = "Tray-Sticker.png"
            val file = File(root, name)
            if (file.exists()) file.delete()
            // Save the resized and compressed bitmap as a PNG file
            try {
                val out = FileOutputStream(file)
                out.write(outputStream.toByteArray())
                out.flush()
                out.close()
                MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file.absolutePath
        }
    }
}