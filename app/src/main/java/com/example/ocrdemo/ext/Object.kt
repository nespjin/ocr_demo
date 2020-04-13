package com.example.ocrdemo.ext

/**
 *
 * Author: <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2020/4/13 12:07 PM
 * Project: OCRDemo
 * Description:
 **/
val <T : Any> T.TAG: String
    get() {
        return javaClass.simpleName
    }