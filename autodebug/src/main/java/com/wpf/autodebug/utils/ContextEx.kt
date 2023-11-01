package com.wpf.autodebug.utils

import android.content.Context
import android.content.res.AssetManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun Context.getSp(key: String, spName: String, defValue: String = ""): String {
    val sp = getSharedPreferences(spName, Context.MODE_PRIVATE)
    return sp.getString(key, defValue) ?: defValue
}

fun Context.saveSp(key: String, spName: String, value: String) {
    val sp = getSharedPreferences(spName, Context.MODE_PRIVATE).edit()
    sp?.putString(key, value)
    sp?.apply()
}

fun Context?.getAssetData(fileName: String): String {
    if (this == null) return ""
    val stringBuilder = StringBuilder()
    // 获得assets资源管理器
    kotlin.runCatching {
        val bufferedReader = BufferedReader(
            InputStreamReader(
                assets.open(fileName), "utf-8"
            )
        )
        var line: String
        while (bufferedReader.readLine().also { line = it } != null) {
            stringBuilder.append(line.trim { it <= ' ' })
        }
        bufferedReader.close()
        assets.close()
    }
    return stringBuilder.toString()
}