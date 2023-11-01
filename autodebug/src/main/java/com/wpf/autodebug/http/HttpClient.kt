package com.wpf.autodebug.http

import android.util.Log
import com.wpf.autodebug.utils.createCheck
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }
    }

    fun get(
        serverUrl: String,
        request: HttpRequestBuilder.() -> Unit = {},
        callback: ((String) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val responseData = client.get(serverUrl, request)
                CoroutineScope(Dispatchers.Main).launch {
                    if (responseData.status == HttpStatusCode.OK) {
                        callback?.invoke(responseData.bodyAsText())
                    } else {
                        callback?.invoke(responseData.bodyAsText())
                    }
                }
            }.onFailure {
                CoroutineScope(Dispatchers.Main).launch {
                    callback?.invoke(it.message ?: "")
                }
            }
        }
    }

    fun downloadFile(
        serverUrl: String,
        request: HttpRequestBuilder.() -> Unit = {},
        savePath: String,
        callback: ((File?) -> Unit)?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val responseData = client.get(serverUrl, request)
                CoroutineScope(Dispatchers.Main).launch {
                    if (responseData.status == HttpStatusCode.OK) {
                        val contentDisposition =
                            responseData.headers[HttpHeaders.ContentDisposition]
                        Log.i("热修复", "下载文件:$contentDisposition")
                        if (contentDisposition?.isNotEmpty() == true && contentDisposition.contains(
                                ContentDisposition.Parameters.FileName + "="
                            )
                        ) {
                            val realSaveFile = File(
                                savePath + contentDisposition.split(";").find {
                                    it.contains(ContentDisposition.Parameters.FileName + "=")
                                }?.substringAfterLast(ContentDisposition.Parameters.FileName + "=")
                            ).createCheck(true)
                            responseData.bodyAsChannel().toInputStream()
                                .copyTo(realSaveFile.outputStream())
                            callback?.invoke(realSaveFile)
                        } else {
                            callback?.invoke(null)
                        }
                    } else {
                        callback?.invoke(null)
                    }
                }
            }.onFailure {
                CoroutineScope(Dispatchers.Main).launch {
                    callback?.invoke(null)
                }
            }
        }
    }
}