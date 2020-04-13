package com.example.ocrdemo.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


/**
 *
 * Author: <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2020/4/13 11:20 AM
 * Project: OCRDemo
 * Description:
 **/
object BitmapUtils {
    private const val TAG = "BitmapUtils"
    private const val MAX_DECODE_PICTURE_SIZE = 1920 * 1440

    fun extractThumbNail(path: String?, height: Int, width: Int, crop: Boolean): Bitmap? {
        if (path == null || path == "" || height <= 0 && width >= 0) {
            return null
        }
        var options: BitmapFactory.Options? = BitmapFactory.Options()
        try {
            options!!.inJustDecodeBounds = true
            var tmp = BitmapFactory.decodeFile(path, options)
            if (tmp != null) {
                tmp.recycle()
                tmp = null
            }
            Log.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop)
            val beY = options.outHeight * 1.0 / height
            val beX = options.outWidth * 1.0 / width
            Log.d(TAG, "extractThumbNail: extract beX = $beX, beY = $beY")
            options.inSampleSize =
                (if (crop) if (beY > beX) beX else beY else if (beY < beX) beX else beY).toInt()
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++
            }
            var newHeight = height
            var newWidth = width
            if (crop) {
                if (beY > beX) {
                    newHeight = (newWidth * 1.0 * options.outHeight / options.outWidth).toInt()
                } else {
                    newWidth = (newHeight * 1.0 * options.outWidth / options.outHeight).toInt()
                }
            } else {
                if (beY < beX) {
                    newHeight = (newWidth * 1.0 * options.outHeight / options.outWidth).toInt()
                } else {
                    newWidth = (newHeight * 1.0 * options.outWidth / options.outHeight).toInt()
                }
            }
            options.inJustDecodeBounds = false
            Log.i(
                TAG,
                "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize
            )
            var bm = BitmapFactory.decodeFile(path, options)
            if (bm == null) {
                Log.e(TAG, "bitmap decode failed")
                return null
            }
            Log.i(TAG, "bitmap decoded size=" + bm.width + "x" + bm.height)
            val scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true)
            if (scale != null) {
                bm.recycle()
                bm = scale
            }
            if (crop) {
                val cropped = Bitmap.createBitmap(
                    bm,
                    bm.width - width shr 1,
                    bm.height - height shr 1,
                    width,
                    height
                )
                    ?: return bm
                bm.recycle()
                bm = cropped
                Log.i(TAG, "bitmap croped size=" + bm.width + "x" + bm.height)
            }
            return bm
        } catch (e: OutOfMemoryError) {
            Log.e(TAG, "decode bitmap failed: " + e.message)
            options = null
        }
        return null
    }

    fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean): ByteArray {
        val output = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output)
        if (needRecycle) {
            bmp.recycle()
        }
        val result: ByteArray = output.toByteArray()
        try {
            output.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }


    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    fun bitmapToBase64(bitmap: Bitmap?): String? {
        var result: String? = null
        var baos: ByteArrayOutputStream? = null
        try {
            if (bitmap != null) {
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                baos.flush()
                baos.close()
                val bitmapBytes: ByteArray = baos.toByteArray()
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (baos != null) {
                    baos.flush()
                    baos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    fun base64ToBitmap(base64Data: String?): Bitmap {
        val bytes: ByteArray = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun bitmapToDrawable(bitmap: Bitmap?): Drawable {
        return BitmapDrawable(bitmap)
    }

    fun bytesToBitmap(byteArray: ByteArray?): Bitmap? {
        if (byteArray == null) return null
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        // 取 drawable 的长宽
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight

        // 取 drawable 的颜色格式
        val config =
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        // 建立对应 bitmap
        val bitmap = Bitmap.createBitmap(w, h, config)
        // 建立对应 bitmap 的画布
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        // 把 drawable 内容画到画布中
        drawable.draw(canvas)
        return bitmap
    }
}