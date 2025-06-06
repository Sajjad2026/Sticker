package com.nova.pack.stickers.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;

public class BitmapUtility {

    static Bitmap getResizedBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap == null || bitmap.isRecycled()) {
            Log.e("getResizedBitmap", "Input bitmap is null or already recycled.");
            return null;
        }

        Bitmap background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        float originalWidth = bitmap.getWidth();
        float originalHeight = bitmap.getHeight();

        Canvas canvas = new Canvas(background);

        // Calculate the scaling factors
        float scale = Math.min(width / originalWidth, height / originalHeight);
        float xTranslation = (width - originalWidth * scale) / 2.0f;
        float yTranslation = (height - originalHeight * scale) / 2.0f;

        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(bitmap, transformation, paint);

        return background;
    }





    static Bitmap getBorderedBitmap(Bitmap image, int borderColor, int borderSize) {

        // Creating a canvas with an empty bitmap, this is the bitmap that gonna store the final canvas changes
        Bitmap finalImage = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(finalImage);

        // Make a smaller copy of the image to draw on top of original
        Bitmap imageCopy = Bitmap.createScaledBitmap(image, image.getWidth() - borderSize, image.getHeight() - borderSize, true);

        // Let's draw the bigger image using a white paint brush
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(borderColor, PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(image, 0, 0, paint);

        int width = image.getWidth();
        int height = image.getHeight();
        float centerX = (width - imageCopy.getWidth()) * 0.5f;
        float centerY = (height - imageCopy.getHeight()) * 0.5f;
        // Now let's draw the original image on top of the white image, passing a null paint because we want to keep it original
        canvas.drawBitmap(imageCopy, centerX, centerY, null);

        // Returning the image with the final results
        return finalImage;
    }
}