package com.wpf.dexfix.http

import java.io.File

object ApkManager {

    fun uploadApk(serverBaseUrl: String, uploadApk: File, callable: (Boolean) -> Unit) {
        HttpClient.uploadApk("${serverBaseUrl}uploadApk", uploadApk, callable)
    }
}