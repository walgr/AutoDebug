package com.wpf.dexfix.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.timeout
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dongliu.apk.parser.ApkParsers
import java.io.File

object HttpClient {
    private val client: HttpClient by lazy {
        HttpClient(CIO) {
            engine {
                requestTimeout = 60000
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 60000
            }
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }
    }

    fun uploadApk(
        serverUrl: String,
        uploadApk: File,
        callback: ((Boolean) -> Unit)?
    ) {
        if (!uploadApk.exists() && uploadApk.extension != "apk") {
            println("请上传apk")
            callback?.invoke(false)
        }
        val apkMetaData = ApkParsers.getMetaInfo(uploadApk)
        val packageName = apkMetaData.packageName
        val versionName = apkMetaData.versionName
        runBlocking {
            runCatching {
                val responseData = client.put(serverUrl) {
                    timeout {
                        requestTimeoutMillis = 120000
                    }
                    setBody(MultiPartFormDataContent(
                        formData {
                            append("package", packageName)
                            append("appVersion", versionName)
                            append("file", uploadApk.readBytes(), apkHeader(uploadApk.path))
                        }
                    ))
                }
                    if (responseData.status == HttpStatusCode.OK) {
                        callback?.invoke(true)
                    } else {
                        callback?.invoke(false)
                    }
            }.onFailure {
                    callback?.invoke(false)
            }
        }
    }

    private fun apkHeader(filePath: String): Headers {
        return Headers.build {
            append(HttpHeaders.ContentType, "application/vnd.android.package-archive")
            append(HttpHeaders.ContentDisposition, "filename=\"${File(filePath).name}\"")
        }
    }
}