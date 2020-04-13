package com.example.ocrdemo.ocrengine

import android.graphics.Bitmap

/**
 *
 * Author: <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2020/4/13 2:21 PM
 * Project: OCRDemo
 * Description:
 **/
interface IOCREngine {


    fun imgFileToText(
        parentPath: String,
        imgFilePath: String,
        onProcessorListener: OnProcessorListener?
    )

    fun bmpToText(
        parentPath: String,
        bitmap: Bitmap,
        onProcessorListener: OnProcessorListener? = null
    )


     fun selectNum(string: String): String {
        val li = ArrayList<String>()
//        val s1 = string.replace("[a-z]", " ")
        val s1 = string.replace("[\\d]", " ").replace("_", "")
        // 把字符串中的字母全部替换为空格
        val strList = s1.split(" ")// 以空格分割字符串

        for (s in strList) {// 把字符串中连续的数字串存至map中，其中key为数字串，value为数字串长度
            if (s.isNotEmpty() && s[0] != " ".toCharArray()[0]) {// 由于有连续空格，使用空格分割时会出现多个空子串，空子串不予考虑，故有此判断
                li.add(s)
            }
        }

        li.sortWith(Comparator { o1, o2 ->
            Integer.valueOf(o1.length).compareTo(
                Integer.valueOf(o2.length)
            )
        })

        val sb = StringBuffer()//用来存储长度最大且相等的数字串
        for (s2 in li) {
            if (s2.length == li[li.size - 1].length) {
                sb.append(li[li.indexOf(s2)])
            }
        }

        return sb.toString()
    }

    interface OnProcessorListener {

        fun onStartProcess()

        fun onLoadBitmap(bitmap: Bitmap)

        fun onFailed(error: String)

        fun onSuccess(text: String)
    }
}