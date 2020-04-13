package com.example.ocrdemo

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.ocrdemo.ext.TAG
import com.example.ocrdemo.ocrengine.IOCREngine
import com.example.ocrdemo.ocrengine.TesseractOCREngine
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IOCREngine.OnProcessorListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStartProcess() {
        printLog("Process start")
    }

    override fun onLoadBitmap(bitmap: Bitmap) {
        imageView.setImageBitmap(bitmap)
    }

    override fun onFailed(error: String) {
        printLog("Process error: $error")
    }

    override fun onSuccess(text: String) {
        printLog("Process success: $text")
    }

    private fun printLog(log: String) {
        tvReslut.text = tvReslut.text.toString() + "\n" + log
    }

    companion object {
        fun getImgFilePath(context: Context): String {
            val path = getParentDirPath(context) + "/test.JPG"
            Log.e(TAG, "MainActivity.getImgFilePath: $path")
            return path
        }

        fun getParentDirPath(context: Context): String {
            return context.getExternalFilesDir(null)?.absolutePath ?: ""
        }
    }

    fun startProcess(view: View) {
        tvReslut.text = ""
        TesseractOCREngine()
            .imgFileToText(getParentDirPath(this), getImgFilePath(this), this)
    }
}
