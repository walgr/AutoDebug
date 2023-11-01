package com.wpf.dexfix.utils

import com.wpf.dexfix.isLinuxRuntime
import com.wpf.dexfix.isMacRuntime
import com.wpf.dexfix.isWinRuntime
import java.io.File

object JADXUtil {

    private var jadxOutPath: String = ""
    private val jadxPath: String by lazy {
        val jadxZipFile = ResourceManager.getResourceFile("jadx-1.4.7.zip", isFile = true)
        jadxOutPath = jadxZipFile.parent + File.separator + "jadx-1.4.7" + File.separator
        FileUtil.unZipFiles(jadxZipFile, jadxOutPath)
        ResourceManager.delResourceByName("jadx-1.4.7.zip")
        return@lazy jadxOutPath + "bin" + File.separator + if (isWinRuntime) "jadx.bat" else "jadx"
    }

    fun delJar() {
        ResourceManager.delResourceByPath("jadx-1.4.7")
    }

    /**
     * jadx-1.4.7/bin/jadx -d "/mnt/d/Android/Android Project/AutoDebug" --single-class "cn.goodjobs.community.SophixStubApplication" classes.dex
     */
    fun getJavaInDex(findJavaFile: String, outJavaPath: String, dexPath: String): Boolean {
        if (isLinuxRuntime || isMacRuntime) {
            Runtime.getRuntime().exec(arrayOf("chmod", "-R", "777", jadxPath))
        }
        val cmd = arrayOf(jadxPath, "-d", outJavaPath, "--single-class", findJavaFile, dexPath)
        val process = Runtime.getRuntime().exec(cmd)
        val error = process.inputStream.readBytes().decodeToString()
        if (!error.contains("Saving class")) {
            println(error)
        }
        val result = process.waitFor()
        process.destroy()
        return result == 0
    }
}