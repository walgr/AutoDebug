package com.wpf.dexfix.utils

import com.wpf.dexfix.isLinuxRuntime
import com.wpf.dexfix.isMacRuntime
import com.wpf.dexfix.isWinRuntime
import java.io.File

/**
 * apk对齐
 */

object ZipalignUtil {
    private val zipalignPath: String by lazy {
        if (isWinRuntime) {
            ResourceManager.getResourceFile("zipalign.exe", isFile = true).path
        } else if (isLinuxRuntime) {
            val zipalignZipFile = ResourceManager.getResourceFile("zipalign_linux.zip", isFile = true)
            val zipalignOutPath = zipalignZipFile.parent + File.separator + "zipalign_linux"
            FileUtil.unZipFiles(zipalignZipFile, zipalignOutPath)
            return@lazy zipalignOutPath + File.separator + "zipalign_linux"
        } else if (isMacRuntime) {
            ResourceManager.getResourceFile("zipalign_mac", isFile = true).path
        } else {
            ""
        }
    }

    fun delJar() {
        ResourceManager.delResourceByName("lib64/libc++.so")
        ResourceManager.delResourceByPath(zipalignPath)
    }

    fun check(inputApkFile: String): Boolean {
        if (isLinuxRuntime || isMacRuntime) {
            Runtime.getRuntime().exec(arrayOf("chmod", "-R", "+x", zipalignPath))
        }
        val cmd = arrayOf(zipalignPath, "-c", "-v", "4", inputApkFile)
        val result = Runtime.getRuntime().exec(cmd)
        val resultStr = result.inputStream.readBytes().decodeToString()
        return resultStr.contains("succesful")
    }

    fun zipalign(inputApkFile: String, outApkFile: String): Boolean {
        if (isLinuxRuntime || isMacRuntime) {
            val resP = Runtime.getRuntime().exec(arrayOf("chmod", "-R", "+x", zipalignPath))
            val resPStr =resP.inputStream.readBytes().decodeToString()
            if (resPStr.isNotEmpty()) {
                println(resPStr)
            }
        }
        val cmd = arrayOf(zipalignPath, "-p", "-f", "-v", "4", inputApkFile, outApkFile)
        val result = Runtime.getRuntime().exec(cmd)
        val resultStr = result.inputStream.readBytes().decodeToString()
        val error = result.errorStream.readBytes().decodeToString()
        if (error.isNotEmpty()) {
            println(error)
        }
        if (!resultStr.contains("succesful")) {
            println(resultStr)
        }
        result.destroy()
        return resultStr.contains("succesful")
    }
}