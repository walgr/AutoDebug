package com.wpf.patchtool.utils

import com.alibaba.fastjson.JSON
import com.taobao.sophix.Main
import java.io.File

object SophixPatchTool {
    val profilesJsonFile: File by lazy {
        ResourceManager.getResourceFile("PatchTool/profiles.json", overwrite = true)
    }

    val sophixPatchFile: File by lazy {
        File(ResourceManager.getTempPath() + "sophix-patch.jar")
    }

    fun delJar() {
        ResourceManager.delResourceByPath(profilesJsonFile.path)
    }

    fun deal(configPath: String): Boolean {
        runCatching {
            val configStr = File(configPath).readBytes().decodeToString().replace("\r\n", "")
            val configJson = JSON.parseObject(configStr)
            println("读取到配置文件内容")
            println(configStr)
            Main.main(
                arrayOf(
                    "-c",
                    "patch",
                    "-s",
                    configJson.getString("OldApkPath"),
                    "-f",
                    configJson.getString("NewApkPath"),
                    "-w",
                    configJson.getString("OutputDirPath"),
                    "-k",
                    configJson.getString("KeyStorePath"),
                    "-p",
                    configJson.getString("KeyPassword"),
                    "-e",
                    configJson.getString("KeyStorePassword"),
                    "-a",
                    configJson.getString("KeyAlias"),
                    "-y",
                    configJson.getString("AesKey"),
                    "-l",
                    configJson.getString("ClassFilterFilePath"),
                    "-o",
                    configJson.getBoolean("isForceColdFix").toString(),
                    "-ignore-so",
                    configJson.getBoolean("isIgnoreSo").toString(),
                    "-checkAppRef",
                    configJson.getBoolean("isIgnoreRes").toString(),
                )
            )
            return true
        }.getOrElse {
            it.printStackTrace()
        }
        return false
    }
}