package com.wpf.autodebug.http

import android.content.Context
import android.util.Log
import com.wpf.autodebug.utils.createCheck
import com.wpf.autodebug.utils.getAssetData
import com.wpf.autodebug.utils.getSp
import com.wpf.autodebug.utils.saveSp
import io.ktor.client.plugins.*
import org.json.JSONObject
import java.io.File
import java.lang.ref.SoftReference

object PatchManager {
    var context: SoftReference<Context> = SoftReference(null)
        set(value) {
            field = value
            File(patchRootPath).deleteRecursively()
            File(apkRootPath).deleteRecursively()
        }

    /**
     * 从autodebug.config中读取配置
     */
    val baseUrl: String by lazy {
        JSONObject(context.get()?.getAssetData("autodebug.config")!!).getString("baseUrl")
    }
    private val patchRootPath: String by lazy {
        context.get()?.externalCacheDir?.absolutePath + File.separator
    }
    private val apkRootPath: String by lazy {
        context.get()?.externalCacheDir?.absolutePath + File.separator
    }
    var loadPatchName = ""
        get() = context.get()?.getSp("loadPatchName", "autodebug") ?: ""
        set(value) {
            field = value
            context.get()?.saveSp("loadPatchName", "autodebug", field)
        }

    fun getPatch(packageName: String, appVersion: String, callable: (File?) -> Unit) {
        val downloadPath =
            patchRootPath + packageName + File.separator + appVersion + File.separator + "patch" + File.separator
        Log.i("热修复", "正在检查是否有补丁:${baseUrl}getPatch?package=$packageName&appVersion=$appVersion")
        HttpClient.downloadFile(
            "${baseUrl}getPatch?package=$packageName&appVersion=$appVersion",
            request = {
                timeout {
                    requestTimeoutMillis = 300000
                }
            },
            savePath = downloadPath
        ) {
            if (it != null) {
                if (loadPatchName != it.name) {
                    callable.invoke(it)
                } else {
                    Log.i("热修复", "patch已经加载")
                    callable.invoke(null)
                }
            } else {
                callable.invoke(null)
            }
        }
    }
}