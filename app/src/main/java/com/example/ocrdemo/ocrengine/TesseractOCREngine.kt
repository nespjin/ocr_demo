package com.example.ocrdemo.ocrengine

import android.graphics.Bitmap
import android.util.Log
import com.example.ocrdemo.ext.TAG
import com.example.ocrdemo.utils.BitmapUtils
import com.example.ocrdemo.utils.FileUtils
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 *
 * Author: <a href="mailto:1756404649@numob.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2020/4/13 10:37 AM
 * Project: OCRDemo
 * Description:
 **/
class TesseractOCREngine : IOCREngine {


   override fun imgFileToText(
        parentPath: String,
        imgFilePath: String,
        onProcessorListener: IOCREngine.OnProcessorListener?
    ) {
        GlobalScope.launch {
            val bytesToBitmap = BitmapUtils.bytesToBitmap(FileUtils.fileToBytes(imgFilePath))
            if (bytesToBitmap == null) {
                onProcessorListener?.onFailed("转Bitmap失败")
                return@launch
            }

            withContext(Dispatchers.Main) {
                onProcessorListener?.onLoadBitmap(bytesToBitmap)
                bmpToText(parentPath, bytesToBitmap, onProcessorListener = onProcessorListener)
            }
        }
    }

    /**
     *
     * @param bitmap 识别的Bitmap
     * @param language 识别的语言 chi_sim : 中文， eng：英文
     * @param onProcessorListener onProcessorListener
     * @return
     */
    override fun bmpToText(
        parentPath: String,
        bitmap: Bitmap,
        onProcessorListener: IOCREngine.OnProcessorListener?
    ) {
        onProcessorListener?.onStartProcess()
        val t = Thread {
            val tessDataDir = File("$parentPath/tessdata")
            if (!tessDataDir.exists()) {
                tessDataDir.mkdirs()
            }

            val tessBaseAPI = TessBaseAPI()
            if (tessBaseAPI.init(parentPath, "eng")) {
                tessBaseAPI.setImage(bitmap)
                val text = tessBaseAPI.utF8Text
                Log.e(TAG, "OCREngine.bmpToText: $text")
                Log.e(TAG, "OCREngine.bmpToTextNum: ${selectNum(text)}")
                GlobalScope.launch(Dispatchers.Main) {
                    onProcessorListener?.onSuccess(selectNum(text))
                }
            } else {
                onProcessorListener?.onFailed("引擎初始化失败")
            }
            tessBaseAPI.end()

        }
        t.start()
    }


}