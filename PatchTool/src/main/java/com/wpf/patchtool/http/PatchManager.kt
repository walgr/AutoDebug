package com.wpf.patchtool.http

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import java.io.File

object PatchManager {

    fun downloadSrcApk(
        serverBaseUrl: String,
        packageName: String,
        versionName: String,
        savePath: String,
        callback: (File?) -> Unit
    ) {
        HttpClient.downloadFile(
            "${serverBaseUrl}/getApk?package=$packageName&appVersion=$versionName",
            request = {
                timeout {
                    requestTimeoutMillis = 300000
                }
            },
            savePath = savePath,
            callback = callback
        )
    }

    fun uploadPatch(
        serverBaseUrl: String,
        uploadPatch: File,
        packageName: String,
        versionName: String,
        callback: (Boolean) -> Unit
    ) {
        HttpClient.uploadApk("${serverBaseUrl}uploadPatch", uploadPatch, packageName, versionName, callback)
    }
}