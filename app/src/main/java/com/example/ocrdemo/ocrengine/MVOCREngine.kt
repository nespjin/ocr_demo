package com.example.ocrdemo.ocrengine

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer

/**
 *
 * Author: <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2020/4/13 4:35 PM
 * Project: OCRDemo
 * Description:
 **/
class MVOCREngine(private val activity: Activity) : IOCREngine {

    private var textRecognizer: TextRecognizer? = null

    var onProcessorListener: IOCREngine.OnProcessorListener? = null

    private val ocrDetectorProcessor = object : Detector.Processor<TextBlock> {

        override fun release() {

        }

        override fun receiveDetections(detections: Detector.Detections<TextBlock>?) {
            val detectedItems = detections?.detectedItems
            if (detectedItems != null) {
                for (i in 0 until detectedItems.size()) {
                    val item = detectedItems[i]
                    if (item != null && item.value != null) {
                        onProcessorListener?.onSuccess(item.value)
                    }
                }
            }

        }

    }

    override fun initEngine() {
        textRecognizer = TextRecognizer.Builder(activity).build()
        textRecognizer?.apply {
            setProcessor(ocrDetectorProcessor)
            if (!isOperational) {
                val lowStorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
                val hasLowStorage = activity.registerReceiver(null, lowStorageFilter) != null

                if (hasLowStorage) {
                    Toast.makeText(activity, "设备空间不够无法下载OCR依赖包...", Toast.LENGTH_LONG).show()
                }
            }
        }

    }


    fun startRecogniz() {

        // check that the device has play services available.
        val code = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(activity.applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(
                activity,
                code,
                9001
            )
            dlg.show()
        }
    }

    override fun imgFileToText(
        parentPath: String,
        imgFilePath: String,
        onProcessorListener: IOCREngine.OnProcessorListener?
    ) {


    }

    override fun release() {

    }

    override fun bmpToText(
        parentPath: String,
        bitmap: Bitmap,
        onProcessorListener: IOCREngine.OnProcessorListener?
    ) {


    }


}