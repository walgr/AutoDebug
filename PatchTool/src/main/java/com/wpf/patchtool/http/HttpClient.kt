package com.wpf.patchtool.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.timeout
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentDisposition
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dongliu.apk.parser.ApkParsers
import java.io.File

object HttpClient {
    private val client: HttpClient by lazy {
        HttpClient(CIO) {
            engine {
                requestTimeout = 120000
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 120000
                connectTimeoutMillis = 120000
            }
//            install(Logging) {
//                logger = Logger.SIMPLE
//                level = LogLevel.ALL
//            }
        }
    }

    fun downloadFile(
        serverUrl: String,
        request: HttpRequestBuilder.() -> Unit = {},
        savePath: String,
        callback: ((File?) -> Unit)? = null
    ) {
        runBlocking {
            runCatching {
                val responseData = client.get(serverUrl, request)
                if (responseData.status == HttpStatusCode.OK) {
                    val contentDisposition =
                        responseData.headers[HttpHeaders.ContentDisposition]
                    println("热修复下载文件:$contentDisposition")
                    if (contentDisposition?.isNotEmpty() == true && contentDisposition.contains(
                            ContentDisposition.Parameters.FileName + "="
                        )
                    ) {
                        val realSaveFile = File(
                            savePath + File.separator
                                    + contentDisposition.split(";").find {
                                it.contains(ContentDisposition.Parameters.FileName + "=")
                            }?.substringAfterLast(ContentDisposition.Parameters.FileName + "=")
                        )
                        responseData.bodyAsChannel().toInputStream()
                            .copyTo(realSaveFile.outputStream())
                        callback?.invoke(realSaveFile)
                    } else {
                        callback?.invoke(null)
                    }
                } else {
                    callback?.invoke(null)
                }
            }.onFailure {
                callback?.invoke(null)
            }
        }
    }

    fun uploadApk(
        serverUrl: String,
        uploadApk: File,
        packageName: String,
        versionName: String,
        callback: ((Boolean) -> Unit)?
    ) {
        if (!uploadApk.exists()) {
            println("请上传文件")
            callback?.invoke(false)
        }
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