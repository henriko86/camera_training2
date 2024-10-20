package com.yuruneji.camera_training.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author toru
 * @version 1.0
 */
public class BitmapUtils2 {

    /**
     * Drawableを画像に変換
     *
     * @param drawable Drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * 画像を回転
     *
     * @param source
     * @param rotation
     * @return
     */
    public static Bitmap flipBitmap(Bitmap source, int rotation) {
        int imageWidth = source.getWidth();
        int imageHeight = source.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(rotation);

        return Bitmap.createBitmap(
                source, 0, 0, imageWidth, imageHeight, matrix, true
        );
    }

    /**
     * 画像をトリミング
     *
     * @param image
     * @param width
     * @param height
     * @return
     */
    public static Bitmap bitmapTrim(Bitmap image, int width, int height) {
        double scale;
        if (image.getWidth() >= image.getHeight()) { // 横長
            scale = width / (double) image.getWidth();
        } else { // 縦長
            scale = height / (double) image.getHeight();
        }

        int dstWidth = (int) (image.getWidth() * scale);
        int dstHeight = (int) (image.getHeight() * scale);

        return Bitmap.createScaledBitmap(image, dstWidth, dstHeight, true);
    }

    /**
     * 画像を範囲で切り抜き
     *
     * @param bmp
     * @param faceRect
     * @param quality
     * @return
     */
    public static Bitmap faceClipping(Bitmap bmp, Rect faceRect, int quality) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1f, 1f);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;

        Bitmap bmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            bmp2.compress(Bitmap.CompressFormat.JPEG, quality, out);

            try (ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray())) {
                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(input, true);
                if (decoder != null) {
                    return decoder.decodeRegion(faceRect, options);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static String toBase64Png(Bitmap bitmap) {
        return toBase64Png(bitmap, 90);
    }

    public static String toBase64Png(Bitmap bitmap, int quality) {
        return toBase64(bitmap, Bitmap.CompressFormat.PNG, quality);
    }

    public static String toBase64Jpeg(Bitmap bitmap) {
        return toBase64Jpeg(bitmap, 90);
    }

    public static String toBase64Jpeg(Bitmap bitmap, int quality) {
        return toBase64(bitmap, Bitmap.CompressFormat.JPEG, quality);
    }

    public static String toBase64(Bitmap bitmap, Bitmap.CompressFormat format) {
        return toBase64(bitmap, format, 90);
    }

    public static String toBase64(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        if (format == Bitmap.CompressFormat.JPEG) {
            return "data:image/jpeg;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}";
        } else if (format == Bitmap.CompressFormat.PNG) {
            return "data:image/png;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}";
        } else {
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
    }

    public static Bitmap toBitmap(String base64) {
        byte[] decodedBytes = Base64.decode(
                base64.substring(base64.indexOf(",") + 1),
                Base64.DEFAULT
        );
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * Bitmapをbyte配列に変換
     *
     * @return byte配列
     */
    public static byte[] toByteArray(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(buffer);
        return buffer.array();
    }
}
