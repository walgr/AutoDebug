package com.wpf.patchtool.deal

import com.wpf.patchtool.utils.SophixPatchTool

object DealApk {

    /**
     * @return patch文件路径
     */
    fun deal(
        srcApkPath: String,
        fixApkPath: String,
        signFilePath: String,
        signAlias: String,
        keyStorePassword: String,
        keyPassword: String,
        classFilterFilePath: String,
        isForceColdFix: Boolean,
        isIgnoreSo: Boolean,
        isIgnoreRes: Boolean,
    ): String {
        val outPatchFile = SophixPatchTool.sophixPatchFile
        val outPatchParentPath = outPatchFile.parent.replace("\\", "\\\\")
        val configFile = SophixPatchTool.profilesJsonFile
        var configStr = configFile.readBytes().decodeToString()
        configStr = configStr.replace("\"OldApkPath\": \"\"", "\"OldApkPath\": \"$srcApkPath\"")
        configStr = configStr.replace("\"NewApkPath\": \"\"", "\"NewApkPath\": \"$fixApkPath\"")
        configStr = configStr.replace("\"KeyStorePath\": \"\"", "\"KeyStorePath\": \"$signFilePath\"")
        configStr = configStr.replace("\"KeyAlias\": \"\"", "\"KeyAlias\": \"$signAlias\"")
        configStr = configStr.replace("\"KeyStorePassword\": \"\"", "\"KeyStorePassword\": \"$keyStorePassword\"")
        configStr = configStr.replace("\"KeyPassword\": \"\"", "\"KeyPassword\": \"$keyPassword\"")
        configStr = configStr.replace("\"ClassFilterFilePath\": \"\"", "\"ClassFilterFilePath\": \"$classFilterFilePath\"")
        configStr = configStr.replace("\"OutputDirPath\": \"\"", "\"OutputDirPath\": \"${outPatchParentPath}\"")
        configStr = configStr.replace("\"isForceColdFix\": false", "\"isForceColdFix\": $isForceColdFix")
        configStr = configStr.replace("\"isIgnoreSo\": false", "\"isIgnoreSo\": $isIgnoreSo")
        configStr = configStr.replace("\"isIgnoreRes\": false", "\"isIgnoreRes\": $isIgnoreRes")
        println("配置文件已修改")
        println(configStr)
        configFile.writeText(configStr)
        return if (SophixPatchTool.deal(configFile.path)) {
            SophixPatchTool.delJar()
            outPatchFile.path.replace("\\", "\\\\")
        } else ""
    }
}