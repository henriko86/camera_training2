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
     * 画像クォリティ
     */
    private static final int IMAGE_QUALITY = 90;

    /**
     * Drawableを画像に変換
     *
     * @param drawable Drawable
     * @return 画像
     */
    public static Bitmap toBitmap(Drawable drawable) {
        if (drawable == null) return Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888);

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
     * @param image    画像
     * @param rotation 回転
     * @return 回転画像
     */
    public static Bitmap flip(Bitmap image, int rotation) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(rotation);

        return Bitmap.createBitmap(image, 0, 0, imageWidth, imageHeight, matrix, true);
    }

    /**
     * 画像をトリミング
     *
     * @param image  画像
     * @param width  トリミング幅
     * @param height トリミング高さ
     * @return トリミング画像
     */
    public static Bitmap trim(Bitmap image, int width, int height) {
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
     * @param image   画像
     * @param rect    範囲
     * @param quality クオリティ
     * @return 切り抜き画像
     */
    public static Bitmap crop(Bitmap image, Rect rect, int quality) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1f, 1f);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;

        Bitmap bmp2 = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            bmp2.compress(Bitmap.CompressFormat.JPEG, quality, out);

            try (ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray())) {
                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(input, true);
                if (decoder != null) {
                    return decoder.decodeRegion(rect, options);
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * 画像をBase64PNG文字列に変換
     *
     * @param image 画像
     * @return Base64PNG文字列
     */
    public static String toBase64Png(Bitmap image) {
        return toBase64Png(image, 90);
    }

    /**
     * 画像をBase64PNG文字列に変換
     *
     * @param image   画像
     * @param quality クオリティ
     * @return Base64PNG文字列
     */
    public static String toBase64Png(Bitmap image, int quality) {
        return toBase64(image, Bitmap.CompressFormat.PNG, quality);
    }

    /**
     * 画像をBase64JPEG文字列に変換
     *
     * @param image 画像
     * @return Base64JPEG文字列
     */
    public static String toBase64Jpeg(Bitmap image) {
        return toBase64Jpeg(image, 90);
    }

    /**
     * 画像をBase64JPEG文字列に変換
     *
     * @param image   画像
     * @param quality クオリティ
     * @return Base64JPEG文字列
     */
    public static String toBase64Jpeg(Bitmap image, int quality) {
        return toBase64(image, Bitmap.CompressFormat.JPEG, quality);
    }

    /**
     * 画像をBase64文字列に変換
     *
     * @param image  画像
     * @param format フォーマット
     * @return Base64文字列
     */
    public static String toBase64(Bitmap image, Bitmap.CompressFormat format) {
        return toBase64(image, format, 90);
    }

    /**
     * 画像をBase64文字列に変換
     *
     * @param image   画像
     * @param format  フォーマット
     * @param quality クオリティ
     * @return Base64文字列
     */
    public static String toBase64(Bitmap image, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        if (format == Bitmap.CompressFormat.JPEG) {
            return "data:image/jpeg;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}";
        } else if (format == Bitmap.CompressFormat.PNG) {
            return "data:image/png;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}";
        } else {
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
    }

    /**
     * Base64文字列を画像に変換
     *
     * @param base64 Base64文字列
     * @return 画像
     */
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
    public static byte[] toByteArray(Bitmap image) {
        ByteBuffer buffer = ByteBuffer.allocate(image.getByteCount());
        image.copyPixelsToBuffer(buffer);
        return buffer.array();
    }
}
